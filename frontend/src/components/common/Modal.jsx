import { X } from 'lucide-react';

export default function Modal({ open, onClose, title, subtitle, children, wide = false }) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-60 flex items-center justify-center animate-in fade-in duration-200">
      <div className="fixed inset-0 bg-black/40 backdrop-blur-xs" onClick={onClose} />
      <div className={`relative bg-white rounded-2xl shadow-2xl mx-4 max-h-[90vh] flex flex-col animate-in zoom-in-95 duration-200 ${wide ? 'w-full max-w-2xl' : 'w-full max-w-lg'}`}>
        <div className="flex items-start justify-between px-6 pt-6 pb-4">
          <div>
            <h2 className="text-lg font-semibold text-accent-500">{title}</h2>
            {subtitle && <p className="text-sm text-primary-400 mt-0.5">{subtitle}</p>}
          </div>
          <button onClick={onClose} className="p-1.5 hover:bg-cream-100 rounded-lg transition-colors cursor-pointer -mt-0.5">
            <X size={18} className="text-primary-400" />
          </button>
        </div>
        <div className="border-t border-cream-200 mx-6" />
        <div className="p-6 overflow-y-auto">{children}</div>
      </div>
    </div>
  );
}
