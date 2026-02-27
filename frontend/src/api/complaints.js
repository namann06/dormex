import api from './axios';

export const getComplaints = () => api.get('/complaints');
export const getMyComplaints = () => api.get('/complaints/my');
export const getComplaint = (id) => api.get(`/complaints/${id}`);
export const getComplaintsByStudent = (studentId) => api.get(`/complaints/student/${studentId}`);
export const getComplaintsByStatus = (status) => api.get(`/complaints/status/${status}`);
export const getComplaintsByCategory = (category) => api.get(`/complaints/category/${category}`);
export const getPendingComplaints = () => api.get('/complaints/pending');
export const createComplaint = (data) => api.post('/complaints', data);
export const updateComplaint = (id, data) => api.put(`/complaints/${id}`, data);
export const updateComplaintStatus = (id, status, remarks) =>
  api.patch(`/complaints/${id}/status`, null, { params: { status, remarks } });
export const deleteComplaint = (id) => api.delete(`/complaints/${id}`);
