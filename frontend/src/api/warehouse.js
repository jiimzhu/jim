import http from './http.js';

export function pageWarehouses(params) {
  return http.get('/warehouses', { params });
}

export function fetchWarehouseStats() {
  return http.get('/warehouses/stats');
}

export function createWarehouse(data) {
  return http.post('/warehouses', data);
}

export function updateWarehouse(id, data) {
  return http.put(`/warehouses/${id}`, data);
}

export function changeWarehouseStatus(id, status) {
  return http.patch(`/warehouses/${id}/status`, { status });
}

export function deleteWarehouse(id) {
  return http.delete(`/warehouses/${id}`);
}
