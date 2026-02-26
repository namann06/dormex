import api from './axios';

export const login = (data) => api.post('/auth/login', data);
export const register = (data) => api.post('/auth/register', data);
export const registerAdmin = (data) => api.post('/auth/register/admin', data);
export const refreshToken = (data) => api.post('/auth/refresh', data);
export const getMe = () => api.get('/users/me');
