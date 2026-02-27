import api from './axios';

export const getDashboardStats = () => api.get('/dashboard/stats');
export const getRecentActivity = (limit = 5) => api.get('/dashboard/recent', { params: { limit } });
export const getBlockOccupancy = () => api.get('/dashboard/block-occupancy');
export const getComplaintSummary = () => api.get('/dashboard/complaint-summary');
