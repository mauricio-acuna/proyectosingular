import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui';
import { ChartBarIcon, DocumentArrowDownIcon, EnvelopeIcon } from '@heroicons/react/24/outline';

interface AssessmentSummary {
  assessmentId: string;
  completedAt: string;
  answerCount: number;
  scores: {
    TECH: number;
    AI: number;
    COMMUNICATION: number;
    PORTFOLIO: number;
    OVERALL: number;
  };
  topGaps: string[];
  topRecommendations: string[];
}

const AssessmentDashboard: React.FC = () => {
  const { assessmentId } = useParams<{ assessmentId: string }>();
  const [summary, setSummary] = useState<AssessmentSummary | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');
  const [generatingReport, setGeneratingReport] = useState<boolean>(false);

  useEffect(() => {
    if (assessmentId) {
      loadAssessmentSummary();
    }
  }, [assessmentId]);

  const loadAssessmentSummary = async () => {
    try {
      const response = await fetch(`/api/v1/assessments/${assessmentId}/summary`);
      if (response.ok) {
        const data = await response.json();
        setSummary(data);
      } else {
        setError('Failed to load assessment summary');
      }
    } catch (error) {
      console.error('Error loading summary:', error);
      setError('Failed to load assessment summary');
    } finally {
      setLoading(false);
    }
  };

  const generateReport = async () => {
    if (!assessmentId) return;

    setGeneratingReport(true);
    try {
      const response = await fetch(`/api/v1/assessments/${assessmentId}/report`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          title: 'AI Readiness Assessment Report',
          includeCharts: true,
          includeRecommendations: true
        })
      });

      if (response.ok) {
        const reportData = await response.json();
        // Open download URL in new tab
        if (reportData.downloadUrl) {
          window.open(reportData.downloadUrl, '_blank');
        }
      } else {
        setError('Failed to generate report');
      }
    } catch (error) {
      console.error('Error generating report:', error);
      setError('Failed to generate report');
    } finally {
      setGeneratingReport(false);
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
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-gray-500">Loading assessment summary...</div>
      </div>
    );
  }

  if (error || !summary) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-red-500">Error: {error || 'Summary not found'}</div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Assessment Dashboard</h1>
          <p className="text-gray-600">Assessment ID: {summary.assessmentId}</p>
          <p className="text-sm text-gray-500">
            Completed: {new Date(summary.completedAt).toLocaleDateString()}
          </p>
        </div>
        <div className="flex space-x-4">
          <button
            onClick={generateReport}
            disabled={generatingReport}
            className="flex items-center px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            {generatingReport ? (
              <>
                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Generating...
              </>
            ) : (
              <>
                <DocumentArrowDownIcon className="h-5 w-5 mr-2" />
                Generate Report
              </>
            )}
          </button>
        </div>
      </div>

      {/* Overall Score */}
      <Card>
        <CardHeader>
          <CardTitle>Overall AI Readiness Score</CardTitle>
          <CardDescription>Your comprehensive assessment result</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-center">
            <div className={`text-6xl font-bold px-8 py-6 rounded-full ${getScoreColor(summary.scores.OVERALL)}`}>
              {Math.round(summary.scores.OVERALL)}%
            </div>
          </div>
          <div className="text-center mt-4">
            <p className={`text-lg font-medium ${getScoreColor(summary.scores.OVERALL).split(' ')[0]}`}>
              {getScoreLabel(summary.scores.OVERALL)}
            </p>
          </div>
        </CardContent>
      </Card>

      {/* Detailed Scores */}
      <Card>
        <CardHeader>
          <CardTitle>Score Breakdown</CardTitle>
          <CardDescription>Performance across assessment pillars</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            <div className="text-center">
              <div className={`text-2xl font-bold mb-2 px-3 py-2 rounded-lg ${getScoreColor(summary.scores.TECH)}`}>
                {Math.round(summary.scores.TECH)}%
              </div>
              <h3 className="font-medium text-gray-900">Technical Skills</h3>
              <p className="text-sm text-gray-600 mt-1">Core technical competencies</p>
            </div>
            
            <div className="text-center">
              <div className={`text-2xl font-bold mb-2 px-3 py-2 rounded-lg ${getScoreColor(summary.scores.AI)}`}>
                {Math.round(summary.scores.AI)}%
              </div>
              <h3 className="font-medium text-gray-900">AI Knowledge</h3>
              <p className="text-sm text-gray-600 mt-1">Understanding of AI concepts</p>
            </div>
            
            <div className="text-center">
              <div className={`text-2xl font-bold mb-2 px-3 py-2 rounded-lg ${getScoreColor(summary.scores.COMMUNICATION)}`}>
                {Math.round(summary.scores.COMMUNICATION)}%
              </div>
              <h3 className="font-medium text-gray-900">Communication</h3>
              <p className="text-sm text-gray-600 mt-1">Ability to explain AI concepts</p>
            </div>
            
            <div className="text-center">
              <div className={`text-2xl font-bold mb-2 px-3 py-2 rounded-lg ${getScoreColor(summary.scores.PORTFOLIO)}`}>
                {Math.round(summary.scores.PORTFOLIO)}%
              </div>
              <h3 className="font-medium text-gray-900">Portfolio</h3>
              <p className="text-sm text-gray-600 mt-1">Demonstrated AI experience</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Key Insights */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Top Gaps */}
        <Card>
          <CardHeader>
            <CardTitle>Areas for Improvement</CardTitle>
            <CardDescription>Key gaps identified in your assessment</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {summary.topGaps.map((gap, index) => (
                <div key={index} className="flex items-start">
                  <div className="flex-shrink-0 w-6 h-6 bg-orange-100 rounded-full flex items-center justify-center mt-1">
                    <span className="text-orange-600 text-sm font-medium">{index + 1}</span>
                  </div>
                  <p className="ml-3 text-gray-700">{gap}</p>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Top Recommendations */}
        <Card>
          <CardHeader>
            <CardTitle>Key Recommendations</CardTitle>
            <CardDescription>Suggested actions to improve your AI readiness</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {summary.topRecommendations.map((recommendation, index) => (
                <div key={index} className="flex items-start">
                  <div className="flex-shrink-0 w-6 h-6 bg-blue-100 rounded-full flex items-center justify-center mt-1">
                    <span className="text-blue-600 text-sm font-medium">{index + 1}</span>
                  </div>
                  <p className="ml-3 text-gray-700">{recommendation}</p>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Assessment Info */}
      <Card>
        <CardHeader>
          <CardTitle>Assessment Details</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
            <div>
              <span className="font-medium text-gray-900">Questions Answered:</span>
              <span className="ml-2 text-gray-600">{summary.answerCount}</span>
            </div>
            <div>
              <span className="font-medium text-gray-900">Completion Date:</span>
              <span className="ml-2 text-gray-600">{new Date(summary.completedAt).toLocaleDateString()}</span>
            </div>
            <div>
              <span className="font-medium text-gray-900">Assessment Type:</span>
              <span className="ml-2 text-gray-600">AI Readiness</span>
            </div>
            <div>
              <span className="font-medium text-gray-900">Version:</span>
              <span className="ml-2 text-gray-600">1.0</span>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default AssessmentDashboard;
