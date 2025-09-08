import React from 'react';

interface AssessmentProgressProps {
  currentQuestion: number;
  totalQuestions: number;
  pillarProgress?: {
    technical: number;
    aiKnowledge: number;
    communication: number;
    portfolio: number;
  };
}

const AssessmentProgress: React.FC<AssessmentProgressProps> = ({
  currentQuestion,
  totalQuestions,
  pillarProgress
}) => {
  const overallProgress = (currentQuestion / totalQuestions) * 100;

  const pillars = [
    { key: 'technical', name: 'Technical Skills', color: 'bg-blue-500' },
    { key: 'aiKnowledge', name: 'AI Knowledge', color: 'bg-purple-500' },
    { key: 'communication', name: 'Communication', color: 'bg-green-500' },
    { key: 'portfolio', name: 'Portfolio', color: 'bg-orange-500' }
  ];

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Assessment Progress</h3>
      
      {/* Overall Progress */}
      <div className="mb-6">
        <div className="flex justify-between items-center mb-2">
          <span className="text-sm font-medium text-gray-700">Overall Progress</span>
          <span className="text-sm text-gray-600">
            {currentQuestion} of {totalQuestions} questions
          </span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-3">
          <div
            className="bg-indigo-600 h-3 rounded-full transition-all duration-500"
            style={{ width: `${overallProgress}%` }}
          ></div>
        </div>
        <div className="text-right mt-1">
          <span className="text-sm text-gray-600">{Math.round(overallProgress)}% complete</span>
        </div>
      </div>

      {/* Pillar Progress */}
      {pillarProgress && (
        <div>
          <h4 className="text-sm font-medium text-gray-700 mb-3">Progress by Area</h4>
          <div className="space-y-3">
            {pillars.map((pillar) => {
              const progress = pillarProgress[pillar.key as keyof typeof pillarProgress] || 0;
              return (
                <div key={pillar.key}>
                  <div className="flex justify-between items-center mb-1">
                    <span className="text-sm text-gray-600">{pillar.name}</span>
                    <span className="text-sm text-gray-600">{Math.round(progress)}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div
                      className={`${pillar.color} h-2 rounded-full transition-all duration-500`}
                      style={{ width: `${progress}%` }}
                    ></div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Tips */}
      <div className="mt-6 p-4 bg-blue-50 rounded-lg">
        <h4 className="text-sm font-medium text-blue-900 mb-2">💡 Assessment Tips</h4>
        <ul className="text-sm text-blue-800 space-y-1">
          <li>• Answer honestly for the most accurate results</li>
          <li>• Take your time - there's no time limit</li>
          <li>• You can go back to previous questions</li>
          <li>• All answers are confidential</li>
        </ul>
      </div>
    </div>
  );
};

export default AssessmentProgress;
