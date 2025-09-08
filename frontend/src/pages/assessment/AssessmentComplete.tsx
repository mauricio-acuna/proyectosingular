import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { CheckCircleIcon, DocumentArrowDownIcon, ArrowRightIcon, ChartBarIcon, ArrowPathIcon } from '@heroicons/react/24/outline';

interface AssessmentResult {
  id: string;
  roleId: string;
  scores: {
    overall: number;
    technical: number;
    aiKnowledge: number;
    communication: number;
    portfolio: number;
  };
  gaps: string[];
  recommendations: string[];
  completedAt: string;
}

const AssessmentComplete: React.FC = () => {
  const { assessmentId } = useParams<{ assessmentId: string }>();
  const location = useLocation();
  const navigate = useNavigate();
  
  const [result, setResult] = useState<AssessmentResult | null>(location.state?.assessment || null);
  const [loading, setLoading] = useState<boolean>(!result);
  const [error, setError] = useState<string>('');
  const [generatingPlan, setGeneratingPlan] = useState<boolean>(false);

  useEffect(() => {
    if (!result && assessmentId) {
      loadResult();
    }
  }, [assessmentId, result]);

  const loadResult = async () => {
    try {
      const response = await fetch(`/api/v1/assessments/${assessmentId}`);
      if (response.ok) {
        const data = await response.json();
        setResult(data);
      } else {
        setError('Assessment results not found');
      }
    } catch (error) {
      console.error('Error loading assessment result:', error);
      setError('Failed to load assessment results');
    } finally {
      setLoading(false);
    }
  };

  const generatePlan = async () => {
    if (!result) return;

    setGeneratingPlan(true);
    try {
      const response = await fetch(`/api/v1/assessments/${result.id}/plan`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ hoursPerWeek: 10 }), // Default hours
      });

      if (response.ok) {
        const planData = await response.json();
        if (planData.success) {
          // Navigate to plan view
          navigate(`/plan/${result.id}`, { state: { plan: planData.data } });
        } else {
          setError('Failed to generate plan');
        }
      } else {
        setError('Failed to generate plan');
      }
    } catch (error) {
      console.error('Error generating plan:', error);
      setError('Failed to generate plan');
    } finally {
      setGeneratingPlan(false);
    }
  };

  const downloadReport = async () => {
    if (!result) return;

    try {
      const response = await fetch(`/api/v1/assessments/${result.id}/report`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const reportData = await response.json();
        // Open report download URL
        window.open(`/api/v1/reports/${reportData.reportId}/download`, '_blank');
      } else {
        setError('Failed to generate report');
      }
    } catch (error) {
      console.error('Error downloading report:', error);
      setError('Failed to download report');
    }
  };

  const getScoreColor = (score: number) => {
    if (score >= 80) return 'text-green-600 bg-green-100';
    if (score >= 60) return 'text-yellow-600 bg-yellow-100';
    return 'text-red-600 bg-red-100';
  };

  const getScoreLabel = (score: number) => {
    if (score >= 80) return 'Excellent';
    if (score >= 60) return 'Good';
    if (score >= 40) return 'Fair';
    return 'Needs Improvement';
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading your results...</p>
        </div>
      </div>
    );
  }

  if (error || !result) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8 max-w-md w-full mx-4">
          <div className="text-center">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Error</h2>
            <p className="text-gray-600 mb-6">{error || 'Results not found'}</p>
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

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <CheckCircleIcon className="h-16 w-16 text-green-500" />
          </div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Assessment Complete!
          </h1>
          <p className="text-lg text-gray-600">
            Thank you for completing the AI Readiness Assessment. Here are your results.
          </p>
        </div>

        {/* Overall Score */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8 mb-8">
          <div className="text-center">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">Overall AI Readiness Score</h2>
            <div className="flex justify-center items-center mb-4">
              <div className={`text-6xl font-bold px-6 py-4 rounded-full ${getScoreColor(result.scores.overall)}`}>
                {Math.round(result.scores.overall)}%
              </div>
            </div>
            <p className={`text-lg font-medium ${getScoreColor(result.scores.overall).split(' ')[0]}`}>
              {getScoreLabel(result.scores.overall)}
            </p>
          </div>
        </div>

        {/* Detailed Scores */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8 mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Detailed Breakdown</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            <div className="text-center">
              <div className={`text-2xl font-bold mb-2 px-3 py-2 rounded-lg ${getScoreColor(result.scores.technical)}`}>
                {Math.round(result.scores.technical)}%
              </div>
              <h3 className="font-medium text-gray-900">Technical Skills</h3>
              <p className="text-sm text-gray-600 mt-1">Core technical competencies</p>
            </div>
            
            <div className="text-center">
              <div className={`text-2xl font-bold mb-2 px-3 py-2 rounded-lg ${getScoreColor(result.scores.aiKnowledge)}`}>
                {Math.round(result.scores.aiKnowledge)}%
              </div>
              <h3 className="font-medium text-gray-900">AI Knowledge</h3>
              <p className="text-sm text-gray-600 mt-1">Understanding of AI concepts</p>
            </div>
            
            <div className="text-center">
              <div className={`text-2xl font-bold mb-2 px-3 py-2 rounded-lg ${getScoreColor(result.scores.communication)}`}>
                {Math.round(result.scores.communication)}%
              </div>
              <h3 className="font-medium text-gray-900">Communication</h3>
              <p className="text-sm text-gray-600 mt-1">Ability to explain AI concepts</p>
            </div>
            
            <div className="text-center">
              <div className={`text-2xl font-bold mb-2 px-3 py-2 rounded-lg ${getScoreColor(result.scores.portfolio)}`}>
                {Math.round(result.scores.portfolio)}%
              </div>
              <h3 className="font-medium text-gray-900">Portfolio</h3>
              <p className="text-sm text-gray-600 mt-1">Demonstrated AI experience</p>
            </div>
          </div>
        </div>

        {/* Key Gaps */}
        {result.gaps && result.gaps.length > 0 && (
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8 mb-8">
            <h2 className="text-xl font-semibold text-gray-900 mb-6">Key Areas for Improvement</h2>
            <div className="space-y-3">
              {result.gaps.map((gap, index) => (
                <div key={index} className="flex items-start">
                  <div className="flex-shrink-0 w-6 h-6 bg-orange-100 rounded-full flex items-center justify-center mt-1">
                    <span className="text-orange-600 text-sm font-medium">{index + 1}</span>
                  </div>
                  <p className="ml-3 text-gray-700">{gap}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Recommendations */}
        {result.recommendations && result.recommendations.length > 0 && (
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8 mb-8">
            <h2 className="text-xl font-semibold text-gray-900 mb-6">Recommendations</h2>
            <div className="space-y-3">
              {result.recommendations.map((recommendation, index) => (
                <div key={index} className="flex items-start">
                  <div className="flex-shrink-0 w-6 h-6 bg-blue-100 rounded-full flex items-center justify-center mt-1">
                    <span className="text-blue-600 text-sm font-medium">{index + 1}</span>
                  </div>
                  <p className="ml-3 text-gray-700">{recommendation}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Actions */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Next Steps</h2>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <button
              onClick={generatePlan}
              disabled={generatingPlan}
              className="flex items-center justify-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {generatingPlan ? (
                <>
                  <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Generating...
                </>
              ) : (
                <>
                  <ArrowRightIcon className="h-5 w-5 mr-2" />
                  Get Development Plan
                </>
              )}
            </button>

            <button
              onClick={downloadReport}
              className="flex items-center justify-center px-6 py-3 border border-gray-300 text-base font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              <DocumentArrowDownIcon className="h-5 w-5 mr-2" />
              Download Report
            </button>

            <button
              onClick={() => navigate(`/assessment/${result.id}/dashboard`)}
              className="flex items-center justify-center px-6 py-3 border border-gray-300 text-base font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              <ChartBarIcon className="h-5 w-5 mr-2" />
              View Dashboard
            </button>

            <button
              onClick={() => navigate('/assessment')}
              className="flex items-center justify-center px-6 py-3 border border-gray-300 text-base font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              <ArrowPathIcon className="h-5 w-5 mr-2" />
              New Assessment
            </button>
          </div>
        </div>

        {/* Assessment Info */}
        <div className="mt-8 text-center text-sm text-gray-500">
          <p>Assessment completed on {new Date(result.completedAt).toLocaleDateString()}</p>
          <p className="mt-1">Assessment ID: {result.id}</p>
        </div>
      </div>
    </div>
  );
};

export default AssessmentComplete;
