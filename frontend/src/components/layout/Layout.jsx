import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { X, AlertTriangle } from 'lucide-react';
import Sidebar from './Sidebar';
import Header from './Header';

export default function Layout() {
  const { isStudent, hasStudentProfile } = useAuth();
  const [showProfileAlert, setShowProfileAlert] = useState(
    isStudent && !hasStudentProfile
  );

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-1 ml-56">
        <Header />
        <main className="p-6">
          {showProfileAlert && (
            <div className="mb-4 flex items-center justify-between gap-3 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
              <div className="flex items-center gap-2">
                <AlertTriangle size={18} className="shrink-0 text-amber-500" />
                <span>
                  <strong>Student profile not found.</strong> Please contact your hostel admin to get registered.
                </span>
              </div>
              <button
                onClick={() => setShowProfileAlert(false)}
                className="shrink-0 rounded p-1 hover:bg-amber-100 transition-colors cursor-pointer"
              >
                <X size={16} />
              </button>
            </div>
          )}
          <Outlet />
        </main>
      </div>
    </div>
  );
}
