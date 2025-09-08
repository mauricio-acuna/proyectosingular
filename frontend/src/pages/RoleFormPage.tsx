import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, Save } from 'lucide-react';
import { Button, Card, CardContent, CardDescription, CardHeader, CardTitle, Input, Label } from '../components/ui';
import { useRole, useCreateRole, useUpdateRole } from '../hooks/useRoles';
import type { CreateRoleRequest, UpdateRoleRequest } from '../types';

interface RoleFormData extends CreateRoleRequest {
  active?: boolean;
}

export default function RoleFormPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = !!id;
  const roleId = id ? parseInt(id) : 0;

  const { data: role, isLoading: isLoadingRole } = useRole(roleId);
  const createRoleMutation = useCreateRole();
  const updateRoleMutation = useUpdateRole();

  const [formData, setFormData] = useState<RoleFormData>({
    name: '',
    description: '',
    category: '',
    active: true,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (isEdit && role) {
      setFormData({
        name: role.name,
        description: role.description || '',
        category: role.category,
        active: role.active,
      });
    }
  }, [isEdit, role]);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Role name is required';
    }

    if (!formData.category.trim()) {
      newErrors.category = 'Category is required';
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
        const updateData: UpdateRoleRequest = {
          id: roleId,
          name: formData.name,
          description: formData.description,
          category: formData.category,
        };
        await updateRoleMutation.mutateAsync(updateData);
      } else {
        const createData: CreateRoleRequest = {
          name: formData.name,
          description: formData.description,
          category: formData.category,
        };
        await createRoleMutation.mutateAsync(createData);
      }
      
      navigate('/admin/roles');
    } catch (error) {
      console.error('Failed to save role:', error);
    }
  };

  const handleChange = (field: keyof RoleFormData, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const isLoading = isLoadingRole || createRoleMutation.isPending || updateRoleMutation.isPending;

  if (isEdit && isLoadingRole) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-gray-500">Loading role...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center space-x-4">
        <Button variant="ghost" onClick={() => navigate('/admin/roles')}>
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Roles
        </Button>
        <div>
          <h1 className="text-3xl font-bold text-gray-900">
            {isEdit ? 'Edit Role' : 'Create New Role'}
          </h1>
          <p className="text-gray-500">
            {isEdit ? 'Update role information and settings' : 'Create a new organizational role'}
          </p>
        </div>
      </div>

      {/* Form */}
      <Card>
        <CardHeader>
          <CardTitle>Role Information</CardTitle>
          <CardDescription>
            Define the basic information for this role
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Name */}
            <div className="space-y-2">
              <Label htmlFor="name">Role Name *</Label>
              <Input
                id="name"
                value={formData.name}
                onChange={(e) => handleChange('name', e.target.value)}
                placeholder="e.g., Software Engineer, Product Manager"
                error={errors.name}
              />
              {errors.name && (
                <p className="text-sm text-red-600">{errors.name}</p>
              )}
            </div>

            {/* Description */}
            <div className="space-y-2">
              <Label htmlFor="description">Description</Label>
              <textarea
                id="description"
                value={formData.description}
                onChange={(e) => handleChange('description', e.target.value)}
                placeholder="Brief description of this role and its responsibilities"
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              />
            </div>

            {/* Category */}
            <div className="space-y-2">
              <Label htmlFor="category">Category *</Label>
              <Input
                id="category"
                value={formData.category}
                onChange={(e) => handleChange('category', e.target.value)}
                placeholder="e.g., Engineering, Management, Design"
                error={errors.category}
              />
              {errors.category && (
                <p className="text-sm text-red-600">{errors.category}</p>
              )}
            </div>

            {/* Active Status - Only show for existing roles */}
            {isEdit && (
              <div className="flex items-center space-x-2">
                <input
                  type="checkbox"
                  id="active"
                  checked={formData.active || false}
                  onChange={(e) => handleChange('active', e.target.checked)}
                  className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                  aria-label="Active Role"
                />
                <Label htmlFor="active">Active Role</Label>
                <p className="text-sm text-gray-500">
                  Active roles are available for assessments
                </p>
              </div>
            )}

            {/* Actions */}
            <div className="flex items-center justify-end space-x-4 pt-6 border-t">
              <Button
                type="button"
                variant="secondary"
                onClick={() => navigate('/admin/roles')}
                disabled={isLoading}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                disabled={isLoading}
              >
                <Save className="mr-2 h-4 w-4" />
                {isLoading ? 'Saving...' : isEdit ? 'Update Role' : 'Create Role'}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
