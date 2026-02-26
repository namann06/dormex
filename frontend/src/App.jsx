import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';
import Layout from './components/layout/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Students from './pages/Students';
import Rooms from './pages/Rooms';
import Blocks from './pages/Blocks';
import Complaints from './pages/Complaints';
import MessMenu from './pages/MessMenu';
import Analytics from './pages/Analytics';
import Profile from './pages/Profile';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Toaster position="top-right" toastOptions={{ duration: 3000 }} />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/oauth2/callback" element={<Login />} />
          <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
            <Route index element={<Dashboard />} />
            <Route path="students" element={<ProtectedRoute adminOnly><Students /></ProtectedRoute>} />
            <Route path="rooms" element={<ProtectedRoute requiresProfile><Rooms /></ProtectedRoute>} />
            <Route path="blocks" element={<ProtectedRoute adminOnly><Blocks /></ProtectedRoute>} />
            <Route path="complaints" element={<Complaints />} />
            <Route path="mess-menu" element={<ProtectedRoute requiresProfile><MessMenu /></ProtectedRoute>} />
            <Route path="analytics" element={<ProtectedRoute adminOnly><Analytics /></ProtectedRoute>} />
            <Route path="profile" element={<Profile />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
