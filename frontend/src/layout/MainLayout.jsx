import {
  AppstoreOutlined,
  DatabaseOutlined,
  LogoutOutlined,
  TeamOutlined
} from '@ant-design/icons';
import { Avatar, Dropdown, Layout, Menu, Space, Tag } from 'antd';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

const { Header, Sider, Content } = Layout;

export default function MainLayout() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const selectedKey = location.pathname.startsWith('/users') ? '/users' : '/warehouses';

  const menuItems = [
    { key: '/warehouses', icon: <AppstoreOutlined />, label: '仓库管理' },
    ...(isAdmin ? [{ key: '/users', icon: <TeamOutlined />, label: '用户管理' }] : [])
  ];

  const userMenu = {
    items: [{ key: 'logout', icon: <LogoutOutlined />, label: '退出登录' }],
    onClick: ({ key }) => {
      if (key === 'logout') {
        logout();
        navigate('/login');
      }
    }
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header className="app-header">
        <div className="app-logo">
          <DatabaseOutlined style={{ color: '#1677ff', fontSize: 20 }} />
          <Link to="/warehouses" style={{ color: 'inherit' }}>
            仓库管理系统
          </Link>
          <Tag color="blue">WMS</Tag>
        </div>
        <Dropdown menu={userMenu} trigger={['click']}>
          <Space style={{ cursor: 'pointer' }}>
            <Avatar style={{ background: '#1677ff' }}>{user?.nickname?.slice(0, 1) || 'U'}</Avatar>
            <span>{user?.nickname}</span>
            <Tag color={isAdmin ? 'gold' : 'default'}>{isAdmin ? '管理员' : '普通用户'}</Tag>
          </Space>
        </Dropdown>
      </Header>

      <Layout>
        <Sider
          width={200}
          theme="light"
          breakpoint="lg"
          collapsedWidth={0}
        >
          <Menu
            mode="inline"
            selectedKeys={[selectedKey]}
            items={menuItems}
            onClick={({ key }) => navigate(key)}
            style={{ height: '100%', borderRight: '1px solid #f0f0f0', paddingTop: 8 }}
          />
        </Sider>
        <Content className="app-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
