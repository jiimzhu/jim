import {
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined
} from '@ant-design/icons';
import {
  Button,
  Card,
  Form,
  Input,
  Modal,
  Popconfirm,
  Select,
  Space,
  Switch,
  Table,
  Tag,
  message
} from 'antd';
import { useCallback, useEffect, useState } from 'react';
import {
  changeUserStatus,
  createUser,
  deleteUser,
  pageUsers,
  updateUser
} from '../api/user.js';

const ROLE_OPTIONS = [
  { label: '管理员', value: 'ADMIN' },
  { label: '普通用户', value: 'USER' }
];

const STATUS_OPTIONS = [
  { label: '启用', value: 1 },
  { label: '停用', value: 0 }
];

export default function UserList() {
  const [form] = Form.useForm();
  const [query, setQuery] = useState({ page: 1, size: 10, keyword: '', status: undefined });
  const [keyword, setKeyword] = useState('');
  const [rows, setRows] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [statusLoadingId, setStatusLoadingId] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await pageUsers({
        page: query.page,
        size: query.size,
        keyword: query.keyword || undefined,
        status: query.status
      });
      setRows(data?.list || []);
      setTotal(data?.total || 0);
    } catch (error) {
      // 错误提示已由拦截器统一处理
    } finally {
      setLoading(false);
    }
  }, [query]);

  useEffect(() => {
    load();
  }, [load]);

  const handleSearch = () => {
    setQuery((prev) => ({ ...prev, keyword: keyword.trim(), page: 1 }));
  };

  const handleReset = () => {
    setKeyword('');
    setQuery({ page: 1, size: query.size, keyword: '', status: undefined });
  };

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    form.setFieldsValue({ role: 'USER', status: 1 });
    setModalOpen(true);
  };

  const openEdit = (record) => {
    setEditing(record);
    form.resetFields();
    form.setFieldsValue({ ...record, password: '' });
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      const values = await form.validateFields();
      if (editing) {
        await updateUser(editing.id, values);
        message.success('修改成功');
      } else {
        await createUser(values);
        message.success('新增成功');
      }
      setModalOpen(false);
      if (editing) {
        load();
      } else {
        setQuery((prev) => ({ ...prev, page: 1 }));
      }
    } catch (error) {
      // 校验失败或接口报错，均已提示
    } finally {
      setSubmitting(false);
    }
  };

  const handleStatus = async (record, checked) => {
    setStatusLoadingId(record.id);
    try {
      await changeUserStatus(record.id, checked ? 1 : 0);
      message.success('状态已更新');
      load();
    } catch (error) {
      // 已提示
    } finally {
      setStatusLoadingId(null);
    }
  };

  const handleDelete = async (record) => {
    try {
      await deleteUser(record.id);
      message.success('删除成功');
      load();
    } catch (error) {
      // 已提示
    }
  };

  const columns = [
    { title: '登录账号', dataIndex: 'username', width: 150 },
    { title: '姓名', dataIndex: 'nickname', width: 110 },
    { title: '邮箱', dataIndex: 'email', ellipsis: true },
    { title: '手机号', dataIndex: 'phone', width: 130 },
    {
      title: '角色',
      dataIndex: 'role',
      width: 110,
      render: (value) => (
        <Tag color={value === 'ADMIN' ? 'gold' : 'blue'}>
          {value === 'ADMIN' ? '管理员' : '普通用户'}
        </Tag>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (value, record) => (
        <Switch
          checked={value === 1}
          checkedChildren="启用"
          unCheckedChildren="停用"
          loading={statusLoadingId === record.id}
          onChange={(checked) => handleStatus(record, checked)}
        />
      )
    },
    { title: '创建时间', dataIndex: 'createdAt', width: 170 },
    {
      title: '操作',
      key: 'action',
      width: 160,
      render: (_, record) => (
        <Space size={0}>
          <Button type="link" icon={<EditOutlined />} onClick={() => openEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="删除用户"
            description={`确认删除「${record.nickname}」吗？删除后不可恢复。`}
            okText="删除"
            okButtonProps={{ danger: true }}
            cancelText="取消"
            onConfirm={() => handleDelete(record)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <Card
      className="page-card"
      title="用户管理"
      extra={
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
          新增用户
        </Button>
      }
    >
      <div className="toolbar">
        <div className="toolbar-left">
          <Input
            allowClear
            style={{ width: 240 }}
            placeholder="按账号、姓名、邮箱搜索"
            prefix={<SearchOutlined />}
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            onPressEnter={handleSearch}
          />
          <Select
            allowClear
            style={{ width: 130 }}
            placeholder="全部状态"
            options={STATUS_OPTIONS}
            value={query.status}
            onChange={(value) => setQuery((prev) => ({ ...prev, status: value, page: 1 }))}
          />
          <Button type="primary" onClick={handleSearch}>
            查询
          </Button>
          <Button icon={<ReloadOutlined />} onClick={handleReset}>
            重置
          </Button>
        </div>
      </div>

      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={rows}
        scroll={{ x: 1100 }}
        pagination={{
          current: query.page,
          pageSize: query.size,
          total,
          showSizeChanger: true,
          showTotal: (count) => `共 ${count} 条`,
          onChange: (page, size) => setQuery((prev) => ({ ...prev, page, size }))
        }}
      />

      <Modal
        open={modalOpen}
        title={editing ? '编辑用户' : '新增用户'}
        okText="保存"
        cancelText="取消"
        confirmLoading={submitting}
        onOk={handleSubmit}
        onCancel={() => setModalOpen(false)}
        forceRender
        width={560}
      >
        <Form form={form} layout="vertical" style={{ marginTop: 12 }}>
          <Form.Item
            name="username"
            label="登录账号"
            rules={
              editing
                ? []
                : [
                    { required: true, message: '请输入登录账号' },
                    { min: 3, max: 20, message: '登录账号长度需在 3 到 20 个字符之间' },
                    { pattern: /^[a-zA-Z0-9_]+$/, message: '只能包含字母、数字和下划线' }
                  ]
            }
          >
            <Input placeholder="请输入登录账号" disabled={!!editing} />
          </Form.Item>

          <Form.Item
            name="password"
            label={editing ? '密码（留空则不修改）' : '密码'}
            rules={
              editing
                ? [{ min: 6, max: 32, message: '密码长度需在 6 到 32 个字符之间' }]
                : [
                    { required: true, message: '请输入密码' },
                    { min: 6, max: 32, message: '密码长度需在 6 到 32 个字符之间' }
                  ]
            }
          >
            <Input.Password placeholder={editing ? '不修改请留空' : '请输入初始密码'} />
          </Form.Item>

          <Form.Item
            name="nickname"
            label="姓名"
            rules={[{ required: true, message: '请输入姓名' }]}
          >
            <Input placeholder="请输入姓名" />
          </Form.Item>

          <Form.Item name="email" label="邮箱" rules={[{ type: 'email', message: '邮箱格式不正确' }]}>
            <Input placeholder="请输入邮箱" />
          </Form.Item>

          <Form.Item
            name="phone"
            label="手机号"
            rules={[{ pattern: /^$|^1[3-9]\d{9}$/, message: '手机号格式不正确' }]}
          >
            <Input placeholder="请输入 11 位手机号" />
          </Form.Item>

          <Form.Item name="role" label="角色" rules={[{ required: true, message: '请选择角色' }]}>
            <Select options={ROLE_OPTIONS} />
          </Form.Item>

          <Form.Item name="status" label="状态" rules={[{ required: true, message: '请选择状态' }]}>
            <Select options={STATUS_OPTIONS} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
