import { Plus, Users, FileQuestion, Settings } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle, Button } from '../components/ui';
import { useRoles } from '../hooks/useRoles';
import { useQuestions } from '../hooks/useQuestions';
import { formatRelativeTime } from '../utils';

export default function Dashboard() {
  const { data: rolesPage } = useRoles(0, 5);
  const { data: questionsPage } = useQuestions(0, 5);

  const stats = [
    {
      name: 'Total Roles',
      value: rolesPage?.totalElements || 0,
      icon: Users,
      color: 'bg-blue-500',
    },
    {
      name: 'Total Questions',
      value: questionsPage?.totalElements || 0,
      icon: FileQuestion,
      color: 'bg-green-500',
    },
    {
      name: 'Active Assessments',
      value: 12, // TODO: Get from API when endpoint is ready
      icon: Settings,
      color: 'bg-purple-500',
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
          <p className="text-gray-500">Manage roles, questions, and platform settings</p>
        </div>
        <div className="flex space-x-3">
          <Button asChild>
            <Link to="/admin/roles/new">
              <Plus className="mr-2 h-4 w-4" />
              New Role
            </Link>
          </Button>
          <Button variant="secondary" asChild>
            <Link to="/admin/questions/new">
              <Plus className="mr-2 h-4 w-4" />
              New Question
            </Link>
          </Button>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {stats.map((stat) => (
          <Card key={stat.name}>
            <CardContent className="p-6">
              <div className="flex items-center">
                <div className={`${stat.color} p-2 rounded-lg`}>
                  <stat.icon className="h-6 w-6 text-white" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-500">{stat.name}</p>
                  <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Recent Activity Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Roles */}
        <Card>
          <CardHeader>
            <CardTitle>Recent Roles</CardTitle>
            <CardDescription>Latest roles created in the system</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {rolesPage?.content.slice(0, 5).map((role) => (
                <div key={role.id} className="flex items-center justify-between">
                  <div>
                    <p className="font-medium text-gray-900">{role.name}</p>
                    <p className="text-sm text-gray-500">{role.category}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm text-gray-500">
                      {formatRelativeTime(role.createdAt)}
                    </p>
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      role.active ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                    }`}>
                      {role.active ? 'Active' : 'Inactive'}
                    </span>
                  </div>
                </div>
              ))}
              {(!rolesPage?.content || rolesPage.content.length === 0) && (
                <p className="text-gray-500 text-center py-4">No roles found</p>
              )}
            </div>
            <div className="mt-4 pt-4 border-t">
              <Button variant="ghost" asChild className="w-full">
                <Link to="/admin/roles">View All Roles</Link>
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Recent Questions */}
        <Card>
          <CardHeader>
            <CardTitle>Recent Questions</CardTitle>
            <CardDescription>Latest questions created in the system</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {questionsPage?.content.slice(0, 5).map((question) => (
                <div key={question.id} className="flex items-center justify-between">
                  <div className="flex-1 min-w-0">
                    <p className="font-medium text-gray-900 truncate">{question.text}</p>
                    <div className="flex items-center space-x-2 mt-1">
                      <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800`}>
                        {question.pillar}
                      </span>
                      <span className="text-xs text-gray-500">{question.type}</span>
                    </div>
                  </div>
                  <div className="text-right ml-4">
                    <p className="text-sm text-gray-500">
                      {formatRelativeTime(question.createdAt)}
                    </p>
                  </div>
                </div>
              ))}
              {(!questionsPage?.content || questionsPage.content.length === 0) && (
                <p className="text-gray-500 text-center py-4">No questions found</p>
              )}
            </div>
            <div className="mt-4 pt-4 border-t">
              <Button variant="ghost" asChild className="w-full">
                <Link to="/admin/questions">View All Questions</Link>
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
