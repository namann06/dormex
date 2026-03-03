import { useState, useEffect } from 'react';
import { getBlocks, createBlock, updateBlock, toggleBlockStatus, deleteBlock } from '../api/blocks';
import Modal from '../components/common/Modal';
import ConfirmModal from '../components/common/ConfirmModal';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageHeader from '../components/common/PageHeader';
import { Plus, Edit, Trash2, ToggleLeft, ToggleRight, Building2 } from 'lucide-react';
import toast from 'react-hot-toast';

export default function Blocks() {
  const [blocks, setBlocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [editBlock, setEditBlock] = useState(null);
  const [deleteId, setDeleteId] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const res = await getBlocks();
      setBlocks(res.data.data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleToggle = async (id) => {
    await toggleBlockStatus(id);
    toast.success('Block status toggled');
    load();
  };

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await deleteBlock(deleteId);
      toast.success('Block deleted');
      setDeleteId(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete block');
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div>
      <PageHeader title="Blocks" subtitle="Manage hostel blocks and their configuration.">
        <button onClick={() => setShowCreate(true)} className="px-5 py-2 bg-accent-500 text-white rounded-full text-sm font-medium hover:bg-accent-600 cursor-pointer">
          Add Block
        </button>
      </PageHeader>

      {loading ? <LoadingSpinner /> : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {blocks.map((block) => (
            <div key={block.id} className={`bg-white rounded-xl border p-6 transition-all ${block.active ? 'border-cream-200' : 'border-orange-200 bg-orange-50/30'}`}>
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${block.active ? 'bg-accent-50 text-accent-600' : 'bg-orange-100 text-orange-600'}`}>
                    <Building2 size={20} />
                  </div>
                  <div>
                    <h3 className="font-semibold text-accent-500">{block.name}</h3>
                    <span className={`text-xs font-medium ${block.active ? 'text-green-600' : 'text-orange-600'}`}>
                      {block.active ? 'Active' : 'Inactive'}
                    </span>
                  </div>
                </div>
              </div>

              {block.description && (
                <p className="text-sm text-primary-400 mb-4">{block.description}</p>
              )}

              <div className="grid grid-cols-2 gap-3 text-sm mb-4">
                <div className="bg-cream-50 rounded-xl p-3">
                  <p className="text-xs text-primary-400">Total Floors</p>
                  <p className="font-semibold text-accent-500">{block.totalFloors || '-'}</p>
                </div>
                <div className="bg-cream-50 rounded-xl p-3">
                  <p className="text-xs text-primary-400">Total Rooms</p>
                  <p className="font-semibold text-accent-500">{block.totalRooms || 0}</p>
                </div>
                <div className="bg-cream-50 rounded-xl p-3 col-span-2">
                  <p className="text-xs text-primary-400">Occupied Rooms</p>
                  <p className="font-semibold text-accent-500">{block.occupiedRooms || 0} / {block.totalRooms || 0}</p>
                </div>
              </div>

              <div className="flex items-center justify-between pt-3 border-t border-cream-200">
                <button onClick={() => handleToggle(block.id)} className="flex items-center gap-1 text-sm text-primary-400 hover:text-accent-500 cursor-pointer">
                  {block.active ? <ToggleRight size={18} className="text-green-500" /> : <ToggleLeft size={18} className="text-primary-300" />}
                  {block.active ? 'Deactivate' : 'Activate'}
                </button>
                <div className="flex gap-1">
                  <button onClick={() => setEditBlock(block)} className="p-1.5 hover:bg-cream-100 rounded-lg cursor-pointer"><Edit size={14} className="text-accent-500" /></button>
                  <button onClick={() => setDeleteId(block.id)} className="p-1.5 hover:bg-red-50 rounded-lg cursor-pointer"><Trash2 size={14} className="text-red-400" /></button>
                </div>
              </div>
            </div>
          ))}
          {blocks.length === 0 && (
            <div className="col-span-full text-center py-8 text-primary-300">No blocks yet</div>
          )}
        </div>
      )}

      <BlockFormModal open={showCreate} onClose={() => setShowCreate(false)} onSuccess={() => { setShowCreate(false); load(); }} />
      <BlockFormModal open={!!editBlock} onClose={() => setEditBlock(null)} block={editBlock} onSuccess={() => { setEditBlock(null); load(); }} />

      <ConfirmModal
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Delete Block"
        message="Are you sure you want to delete this block? It must have no rooms assigned."
        loading={deleting}
      />
    </div>
  );
}

function BlockFormModal({ open, onClose, block, onSuccess }) {
  const isEdit = !!block;
  const [form, setForm] = useState({ name: '', description: '', totalFloors: '' });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (block) {
      setForm({ name: block.name || '', description: block.description || '', totalFloors: block.totalFloors || '' });
    } else {
      setForm({ name: '', description: '', totalFloors: '' });
    }
  }, [block, open]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const data = { ...form, totalFloors: form.totalFloors ? Number(form.totalFloors) : null };
      if (isEdit) {
        await updateBlock(block.id, data);
        toast.success('Block updated');
      } else {
        await createBlock(data);
        toast.success('Block created');
      }
      onSuccess();
    } finally {
      setSubmitting(false);
    }
  };

  const set = (f) => (e) => setForm({ ...form, [f]: e.target.value });

  return (
    <Modal open={open} onClose={onClose} title={isEdit ? 'Edit Block' : 'Add Block'} subtitle={isEdit ? 'Update block configuration.' : 'Configure a new hostel block.'}>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Block Name *</label>
          <input value={form.name} onChange={set('name')} required maxLength={50} className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
        </div>
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Description</label>
          <textarea value={form.description} onChange={set('description')} maxLength={255} rows={2} className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all resize-none" />
        </div>
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Total Floors</label>
          <input type="number" value={form.totalFloors} onChange={set('totalFloors')} min={1} className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
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
