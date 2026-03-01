const colorMap = {
  teal: 'bg-teal-50 text-teal-600',
  blue: 'bg-blue-50 text-blue-600',
  amber: 'bg-amber-50 text-amber-600',
  emerald: 'bg-emerald-50 text-emerald-600',
  red: 'bg-red-50 text-red-600',
  violet: 'bg-violet-50 text-violet-600',
  gray: 'bg-cream-200 text-primary-500',
};

export default function StatCard({ label, value, icon: Icon, color = 'teal' }) {
  const colorClass = colorMap[color] || colorMap.teal;

  return (
    <div className="bg-white rounded-xl border border-cream-200 p-5 flex items-start justify-between">
      <div>
        <p className="text-sm text-primary-400 mb-1">{label}</p>
        <p className="text-2xl font-bold text-accent-500">{value}</p>
      </div>
      {Icon && (
        <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${colorClass}`}>
          <Icon size={20} />
        </div>
      )}
    </div>
  );
}
