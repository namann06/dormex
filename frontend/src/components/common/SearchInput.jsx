import { Search } from 'lucide-react';

export default function SearchInput({ value, onChange, onSubmit, placeholder = 'Search...' }) {
  const input = (
    <div className="relative min-w-0">
      <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-primary-400" />
      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="w-full min-w-32 max-w-64 pl-9 pr-4 py-2 border border-cream-200 rounded-lg text-sm focus:ring-2 focus:ring-accent-500 focus:border-accent-500 outline-none bg-white text-accent-500"
      />
    </div>
  );

  if (onSubmit) {
    return <form onSubmit={(e) => { e.preventDefault(); onSubmit(); }}>{input}</form>;
  }
  return input;
}
