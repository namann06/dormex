import { useState, useEffect } from 'react';
import { getRooms, getRoomsByBlock, getVacantRooms, createRoom, updateRoom, updateRoomStatus, deleteRoom } from '../api/rooms';
import { getActiveBlocks } from '../api/blocks';
import Modal from '../components/common/Modal';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageHeader from '../components/common/PageHeader';
import SearchInput from '../components/common/SearchInput';
import { useAuth } from '../context/AuthContext';
import { Edit, Trash2, Users } from 'lucide-react';
import toast from 'react-hot-toast';

const ROOM_STATUSES = ['AVAILABLE', 'OCCUPIED', 'FULL', 'UNDER_MAINTENANCE'];

export default function Rooms() {
  const { isAdmin } = useAuth();
  const [rooms, setRooms] = useState([]);
  const [blocks, setBlocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterBlock, setFilterBlock] = useState('');
  const [filterVacant, setFilterVacant] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [showCreate, setShowCreate] = useState(false);
  const [editRoom, setEditRoom] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      const [blocksRes, roomsRes] = await Promise.all([
        getActiveBlocks(),
        filterVacant
          ? getVacantRooms()
          : filterBlock
          ? getRoomsByBlock(filterBlock)
          : getRooms()
      ]);
      setBlocks(blocksRes.data.data || []);
      setRooms(roomsRes.data.data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [filterBlock, filterVacant]);

  const handleStatusChange = async (id, status) => {
    await updateRoomStatus(id, status);
    toast.success('Status updated');
    load();
  };

  const filteredRooms = rooms.filter((r) =>
    !searchTerm || r.roomNumber?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div>
      <PageHeader title="Rooms" subtitle={isAdmin ? "View and manage room availability and occupancy." : undefined}>
        <span className="text-sm text-primary-400">{filteredRooms.length} rooms</span>
      </PageHeader>

      <div className="flex gap-3 items-center justify-between mb-6">
        <div className="flex gap-3 items-center">
          <SearchInput
            value={searchTerm}
            onChange={setSearchTerm}
            placeholder="Search By Room No"
          />
          <select
            value={filterBlock}
            onChange={(e) => { setFilterBlock(e.target.value); setFilterVacant(false); }}
            className="px-3 py-2 border border-cream-200 rounded-lg text-sm bg-white cursor-pointer"
          >
            <option value="">All Blocks</option>
            {blocks.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
          </select>
          <button
            onClick={() => { setFilterVacant(!filterVacant); setFilterBlock(''); }}
            className={`px-3 py-2 rounded-lg text-sm font-medium border cursor-pointer transition-colors whitespace-nowrap ${
              filterVacant ? 'bg-emerald-50 border-emerald-300 text-emerald-700' : 'border-cream-200 text-primary-500 hover:bg-cream-50'
            }`}
          >
            Vacant
          </button>
        </div>
        {isAdmin && (
          <button onClick={() => setShowCreate(true)} className="px-5 py-2 bg-accent-500 text-white rounded-full text-sm font-medium hover:bg-accent-600 cursor-pointer whitespace-nowrap">
            Add Room
          </button>
        )}
      </div>

      {/* Card Grid */}
      {loading ? <LoadingSpinner /> : (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {filteredRooms.map((room) => {
            const occupancyPct = room.capacity > 0 ? Math.round((room.currentOccupancy / room.capacity) * 100) : 0;
            const barColor = occupancyPct >= 100 ? 'bg-red-500' : occupancyPct >= 60 ? 'bg-amber-500' : 'bg-emerald-500';
            const dotColor = occupancyPct >= 100 ? 'bg-red-500' : occupancyPct >= 60 ? 'bg-amber-500' : 'bg-emerald-500';
            return (
              <div key={room.id} onClick={() => isAdmin && setEditRoom(room)} className={`bg-white rounded-xl border border-cream-200 p-5 hover:shadow-md transition-shadow ${isAdmin ? 'cursor-pointer' : ''}`}>
                <h3 className="font-bold text-accent-500 mb-1">{room.roomNumber}</h3>
                <p className="text-xs text-primary-400 mb-3">Capacity - {room.currentOccupancy}/{room.capacity}</p>

                {/* Occupancy bar */}
                <div className="w-full bg-cream-200 rounded-full h-2 mb-2">
                  <div
                    className={`h-2 rounded-full transition-all ${barColor}`}
                    style={{ width: `${Math.min(occupancyPct, 100)}%` }}
                  />
                </div>

                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-1.5">
                    <span className={`w-2 h-2 rounded-full ${dotColor} inline-block`} />
                    <span className="text-xs font-medium text-primary-500">{occupancyPct}%</span>
                  </div>
                  <span className="text-xs text-primary-400">{room.availableSlots} vacant</span>
                </div>

                {room.studentNames && room.studentNames.length > 0 && (
                  <div className="mt-3 pt-3 border-t border-cream-200">
                    <div className="flex items-center gap-1.5 mb-1.5">
                      <Users size={12} className="text-primary-400" />
                      <span className="text-xs font-medium text-primary-500">Assigned Students</span>
                    </div>
                    <div className="space-y-0.5">
                      {room.studentNames.map((name, idx) => (
                        <p key={idx} className="text-xs text-primary-400 truncate">{name}</p>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            );
          })}
          {filteredRooms.length === 0 && (
            <div className="col-span-full text-center py-8 text-primary-300">No rooms found</div>
          )}
        </div>
      )}

      {/* Create Modal */}
      <RoomFormModal open={showCreate} onClose={() => setShowCreate(false)} blocks={blocks} onSuccess={() => { setShowCreate(false); load(); }} />

      {/* Edit Modal */}
      <RoomFormModal open={!!editRoom} onClose={() => setEditRoom(null)} room={editRoom} blocks={blocks} onSuccess={() => { setEditRoom(null); load(); }} />
    </div>
  );
}

function RoomFormModal({ open, onClose, room, blocks, onSuccess }) {
  const isEdit = !!room;
  const [form, setForm] = useState({ blockId: '', roomNumber: '', floor: 1, capacity: 1, roomType: '', amenities: '' });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (room) {
      setForm({
        blockId: room.blockId || '',
        roomNumber: room.roomNumber || '',
        floor: room.floor || 1,
        capacity: room.capacity || 1,
        roomType: room.roomType || '',
        amenities: room.amenities || '',
      });
    } else {
      setForm({ blockId: blocks?.[0]?.id || '', roomNumber: '', floor: 1, capacity: 1, roomType: '', amenities: '' });
    }
  }, [room, open, blocks]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      if (isEdit) {
        await updateRoom(room.id, { capacity: Number(form.capacity), roomType: form.roomType, amenities: form.amenities });
        toast.success('Room updated');
      } else {
        await createRoom({ ...form, blockId: Number(form.blockId), floor: Number(form.floor), capacity: Number(form.capacity) });
        toast.success('Room created');
      }
      onSuccess();
    } finally {
      setSubmitting(false);
    }
  };

  const set = (f) => (e) => setForm({ ...form, [f]: e.target.value });

  return (
    <Modal open={open} onClose={onClose} title={isEdit ? 'Edit Room' : 'Add Room'} subtitle={isEdit ? 'Update room details and capacity.' : 'Set up a new room in a block.'}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {!isEdit && (
          <>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Block *</label>
              <select value={form.blockId} onChange={set('blockId')} required className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all">
                <option value="">Select block</option>
                {blocks.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Room Number *</label>
              <input value={form.roomNumber} onChange={set('roomNumber')} required maxLength={20} className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
            <div>
              <label className="block text-sm font-medium text-primary-500 mb-1.5">Floor *</label>
              <input type="number" value={form.floor} onChange={set('floor')} required min={1} className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
            </div>
          </>
        )}
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Capacity</label>
          <input type="number" value={form.capacity} onChange={set('capacity')} min={1} className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
        </div>
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Room Type</label>
          <input value={form.roomType} onChange={set('roomType')} placeholder="e.g. Single, Double" className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 placeholder:text-primary-300 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
        </div>
        <div>
          <label className="block text-sm font-medium text-primary-500 mb-1.5">Amenities</label>
          <input value={form.amenities} onChange={set('amenities')} placeholder="e.g. WiFi, AC, Geyser" className="w-full px-3.5 py-2.5 bg-cream-50/50 border border-cream-200 rounded-lg text-sm text-accent-500 placeholder:text-primary-300 focus:ring-2 focus:ring-accent-500/20 focus:border-accent-400 outline-none transition-all" />
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
