import { useAuth } from '../context/AuthContext';
import { User, Mail, Shield, Clock } from 'lucide-react';
import PageHeader from '../components/common/PageHeader';

export default function Profile() {
  const { user } = useAuth();

  return (
    <div className="max-w-2xl mx-auto">
      <PageHeader title="Profile" subtitle="Your account details" />

      <div className="bg-white rounded-xl border border-cream-200 overflow-hidden">
        {/* Header */}
        <div className="bg-linear-to-r from-accent-500 to-accent-700 px-8 py-10 text-center">
          <div className="w-20 h-20 rounded-full bg-white/20 flex items-center justify-center mx-auto mb-4">
            <User size={36} className="text-white" />
          </div>
          <h2 className="text-2xl font-bold text-white">{user?.name}</h2>
          <p className="text-white/70 mt-1 capitalize">{user?.role?.toLowerCase()}</p>
        </div>

        {/* Details */}
        <div className="p-8 space-y-5">
          <div className="flex items-center gap-4 p-4 bg-cream-50 rounded-lg">
            <Mail size={20} className="text-primary-300" />
            <div>
              <p className="text-xs text-primary-400">Email</p>
              <p className="text-sm font-medium text-accent-500">{user?.email}</p>
            </div>
          </div>
          <div className="flex items-center gap-4 p-4 bg-cream-50 rounded-xl">
            <Shield size={20} className="text-primary-300" />
            <div>
              <p className="text-xs text-primary-400">Role</p>
              <p className="text-sm font-medium text-accent-500 capitalize">{user?.role?.toLowerCase()}</p>
            </div>
          </div>
          <div className="flex items-center gap-4 p-4 bg-cream-50 rounded-xl">
            <Clock size={20} className="text-primary-300" />
            <div>
              <p className="text-xs text-primary-400">User ID</p>
              <p className="text-sm font-medium text-accent-500">{user?.id}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
