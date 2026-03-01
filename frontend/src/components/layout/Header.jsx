import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Header() {
  const { user } = useAuth();
  const navigate = useNavigate();

  return (
    <header className="h-16 bg-white border-b border-cream-200 flex items-center justify-between px-6 sticky top-0 z-40">
      <div />
      <button
        onClick={() => navigate('/profile')}
        className="flex items-center gap-3 cursor-pointer"
      >
        <div className="text-right">
          <p className="text-sm font-semibold text-accent-500 leading-tight">{user?.name}</p>
          <p className="text-[11px] text-primary-400 leading-tight capitalize">{user?.role?.toLowerCase()}</p>
        </div>
        <div className="w-9 h-9 rounded-full bg-accent-500 flex items-center justify-center">
          <span className="text-sm font-bold text-white">{user?.name?.charAt(0)}</span>
        </div>
      </button>
    </header>
  );
}
