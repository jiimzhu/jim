import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Form, Input, message } from 'antd';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values) => {
    setLoading(true);
    try {
      await login(values);
      message.success('登录成功');
      navigate('/warehouses');
    } catch (error) {
      // 错误提示已由 http 拦截器统一处理
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h1 className="login-title">仓库管理系统</h1>
        <p className="login-subtitle">Warehouse Management System</p>

        <Form
          layout="vertical"
          onFinish={onFinish}
          initialValues={{ username: 'admin', password: 'admin123' }}
          requiredMark={false}
        >
          <Form.Item
            name="username"
            label="登录账号"
            rules={[{ required: true, message: '请输入登录账号' }]}
          >
            <Input
              size="large"
              prefix={<UserOutlined />}
              placeholder="请输入登录账号"
              autoComplete="username"
            />
          </Form.Item>

          <Form.Item
            name="password"
            label="密码"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              size="large"
              prefix={<LockOutlined />}
              placeholder="请输入密码"
              autoComplete="current-password"
            />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0 }}>
            <Button type="primary" htmlType="submit" size="large" block loading={loading}>
              登录
            </Button>
          </Form.Item>
        </Form>

        <div className="login-hint">
          演示账号：管理员 admin / admin123，普通用户 zhangsan / 123456
        </div>
      </div>
    </div>
  );
}
