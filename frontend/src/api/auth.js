import http from './http.js';

export function login(data) {
  return http.post('/auth/login', data);
}

export function fetchMe() {
  return http.get('/auth/me');
}
