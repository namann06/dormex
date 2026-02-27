import api from './axios';

export const getBlocks = () => api.get('/blocks');
export const getActiveBlocks = () => api.get('/blocks/active');
export const getBlock = (id) => api.get(`/blocks/${id}`);
export const createBlock = (data) => api.post('/blocks', data);
export const updateBlock = (id, data) => api.put(`/blocks/${id}`, data);
export const toggleBlockStatus = (id) => api.patch(`/blocks/${id}/toggle-status`);
export const deleteBlock = (id) => api.delete(`/blocks/${id}`);
