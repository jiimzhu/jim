import { createContext, useContext, useState } from 'react';
import { fetchMe, login as loginApi } from '../api/auth.js';

const AuthContext = createContext(null);

function readCachedUser() {
  try {
    return JSON.parse(localStorage.getItem('wms_user') || 'null');
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readCachedUser);

  const login = async (values) => {
    const data = await loginApi(values);
    localStorage.setItem('wms_token', data.token);
    localStorage.setItem('wms_user', JSON.stringify(data.user));
    setUser(data.user);
    return data;
  };

  const logout = () => {
    localStorage.removeItem('wms_token');
    localStorage.removeItem('wms_user');
    setUser(null);
  };

  /** 页面刷新后同步一次服务端用户信息，防止本地缓存过期 */
  const refreshMe = async () => {
    const data = await fetchMe();
    localStorage.setItem('wms_user', JSON.stringify(data));
    setUser(data);
    return data;
  };

  return (
    <AuthContext.Provider
      value={{ user, login, logout, refreshMe, isAdmin: user?.role === 'ADMIN' }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth 必须在 AuthProvider 内使用');
  }
  return context;
}
