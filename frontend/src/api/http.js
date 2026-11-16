import axios from 'axios';
import { message } from 'antd';

/**
 * 统一 http 客户端：
 * 1. 请求自动携带 JWT
 * 2. 响应自动拆解 { code, message, data } 外壳
 * 3. 401 自动清理登录态并跳转登录页
 */
const http = axios.create({
  baseURL: '/api',
  timeout: 15000
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('wms_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => {
    const body = response.data;
    if (body && typeof body.code === 'number' && body.code !== 0) {
      message.error(body.message || '请求失败');
      return Promise.reject(new Error(body.message || '请求失败'));
    }
    return body ? body.data : undefined;
  },
  (error) => {
    const status = error.response?.status;
    const text = error.response?.data?.message || error.message || '网络异常';

    if (status === 401) {
      localStorage.removeItem('wms_token');
      localStorage.removeItem('wms_user');
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login';
      }
    } else {
      message.error(text);
    }
    return Promise.reject(error);
  }
);

export default http;
