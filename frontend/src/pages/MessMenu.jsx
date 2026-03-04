import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { getWeeklyMenu, getTodayMenu, createMenu, updateMenu, deleteMenu } from '../api/menu';
import Modal from '../components/common/Modal';
import ConfirmModal from '../components/common/ConfirmModal';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageHeader from '../components/common/PageHeader';
import { Plus, Edit, Trash2, UtensilsCrossed, ChevronLeft, ChevronRight } from 'lucide-react';
import toast from 'react-hot-toast';

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
const MEALS = ['BREAKFAST', 'LUNCH', 'SNACKS', 'DINNER'];

const mealTimes = {
  BREAKFAST: '07:00am - 10:00am',
  LUNCH: '01:00pm - 03:00pm',
  SNACKS: '04:00pm - 07:00pm',
  DINNER: '08:00pm - 10:00pm',
};

export default function MessMenu() {
  const { isAdmin } = useAuth();
  const [weeklyMenu, setWeeklyMenu] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [editMeal, setEditMeal] = useState(null);
  const [deleteId, setDeleteId] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [activeMeal, setActiveMeal] = useState('LUNCH');
  const [viewMode, setViewMode] = useState('today');

  const todayIndex = new Date().getDay() === 0 ? 6 : new Date().getDay() - 1;
  const [selectedDay, setSelectedDay] = useState(todayIndex);

  const load = async () => {
    setLoading(true);
    try {
      const res = await getWeeklyMenu();
      setWeeklyMenu(res.data.data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await deleteMenu(deleteId);
      toast.success('Menu item deleted');
      setDeleteId(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete menu item');
    } finally {
      setDeleting(false);
    }
  };

  const currentDay = DAYS[selectedDay];
  const dayData = weeklyMenu.find((d) => d.day === currentDay);
  const dateStr = new Date().toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit', year: 'numeric' }).replace(/\//g, '-');

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      {/* Header */}
      <PageHeader title="Today's Menu" subtitle={isAdmin ? "Manage daily mess menu and meal schedules." : undefined}>
        <span className="text-sm text-primary-400">{dateStr}</span>
        <div className="flex border border-cream-200 rounded-lg overflow-hidden">
          <button
            onClick={() => setViewMode('today')}
            className={`px-3 py-1.5 text-xs font-medium cursor-pointer ${viewMode === 'today' ? 'bg-accent-500 text-white' : 'bg-white text-primary-500 hover:bg-cream-50'}`}
          >
            Daily
          </button>
          <button
            onClick={() => setViewMode('weekly')}
            className={`px-3 py-1.5 text-xs font-medium cursor-pointer ${viewMode === 'weekly' ? 'bg-accent-500 text-white' : 'bg-white text-primary-500 hover:bg-cream-50'}`}
          >
            Weekly
          </button>
        </div>
        {isAdmin && (
          <button onClick={() => setShowCreate(true)} className="px-5 py-2 bg-accent-500 text-white rounded-full text-sm font-medium hover:bg-accent-600 cursor-pointer">
            Edit Menu
          </button>
        )}
      </PageHeader>

      {viewMode === 'today' ? (
        <>
          {/* Day selector */}
          <div className="flex items-center gap-3 mb-6">
            <button onClick={() => setSelectedDay((selectedDay - 1 + 7) % 7)} className="p-1.5 hover:bg-cream-100 rounded-lg cursor-pointer">
              <ChevronLeft size={16} className="text-primary-400" />
            </button>
            <span className="text-sm font-semibold text-primary-600 capitalize">{currentDay.toLowerCase()}</span>
            <button onClick={() => setSelectedDay((selectedDay + 1) % 7)} className="p-1.5 hover:bg-cream-100 rounded-lg cursor-pointer">
              <ChevronRight size={16} className="text-primary-400" />
            </button>
          </div>

          {/* Meal Sections */}
          <div className="space-y-6">
            {MEALS.map((meal) => {
              const mealData = dayData?.meals?.find((m) => m.mealType === meal);
              const items = mealData?.items?.split(',').map((i) => i.trim()).filter(Boolean) || [];

              return (
                <div key={meal} className="bg-white rounded-xl border border-cream-200 overflow-hidden">
                  <div className="px-6 py-4 border-b border-cream-200 flex items-center justify-between">
                    <div>
                      <h3 className="font-semibold text-accent-500 capitalize">{meal.toLowerCase()}</h3>
                      <p className="text-xs text-primary-300 mt-0.5">{mealTimes[meal]}</p>
                    </div>
                    {isAdmin && mealData && (
                      <div className="flex gap-2">
                        <button onClick={() => setEditMeal(mealData)} className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-primary-500 bg-cream-50 hover:bg-cream-100 rounded-lg cursor-pointer"><Edit size={12} /> Edit</button>
                        <button onClick={() => setDeleteId(mealData.id)} className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-red-600 bg-red-50 hover:bg-red-100 rounded-lg cursor-pointer"><Trash2 size={12} /> Delete</button>
                      </div>
                    )}
                  </div>
                  <div className="p-6">
                    {items.length > 0 ? (
                      <>
                        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
                          {items.map((item, i) => (
                            <div key={i} className="flex items-center gap-2.5 px-4 py-3 bg-cream-50 rounded-xl border border-cream-200">
                              <div className="w-8 h-8 rounded-lg bg-amber-50 flex items-center justify-center shrink-0">
                                <UtensilsCrossed size={14} className="text-amber-600" />
                              </div>
                              <span className="text-sm font-medium text-primary-600">{item}</span>
                            </div>
                          ))}
                        </div>
                        {mealData?.specialNote && (
                          <p className="text-sm text-amber-600 bg-amber-50 rounded-lg px-3 py-2 mt-4">📌 {mealData.specialNote}</p>
                        )}
                      </>
                    ) : (
                      <div className="text-center py-6">
                        <p className="text-sm text-primary-300">No items added for {meal.toLowerCase()}</p>
                        {isAdmin && (
                          <button
                            onClick={() => setShowCreate({ day: currentDay, meal })}
                            className="text-sm text-accent-500 hover:text-accent-600 font-medium cursor-pointer mt-2"
                          >
                            + Add items
                          </button>
                        )}
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </>
      ) : (
        /* Weekly View */
        <div className="space-y-4">
          {DAYS.map((day) => {
            const dData = weeklyMenu.find((d) => d.day === day);
            const isToday = DAYS[todayIndex] === day;

            return (
              <div key={day} className={`bg-white rounded-xl border overflow-hidden ${isToday ? 'border-accent-400' : 'border-cream-200'}`}>
                <div className={`px-5 py-3 flex items-center gap-2 ${isToday ? 'bg-cream-100' : 'bg-cream-50'} border-b border-cream-200`}>
                  <h3 className={`font-semibold capitalize text-sm ${isToday ? 'text-accent-600' : 'text-accent-500'}`}>
                    {day.toLowerCase()}
                  </h3>
                  {isToday && <span className="text-xs px-2 py-0.5 bg-cream-200 text-accent-600 rounded-full">Today</span>}
                </div>
                <div className="p-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-3">
                  {MEALS.map((meal) => {
                    const mealData = dData?.meals?.find((m) => m.mealType === meal);
                    return (
                      <div key={meal} className="p-3 bg-cream-50 rounded-xl">
                        <div className="flex items-center justify-between mb-1">
                          <p className="text-xs font-semibold text-primary-500 capitalize">{meal.toLowerCase()}</p>
                          {isAdmin && mealData && (
                            <div className="flex gap-0.5">
                              <button onClick={() => setEditMeal(mealData)} className="p-0.5 hover:bg-cream-200 rounded cursor-pointer"><Edit size={11} className="text-primary-300" /></button>
                              <button onClick={() => setDeleteId(mealData.id)} className="p-0.5 hover:bg-cream-200 rounded cursor-pointer"><Trash2 size={11} className="text-primary-300" /></button>
                            </div>
                          )}
                        </div>
                        <p className="text-xs text-primary-400">{mealData?.items || 'No items'}</p>
                      </div>
                    );
                  })}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Empty state */}
      {weeklyMenu.length === 0 && !loading && (
        <div className="text-center py-12 text-primary-300">
          <UtensilsCrossed size={40} className="mx-auto mb-3 text-primary-200" />
          <p>No menu items configured yet</p>
        </div>
      )}

      {/* Create Modal */}
      <MenuFormModal
        open={!!showCreate}
        onClose={() => setShowCreate(false)}
        prefill={typeof showCreate === 'object' ? showCreate : null}
        onSuccess={() => { setShowCreate(false); load(); }}
      />

      {/* Edit Modal */}
      <MenuFormModal
        open={!!editMeal}
        onClose={() => setEditMeal(null)}
        meal={editMeal}
        onSuccess={() => { setEditMeal(null); load(); }}
      />

      <ConfirmModal
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Delete Menu Item"
        message="Are you sure you want to delete this menu item?"
        loading={deleting}
      />
    </div>
  );
}

function MenuFormModal({ open, onClose, meal, prefill, onSuccess }) {
  const isEdit = !!meal;
  const [form, setForm] = useState({ dayOfWeek: 'MONDAY', mealType: 'BREAKFAST', items: '', specialNote: '' });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (meal) {
      setForm({
        dayOfWeek: meal.dayOfWeek || 'MONDAY',
        mealType: meal.mealType || 'BREAKFAST',
        items: meal.items || '',
        specialNote: meal.specialNote || '',
      });
    } else if (prefill) {
      setForm({ dayOfWeek: prefill.day, mealType: prefill.meal, items: '', specialNote: '' });
    } else {
      setForm({ dayOfWeek: 'MONDAY', mealType: 'BREAKFAST', items: '', specialNote: '' });
    }
  }, [meal, prefill, open]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      if (isEdit) {
        await updateMenu(meal.id, { items: form.items, specialNote: form.specialNote });
        toast.success('Menu updated');
      } else {
        await createMenu(form);
        toast.success('Menu item created');
      }
      onSuccess();
    } finally {
      setSubmitting(false);
    }
  };

  const set = (f) => (e) => setForm({ ...form, [f]: e.target.value });

  return (
    <Modal open={open} onClose={onClose} title={isEdit ? 'Edit Menu Item' : 'Add Menu Item'} subtitle={isEdit ? 'Update the meal details.' : 'Configure meal items and schedule.'}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {!isEdit && (
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Day *</label>
              <select value={form.dayOfWeek} onChange={set('dayOfWeek')} required className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all">
                {DAYS.map((d) => <option key={d} value={d}>{d}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Meal *</label>
              <select value={form.mealType} onChange={set('mealType')} required className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all">
                {MEALS.map((m) => <option key={m} value={m}>{m}</option>)}
              </select>
            </div>
          </div>
        )}
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Items *</label>
          <textarea value={form.items} onChange={set('items')} required maxLength={500} rows={3} placeholder="e.g. Rice, Dal, Paneer Curry, Salad" className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 placeholder:text-primary-300 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all resize-none" />
        </div>
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Special Note</label>
          <input value={form.specialNote} onChange={set('specialNote')} maxLength={200} placeholder="e.g. Contains nuts" className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 placeholder:text-primary-300 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
        </div>
        <div className="flex justify-end gap-3 pt-4 border-t border-cream-200">
          <button type="button" onClick={onClose} className="px-5 py-2.5 border border-cream-200 rounded-full text-sm font-medium text-primary-500 hover:bg-cream-50 cursor-pointer transition-colors">Cancel</button>
          <button type="submit" disabled={submitting} className="px-5 py-2.5 bg-accent-500 text-white rounded-full text-sm font-medium hover:bg-accent-600 disabled:opacity-50 cursor-pointer shadow-sm transition-colors">
            {submitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create'}
          </button>
        </div>
      </form>
    </Modal>
  );
}
