import axios from 'axios';
import type {
  Role,
  Question,
  RoleVersion,
  Assessment,
  Plan,
  CreateRoleRequest,
  UpdateRoleRequest,
  CreateQuestionRequest,
  UpdateQuestionRequest,
  AssignQuestionToRoleRequest,
  CreateAssessmentRequest,
  AnswerDto,
  AssessmentResponse,
  PageResponse
} from '../types';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for auth tokens (future implementation)
api.interceptors.request.use((config) => {
  // TODO: Add auth token when authentication is implemented
  // const token = localStorage.getItem('authToken');
  // if (token) {
  //   config.headers.Authorization = `Bearer ${token}`;
  // }
  return config;
});

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

// Admin Panel API
export const adminApi = {
  // Role management
  roles: {
    create: (data: CreateRoleRequest): Promise<Role> =>
      api.post('/admin/roles', data).then(res => res.data),
    
    update: (data: UpdateRoleRequest): Promise<Role> =>
      api.put('/admin/roles', data).then(res => res.data),
    
    delete: (id: number): Promise<void> =>
      api.delete(`/admin/roles/${id}`).then(() => {}),
    
    getAll: (page = 0, size = 10, search?: string): Promise<PageResponse<Role>> => {
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
      });
      if (search) {
        params.append('search', search);
      }
      return api.get(`/admin/roles?${params.toString()}`).then(res => res.data);
    },
    
    getById: (id: number): Promise<Role> =>
      api.get(`/admin/roles/${id}`).then(res => res.data),
    
    getByCategory: (category: string): Promise<Role[]> =>
      api.get(`/admin/roles/category/${category}`).then(res => res.data),
    
    getQuestions: (roleId: number): Promise<Question[]> =>
      api.get(`/admin/roles/${roleId}/questions`).then(res => res.data),
    
    getVersions: (roleId: number): Promise<RoleVersion[]> =>
      api.get(`/admin/roles/${roleId}/versions`).then(res => res.data),
    
    activateVersion: (roleId: number, versionNumber: number): Promise<void> =>
      api.post(`/admin/roles/${roleId}/versions/${versionNumber}/activate`).then(() => {}),
  },

  // Question management
  questions: {
    create: (data: CreateQuestionRequest): Promise<Question> =>
      api.post('/admin/questions', data).then(res => res.data),
    
    update: (data: UpdateQuestionRequest): Promise<Question> =>
      api.put('/admin/questions', data).then(res => res.data),
    
    delete: (id: number): Promise<void> =>
      api.delete(`/admin/questions/${id}`).then(() => {}),
    
    getAll: (page = 0, size = 10, search?: string, pillar?: string, type?: string): Promise<PageResponse<Question>> => {
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
      });
      if (search) {
        params.append('search', search);
      }
      if (pillar) {
        params.append('pillar', pillar);
      }
      if (type) {
        params.append('type', type);
      }
      return api.get(`/admin/questions?${params.toString()}`).then(res => res.data);
    },
    
    getById: (id: number): Promise<Question> =>
      api.get(`/admin/questions/${id}`).then(res => res.data),
    
    assignToRole: (data: AssignQuestionToRoleRequest): Promise<void> =>
      api.post('/admin/roles/questions/assign', data).then(() => {}),
    
    removeFromRole: (roleId: number, questionId: number): Promise<void> =>
      api.delete(`/admin/roles/${roleId}/questions/${questionId}`).then(() => {}),
  },

  // Health check
  healthCheck: (): Promise<string> =>
    api.get('/admin/health').then(res => res.data),
};

// Public API for assessments
export const assessmentApi = {
  // Assessment management
  create: (data: CreateAssessmentRequest): Promise<AssessmentResponse> =>
    api.post('/assessments', data).then(res => res.data),
  
  getById: (id: number): Promise<AssessmentResponse> =>
    api.get(`/assessments/${id}`).then(res => res.data),
  
  submitAnswers: (assessmentId: number, answers: AnswerDto[]): Promise<Assessment> =>
    api.post(`/assessments/${assessmentId}/answers`, answers).then(res => res.data),
  
  complete: (assessmentId: number): Promise<Assessment> =>
    api.post(`/assessments/${assessmentId}/complete`).then(res => res.data),
  
  getResults: (assessmentId: number): Promise<Assessment> =>
    api.get(`/assessments/${assessmentId}/results`).then(res => res.data),
};

// Plan API
export const planApi = {
  generate: (assessmentId: number): Promise<Plan> =>
    api.post(`/plans/generate/${assessmentId}`).then(res => res.data),
  
  getByAssessment: (assessmentId: number): Promise<Plan> =>
    api.get(`/plans/assessment/${assessmentId}`).then(res => res.data),
};

// Catalog API (public endpoints)
export const catalogApi = {
  getRoles: (): Promise<Role[]> =>
    api.get('/catalog/roles').then(res => res.data),
  
  getRole: (roleId: number): Promise<Role> =>
    api.get(`/catalog/roles/${roleId}`).then(res => res.data),
  
  getRoleQuestions: (roleId: number): Promise<Question[]> =>
    api.get(`/catalog/roles/${roleId}/questions`).then(res => res.data),
};

export default api;
