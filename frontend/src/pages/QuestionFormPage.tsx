import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, Save, Plus, X } from 'lucide-react';
import { Button, Card, CardContent, CardDescription, CardHeader, CardTitle, Input, Label } from '../components/ui';
import { useQuestion, useCreateQuestion, useUpdateQuestion } from '../hooks/useQuestions';
import type { CreateQuestionRequest, UpdateQuestionRequest } from '../types';
import { Pillar, QuestionType } from '../types';

interface QuestionFormData extends CreateQuestionRequest {
  id?: number;
}

export default function QuestionFormPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = !!id;
  const questionId = id ? parseInt(id) : 0;

  const { data: question, isLoading: isLoadingQuestion } = useQuestion(questionId);
  const createQuestionMutation = useCreateQuestion();
  const updateQuestionMutation = useUpdateQuestion();

  const [formData, setFormData] = useState<QuestionFormData>({
    text: '',
    type: QuestionType.TEXT,
    pillar: Pillar.TECH,
    options: [],
    context: '',
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [newOption, setNewOption] = useState('');

  useEffect(() => {
    if (isEdit && question) {
      setFormData({
        id: question.id,
        text: question.text,
        type: question.type,
        pillar: question.pillar,
        options: question.options || [],
        context: question.context || '',
      });
    }
  }, [isEdit, question]);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.text.trim()) {
      newErrors.text = 'Question text is required';
    }

    if ((formData.type === QuestionType.MULTIPLE || formData.type === QuestionType.LIKERT) && 
        (!formData.options || formData.options.length < 2)) {
      newErrors.options = 'At least 2 options are required for this question type';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      if (isEdit) {
        const updateData: UpdateQuestionRequest = {
          id: questionId,
          text: formData.text,
          type: formData.type,
          pillar: formData.pillar,
          options: formData.options,
          context: formData.context,
        };
        await updateQuestionMutation.mutateAsync(updateData);
      } else {
        const createData: CreateQuestionRequest = {
          text: formData.text,
          type: formData.type,
          pillar: formData.pillar,
          options: formData.options,
          context: formData.context,
        };
        await createQuestionMutation.mutateAsync(createData);
      }
      
      navigate('/admin/questions');
    } catch (error) {
      console.error('Failed to save question:', error);
    }
  };

  const handleChange = (field: keyof QuestionFormData, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const handleTypeChange = (type: QuestionType) => {
    setFormData(prev => ({
      ...prev,
      type,
      // Clear options if changing to TEXT type
      options: type === QuestionType.TEXT ? [] : prev.options,
    }));
    if (errors.options) {
      setErrors(prev => ({ ...prev, options: '' }));
    }
  };

  const addOption = () => {
    if (newOption.trim()) {
      setFormData(prev => ({
        ...prev,
        options: [...(prev.options || []), newOption.trim()]
      }));
      setNewOption('');
      if (errors.options) {
        setErrors(prev => ({ ...prev, options: '' }));
      }
    }
  };

  const removeOption = (index: number) => {
    setFormData(prev => ({
      ...prev,
      options: prev.options?.filter((_, i) => i !== index) || []
    }));
  };

  const needsOptions = formData.type === QuestionType.MULTIPLE || formData.type === QuestionType.LIKERT;
  const isLoading = isLoadingQuestion || createQuestionMutation.isPending || updateQuestionMutation.isPending;

  if (isEdit && isLoadingQuestion) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-gray-500">Loading question...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center space-x-4">
        <Button variant="ghost" onClick={() => navigate('/admin/questions')}>
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Questions
        </Button>
        <div>
          <h1 className="text-3xl font-bold text-gray-900">
            {isEdit ? 'Edit Question' : 'Create New Question'}
          </h1>
          <p className="text-gray-500">
            {isEdit ? 'Update question information and settings' : 'Create a new assessment question'}
          </p>
        </div>
      </div>

      {/* Form */}
      <Card>
        <CardHeader>
          <CardTitle>Question Information</CardTitle>
          <CardDescription>
            Define the question content, type, and assessment pillar
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Question Text */}
            <div className="space-y-2">
              <Label htmlFor="text">Question Text *</Label>
              <textarea
                id="text"
                value={formData.text}
                onChange={(e) => handleChange('text', e.target.value)}
                placeholder="Enter the question text..."
                rows={3}
                className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 ${
                  errors.text ? 'border-red-300' : 'border-gray-300'
                }`}
              />
              {errors.text && (
                <p className="text-sm text-red-600">{errors.text}</p>
              )}
            </div>

            {/* Pillar */}
            <div className="space-y-2">
              <Label htmlFor="pillar">Assessment Pillar *</Label>
              <select
                id="pillar"
                value={formData.pillar}
                onChange={(e) => handleChange('pillar', e.target.value as Pillar)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                aria-label="Select assessment pillar"
              >
                <option value={Pillar.TECH}>Tech</option>
                <option value={Pillar.AI}>AI</option>
                <option value={Pillar.COMMUNICATION}>Communication</option>
                <option value={Pillar.PORTFOLIO}>Portfolio</option>
              </select>
            </div>

            {/* Question Type */}
            <div className="space-y-2">
              <Label htmlFor="type">Question Type *</Label>
              <select
                id="type"
                value={formData.type}
                onChange={(e) => handleTypeChange(e.target.value as QuestionType)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                aria-label="Select question type"
              >
                <option value={QuestionType.TEXT}>Text Response</option>
                <option value={QuestionType.MULTIPLE}>Multiple Choice</option>
                <option value={QuestionType.LIKERT}>Likert Scale</option>
              </select>
              <p className="text-xs text-gray-500">
                {formData.type === QuestionType.TEXT && 'Users will provide a free-text response'}
                {formData.type === QuestionType.MULTIPLE && 'Users will select from predefined options'}
                {formData.type === QuestionType.LIKERT && 'Users will rate on a scale (e.g., 1-5, Strongly Disagree to Strongly Agree)'}
              </p>
            </div>

            {/* Options (for MULTIPLE and LIKERT types) */}
            {needsOptions && (
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label>Answer Options *</Label>
                  <p className="text-sm text-gray-500">
                    {formData.type === QuestionType.MULTIPLE 
                      ? 'Add the available choices for this question'
                      : 'Define the scale options (e.g., "Strongly Disagree", "Disagree", "Neutral", "Agree", "Strongly Agree")'
                    }
                  </p>
                </div>

                {/* Current Options */}
                {formData.options && formData.options.length > 0 && (
                  <div className="space-y-2">
                    {formData.options.map((option, index) => (
                      <div key={index} className="flex items-center space-x-2 p-2 bg-gray-50 rounded">
                        <span className="flex-1 text-sm">{option}</span>
                        <Button
                          type="button"
                          variant="ghost"
                          size="sm"
                          onClick={() => removeOption(index)}
                        >
                          <X className="h-4 w-4" />
                        </Button>
                      </div>
                    ))}
                  </div>
                )}

                {/* Add New Option */}
                <div className="flex space-x-2">
                  <Input
                    value={newOption}
                    onChange={(e) => setNewOption(e.target.value)}
                    placeholder="Enter option text..."
                    onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addOption())}
                  />
                  <Button
                    type="button"
                    variant="secondary"
                    onClick={addOption}
                    disabled={!newOption.trim()}
                  >
                    <Plus className="h-4 w-4 mr-1" />
                    Add
                  </Button>
                </div>

                {errors.options && (
                  <p className="text-sm text-red-600">{errors.options}</p>
                )}
              </div>
            )}

            {/* Context */}
            <div className="space-y-2">
              <Label htmlFor="context">Context (Optional)</Label>
              <textarea
                id="context"
                value={formData.context}
                onChange={(e) => handleChange('context', e.target.value)}
                placeholder="Additional context or instructions for this question..."
                rows={2}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              />
              <p className="text-xs text-gray-500">
                Provide additional context or instructions to help users understand the question
              </p>
            </div>

            {/* Actions */}
            <div className="flex items-center justify-end space-x-4 pt-6 border-t">
              <Button
                type="button"
                variant="secondary"
                onClick={() => navigate('/admin/questions')}
                disabled={isLoading}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                disabled={isLoading}
              >
                <Save className="mr-2 h-4 w-4" />
                {isLoading ? 'Saving...' : isEdit ? 'Update Question' : 'Create Question'}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
