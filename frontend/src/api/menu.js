import api from './axios';

export const getTodayMenu = () => api.get('/menu/today');
export const getWeeklyMenu = () => api.get('/menu/weekly');
export const getMenuByDay = (day) => api.get(`/menu/day/${day}`);
export const getMenuByDayAndMeal = (day, meal) => api.get(`/menu/day/${day}/meal/${meal}`);
export const getMenu = (id) => api.get(`/menu/${id}`);
export const createMenu = (data) => api.post('/menu', data);
export const updateMenu = (id, data) => api.put(`/menu/${id}`, data);
export const updateMenuByDayAndMeal = (day, meal, data) => api.put(`/menu/day/${day}/meal/${meal}`, data);
export const deleteMenu = (id) => api.delete(`/menu/${id}`);
