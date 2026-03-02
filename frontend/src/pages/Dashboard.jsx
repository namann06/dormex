import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { getDashboardStats, getRecentActivity, getBlockOccupancy, getComplaintSummary } from '../api/dashboard';
import { getMyComplaints } from '../api/complaints';
import { getTodayMenu } from '../api/menu';
import StatusBadge from '../components/common/StatusBadge';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageHeader from '../components/common/PageHeader';
import StatCard from '../components/common/StatCard';
import { Users, BedDouble, MessageSquareWarning, CheckCircle, AlertCircle } from 'lucide-react';

function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getDashboardStats()
      .then((s) => setStats(s.data.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;

  const occupiedRooms = (stats?.totalRooms || 0) - (stats?.availableRooms || 0);

  return (
    <div>
      <PageHeader title="Dashboard" subtitle="Overview of hostel operations and key metrics." />

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 mb-4">
        <StatCard label="Total Students" value={stats?.totalStudents || 0} icon={Users} color="blue" />
        <StatCard label="Occupied Rooms" value={occupiedRooms} icon={BedDouble} color="amber" />
        <StatCard label="Vacant Rooms" value={stats?.availableRooms || 0} icon={BedDouble} color="emerald" />
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <StatCard label="Complaints" value={stats?.totalComplaints || 0} icon={MessageSquareWarning} color="violet" />
        <StatCard label="Open Complaints" value={stats?.openComplaints || 0} icon={AlertCircle} color="red" />
        <StatCard label="Closed Complaints" value={stats?.resolvedComplaints || 0} icon={CheckCircle} color="emerald" />
      </div>
    </div>
  );
}

function StudentDashboard() {
  const { user } = useAuth();
  const [complaints, setComplaints] = useState([]);
  const [todayMenu, setTodayMenu] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getMyComplaints(), getTodayMenu()])
      .then(([c, m]) => {
        setComplaints(c.data.data || []);
        setTodayMenu(m.data.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;

  const openCount = complaints.filter((c) => c.status === 'OPEN' || c.status === 'IN_PROGRESS').length;
  const resolvedCount = complaints.filter((c) => c.status === 'RESOLVED' || c.status === 'CLOSED').length;

  return (
    <div>
      <PageHeader title="Dashboard" subtitle={`Welcome back, ${user?.name}`} />

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 mb-6">
        <StatCard label="Total Complaints" value={complaints.length} icon={MessageSquareWarning} color="violet" />
        <StatCard label="Open Complaints" value={openCount} icon={AlertCircle} color="red" />
        <StatCard label="Resolved" value={resolvedCount} icon={CheckCircle} color="emerald" />
      </div>

      {/* Today's Menu */}
      {todayMenu && (
        <div className="bg-white rounded-xl border border-cream-200 mb-6">
          <div className="px-6 py-4 border-b border-cream-200">
            <h3 className="font-semibold text-accent-500">Today&apos;s Menu ({todayMenu.day})</h3>
          </div>
          <div className="p-6 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {todayMenu.meals?.map((meal) => (
              <div key={meal.id} className="p-4 bg-cream-100 rounded-xl">
                <p className="text-sm font-semibold text-accent-500 mb-2 capitalize">{meal.mealType?.toLowerCase()}</p>
                <p className="text-sm text-primary-500">{meal.items}</p>
                {meal.specialNote && <p className="text-xs text-amber-600 mt-2">{meal.specialNote}</p>}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Recent Complaints */}
      <div className="bg-white rounded-xl border border-cream-200">
        <div className="px-6 py-4 border-b border-cream-200">
          <h3 className="font-semibold text-accent-500">My Recent Complaints</h3>
        </div>
        <div className="divide-y divide-cream-200">
          {complaints.slice(0, 5).map((c) => (
            <div key={c.id} className="px-6 py-3.5 flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-accent-500">{c.title}</p>
                <p className="text-xs text-primary-400 capitalize">{c.category?.toLowerCase().replace(/_/g, ' ')}</p>
              </div>
              <StatusBadge status={c.status} />
            </div>
          ))}
          {complaints.length === 0 && <p className="px-6 py-5 text-sm text-primary-400">No complaints filed yet</p>}
        </div>
      </div>
    </div>
  );
}

export default function Dashboard() {
  const { isAdmin } = useAuth();
  return isAdmin ? <AdminDashboard /> : <StudentDashboard />;
}
