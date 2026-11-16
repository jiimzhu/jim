import http from './http.js';

export function pageUsers(params) {
  return http.get('/users', { params });
}

export function createUser(data) {
  return http.post('/users', data);
}

export function updateUser(id, data) {
  return http.put(`/users/${id}`, data);
}

export function changeUserStatus(id, status) {
  return http.patch(`/users/${id}/status`, { status });
}

export function deleteUser(id) {
  return http.delete(`/users/${id}`);
}
