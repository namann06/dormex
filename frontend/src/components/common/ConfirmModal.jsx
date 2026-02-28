import { AlertTriangle } from 'lucide-react';

export default function ConfirmModal({ open, onClose, onConfirm, title = 'Confirm Delete', message = 'Are you sure? This action cannot be undone.', confirmText = 'Delete', loading = false }) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-60 flex items-center justify-center animate-in fade-in duration-200">
      <div className="fixed inset-0 bg-black/40 backdrop-blur-xs" onClick={onClose} />
      <div className="relative bg-white rounded-2xl shadow-2xl mx-4 w-full max-w-sm animate-in zoom-in-95 duration-200">
        <div className="flex flex-col items-center text-center px-6 pt-8 pb-6">
          <div className="w-12 h-12 rounded-full bg-red-50 flex items-center justify-center mb-4">
            <AlertTriangle size={24} className="text-red-500" />
          </div>
          <h3 className="text-lg font-semibold text-accent-500">{title}</h3>
          <p className="text-sm text-primary-400 mt-2 leading-relaxed">{message}</p>
        </div>
        <div className="border-t border-cream-200 px-6 py-4 flex gap-3">
          <button
            onClick={onClose}
            disabled={loading}
            className="flex-1 px-4 py-2.5 text-sm font-medium text-primary-500 bg-cream-100 hover:bg-cream-200 rounded-xl transition-colors cursor-pointer"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            disabled={loading}
            className="flex-1 px-4 py-2.5 text-sm font-medium text-white bg-red-500 hover:bg-red-600 rounded-xl transition-colors cursor-pointer disabled:opacity-50"
          >
            {loading ? 'Deleting...' : confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
