import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronRightIcon, PlayIcon, ClockIcon, UserGroupIcon } from '@heroicons/react/24/outline';

interface Role {
  id: string;
  name: string;
  description: string;
  questionCount: number;
  estimatedTimeMinutes: number;
  targetAudience: string;
}

const AssessmentStart: React.FC = () => {
  const navigate = useNavigate();
  const [roles, setRoles] = useState<Role[]>([]);
  const [selectedRole, setSelectedRole] = useState<string>('');
  const [userEmail, setUserEmail] = useState<string>('');
  const [consent, setConsent] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    // Load available roles
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      const response = await fetch('/api/v1/roles');
      if (response.ok) {
        const data = await response.json();
        if (data.success && data.data) {
          setRoles(data.data);
        }
      }
    } catch (error) {
      console.error('Error fetching roles:', error);
      setError('Failed to load assessment roles');
    }
  };

  const startAssessment = async () => {
    if (!selectedRole || !consent) {
      setError('Please select a role and accept the terms');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const assessmentData = {
        roleId: selectedRole,
        version: '1.0', // Default version
        locale: 'en',
        hoursPerWeek: 40, // Default
        email: userEmail || undefined,
        consent: consent,
        answers: [] // Will be populated during assessment
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
        if (result.success && result.data) {
          // Navigate to assessment wizard with assessment ID
          navigate(`/assessment/${result.data.id}/wizard`, { 
            state: { assessment: result.data } 
          });
        } else {
          setError(result.message || 'Failed to start assessment');
        }
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Failed to start assessment');
      }
    } catch (error) {
      console.error('Error starting assessment:', error);
      setError('An unexpected error occurred');
    } finally {
      setLoading(false);
    }
  };

  const selectedRoleData = roles.find(role => role.id === selectedRole);

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            AI Readiness Assessment
          </h1>
          <p className="text-xl text-gray-600 max-w-2xl mx-auto">
            Discover your current AI skills and get a personalized development plan 
            to advance your career in the age of artificial intelligence.
          </p>
        </div>

        {/* Error Display */}
        {error && (
          <div className="mb-6 bg-red-50 border border-red-200 rounded-md p-4">
            <div className="flex">
              <div className="ml-3">
                <h3 className="text-sm font-medium text-red-800">Error</h3>
                <div className="mt-2 text-sm text-red-700">{error}</div>
              </div>
            </div>
          </div>
        )}

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          {/* Role Selection */}
          <div className="p-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-6">
              Select Your Role
            </h2>
            
            <div className="space-y-4">
              {roles.map((role) => (
                <div
                  key={role.id}
                  className={`relative rounded-lg border p-6 cursor-pointer transition-all hover:border-indigo-300 ${
                    selectedRole === role.id
                      ? 'border-indigo-500 bg-indigo-50'
                      : 'border-gray-200'
                  }`}
                  onClick={() => setSelectedRole(role.id)}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center">
                        <input
                          type="radio"
                          id={`role-${role.id}`}
                          name="selectedRole"
                          className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300"
                          checked={selectedRole === role.id}
                          onChange={() => setSelectedRole(role.id)}
                        />
                        <label htmlFor={`role-${role.id}`} className="ml-3 text-lg font-medium text-gray-900">
                          {role.name}
                        </label>
                      </div>
                      <p className="mt-2 text-gray-600">{role.description}</p>
                      
                      <div className="mt-4 flex items-center space-x-6 text-sm text-gray-500">
                        <div className="flex items-center">
                          <ClockIcon className="h-4 w-4 mr-1" />
                          ~{role.estimatedTimeMinutes} minutes
                        </div>
                        <div className="flex items-center">
                          <UserGroupIcon className="h-4 w-4 mr-1" />
                          {role.targetAudience}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Assessment Details */}
          {selectedRoleData && (
            <div className="bg-gray-50 px-8 py-6 border-t border-gray-200">
              <h3 className="text-lg font-medium text-gray-900 mb-4">
                Assessment Details
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
                <div>
                  <span className="font-medium text-gray-900">Questions:</span>
                  <span className="ml-2 text-gray-600">{selectedRoleData.questionCount}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-900">Duration:</span>
                  <span className="ml-2 text-gray-600">~{selectedRoleData.estimatedTimeMinutes} minutes</span>
                </div>
                <div>
                  <span className="font-medium text-gray-900">Target:</span>
                  <span className="ml-2 text-gray-600">{selectedRoleData.targetAudience}</span>
                </div>
              </div>
            </div>
          )}

          {/* User Information */}
          <div className="px-8 py-6 border-t border-gray-200">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Contact Information (Optional)
            </h3>
            <div className="max-w-md">
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                Email Address
              </label>
              <input
                type="email"
                id="email"
                value={userEmail}
                onChange={(e) => setUserEmail(e.target.value)}
                placeholder="your.email@example.com"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
              />
              <p className="mt-2 text-sm text-gray-500">
                We'll use this to send you your assessment results and personalized plan.
              </p>
            </div>
          </div>

          {/* Consent */}
          <div className="px-8 py-6 border-t border-gray-200">
            <div className="flex items-start">
              <input
                type="checkbox"
                id="consent"
                checked={consent}
                onChange={(e) => setConsent(e.target.checked)}
                className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded mt-1"
              />
              <label htmlFor="consent" className="ml-3 text-sm text-gray-700">
                I agree to participate in this assessment and understand that my responses will be used 
                to generate personalized recommendations. My data will be handled according to our privacy policy.
              </label>
            </div>
          </div>

          {/* Start Button */}
          <div className="px-8 py-6 border-t border-gray-200 bg-gray-50">
            <button
              onClick={startAssessment}
              disabled={!selectedRole || !consent || loading}
              className={`w-full flex justify-center items-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-white transition-colors ${
                !selectedRole || !consent || loading
                  ? 'bg-gray-400 cursor-not-allowed'
                  : 'bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500'
              }`}
            >
              {loading ? (
                <>
                  <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Starting Assessment...
                </>
              ) : (
                <>
                  <PlayIcon className="h-5 w-5 mr-2" />
                  Start Assessment
                  <ChevronRightIcon className="h-5 w-5 ml-2" />
                </>
              )}
            </button>
          </div>
        </div>

        {/* Info Section */}
        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="text-center">
            <div className="bg-indigo-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
              <ClockIcon className="h-8 w-8 text-indigo-600" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">Quick & Efficient</h3>
            <p className="text-gray-600">Complete the assessment in 15-30 minutes at your own pace.</p>
          </div>
          
          <div className="text-center">
            <div className="bg-green-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
              <UserGroupIcon className="h-8 w-8 text-green-600" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">Role-Specific</h3>
            <p className="text-gray-600">Tailored questions based on your professional role and goals.</p>
          </div>
          
          <div className="text-center">
            <div className="bg-purple-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
              <ChevronRightIcon className="h-8 w-8 text-purple-600" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">Actionable Results</h3>
            <p className="text-gray-600">Get a personalized development plan with concrete next steps.</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AssessmentStart;
