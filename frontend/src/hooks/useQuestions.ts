import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '../services/api';
import type { Question, CreateQuestionRequest, UpdateQuestionRequest, AssignQuestionToRoleRequest } from '../types';

// Query keys for questions
export const questionKeys = {
  all: ['questions'] as const,
  lists: () => [...questionKeys.all, 'list'] as const,
  list: (page: number, size: number) => [...questionKeys.lists(), { page, size }] as const,
  details: () => [...questionKeys.all, 'detail'] as const,
  detail: (id: number) => [...questionKeys.details(), id] as const,
};

// Get all questions with pagination
// Get all questions with pagination and optional filters
export const useQuestions = (page = 0, size = 10, search?: string, pillar?: string, type?: string) => {
  return useQuery({
    queryKey: [...questionKeys.list(page, size), search, pillar, type].filter(Boolean),
    queryFn: () => adminApi.questions.getAll(page, size, search, pillar, type),
  });
};

// Get single question by ID
export const useQuestion = (id: number) => {
  return useQuery({
    queryKey: questionKeys.detail(id),
    queryFn: () => adminApi.questions.getById(id),
    enabled: !!id,
  });
};

// Create question mutation
export const useCreateQuestion = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreateQuestionRequest) => adminApi.questions.create(data),
    onSuccess: () => {
      // Invalidate and refetch questions list
      queryClient.invalidateQueries({ queryKey: questionKeys.lists() });
    },
  });
};

// Update question mutation
export const useUpdateQuestion = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: UpdateQuestionRequest) => adminApi.questions.update(data),
    onSuccess: (updatedQuestion) => {
      // Update specific question in cache
      queryClient.setQueryData(questionKeys.detail(updatedQuestion.id), updatedQuestion);
      // Invalidate questions list to reflect changes
      queryClient.invalidateQueries({ queryKey: questionKeys.lists() });
    },
  });
};

// Delete question mutation
export const useDeleteQuestion = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: number) => adminApi.questions.delete(id),
    onSuccess: (_, deletedId) => {
      // Remove from cache
      queryClient.removeQueries({ queryKey: questionKeys.detail(deletedId) });
      // Invalidate lists to reflect deletion
      queryClient.invalidateQueries({ queryKey: questionKeys.lists() });
    },
  });
};

// Assign question to role mutation
export const useAssignQuestionToRole = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: AssignQuestionToRoleRequest) => adminApi.questions.assignToRole(data),
    onSuccess: (_, { roleId }) => {
      // Invalidate role questions to reflect the new assignment
      queryClient.invalidateQueries({ queryKey: ['roles', 'detail', roleId, 'questions'] });
      // Also invalidate role versions as a new version was created
      queryClient.invalidateQueries({ queryKey: ['roles', 'detail', roleId, 'versions'] });
    },
  });
};

// Remove question from role mutation
export const useRemoveQuestionFromRole = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ roleId, questionId }: { roleId: number; questionId: number }) =>
      adminApi.questions.removeFromRole(roleId, questionId),
    onSuccess: (_, { roleId }) => {
      // Invalidate role questions to reflect the removal
      queryClient.invalidateQueries({ queryKey: ['roles', 'detail', roleId, 'questions'] });
      // Also invalidate role versions as a new version was created
      queryClient.invalidateQueries({ queryKey: ['roles', 'detail', roleId, 'versions'] });
    },
  });
};
