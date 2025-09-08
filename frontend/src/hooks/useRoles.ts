import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '../services/api';
import type { Role, CreateRoleRequest, UpdateRoleRequest } from '../types';

// Query keys for consistent cache management
export const roleKeys = {
  all: ['roles'] as const,
  lists: () => [...roleKeys.all, 'list'] as const,
  list: (page: number, size: number) => [...roleKeys.lists(), { page, size }] as const,
  details: () => [...roleKeys.all, 'detail'] as const,
  detail: (id: number) => [...roleKeys.details(), id] as const,
  questions: (id: number) => [...roleKeys.detail(id), 'questions'] as const,
  versions: (id: number) => [...roleKeys.detail(id), 'versions'] as const,
  category: (category: string) => [...roleKeys.all, 'category', category] as const,
};

// Get all roles with pagination and optional search
export const useRoles = (page = 0, size = 10, search?: string) => {
  return useQuery({
    queryKey: [...roleKeys.list(page, size), search].filter(Boolean),
    queryFn: () => adminApi.roles.getAll(page, size, search),
  });
};

// Get single role by ID
export const useRole = (id: number) => {
  return useQuery({
    queryKey: roleKeys.detail(id),
    queryFn: () => adminApi.roles.getById(id),
    enabled: !!id,
  });
};

// Get roles by category
export const useRolesByCategory = (category: string) => {
  return useQuery({
    queryKey: roleKeys.category(category),
    queryFn: () => adminApi.roles.getByCategory(category),
    enabled: !!category,
  });
};

// Get questions for a role
export const useRoleQuestions = (roleId: number) => {
  return useQuery({
    queryKey: roleKeys.questions(roleId),
    queryFn: () => adminApi.roles.getQuestions(roleId),
    enabled: !!roleId,
  });
};

// Get role versions
export const useRoleVersions = (roleId: number) => {
  return useQuery({
    queryKey: roleKeys.versions(roleId),
    queryFn: () => adminApi.roles.getVersions(roleId),
    enabled: !!roleId,
  });
};

// Create role mutation
export const useCreateRole = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreateRoleRequest) => adminApi.roles.create(data),
    onSuccess: () => {
      // Invalidate and refetch roles list
      queryClient.invalidateQueries({ queryKey: roleKeys.lists() });
    },
  });
};

// Update role mutation
export const useUpdateRole = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: UpdateRoleRequest) => adminApi.roles.update(data),
    onSuccess: (updatedRole) => {
      // Update specific role in cache
      queryClient.setQueryData(roleKeys.detail(updatedRole.id), updatedRole);
      // Invalidate roles list to reflect changes
      queryClient.invalidateQueries({ queryKey: roleKeys.lists() });
    },
  });
};

// Delete role mutation
export const useDeleteRole = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: number) => adminApi.roles.delete(id),
    onSuccess: (_, deletedId) => {
      // Remove from cache
      queryClient.removeQueries({ queryKey: roleKeys.detail(deletedId) });
      // Invalidate lists to reflect deletion
      queryClient.invalidateQueries({ queryKey: roleKeys.lists() });
    },
  });
};

// Activate role version mutation
export const useActivateRoleVersion = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ roleId, versionNumber }: { roleId: number; versionNumber: number }) =>
      adminApi.roles.activateVersion(roleId, versionNumber),
    onSuccess: (_, { roleId }) => {
      // Invalidate role versions to reflect the change
      queryClient.invalidateQueries({ queryKey: roleKeys.versions(roleId) });
      // Also invalidate role details as the active version changed
      queryClient.invalidateQueries({ queryKey: roleKeys.detail(roleId) });
    },
  });
};
