import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  BedDouble,
  Building2,
  MessageSquareWarning,
  UtensilsCrossed,
  BarChart3,
  LogOut,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

const adminLinks = [
  { to: '/', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/students', icon: Users, label: 'Students' },
  { to: '/blocks', icon: Building2, label: 'Blocks' },
  { to: '/rooms', icon: BedDouble, label: 'Rooms' },
  { to: '/complaints', icon: MessageSquareWarning, label: 'Complaints' },
  { to: '/mess-menu', icon: UtensilsCrossed, label: 'Mess Menu' },
  { to: '/analytics', icon: BarChart3, label: 'Analytics' },
];

const studentLinks = [
  { to: '/', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/rooms', icon: BedDouble, label: 'Rooms' },
  { to: '/complaints', icon: MessageSquareWarning, label: 'Complaints' },
  { to: '/mess-menu', icon: UtensilsCrossed, label: 'Mess Menu' },
];

export default function Sidebar() {
  const { isAdmin, isStudent, hasStudentProfile, logout } = useAuth();

  let links;
  if (isAdmin) {
    links = adminLinks;
  } else if (isStudent && !hasStudentProfile) {
    // OAuth users without a student profile: only Dashboard and Complaints
    links = studentLinks.filter(l => l.to === '/' || l.to === '/complaints');
  } else {
    links = studentLinks;
  }

  return (
    <aside className="fixed top-0 left-0 h-screen w-56 bg-accent-500 flex flex-col z-50">
      {/* Logo */}
      <div className="flex items-center gap-3 px-5 h-16 border-b border-white/10">
        <div className="w-8 h-8 rounded-lg bg-white/15 flex items-center justify-center">
          <Building2 size={16} className="text-white" />
        </div>
        <span className="text-lg font-bold text-white tracking-tight">Dormex</span>
      </div>

      {/* Nav Links */}
      <nav className="flex-1 py-4 px-3 space-y-1 overflow-y-auto">
        {links.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/'}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-[13px] font-medium transition-colors ${
                isActive
                  ? 'bg-white/15 text-white'
                  : 'text-white/60 hover:bg-white/10 hover:text-white'
              }`
            }
          >
            <Icon size={18} className="shrink-0" />
            <span>{label}</span>
          </NavLink>
        ))}
      </nav>

      {/* Logout */}
      <div className="px-3 pb-4 pt-3 border-t border-white/10">
        <button
          onClick={logout}
          className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-[13px] font-medium text-white/60 hover:bg-red-500/20 hover:text-red-300 w-full transition-colors cursor-pointer"
        >
          <LogOut size={18} className="shrink-0" />
          <span>Logout</span>
        </button>
      </div>
    </aside>
  );
}
