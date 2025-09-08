export enum Pillar {
  TECH = 'TECH',
  AI = 'AI',
  COMMUNICATION = 'COMMUNICATION',
  PORTFOLIO = 'PORTFOLIO'
}

export enum QuestionType {
  LIKERT = 'LIKERT',
  MULTIPLE = 'MULTIPLE',
  TEXT = 'TEXT'
}

export interface Role {
  id: number;
  name: string;
  description: string;
  category: string;
  active: boolean;
  createdAt: string;
  updatedAt?: string;
}

export interface Question {
  id: number;
  text: string;
  type: QuestionType;
  pillar: Pillar;
  options?: string[];
  context?: string;
  active: boolean;
  createdAt: string;
  updatedAt?: string;
}

export interface RoleVersion {
  id: number;
  role: Role;
  versionNumber: number;
  active: boolean;
  createdAt: string;
  questions: RoleQuestion[];
}

export interface RoleQuestion {
  id: number;
  roleVersion: RoleVersion;
  question: Question;
}

export interface Assessment {
  id: number;
  userId: string;
  roleId: number;
  status: AssessmentStatus;
  createdAt: string;
  completedAt?: string;
  answers: Answer[];
  scores?: AssessmentScores;
}

export enum AssessmentStatus {
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED'
}

export interface Answer {
  id: number;
  assessment: Assessment;
  question: Question;
  value: string;
  createdAt: string;
}

export interface AssessmentScores {
  tech: number;
  ai: number;
  communication: number;
  portfolio: number;
  overall: number;
}

export interface Plan {
  id: number;
  assessment: Assessment;
  content: string;
  createdAt: string;
}

// API Request/Response types
export interface CreateRoleRequest {
  name: string;
  description: string;
  category: string;
}

export interface UpdateRoleRequest {
  id: number;
  name: string;
  description: string;
  category: string;
}

export interface CreateQuestionRequest {
  text: string;
  type: QuestionType;
  pillar: Pillar;
  options?: string[];
  context?: string;
}

export interface UpdateQuestionRequest {
  id: number;
  text: string;
  type: QuestionType;
  pillar: Pillar;
  options?: string[];
  context?: string;
}

export interface AssignQuestionToRoleRequest {
  roleId: number;
  questionId: number;
}

export interface CreateAssessmentRequest {
  userId: string;
  roleId: number;
}

export interface AnswerDto {
  questionId: number;
  value: string;
}

export interface AssessmentResponse {
  assessment: Assessment;
  questions: Question[];
}

// API Response wrappers
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
}
