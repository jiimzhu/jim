import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './context/AuthContext.jsx';
import MainLayout from './layout/MainLayout.jsx';
import Login from './pages/Login.jsx';
import UserList from './pages/UserList.jsx';
import WarehouseList from './pages/WarehouseList.jsx';

function RequireAuth({ children }) {
  const { user } = useAuth();
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

function RequireAdmin({ children }) {
  const { isAdmin } = useAuth();
  if (!isAdmin) {
    return <Navigate to="/warehouses" replace />;
  }
  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <RequireAuth>
            <MainLayout />
          </RequireAuth>
        }
      >
        <Route index element={<Navigate to="/warehouses" replace />} />
        <Route path="warehouses" element={<WarehouseList />} />
        <Route
          path="users"
          element={
            <RequireAdmin>
              <UserList />
            </RequireAdmin>
          }
        />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
