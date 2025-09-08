import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { Plus, Search, Edit, Trash2, ArrowLeft, ArrowRight, Filter } from 'lucide-react';
import { Button, Card, CardContent, CardDescription, CardHeader, CardTitle, Input } from '../components/ui';
import { useQuestions, useDeleteQuestion } from '../hooks/useQuestions';
import type { Question } from '../types';
import { Pillar, QuestionType } from '../types';
import { formatRelativeTime } from '../utils';

export default function QuestionsPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const [searchTerm, setSearchTerm] = useState(searchParams.get('search') || '');
  const [selectedPillar, setSelectedPillar] = useState<Pillar | ''>((searchParams.get('pillar') as Pillar) || '');
  const [selectedType, setSelectedType] = useState<QuestionType | ''>((searchParams.get('type') as QuestionType) || '');
  
  const page = parseInt(searchParams.get('page') || '0');
  const size = parseInt(searchParams.get('size') || '10');
  
  const { data, isLoading, error } = useQuestions(page, size, searchTerm, selectedPillar, selectedType);
  const deleteQuestionMutation = useDeleteQuestion();

  const handleSearch = (value: string) => {
    setSearchTerm(value);
    updateSearchParams({ search: value });
  };

  const handlePillarFilter = (pillar: Pillar | '') => {
    setSelectedPillar(pillar);
    updateSearchParams({ pillar });
  };

  const handleTypeFilter = (type: QuestionType | '') => {
    setSelectedType(type);
    updateSearchParams({ type });
  };

  const updateSearchParams = (updates: Record<string, string>) => {
    const newParams = new URLSearchParams(searchParams);
    Object.entries(updates).forEach(([key, value]) => {
      if (value) {
        newParams.set(key, value);
      } else {
        newParams.delete(key);
      }
    });
    newParams.set('page', '0'); // Reset to first page on filter change
    setSearchParams(newParams);
  };

  const handlePageChange = (newPage: number) => {
    const newParams = new URLSearchParams(searchParams);
    newParams.set('page', newPage.toString());
    setSearchParams(newParams);
  };

  const handleDeleteQuestion = async (question: Question) => {
    if (window.confirm(`Are you sure you want to delete this question: "${question.text.substring(0, 50)}..."?`)) {
      try {
        await deleteQuestionMutation.mutateAsync(question.id);
      } catch (error) {
        console.error('Failed to delete question:', error);
      }
    }
  };

  const getPillarColor = (pillar: Pillar) => {
    const colors: Record<Pillar, string> = {
      [Pillar.TECH]: 'bg-blue-100 text-blue-800',
      [Pillar.AI]: 'bg-purple-100 text-purple-800',
      [Pillar.COMMUNICATION]: 'bg-green-100 text-green-800',
      [Pillar.PORTFOLIO]: 'bg-orange-100 text-orange-800',
    };
    return colors[pillar] || 'bg-gray-100 text-gray-800';
  };

  const getTypeColor = (type: QuestionType) => {
    const colors: Record<QuestionType, string> = {
      [QuestionType.LIKERT]: 'bg-purple-100 text-purple-800',
      [QuestionType.MULTIPLE]: 'bg-blue-100 text-blue-800',
      [QuestionType.TEXT]: 'bg-gray-100 text-gray-800',
    };
    return colors[type] || 'bg-gray-100 text-gray-800';
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-gray-500">Loading questions...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-red-500">Error loading questions: {error.message}</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Questions Management</h1>
          <p className="text-gray-500">Manage assessment questions across all pillars</p>
        </div>
        <Button asChild>
          <Link to="/admin/questions/new">
            <Plus className="mr-2 h-4 w-4" />
            Create Question
          </Link>
        </Button>
      </div>

      {/* Search and Filters */}
      <Card>
        <CardContent className="p-6">
          <div className="space-y-4">
            {/* Search */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
              <Input
                placeholder="Search questions by text or context..."
                value={searchTerm}
                onChange={(e) => handleSearch(e.target.value)}
                className="pl-10"
              />
            </div>

            {/* Filters */}
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <Filter className="h-4 w-4 text-gray-500" />
                <span className="text-sm font-medium text-gray-700">Filters:</span>
              </div>

              {/* Pillar Filter */}
              <select
                value={selectedPillar}
                onChange={(e) => handlePillarFilter(e.target.value as Pillar | '')}
                className="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                aria-label="Filter by pillar"
              >
                <option value="">All Pillars</option>
                <option value="TECH">Tech</option>
                <option value="AI">AI</option>
                <option value="COMMUNICATION">Communication</option>
                <option value="PORTFOLIO">Portfolio</option>
              </select>

              {/* Type Filter */}
              <select
                value={selectedType}
                onChange={(e) => handleTypeFilter(e.target.value as QuestionType | '')}
                className="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                aria-label="Filter by question type"
              >
                <option value="">All Types</option>
                <option value="LIKERT">Likert Scale</option>
                <option value="MULTIPLE">Multiple Choice</option>
                <option value="TEXT">Text</option>
              </select>

              {/* Clear Filters */}
              {(selectedPillar || selectedType) && (
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => {
                    setSelectedPillar('');
                    setSelectedType('');
                    const newParams = new URLSearchParams(searchParams);
                    newParams.delete('pillar');
                    newParams.delete('type');
                    newParams.set('page', '0');
                    setSearchParams(newParams);
                  }}
                >
                  Clear Filters
                </Button>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Questions Table */}
      <Card>
        <CardHeader>
          <CardTitle>Questions ({data?.totalElements || 0})</CardTitle>
          <CardDescription>All assessment questions in the system</CardDescription>
        </CardHeader>
        <CardContent>
          {data?.content && data.content.length > 0 ? (
            <div className="space-y-4">
              {/* Table Header */}
              <div className="grid grid-cols-12 gap-4 pb-2 border-b text-sm font-medium text-gray-500">
                <div className="col-span-5">Question</div>
                <div className="col-span-2">Pillar</div>
                <div className="col-span-2">Type</div>
                <div className="col-span-2">Created</div>
                <div className="col-span-1">Actions</div>
              </div>

              {/* Table Rows */}
              {data.content.map((question) => (
                <div key={question.id} className="grid grid-cols-12 gap-4 py-3 border-b border-gray-100 hover:bg-gray-50">
                  <div className="col-span-5">
                    <div>
                      <p className="font-medium text-gray-900 line-clamp-2">{question.text}</p>
                      {question.context && (
                        <p className="text-sm text-gray-500 line-clamp-1 mt-1">{question.context}</p>
                      )}
                      {question.options && question.options.length > 0 && (
                        <p className="text-xs text-gray-400 mt-1">
                          {question.options.length} option{question.options.length > 1 ? 's' : ''}
                        </p>
                      )}
                    </div>
                  </div>
                  <div className="col-span-2">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getPillarColor(question.pillar)}`}>
                      {question.pillar.replace('_', ' ')}
                    </span>
                  </div>
                  <div className="col-span-2">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getTypeColor(question.type)}`}>
                      {question.type.replace('_', ' ')}
                    </span>
                  </div>
                  <div className="col-span-2">
                    <p className="text-sm text-gray-900">{formatRelativeTime(question.createdAt)}</p>
                  </div>
                  <div className="col-span-1">
                    <div className="flex items-center space-x-2">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => navigate(`/admin/questions/${question.id}/edit`)}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDeleteQuestion(question)}
                        disabled={deleteQuestionMutation.isPending}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <p className="text-gray-500">No questions found</p>
              {(searchTerm || selectedPillar || selectedType) && (
                <p className="text-sm text-gray-400 mt-1">Try adjusting your search terms or filters</p>
              )}
            </div>
          )}

          {/* Pagination */}
          {data && data.totalPages > 1 && (
            <div className="flex items-center justify-between mt-6 pt-4 border-t">
              <div className="text-sm text-gray-500">
                Showing {data.number * data.size + 1} to {Math.min((data.number + 1) * data.size, data.totalElements)} of {data.totalElements} questions
              </div>
              <div className="flex items-center space-x-2">
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => handlePageChange(page - 1)}
                  disabled={data.first}
                >
                  <ArrowLeft className="h-4 w-4 mr-1" />
                  Previous
                </Button>
                <span className="text-sm text-gray-500">
                  Page {data.number + 1} of {data.totalPages}
                </span>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => handlePageChange(page + 1)}
                  disabled={data.last}
                >
                  Next
                  <ArrowRight className="h-4 w-4 ml-1" />
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
