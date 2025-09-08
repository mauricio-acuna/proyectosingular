import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import RolesPage from './pages/RolesPage';
import RoleFormPage from './pages/RoleFormPage';
import QuestionsPage from './pages/QuestionsPage';
import QuestionFormPage from './pages/QuestionFormPage';
import { 
  AssessmentStart, 
  AssessmentWizard, 
  AssessmentComplete 
} from './pages/assessment';

function App() {
  return (
    <Router>
      <Routes>
        {/* Redirect root to admin dashboard */}
        <Route path="/" element={<Navigate to="/admin" replace />} />
        
        {/* Admin routes with layout */}
        <Route path="/admin" element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="roles" element={<RolesPage />} />
          <Route path="roles/new" element={<RoleFormPage />} />
          <Route path="roles/:id/edit" element={<RoleFormPage />} />
          <Route path="questions" element={<QuestionsPage />} />
          <Route path="questions/new" element={<QuestionFormPage />} />
          <Route path="questions/:id/edit" element={<QuestionFormPage />} />
          
          {/* TODO: Add more admin routes */}
          {/* <Route path="assessments" element={<AssessmentsPage />} /> */}
          {/* <Route path="settings" element={<SettingsPage />} /> */}
        </Route>

        {/* Public assessment routes */}
        <Route path="/assessment" element={<AssessmentStart />} />
        <Route path="/assessment/:assessmentId/wizard" element={<AssessmentWizard />} />
        <Route path="/assessment/:assessmentId/complete" element={<AssessmentComplete />} />

        {/* 404 fallback */}
        <Route path="*" element={<Navigate to="/admin" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
