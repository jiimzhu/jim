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
  Col,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Progress,
  Row,
  Select,
  Space,
  Statistic,
  Switch,
  Table,
  Tag,
  message
} from 'antd';
import { useCallback, useEffect, useState } from 'react';
import {
  changeWarehouseStatus,
  createWarehouse,
  deleteWarehouse,
  fetchWarehouseStats,
  pageWarehouses,
  updateWarehouse
} from '../api/warehouse.js';
import { useAuth } from '../context/AuthContext.jsx';

const STATUS_OPTIONS = [
  { label: '启用', value: 1 },
  { label: '停用', value: 0 }
];

export default function WarehouseList() {
  const { isAdmin } = useAuth();
  const [form] = Form.useForm();
  const [query, setQuery] = useState({ page: 1, size: 10, keyword: '', status: undefined });
  const [keyword, setKeyword] = useState('');
  const [rows, setRows] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [statusLoadingId, setStatusLoadingId] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await pageWarehouses({
        page: query.page,
        size: query.size,
        keyword: query.keyword || undefined,
        status: query.status
      });
      setRows(data?.list || []);
      setTotal(data?.total || 0);
      const statData = await fetchWarehouseStats();
      setStats(statData);
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
    form.setFieldsValue({ status: 1, capacity: 1000, usedCapacity: 0 });
    setModalOpen(true);
  };

  const openEdit = (record) => {
    setEditing(record);
    form.resetFields();
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      const values = await form.validateFields();
      if (editing) {
        await updateWarehouse(editing.id, values);
        message.success('修改成功');
      } else {
        await createWarehouse(values);
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
      await changeWarehouseStatus(record.id, checked ? 1 : 0);
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
      await deleteWarehouse(record.id);
      message.success('删除成功');
      load();
    } catch (error) {
      // 已提示
    }
  };

  const columns = [
    { title: '仓库编码', dataIndex: 'code', width: 140 },
    { title: '仓库名称', dataIndex: 'name', width: 180 },
    { title: '地址', dataIndex: 'address', ellipsis: true },
    { title: '负责人', dataIndex: 'manager', width: 100 },
    { title: '联系电话', dataIndex: 'phone', width: 130 },
    {
      title: '容量使用',
      key: 'capacity',
      width: 190,
      render: (_, record) => {
        const percent = record.capacity > 0
          ? Math.round((record.usedCapacity / record.capacity) * 100)
          : 0;
        return (
          <div>
            <Progress
              percent={percent}
              size="small"
              status={percent >= 95 ? 'exception' : 'normal'}
            />
            <span style={{ color: '#8c8c8c', fontSize: 12 }}>
              {record.usedCapacity} / {record.capacity} 托位
            </span>
          </div>
        );
      }
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
          disabled={!isAdmin}
          loading={statusLoadingId === record.id}
          onChange={(checked) => handleStatus(record, checked)}
        />
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 160,
      render: (_, record) => (
        <Space size={0}>
          <Button
            type="link"
            icon={<EditOutlined />}
            disabled={!isAdmin}
            onClick={() => openEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="删除仓库"
            description={`确认删除「${record.name}」吗？删除后不可恢复。`}
            okText="删除"
            okButtonProps={{ danger: true }}
            cancelText="取消"
            disabled={!isAdmin}
            onConfirm={() => handleDelete(record)}
          >
            <Button type="link" danger icon={<DeleteOutlined />} disabled={!isAdmin}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <>
      <Row gutter={[16, 16]} className="stat-row">
        <Col xs={12} md={6}>
          <Card className="page-card">
            <Statistic title="仓库总数" value={stats?.total ?? 0} suffix="个" />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card className="page-card">
            <Statistic
              title="启用仓库"
              value={stats?.enabled ?? 0}
              suffix="个"
              valueStyle={{ color: '#389e0d' }}
            />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card className="page-card">
            <Statistic title="总容量" value={stats?.totalCapacity ?? 0} suffix="托位" />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card className="page-card">
            <Statistic
              title="整体使用率"
              value={stats?.usageRate ?? 0}
              precision={2}
              suffix="%"
              valueStyle={{ color: (stats?.usageRate ?? 0) >= 90 ? '#cf1322' : '#1677ff' }}
            />
          </Card>
        </Col>
      </Row>

      <Card
        className="page-card"
        title="仓库管理"
        extra={
          isAdmin ? (
            <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
              新增仓库
            </Button>
          ) : (
            <Tag color="default">普通用户仅可查看</Tag>
          )
        }
      >
        <div className="toolbar">
          <div className="toolbar-left">
            <Input
              allowClear
              style={{ width: 240 }}
              placeholder="按编码、名称、负责人搜索"
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
          scroll={{ x: 1200 }}
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
          title={editing ? '编辑仓库' : '新增仓库'}
          okText="保存"
          cancelText="取消"
          confirmLoading={submitting}
          onOk={handleSubmit}
          onCancel={() => setModalOpen(false)}
          forceRender
          width={600}
        >
          <Form form={form} layout="vertical" style={{ marginTop: 12 }}>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="code"
                  label="仓库编码"
                  rules={[
                    { required: true, message: '请输入仓库编码' },
                    { pattern: /^[A-Za-z0-9-]+$/, message: '只能包含字母、数字和短横线' }
                  ]}
                >
                  <Input placeholder="例如 WH-SH-003" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="name"
                  label="仓库名称"
                  rules={[{ required: true, message: '请输入仓库名称' }]}
                >
                  <Input placeholder="请输入仓库名称" />
                </Form.Item>
              </Col>
            </Row>

            <Form.Item name="address" label="仓库地址">
              <Input placeholder="请输入详细地址" />
            </Form.Item>

            <Row gutter={16}>
              <Col span={12}>
                <Form.Item name="manager" label="负责人">
                  <Input placeholder="请输入负责人姓名" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="phone"
                  label="联系电话"
                  rules={[{ pattern: /^$|^1[3-9]\d{9}$/, message: '手机号格式不正确' }]}
                >
                  <Input placeholder="请输入 11 位手机号" />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="capacity"
                  label="总容量（托位）"
                  rules={[{ required: true, message: '请输入总容量' }]}
                >
                  <InputNumber min={0} style={{ width: '100%' }} placeholder="请输入总容量" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item name="usedCapacity" label="已用容量（托位）">
                  <InputNumber min={0} style={{ width: '100%' }} placeholder="请输入已用容量" />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={16}>
              <Col span={12}>
                <Form.Item name="status" label="状态">
                  <Select options={STATUS_OPTIONS} />
                </Form.Item>
              </Col>
            </Row>

            <Form.Item name="remark" label="备注">
              <Input.TextArea rows={3} placeholder="补充说明，选填" maxLength={500} showCount />
            </Form.Item>
          </Form>
        </Modal>
      </Card>
    </>
  );
}
