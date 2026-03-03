import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import {
  getComplaints, getMyComplaints, getComplaintsByStatus, getComplaintsByCategory,
  getPendingComplaints, createComplaint, updateComplaint, updateComplaintStatus, deleteComplaint
} from '../api/complaints';
import StatusBadge from '../components/common/StatusBadge';
import Modal from '../components/common/Modal';
import ConfirmModal from '../components/common/ConfirmModal';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageHeader from '../components/common/PageHeader';
import SearchInput from '../components/common/SearchInput';
import { Eye, Trash2, MessageSquare } from 'lucide-react';
import toast from 'react-hot-toast';

const CATEGORIES = ['MAINTENANCE', 'ELECTRICAL', 'PLUMBING', 'CLEANING', 'SECURITY', 'NOISE', 'ROOMMATE', 'FOOD', 'OTHER'];
const STATUSES = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];

export default function Complaints() {
  const { isAdmin, isStudent } = useAuth();
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState('');
  const [filterCategory, setFilterCategory] = useState('');
  const [showPending, setShowPending] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [showCreate, setShowCreate] = useState(false);
  const [detailComplaint, setDetailComplaint] = useState(null);
  const [deleteId, setDeleteId] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      let res;
      if (isStudent) {
        res = await getMyComplaints();
      } else if (showPending) {
        res = await getPendingComplaints();
      } else if (filterStatus) {
        res = await getComplaintsByStatus(filterStatus);
      } else if (filterCategory) {
        res = await getComplaintsByCategory(filterCategory);
      } else {
        res = await getComplaints();
      }
      setComplaints(res.data.data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [filterStatus, filterCategory, showPending]);

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await deleteComplaint(deleteId);
      toast.success('Complaint deleted');
      setDeleteId(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete complaint');
    } finally {
      setDeleting(false);
    }
  };

  const filtered = complaints.filter((c) =>
    !searchTerm || String(c.id).includes(searchTerm) || c.title?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div>
      <PageHeader title={isAdmin ? "Complaint Management" : "Complaints"} subtitle={isAdmin ? "Track and resolve student complaints." : undefined}>
        <SearchInput
          value={searchTerm}
          onChange={setSearchTerm}
          placeholder="Search By Complaint ID"
        />
        {isAdmin && (
          <>
            <select
              value={filterStatus}
              onChange={(e) => { setFilterStatus(e.target.value); setFilterCategory(''); setShowPending(false); }}
              className="px-3 py-2 border border-cream-200 rounded-lg text-sm bg-white cursor-pointer"
            >
              <option value="">All Status</option>
              {STATUSES.map((s) => <option key={s} value={s}>{s.replace(/_/g, ' ')}</option>)}
            </select>
            <select
              value={filterCategory}
              onChange={(e) => { setFilterCategory(e.target.value); setFilterStatus(''); setShowPending(false); }}
              className="px-3 py-2 border border-cream-200 rounded-lg text-sm bg-white cursor-pointer"
            >
              <option value="">All Categories</option>
              {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
            <button
              onClick={() => { setShowPending(!showPending); setFilterStatus(''); setFilterCategory(''); }}
              className={`px-3 py-2 rounded-lg text-sm font-medium border cursor-pointer transition-colors whitespace-nowrap ${
                showPending ? 'bg-amber-50 border-amber-300 text-amber-700' : 'border-cream-200 text-primary-500 hover:bg-cream-100'
              }`}
            >
              Pending
            </button>
          </>
        )}
        {isStudent && (
          <button onClick={() => setShowCreate(true)} className="px-5 py-2 bg-accent-500 text-white rounded-full text-sm font-medium hover:bg-accent-600 cursor-pointer whitespace-nowrap">
            Add Complaint
          </button>
        )}
      </PageHeader>

      {/* Table */}
      {loading ? <LoadingSpinner /> : (
        <div className="bg-white rounded-xl border border-cream-200 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-accent-500 text-left">
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">ID</th>
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">Category</th>
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">Date</th>
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">Status</th>
                  {isAdmin && <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">Student</th>}
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">Title</th>
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-cream-200">
                {filtered.map((c) => (
                  <tr key={c.id} className="hover:bg-cream-100 transition-colors">
                    <td className="px-5 py-3.5 text-primary-500">#{c.id}</td>
                    <td className="px-5 py-3.5 text-primary-500 capitalize">{c.category?.toLowerCase().replace(/_/g, ' ')}</td>
                    <td className="px-5 py-3.5 text-primary-500">{new Date(c.createdAt).toLocaleDateString()}</td>
                    <td className="px-5 py-3.5"><StatusBadge status={c.status} /></td>
                    {isAdmin && <td className="px-5 py-3.5 text-primary-500">{c.studentName}</td>}
                    <td className="px-5 py-3.5 text-accent-500 font-medium truncate max-w-50">{c.title}</td>
                    <td className="px-5 py-3.5">
                      <div className="flex items-center gap-2">
                        <button onClick={() => setDetailComplaint(c)} className="p-1.5 hover:bg-cream-100 rounded-lg cursor-pointer" title="View"><Eye size={16} className="text-primary-400" /></button>
                        {isAdmin && <button onClick={() => setDeleteId(c.id)} className="p-1.5 hover:bg-red-50 rounded-lg cursor-pointer" title="Delete"><Trash2 size={16} className="text-red-400" /></button>}
                      </div>
                    </td>
                  </tr>
                ))}
                {filtered.length === 0 && (
                  <tr>
                    <td colSpan={isAdmin ? 7 : 6} className="px-5 py-8 text-center text-primary-400">
                      <MessageSquare size={32} className="mx-auto mb-2 text-primary-300" />
                      No complaints found
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Create Modal */}
      <CreateComplaintModal open={showCreate} onClose={() => setShowCreate(false)} onSuccess={() => { setShowCreate(false); load(); }} />

      {/* Detail Modal */}
      <ComplaintDetailModal complaint={detailComplaint} onClose={() => setDetailComplaint(null)} isAdmin={isAdmin} onUpdate={() => { setDetailComplaint(null); load(); }} />

      <ConfirmModal
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Delete Complaint"
        message="Are you sure you want to delete this complaint? This action cannot be undone."
        loading={deleting}
      />
    </div>
  );
}

function CreateComplaintModal({ open, onClose, onSuccess }) {
  const [form, setForm] = useState({ category: 'MAINTENANCE', title: '', description: '' });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open) setForm({ category: 'MAINTENANCE', title: '', description: '' });
  }, [open]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await createComplaint(form);
      toast.success('Complaint submitted');
      onSuccess();
    } finally {
      setSubmitting(false);
    }
  };

  const set = (f) => (e) => setForm({ ...form, [f]: e.target.value });

  return (
    <Modal open={open} onClose={onClose} title="New Complaint" subtitle="Submit a new complaint for resolution.">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Category *</label>
          <select value={form.category} onChange={set('category')} required className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all">
            {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Title *</label>
          <input value={form.title} onChange={set('title')} required maxLength={200} className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
        </div>
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Description *</label>
          <textarea value={form.description} onChange={set('description')} required maxLength={2000} rows={4} className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all resize-none" />
        </div>
        <div className="flex justify-end gap-3 pt-4 border-t border-cream-200">
          <button type="button" onClick={onClose} className="px-5 py-2.5 border border-cream-200 rounded-full text-sm font-medium text-primary-500 hover:bg-cream-50 cursor-pointer transition-colors">Cancel</button>
          <button type="submit" disabled={submitting} className="px-5 py-2.5 bg-accent-500 text-white rounded-full text-sm font-medium hover:bg-accent-600 disabled:opacity-50 cursor-pointer shadow-sm transition-colors">
            {submitting ? 'Submitting...' : 'Submit'}
          </button>
        </div>
      </form>
    </Modal>
  );
}

function ComplaintDetailModal({ complaint, onClose, isAdmin, onUpdate }) {
  const [status, setStatus] = useState('');
  const [remarks, setRemarks] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (complaint) {
      setStatus(complaint.status || '');
      setRemarks(complaint.adminRemarks || '');
    }
  }, [complaint]);

  if (!complaint) return null;

  const handleUpdateStatus = async () => {
    setSubmitting(true);
    try {
      await updateComplaintStatus(complaint.id, status, remarks || undefined);
      toast.success('Complaint updated');
      onUpdate();
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal open={true} onClose={onClose} title="Complaint Details" subtitle="View complaint information and status." wide>
      <div className="space-y-4">
        <div className="flex items-center gap-3">
          <StatusBadge status={complaint.status} />
          <span className="text-xs px-2 py-0.5 bg-cream-100 rounded-full text-primary-400 capitalize">{complaint.category?.toLowerCase()}</span>
        </div>
        <h3 className="text-lg font-semibold text-accent-500">{complaint.title}</h3>
        <p className="text-sm text-primary-500 whitespace-pre-wrap">{complaint.description}</p>

        <div className="grid grid-cols-2 gap-4 pt-4 border-t border-cream-200">
          {isAdmin && (
            <>
              <div>
                <p className="text-xs text-primary-400">Student</p>
                <p className="text-sm font-medium text-accent-500">{complaint.studentName}</p>
              </div>
              <div>
                <p className="text-xs text-primary-400">Roll Number</p>
                <p className="text-sm font-medium text-accent-500">{complaint.studentRollNumber}</p>
              </div>
            </>
          )}
          <div>
            <p className="text-xs text-primary-400">Created</p>
            <p className="text-sm text-accent-500">{new Date(complaint.createdAt).toLocaleString()}</p>
          </div>
          {complaint.resolvedAt && (
            <div>
              <p className="text-xs text-primary-400">Resolved</p>
              <p className="text-sm text-accent-500">{new Date(complaint.resolvedAt).toLocaleString()}</p>
            </div>
          )}
        </div>

        {complaint.adminRemarks && !isAdmin && (
          <div className="pt-4 border-t border-cream-200">
            <p className="text-xs text-primary-400 mb-1">Admin Remarks</p>
            <p className="text-sm text-accent-500 bg-amber-50 p-3 rounded-lg">{complaint.adminRemarks}</p>
          </div>
        )}

        {isAdmin && (
          <div className="pt-4 border-t border-cream-200 space-y-3">
            <p className="text-sm font-medium text-primary-500">Update Complaint</p>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Status</label>
              <select value={status} onChange={(e) => setStatus(e.target.value)} className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all">
                {STATUSES.map((s) => <option key={s} value={s}>{s.replace(/_/g, ' ')}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Admin Remarks</label>
              <textarea value={remarks} onChange={(e) => setRemarks(e.target.value)} maxLength={1000} rows={3} className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all resize-none" />
            </div>
            <div className="flex justify-end">
              <button
                onClick={handleUpdateStatus}
                disabled={submitting}
                className="px-5 py-2.5 bg-accent-500 text-white rounded-full text-sm font-medium hover:bg-accent-600 disabled:opacity-50 cursor-pointer shadow-sm transition-colors"
              >
                {submitting ? 'Updating...' : 'Update'}
              </button>
            </div>
          </div>
        )}
      </div>
    </Modal>
  );
}
