import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { ChevronLeftIcon, ChevronRightIcon, CheckIcon } from '@heroicons/react/24/outline';

interface Question {
  id: string;
  text: string;
  type: 'LIKERT_SCALE' | 'MULTIPLE_CHOICE' | 'TEXT' | 'NUMERIC';
  pillar: 'TECHNICAL' | 'AI_KNOWLEDGE' | 'COMMUNICATION' | 'PORTFOLIO';
  options?: string[];
  required: boolean;
}

interface Answer {
  questionId: string;
  valueNumeric?: number;
  valueText?: string;
}

interface Assessment {
  id: string;
  roleId: string;
  version: string;
  answers: Answer[];
}

const AssessmentWizard: React.FC = () => {
  const { assessmentId } = useParams<{ assessmentId: string }>();
  const location = useLocation();
  const navigate = useNavigate();
  
  const [assessment, setAssessment] = useState<Assessment | null>(location.state?.assessment || null);
  const [questions, setQuestions] = useState<Question[]>([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState<number>(0);
  const [answers, setAnswers] = useState<Record<string, Answer>>({});
  const [loading, setLoading] = useState<boolean>(true);
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    if (!assessment && assessmentId) {
      // Load assessment if not passed via state
      loadAssessment();
    } else if (assessment) {
      loadQuestions();
    }
  }, [assessment, assessmentId]);

  const loadAssessment = async () => {
    try {
      const response = await fetch(`/api/v1/assessments/${assessmentId}`);
      if (response.ok) {
        const data = await response.json();
        setAssessment(data);
        loadQuestions();
      } else {
        setError('Assessment not found');
      }
    } catch (error) {
      console.error('Error loading assessment:', error);
      setError('Failed to load assessment');
    }
  };

  const loadQuestions = async () => {
    if (!assessment) return;
    
    setLoading(true);
    try {
      // Load questions for the specific role
      const response = await fetch(`/api/v1/roles/${assessment.roleId}/questions`);
      if (response.ok) {
        const data = await response.json();
        if (data.success && data.data) {
          setQuestions(data.data);
          
          // Load existing answers if any
          const existingAnswers: Record<string, Answer> = {};
          assessment.answers.forEach(answer => {
            existingAnswers[answer.questionId] = answer;
          });
          setAnswers(existingAnswers);
        }
      } else {
        setError('Failed to load questions');
      }
    } catch (error) {
      console.error('Error loading questions:', error);
      setError('Failed to load questions');
    } finally {
      setLoading(false);
    }
  };

  const handleAnswer = (questionId: string, value: number | string) => {
    const answer: Answer = {
      questionId,
      ...(typeof value === 'number' ? { valueNumeric: value } : { valueText: value })
    };

    setAnswers(prev => ({
      ...prev,
      [questionId]: answer
    }));
  };

  const goToNextQuestion = () => {
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(prev => prev + 1);
    }
  };

  const goToPreviousQuestion = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(prev => prev - 1);
    }
  };

  const submitAssessment = async () => {
    if (!assessment) return;

    setSubmitting(true);
    setError('');

    try {
      const assessmentData = {
        roleId: assessment.roleId,
        version: assessment.version,
        locale: 'en',
        hoursPerWeek: 40,
        consent: true,
        answers: Object.values(answers)
      };

      const response = await fetch('/api/v1/assessments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(assessmentData),
      });

      if (response.ok) {
        const result = await response.json();
        if (result.success) {
          // Navigate to completion page
          navigate(`/assessment/${result.data.id}/complete`, {
            state: { assessment: result.data }
          });
        } else {
          setError(result.message || 'Failed to submit assessment');
        }
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Failed to submit assessment');
      }
    } catch (error) {
      console.error('Error submitting assessment:', error);
      setError('An unexpected error occurred');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading assessment...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8 max-w-md w-full mx-4">
          <div className="text-center">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Error</h2>
            <p className="text-gray-600 mb-6">{error}</p>
            <button
              onClick={() => navigate('/assessment')}
              className="w-full bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              Start New Assessment
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (questions.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-600">No questions available for this role.</p>
        </div>
      </div>
    );
  }

  const currentQuestion = questions[currentQuestionIndex];
  const currentAnswer = answers[currentQuestion.id];
  const isLastQuestion = currentQuestionIndex === questions.length - 1;
  const progress = ((currentQuestionIndex + 1) / questions.length) * 100;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header with Progress */}
      <div className="bg-white border-b border-gray-200 sticky top-0 z-10">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-lg font-semibold text-gray-900">AI Readiness Assessment</h1>
              <p className="text-sm text-gray-600">
                Question {currentQuestionIndex + 1} of {questions.length}
              </p>
            </div>
            <div className="text-sm text-gray-600">
              {Math.round(progress)}% Complete
            </div>
          </div>
          
          {/* Progress Bar */}
          <div className="mt-4">
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-indigo-600 h-2 rounded-full transition-all duration-300"
                style={{ width: `${progress}%` }}
              ></div>
            </div>
          </div>
        </div>
      </div>

      {/* Question Content */}
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <div className="p-8">
            {/* Question */}
            <div className="mb-8">
              <div className="mb-4">
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-800">
                  {currentQuestion.pillar.replace('_', ' ')}
                </span>
              </div>
              <h2 className="text-2xl font-semibold text-gray-900 mb-2">
                {currentQuestion.text}
              </h2>
              {currentQuestion.required && (
                <p className="text-sm text-red-600">* This question is required</p>
              )}
            </div>

            {/* Answer Input */}
            <div className="mb-8">
              {currentQuestion.type === 'LIKERT_SCALE' && (
                <div className="space-y-2">
                  <div className="flex justify-between text-sm text-gray-600 mb-4">
                    <span>Strongly Disagree</span>
                    <span>Strongly Agree</span>
                  </div>
                  <div className="flex space-x-4 justify-center">
                    {[1, 2, 3, 4, 5].map((value) => (
                      <label key={value} className="flex flex-col items-center cursor-pointer">
                        <input
                          type="radio"
                          name={`question-${currentQuestion.id}`}
                          value={value}
                          checked={currentAnswer?.valueNumeric === value}
                          onChange={() => handleAnswer(currentQuestion.id, value)}
                          className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300"
                        />
                        <span className="mt-2 text-sm text-gray-600">{value}</span>
                      </label>
                    ))}
                  </div>
                </div>
              )}

              {currentQuestion.type === 'MULTIPLE_CHOICE' && currentQuestion.options && (
                <div className="space-y-3">
                  {currentQuestion.options.map((option, index) => (
                    <label key={index} className="flex items-center cursor-pointer">
                      <input
                        type="radio"
                        name={`question-${currentQuestion.id}`}
                        value={index + 1}
                        checked={currentAnswer?.valueNumeric === index + 1}
                        onChange={() => handleAnswer(currentQuestion.id, index + 1)}
                        className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300"
                      />
                      <span className="ml-3 text-gray-700">{option}</span>
                    </label>
                  ))}
                </div>
              )}

              {currentQuestion.type === 'TEXT' && (
                <textarea
                  value={currentAnswer?.valueText || ''}
                  onChange={(e) => handleAnswer(currentQuestion.id, e.target.value)}
                  rows={4}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="Enter your response..."
                />
              )}

              {currentQuestion.type === 'NUMERIC' && (
                <input
                  type="number"
                  value={currentAnswer?.valueNumeric || ''}
                  onChange={(e) => handleAnswer(currentQuestion.id, parseInt(e.target.value))}
                  className="w-full max-w-xs px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="Enter a number..."
                />
              )}
            </div>
          </div>

          {/* Navigation */}
          <div className="bg-gray-50 px-8 py-6 border-t border-gray-200 flex justify-between items-center">
            <button
              onClick={goToPreviousQuestion}
              disabled={currentQuestionIndex === 0}
              className={`flex items-center px-4 py-2 border border-gray-300 rounded-md text-sm font-medium ${
                currentQuestionIndex === 0
                  ? 'text-gray-400 cursor-not-allowed'
                  : 'text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500'
              }`}
            >
              <ChevronLeftIcon className="h-4 w-4 mr-2" />
              Previous
            </button>

            {isLastQuestion ? (
              <button
                onClick={submitAssessment}
                disabled={submitting || (currentQuestion.required && !currentAnswer)}
                className={`flex items-center px-6 py-2 border border-transparent rounded-md text-sm font-medium text-white ${
                  submitting || (currentQuestion.required && !currentAnswer)
                    ? 'bg-gray-400 cursor-not-allowed'
                    : 'bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500'
                }`}
              >
                {submitting ? (
                  <>
                    <svg className="animate-spin -ml-1 mr-3 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Submitting...
                  </>
                ) : (
                  <>
                    <CheckIcon className="h-4 w-4 mr-2" />
                    Complete Assessment
                  </>
                )}
              </button>
            ) : (
              <button
                onClick={goToNextQuestion}
                disabled={currentQuestion.required && !currentAnswer}
                className={`flex items-center px-4 py-2 border border-transparent rounded-md text-sm font-medium text-white ${
                  currentQuestion.required && !currentAnswer
                    ? 'bg-gray-400 cursor-not-allowed'
                    : 'bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500'
                }`}
              >
                Next
                <ChevronRightIcon className="h-4 w-4 ml-2" />
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AssessmentWizard;
