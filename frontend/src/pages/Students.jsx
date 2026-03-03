import { useState, useEffect, useMemo } from 'react';
import { getStudents, searchStudents, getStudentsByStatus, createStudent, updateStudent, updateStudentStatus, assignRoom, deleteStudent } from '../api/students';
import { getVacantRooms } from '../api/rooms';
import StatusBadge from '../components/common/StatusBadge';
import Modal from '../components/common/Modal';
import ConfirmModal from '../components/common/ConfirmModal';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageHeader from '../components/common/PageHeader';
import SearchInput from '../components/common/SearchInput';
import { Trash2, Edit, BedDouble, Eye, Filter } from 'lucide-react';
import toast from 'react-hot-toast';

const STATUSES = ['ACTIVE', 'LEFT', 'TRANSFERRED'];

export default function Students() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterBlock, setFilterBlock] = useState('');
  const [filterDepartment, setFilterDepartment] = useState('');
  const [filterYear, setFilterYear] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const [showCreate, setShowCreate] = useState(false);
  const [editStudent, setEditStudent] = useState(null);
  const [detailStudent, setDetailStudent] = useState(null);
  const [showAssignRoom, setShowAssignRoom] = useState(null);
  const [vacantRooms, setVacantRooms] = useState([]);
  const [deleteId, setDeleteId] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      let res;
      if (search.trim()) {
        res = await searchStudents(search.trim());
      } else if (filterStatus) {
        res = await getStudentsByStatus(filterStatus);
      } else {
        res = await getStudents();
      }
      setStudents(res.data.data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [filterStatus]);

  const handleSearch = (e) => {
    e.preventDefault();
    load();
  };

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await deleteStudent(deleteId);
      toast.success('Student deleted');
      setDeleteId(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete student');
    } finally {
      setDeleting(false);
    }
  };

  const handleStatusChange = async (id, status) => {
    await updateStudentStatus(id, status);
    toast.success('Status updated');
    load();
  };

  const openAssignRoom = async (student) => {
    const res = await getVacantRooms();
    setVacantRooms(res.data.data || []);
    setShowAssignRoom(student);
  };

  const handleAssignRoom = async (studentId, roomId) => {
    await assignRoom(studentId, roomId);
    toast.success('Room assigned');
    setShowAssignRoom(null);
    load();
  };

  const filterOptions = useMemo(() => {
    const blocks = [...new Set(students.map(s => s.blockName).filter(Boolean))].sort();
    const departments = [...new Set(students.map(s => s.department).filter(Boolean))].sort();
    const years = [...new Set(students.map(s => s.year).filter(Boolean))].sort();
    return { blocks, departments, years };
  }, [students]);

  const activeFilterCount = [filterBlock, filterDepartment, filterYear].filter(Boolean).length;

  const filteredStudents = useMemo(() => {
    return students.filter(s => {
      if (filterBlock && s.blockName !== filterBlock) return false;
      if (filterDepartment && s.department !== filterDepartment) return false;
      if (filterYear && s.year !== filterYear) return false;
      return true;
    });
  }, [students, filterBlock, filterDepartment, filterYear]);

  const clearFilters = () => {
    setFilterBlock('');
    setFilterDepartment('');
    setFilterYear('');
  };

  return (
    <div>
      <PageHeader title="Students" subtitle="Manage student records and room assignments.">
        <SearchInput
          value={search}
          onChange={setSearch}
          onSubmit={load}
          placeholder="Search By Name"
        />
        <button
          onClick={() => setShowFilters(!showFilters)}
          className={`relative px-4 py-2 border rounded-full text-sm font-medium cursor-pointer whitespace-nowrap transition-colors ${
            showFilters || activeFilterCount > 0
              ? 'bg-accent-50 border-accent-300 text-accent-700'
              : 'border-cream-200 text-primary-500 hover:bg-cream-100'
          }`}
        >
          <Filter size={14} className="inline mr-1.5 -mt-0.5" />
          Filters
          {activeFilterCount > 0 && (
            <span className="ml-1.5 inline-flex items-center justify-center w-5 h-5 text-xs font-bold text-white bg-accent-500 rounded-full">
              {activeFilterCount}
            </span>
          )}
        </button>
        <button onClick={() => setShowCreate(true)} className="px-5 py-2 bg-accent-500 text-white rounded-full text-sm font-medium hover:bg-accent-600 cursor-pointer whitespace-nowrap">
          Add Student
        </button>
      </PageHeader>

      {/* Filter Bar */}
      {showFilters && (
        <div className="bg-white rounded-xl border border-cream-200 p-4 mb-4 flex flex-wrap items-end gap-4">
          <div className="min-w-40">
            <label className="block text-xs font-medium text-primary-400 mb-1.5">Block</label>
            <select
              value={filterBlock}
              onChange={(e) => setFilterBlock(e.target.value)}
              className="w-full px-3 py-2 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all"
            >
              <option value="">All Blocks</option>
              {filterOptions.blocks.map((b) => (
                <option key={b} value={b}>{b}</option>
              ))}
            </select>
          </div>
          <div className="min-w-40">
            <label className="block text-xs font-medium text-primary-400 mb-1.5">Department</label>
            <select
              value={filterDepartment}
              onChange={(e) => setFilterDepartment(e.target.value)}
              className="w-full px-3 py-2 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all"
            >
              <option value="">All Departments</option>
              {filterOptions.departments.map((d) => (
                <option key={d} value={d}>{d}</option>
              ))}
            </select>
          </div>
          <div className="min-w-40">
            <label className="block text-xs font-medium text-primary-400 mb-1.5">Year</label>
            <select
              value={filterYear}
              onChange={(e) => setFilterYear(e.target.value)}
              className="w-full px-3 py-2 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all"
            >
              <option value="">All Years</option>
              {filterOptions.years.map((y) => (
                <option key={y} value={y}>{y}</option>
              ))}
            </select>
          </div>
          {activeFilterCount > 0 && (
            <button
              onClick={clearFilters}
              className="px-4 py-2 text-sm font-medium text-red-500 hover:bg-red-50 rounded-lg cursor-pointer transition-colors"
            >
              Clear All
            </button>
          )}
        </div>
      )}

      {/* Table */}
      {loading ? <LoadingSpinner /> : (
        <div className="bg-white rounded-xl border border-cream-200 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-accent-500 text-left">
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">Name</th>
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">ID</th>
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">Room</th>
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">Status</th>
                  <th className="px-5 py-3.5 font-semibold text-white text-xs uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-cream-200">
                {filteredStudents.map((s) => (
                  <tr key={s.id} className="hover:bg-cream-100 transition-colors">
                    <td className="px-5 py-3.5">
                      <p className="font-medium text-accent-500">{s.name}</p>
                    </td>
                    <td className="px-5 py-3.5 text-primary-500">{s.rollNumber}</td>
                    <td className="px-5 py-3.5 text-primary-500">{s.roomNumber || '-'}</td>
                    <td className="px-5 py-3.5"><StatusBadge status={s.status} /></td>
                    <td className="px-5 py-3.5">
                      <div className="flex items-center gap-3">
                        <button onClick={() => setDetailStudent(s)} className="p-1.5 hover:bg-cream-100 rounded-lg cursor-pointer" title="View Details"><Eye size={16} className="text-primary-400" /></button>
                        <button onClick={() => setEditStudent(s)} className="p-1.5 hover:bg-cream-100 rounded-lg cursor-pointer" title="Edit"><Edit size={16} className="text-primary-400" /></button>
                        {!s.roomNumber && (
                          <button onClick={() => openAssignRoom(s)} className="p-1.5 hover:bg-cream-100 rounded-lg cursor-pointer" title="Assign Room"><BedDouble size={16} className="text-primary-400" /></button>
                        )}
                        <button onClick={() => setDeleteId(s.id)} className="p-1.5 hover:bg-red-50 rounded-lg cursor-pointer" title="Delete"><Trash2 size={16} className="text-red-400" /></button>
                      </div>
                    </td>
                  </tr>
                ))}
                {filteredStudents.length === 0 && (
                  <tr><td colSpan={5} className="px-5 py-8 text-center text-primary-400">No students found</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Create Modal */}
      <StudentFormModal open={showCreate} onClose={() => setShowCreate(false)} onSuccess={() => { setShowCreate(false); load(); }} />

      {/* Edit Modal */}
      <StudentFormModal open={!!editStudent} onClose={() => setEditStudent(null)} student={editStudent} onSuccess={() => { setEditStudent(null); load(); }} />

      {/* Detail Modal */}
      <StudentDetailModal student={detailStudent} onClose={() => setDetailStudent(null)} onStatusChange={(id, status) => { handleStatusChange(id, status); setDetailStudent(null); }} />

      {/* Delete Confirm Modal */}
      <ConfirmModal
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Delete Student"
        message="Are you sure you want to delete this student? All associated data including complaints will be permanently removed."
        loading={deleting}
      />

      {/* Assign Room Modal */}
      <Modal open={!!showAssignRoom} onClose={() => setShowAssignRoom(null)} title={`Assign Room - ${showAssignRoom?.name}`} subtitle="Select a room to assign to this student.">
        <div className="space-y-3 max-h-80 overflow-y-auto">
          {vacantRooms.length === 0 ? (
            <p className="text-sm text-primary-400">No vacant rooms available</p>
          ) : (
            vacantRooms.map((r) => (
              <button
                key={r.id}
                onClick={() => handleAssignRoom(showAssignRoom.id, r.id)}
                className="w-full flex items-center justify-between p-4 border border-cream-200 rounded-xl hover:bg-accent-50 hover:border-accent-300 transition-colors cursor-pointer text-left"
              >
                <div>
                  <p className="font-medium text-accent-500">{r.blockName} - {r.roomNumber}</p>
                  <p className="text-xs text-primary-400 mt-0.5">Floor {r.floor} &middot; {r.roomType} &middot; {r.availableSlots} slots available</p>
                </div>
                <StatusBadge status={r.status} />
              </button>
            ))
          )}
        </div>
      </Modal>
    </div>
  );
}

function StudentFormModal({ open, onClose, student, onSuccess }) {
  const [form, setForm] = useState({
    name: '', email: '', password: '', rollNumber: '', phone: '',
    department: '', year: '', address: '', guardianName: '', guardianPhone: '',
    dateOfBirth: '', joiningDate: '',
  });
  const [submitting, setSubmitting] = useState(false);
  const isEdit = !!student;

  useEffect(() => {
    if (student) {
      setForm({
        name: student.name || '', email: student.email || '', password: '',
        rollNumber: student.rollNumber || '', phone: student.phone || '',
        department: student.department || '', year: student.year || '',
        address: student.address || '', guardianName: student.guardianName || '',
        guardianPhone: student.guardianPhone || '',
        dateOfBirth: student.dateOfBirth || '', joiningDate: student.joiningDate || '',
      });
    } else {
      setForm({ name: '', email: '', password: '', rollNumber: '', phone: '', department: '', year: '', address: '', guardianName: '', guardianPhone: '', dateOfBirth: '', joiningDate: '' });
    }
  }, [student, open]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const data = { ...form };
      if (!data.dateOfBirth) delete data.dateOfBirth;
      if (!data.joiningDate) delete data.joiningDate;

      if (isEdit) {
        delete data.email;
        delete data.password;
        await updateStudent(student.id, data);
        toast.success('Student updated');
      } else {
        await createStudent(data);
        toast.success('Student created');
      }
      onSuccess();
    } finally {
      setSubmitting(false);
    }
  };

  const set = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  return (
    <Modal open={open} onClose={onClose} title={isEdit ? 'Edit Student' : 'Add New Student'} subtitle={isEdit ? 'Update student information.' : 'Enter student details and credentials.'} wide>
      <form onSubmit={handleSubmit} className="space-y-5">
        {/* Personal Information */}
        <div>
          <h4 className="text-xs font-semibold text-primary-400 uppercase tracking-wider mb-3">Personal Information</h4>
          <div className="grid grid-cols-2 gap-x-4 gap-y-3">
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Name *</label>
              <input value={form.name} onChange={set('name')} required minLength={2} maxLength={100} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
            {!isEdit && (
              <>
                <div>
                  <label className="block text-sm font-medium text-primary-500 mb-1.5">Email *</label>
                  <input type="email" value={form.email} onChange={set('email')} required className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-primary-500 mb-1.5">Password *</label>
                  <input type="password" value={form.password} onChange={set('password')} required minLength={6} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
                </div>
              </>
            )}
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Phone</label>
              <input value={form.phone} onChange={set('phone')} maxLength={15} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Date of Birth</label>
              <input type="date" value={form.dateOfBirth} onChange={set('dateOfBirth')} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
            <div className="col-span-2">
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Address</label>
              <input value={form.address} onChange={set('address')} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
          </div>
        </div>

        {/* Academic Information */}
        <div className="pt-1 border-t border-cream-200">
          <h4 className="text-xs font-semibold text-primary-400 uppercase tracking-wider mb-3 mt-1">Academic Information</h4>
          <div className="grid grid-cols-2 gap-x-4 gap-y-3">
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Roll Number *</label>
              <input value={form.rollNumber} onChange={set('rollNumber')} required maxLength={20} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Department</label>
              <input value={form.department} onChange={set('department')} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Year</label>
              <input value={form.year} onChange={set('year')} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Joining Date</label>
              <input type="date" value={form.joiningDate} onChange={set('joiningDate')} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
          </div>
        </div>

        {/* Guardian Information */}
        <div className="pt-1 border-t border-cream-200">
          <h4 className="text-xs font-semibold text-primary-400 uppercase tracking-wider mb-3 mt-1">Guardian Information</h4>
          <div className="grid grid-cols-2 gap-x-4 gap-y-3">
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Guardian Name</label>
              <input value={form.guardianName} onChange={set('guardianName')} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Guardian Phone</label>
              <input value={form.guardianPhone} onChange={set('guardianPhone')} maxLength={15} className="w-full px-3.5 py-2.5 bg-cream-50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
          </div>
        </div>

        <div className="flex justify-end gap-3 pt-4 border-t border-cream-200">
          <button type="button" onClick={onClose} className="px-5 py-2.5 border border-cream-200 rounded-full text-sm font-medium text-primary-500 hover:bg-cream-100 cursor-pointer transition-colors">Cancel</button>
          <button type="submit" disabled={submitting} className="px-5 py-2.5 bg-accent-500 text-white rounded-full text-sm font-medium hover:bg-accent-600 disabled:opacity-50 cursor-pointer shadow-sm transition-colors">
            {submitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create'}
          </button>
        </div>
      </form>
    </Modal>
  );
}

function StudentDetailModal({ student, onClose, onStatusChange }) {
  if (!student) return null;
  const fields = [
    ['Name', student.name], ['Email', student.email], ['Roll Number', student.rollNumber],
    ['Phone', student.phone], ['Department', student.department], ['Year', student.year],
    ['Address', student.address], ['Guardian', student.guardianName], ['Guardian Phone', student.guardianPhone],
    ['DOB', student.dateOfBirth], ['Joining Date', student.joiningDate], ['Leaving Date', student.leavingDate],
    ['Room', student.roomNumber],
  ];

  return (
    <Modal open={true} onClose={onClose} title="Student Details" subtitle="View complete student information." wide>
      <div className="grid grid-cols-2 gap-3">
        {fields.map(([label, val]) => (
          <div key={label} className="bg-cream-100 rounded-lg px-3.5 py-2.5">
            <p className="text-xs text-primary-400 mb-0.5">{label}</p>
            <p className="text-sm font-medium text-accent-500">{val || '-'}</p>
          </div>
        ))}
        <div className="bg-cream-100 rounded-lg px-3.5 py-2.5">
          <p className="text-xs text-primary-400 mb-0.5">Status</p>
          <StatusBadge status={student.status} />
        </div>
      </div>
      <div className="mt-6 pt-4 border-t border-cream-200">
        <p className="text-sm font-medium text-primary-500 mb-3">Change Status</p>
        <div className="flex gap-2">
          {STATUSES.filter((s) => s !== student.status).map((s) => (
            <button
              key={s}
              onClick={() => onStatusChange(student.id, s)}
              className="px-3.5 py-1.5 border border-cream-200 rounded-full text-xs font-medium text-primary-500 hover:bg-accent-50 hover:border-accent-300 hover:text-accent-700 cursor-pointer transition-colors"
            >
              Mark as {s}
            </button>
          ))}
        </div>
      </div>
    </Modal>
  );
}
