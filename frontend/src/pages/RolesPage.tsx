import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { Plus, Search, Edit, Trash2, ArrowLeft, ArrowRight } from 'lucide-react';
import { Button, Card, CardContent, CardDescription, CardHeader, CardTitle, Input } from '../components/ui';
import { useRoles, useDeleteRole } from '../hooks/useRoles';
import type { Role } from '../types';
import { formatRelativeTime } from '../utils';

export default function RolesPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const [searchTerm, setSearchTerm] = useState(searchParams.get('search') || '');
  
  const page = parseInt(searchParams.get('page') || '0');
  const size = parseInt(searchParams.get('size') || '10');
  
  const { data, isLoading, error } = useRoles(page, size, searchTerm);
  const deleteRoleMutation = useDeleteRole();

  const handleSearch = (value: string) => {
    setSearchTerm(value);
    const newParams = new URLSearchParams(searchParams);
    if (value) {
      newParams.set('search', value);
    } else {
      newParams.delete('search');
    }
    newParams.set('page', '0'); // Reset to first page on search
    setSearchParams(newParams);
  };

  const handlePageChange = (newPage: number) => {
    const newParams = new URLSearchParams(searchParams);
    newParams.set('page', newPage.toString());
    setSearchParams(newParams);
  };

  const handleDeleteRole = async (role: Role) => {
    if (window.confirm(`Are you sure you want to delete the role "${role.name}"?`)) {
      try {
        await deleteRoleMutation.mutateAsync(role.id);
      } catch (error) {
        console.error('Failed to delete role:', error);
      }
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-gray-500">Loading roles...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-red-500">Error loading roles: {error.message}</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Roles Management</h1>
          <p className="text-gray-500">Manage organizational roles and their assessments</p>
        </div>
        <Button asChild>
          <Link to="/admin/roles/new">
            <Plus className="mr-2 h-4 w-4" />
            Create Role
          </Link>
        </Button>
      </div>

      {/* Search and Filters */}
      <Card>
        <CardContent className="p-6">
          <div className="flex items-center space-x-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
              <Input
                placeholder="Search roles by name or category..."
                value={searchTerm}
                onChange={(e) => handleSearch(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Roles Table */}
      <Card>
        <CardHeader>
          <CardTitle>Roles ({data?.totalElements || 0})</CardTitle>
          <CardDescription>All organizational roles in the system</CardDescription>
        </CardHeader>
        <CardContent>
          {data?.content && data.content.length > 0 ? (
            <div className="space-y-4">
              {/* Table Header */}
              <div className="grid grid-cols-11 gap-4 pb-2 border-b text-sm font-medium text-gray-500">
                <div className="col-span-3">Name</div>
                <div className="col-span-2">Category</div>
                <div className="col-span-2">Status</div>
                <div className="col-span-3">Created</div>
                <div className="col-span-1">Actions</div>
              </div>

              {/* Table Rows */}
              {data.content.map((role) => (
                <div key={role.id} className="grid grid-cols-11 gap-4 py-3 border-b border-gray-100 hover:bg-gray-50">
                  <div className="col-span-3">
                    <div>
                      <p className="font-medium text-gray-900">{role.name}</p>
                      {role.description && (
                        <p className="text-sm text-gray-500 truncate">{role.description}</p>
                      )}
                    </div>
                  </div>
                  <div className="col-span-2">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      {role.category}
                    </span>
                  </div>
                  <div className="col-span-2">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      role.active ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                    }`}>
                      {role.active ? 'Active' : 'Inactive'}
                    </span>
                  </div>
                  <div className="col-span-3">
                    <p className="text-sm text-gray-900">{formatRelativeTime(role.createdAt)}</p>
                  </div>
                  <div className="col-span-1">
                    <div className="flex items-center space-x-2">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => navigate(`/admin/roles/${role.id}/edit`)}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDeleteRole(role)}
                        disabled={deleteRoleMutation.isPending}
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
              <p className="text-gray-500">No roles found</p>
              {searchTerm && (
                <p className="text-sm text-gray-400 mt-1">Try adjusting your search terms</p>
              )}
            </div>
          )}

          {/* Pagination */}
          {data && data.totalPages > 1 && (
            <div className="flex items-center justify-between mt-6 pt-4 border-t">
              <div className="text-sm text-gray-500">
                Showing {data.number * data.size + 1} to {Math.min((data.number + 1) * data.size, data.totalElements)} of {data.totalElements} roles
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
