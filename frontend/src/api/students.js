import api from './axios';

export const getStudents = () => api.get('/students');
export const getStudent = (id) => api.get(`/students/${id}`);
export const getStudentByRoll = (roll) => api.get(`/students/roll/${roll}`);
export const searchStudents = (keyword) => api.get('/students/search', { params: { keyword } });
export const getStudentsByStatus = (status) => api.get(`/students/status/${status}`);
export const createStudent = (data) => api.post('/students', data);
export const updateStudent = (id, data) => api.put(`/students/${id}`, data);
export const updateStudentStatus = (id, status) => api.patch(`/students/${id}/status`, null, { params: { status } });
export const assignRoom = (id, roomId) => api.patch(`/students/${id}/room`, null, { params: { roomId } });
export const deleteStudent = (id) => api.delete(`/students/${id}`);
