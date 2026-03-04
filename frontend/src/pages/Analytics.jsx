import { useState, useEffect } from 'react';
import { getDashboardStats, getBlockOccupancy, getComplaintSummary } from '../api/dashboard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageHeader from '../components/common/PageHeader';
import StatCard from '../components/common/StatCard';
import { BarChart3, Building2, Users, AlertCircle } from 'lucide-react';

export default function Analytics() {
  const [stats, setStats] = useState(null);
  const [occupancy, setOccupancy] = useState([]);
  const [complaintSummary, setComplaintSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getDashboardStats(), getBlockOccupancy(), getComplaintSummary()])
      .then(([s, o, c]) => {
        setStats(s.data.data);
        setOccupancy(o.data.data || []);
        setComplaintSummary(c.data.data);
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;

  const occupancyRate = stats?.occupancyRate?.toFixed(1) || 0;

  return (
    <div>
      <PageHeader title="Analytics" subtitle="Data insights and occupancy overview" />

      {/* Overview Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <StatCard label="Occupancy Rate" value={`${occupancyRate}%`} icon={BarChart3} color="teal" />
        <StatCard label="Total Blocks" value={stats?.totalBlocks || 0} icon={Building2} color="blue" />
        <StatCard label="Active Students" value={stats?.activeStudents || 0} icon={Users} color="emerald" />
        <StatCard label="In-Progress Complaints" value={stats?.inProgressComplaints || 0} icon={AlertCircle} color="amber" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Block Occupancy */}
        <div className="bg-white rounded-xl border border-cream-200">
          <div className="px-5 py-4 border-b border-cream-200">
            <h3 className="font-semibold text-accent-500">Block Occupancy</h3>
          </div>
          <div className="p-5 space-y-4">
            {occupancy.map((block) => (
              <div key={block.blockId}>
                <div className="flex justify-between text-sm mb-1">
                  <span className="font-medium text-primary-600">{block.blockName}</span>
                  <span className="text-primary-400">{block.occupancyRate?.toFixed(0)}%</span>
                </div>
                <div className="w-full bg-cream-200 rounded-full h-2.5">
                  <div
                    className={`h-2.5 rounded-full transition-all ${
                      block.occupancyRate >= 90 ? 'bg-red-500' : block.occupancyRate >= 60 ? 'bg-amber-500' : 'bg-green-500'
                    }`}
                    style={{ width: `${Math.min(block.occupancyRate || 0, 100)}%` }}
                  />
                </div>
                <div className="flex justify-between text-xs text-primary-300 mt-1">
                  <span>{block.occupiedRooms} occupied</span>
                  <span>{block.totalRooms} total rooms</span>
                </div>
              </div>
            ))}
            {occupancy.length === 0 && <p className="text-sm text-primary-300">No block data available</p>}
          </div>
        </div>

        {/* Complaint Breakdown */}
        <div className="bg-white rounded-xl border border-cream-200">
          <div className="px-5 py-4 border-b border-cream-200">
            <h3 className="font-semibold text-accent-500">Complaint Breakdown</h3>
          </div>
          <div className="p-5">
            {/* By Status */}
            <h4 className="text-xs font-semibold text-primary-400 uppercase tracking-wider mb-3">By Status</h4>
            <div className="grid grid-cols-2 gap-3 mb-5">
              {complaintSummary?.byStatus &&
                Object.entries(complaintSummary.byStatus).map(([status, count]) => {
                  const colors = {
                    OPEN: 'bg-red-50 text-red-700 border-red-200',
                    IN_PROGRESS: 'bg-amber-50 text-amber-700 border-amber-200',
                    RESOLVED: 'bg-green-50 text-green-700 border-green-200',
                    CLOSED: 'bg-cream-50 text-primary-600 border-cream-200',
                  };
                  return (
                    <div key={status} className={`flex items-center justify-between p-3 rounded-xl border ${colors[status] || 'bg-cream-50 border-cream-200'}`}>
                      <span className="text-sm font-medium capitalize">{status.toLowerCase().replace(/_/g, ' ')}</span>
                      <span className="text-lg font-bold">{count}</span>
                    </div>
                  );
                })}
            </div>

            {/* By Category */}
            <h4 className="text-xs font-semibold text-primary-400 uppercase tracking-wider mb-3">By Category</h4>
            <div className="space-y-2">
              {complaintSummary?.byCategory &&
                Object.entries(complaintSummary.byCategory).map(([cat, count]) => (
                  <div key={cat} className="flex justify-between items-center text-sm py-1.5">
                    <span className="text-primary-500 capitalize">{cat.toLowerCase().replace(/_/g, ' ')}</span>
                    <span className="font-semibold text-accent-500">{count}</span>
                  </div>
                ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
