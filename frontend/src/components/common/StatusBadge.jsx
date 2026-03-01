const statusConfig = {
  ACTIVE: { bg: 'bg-emerald-50', text: 'text-emerald-700', dot: 'bg-emerald-500' },
  LEFT: { bg: 'bg-red-50', text: 'text-red-700', dot: 'bg-red-500' },
  TRANSFERRED: { bg: 'bg-amber-50', text: 'text-amber-700', dot: 'bg-amber-500' },
  AVAILABLE: { bg: 'bg-emerald-50', text: 'text-emerald-700', dot: 'bg-emerald-500' },
  OCCUPIED: { bg: 'bg-blue-50', text: 'text-blue-700', dot: 'bg-blue-500' },
  FULL: { bg: 'bg-red-50', text: 'text-red-700', dot: 'bg-red-500' },
  UNDER_MAINTENANCE: { bg: 'bg-orange-50', text: 'text-orange-700', dot: 'bg-orange-500' },
  OPEN: { bg: 'bg-red-50', text: 'text-red-700', dot: 'bg-red-500' },
  IN_PROGRESS: { bg: 'bg-amber-50', text: 'text-amber-700', dot: 'bg-amber-500' },
  RESOLVED: { bg: 'bg-emerald-50', text: 'text-emerald-700', dot: 'bg-emerald-500' },
  CLOSED: { bg: 'bg-cream-200', text: 'text-primary-500', dot: 'bg-primary-300' },
};

export default function StatusBadge({ status }) {
  const config = statusConfig[status] || { bg: 'bg-cream-200', text: 'text-primary-500', dot: 'bg-primary-300' };
  const label = status?.replace(/_/g, ' ');
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium whitespace-nowrap ${config.bg} ${config.text}`}>
      <span className={`w-1.5 h-1.5 rounded-full ${config.dot}`} />
      {label?.toLowerCase()}
    </span>
  );
}
