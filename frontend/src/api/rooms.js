import api from './axios';

export const getRooms = () => api.get('/rooms');
export const getRoom = (id) => api.get(`/rooms/${id}`);
export const getRoomsByBlock = (blockId) => api.get(`/rooms/block/${blockId}`);
export const getRoomsByBlockAndFloor = (blockId, floor) => api.get(`/rooms/block/${blockId}/floor/${floor}`);
export const getVacantRooms = () => api.get('/rooms/vacant');
export const getVacantRoomsByBlock = (blockId) => api.get(`/rooms/vacant/block/${blockId}`);
export const createRoom = (data) => api.post('/rooms', data);
export const updateRoom = (id, data) => api.put(`/rooms/${id}`, data);
export const updateRoomStatus = (id, status) => api.patch(`/rooms/${id}/status`, null, { params: { status } });
export const deleteRoom = (id) => api.delete(`/rooms/${id}`);
