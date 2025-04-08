<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="header-section">
        <h2 class="page-title">客户管理</h2>
        <p class="page-description">管理系统用户信息、权限与状态</p>
      </div>
      
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            新增用户
          </a-button>
          <a-button @click="handleBatchDelete">
            <template #icon><delete-outlined /></template>
            批量删除
          </a-button>
          <a-input-search 
            addon-before="用户名" 
            placeholder="搜索用户" 
            enter-button 
            @search="onSearch" 
            @change="onSearchChange"
            style="width: 280px" 
          />
        </a-space>
      </div>
      
      <a-card class="table-card">
        <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.userList"
          :scroll="{ x: 'max-content' }"
          :row-selection="rowSelection"
          :pagination="{
            size: 'default',
            current: data.page,
            pageSize: data.pageSize,
            onChange: (current) => (data.page = current),
            showSizeChanger: false,
            showTotal: (total) => `共${total}条数据`,
          }"
        >
          <template #bodyCell="{ text, record, index, column }">
            <template v-if="column.key === 'role'">
              <a-tag :color="getRoleColor(text)">
                <span v-if="text === '1'">普通用户</span>
                <span v-if="text === '2'">演示帐号</span>
                <span v-if="text === '3'">管理员</span>
              </a-tag>
            </template>
            <template v-if="column.key === 'status'">
              <a-badge :status="text === '0' ? 'success' : 'error'" :text="text === '0' ? '正常' : '封号'" />
            </template>
            <template v-if="column.key === 'operation'">
              <a-space>
                <a-button type="link" size="small" @click="handleEdit(record)">
                  <template #icon><edit-outlined /></template>
                  编辑
                </a-button>
                <a-divider type="vertical" />
                <a-popconfirm 
                  title="确定删除此用户?" 
                  ok-text="是" 
                  cancel-text="否" 
                  @confirm="confirmDelete(record)"
                >
                  <a-button type="link" danger size="small">
                    <template #icon><delete-outlined /></template>
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>

    <!--弹窗区域-->
    <div>
      <a-modal
        :visible="modal.visile"
        :forceRender="true"
        :title="modal.title"
        ok-text="确认"
        cancel-text="取消"
        width="600px"
        :maskClosable="false"
        @cancel="handleCancel"
        @ok="handleOk"
      >
        <div>
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="用户名" name="username">
                  <a-input 
                    :disabled="modal.editFlag" 
                    placeholder="请输入用户名" 
                    v-model:value="modal.form.username"
                    allowClear 
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12" v-if="!modal.editFlag">
                <a-form-item label="密码" name="password">
                  <a-input 
                    placeholder="请输入密码" 
                    type="password" 
                    v-model:value="modal.form.password" 
                    allowClear 
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="昵称" name="nickname">
                  <a-input 
                    placeholder="请输入昵称" 
                    v-model:value="modal.form.nickname" 
                    allowClear 
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="角色" name="role">
                  <a-select 
                    placeholder="请选择角色" 
                    allowClear 
                    v-model:value="modal.form.role"
                  >
                    <template v-for="item in modal.roleData">
                      <a-select-option :value="item.id">{{ item.title }}</a-select-option>
                    </template>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="状态" name="status">
                  <a-select 
                    placeholder="请选择状态" 
                    allowClear 
                    v-model:value="modal.form.status"
                  >
                    <a-select-option key="0" value="0">正常</a-select-option>
                    <a-select-option key="1" value="1">封号</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱" name="email">
                  <a-input 
                    placeholder="请输入邮箱地址" 
                    v-model:value="modal.form.email" 
                    allowClear 
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="手机号" name="mobile">
                  <a-input 
                    placeholder="请输入手机号码" 
                    v-model:value="modal.form.mobile" 
                    allowClear 
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
      </a-modal>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { FormInstance, message } from 'ant-design-vue';
  import { createApi, listApi, updateApi, deleteApi } from '/@/api/user';
  import { getFormatTime } from "/@/utils";
  import { 
    EditOutlined, 
    DeleteOutlined, 
    PlusOutlined, 
    UserOutlined, 
    PhoneOutlined,
    MailOutlined
  } from '@ant-design/icons-vue';
  import { reactive, ref, onMounted } from 'vue';

  const columns = reactive([
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      align: 'center',
      width: 80
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      align: 'center',
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      key: 'nickname',
      align: 'center',
    },
    {
      title: '角色',
      dataIndex: 'role',
      key: 'role',
      align: 'center',
      width: 100
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      align: 'center',
      width: 100
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      align: 'center',
    },
    {
      title: '手机号',
      dataIndex: 'mobile',
      key: 'mobile',
      align: 'center',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      align: 'center',
      customRender: ({text}) => getFormatTime(text, true)
    },
    {
      title: '操作',
      dataIndex: 'action',
      key: 'operation',
      align: 'center',
      fixed: 'right',
      width: 150,
    },
  ]);

  // 获取角色对应的标签颜色
  const getRoleColor = (role) => {
    switch (role) {
      case '1': return '#5a7be0'; // 普通用户
      case '2': return '#52c41a'; // 演示帐号
      case '3': return '#fa541c'; // 管理员
      default: return 'default';
    }
  };

  // 页面数据
  const data = reactive({
    userList: [],
    loading: false,
    currentAdminUserName: '',
    keyword: '',
    selectedRowKeys: [] as any[],
    pageSize: 10,
    page: 1,
  });

  // 弹窗数据源
  const modal = reactive({
    visile: false,
    editFlag: false,
    title: '',
    roleData: [
      {
        id: '1',
        title: '普通用户',
      },
      {
        id: '2',
        title: '演示帐号',
      },
      {
        id: '3',
        title: '管理员',
      },
    ],
    form: {
      id: undefined,
      username: undefined,
      password: undefined,
      role: undefined,
      status: undefined,
      nickname: undefined,
      email: undefined,
      mobile: undefined,
    },
    rules: {
      username: [{ required: true, message: '请输入用户名', trigger: 'change' }],
      password: [{ required: true, message: '请输入密码', trigger: 'change' }],
      role: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
      status: [{ required: true, message: '请选择用户状态', trigger: 'change' }],
    },
  });

  const myform = ref<FormInstance>();

  onMounted(() => {
    getUserList();
  });

  const getUserList = () => {
    data.loading = true;
    listApi({
      keyword: data.keyword,
    })
      .then((res) => {
        data.loading = false;
        console.log(res);
        res.data.forEach((item: any, index: any) => {
          item.index = index + 1;
        });
        data.userList = res.data;
      })
      .catch((err) => {
        data.loading = false;
        console.log(err);
      });
  };

  const onSearchChange = (e: Event) => {
    data.keyword = e?.target?.value;
    console.log(data.keyword);
  };

  const onSearch = () => {
    getUserList();
  };

  const rowSelection = ref({
    onChange: (selectedRowKeys: (string | number)[], selectedRows: DataItem[]) => {
      console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
      data.selectedRowKeys = selectedRowKeys;
    },
  });

  const handleAdd = () => {
    resetModal();
    modal.visile = true;
    modal.editFlag = false;
    modal.title = '新增用户';
    // 重置
    for (const key in modal.form) {
      modal.form[key] = undefined;
    }
  };
  
  const handleEdit = (record: any) => {
    resetModal();
    modal.visile = true;
    modal.editFlag = true;
    modal.title = '编辑用户信息';
    // 重置
    for (const key in modal.form) {
      modal.form[key] = undefined;
    }
    for (const key in record) {
      modal.form[key] = record[key];
    }
  };

  const confirmDelete = (record: any) => {
    console.log('delete', record);
    deleteApi({ ids: record.id })
      .then((res) => {
        message.success('删除成功');
        getUserList();
      })
      .catch((err) => {
        message.warn(err.msg || "操作失败")
      });
  };

  const handleBatchDelete = () => {
    console.log(data.selectedRowKeys);
    if (data.selectedRowKeys.length <= 0) {
      console.log('hello');
      message.warn('请勾选要删除的用户');
      return;
    }
    deleteApi({ ids: data.selectedRowKeys.join(',') })
      .then((res) => {
        message.success('批量删除成功');
        data.selectedRowKeys = [];
        getUserList();
      })
      .catch((err) => {
        message.warn(err.msg || "操作失败")
      });
  };

  const handleOk = () => {
    myform.value
      ?.validate()
      .then(() => {
        const formData = new FormData();
        if (modal.form.id) {
          formData.append('id', modal.form.id);
        }
        if (modal.form.username) {
          formData.append('username', modal.form.username);
        }
        if (modal.form.password) {
          formData.append('password', modal.form.password);
        }
        if (modal.form.nickname) {
          formData.append('nickname', modal.form.nickname);
        }
        if (modal.form.role) {
          formData.append('role', modal.form.role);
        }
        if (modal.form.status) {
          formData.append('status', modal.form.status);
        }
        if (modal.form.cover) {
          formData.append('cover', modal.form.cover);
        }
        if (modal.form.mobile) {
          formData.append('mobile', modal.form.mobile);
        }
        if (modal.form.email) {
          formData.append('email', modal.form.email);
        }
        if (modal.editFlag) {
          updateApi(formData)
            .then((res) => {
              message.success('更新成功');
              hideModal();
              getUserList();
            })
            .catch((err) => {
              console.log(err);
              message.warn(err.msg || "操作失败")
            });
        } else {
          createApi(formData)
            .then((res) => {
              message.success('创建成功');
              hideModal();
              getUserList();
            })
            .catch((err) => {
              console.log(err);
              message.warn(err.msg || "操作失败")
            });
        }
      })
      .catch((err) => {
        console.log('表单验证失败');
      });
  };

  const handleCancel = () => {
    hideModal();
  };

  // 恢复表单初始状态
  const resetModal = () => {
    myform.value?.resetFields();
  };

  // 关闭弹窗
  const hideModal = () => {
    modal.visile = false;
  };
</script>

<style scoped lang="less">
  .page-view {
    min-height: 100%;
    background: #f0f2fa;
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .header-section {
    margin-bottom: 8px;
    
    .page-title {
      margin: 0 0 8px 0;
      font-size: 24px;
      font-weight: 500;
      color: #333;
    }
    
    .page-description {
      margin: 0;
      color: #666;
      font-size: 14px;
    }
  }

  .table-operations {
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;
    
    :deep(.ant-btn-primary) {
      background-color: #5a7be0;
      border-color: #5a7be0;
      
      &:hover {
        background-color: #6e89e8;
        border-color: #6e89e8;
      }
    }
  }
  
  .table-card {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
    
    :deep(.ant-table-thead > tr > th) {
      background-color: #f5f7ff;
      color: #333;
      font-weight: 500;
    }
    
    :deep(.ant-pagination-item-active) {
      border-color: #5a7be0;
      
      a {
        color: #5a7be0;
      }
    }
    
    :deep(.ant-table-row:hover) {
      td {
        background-color: #f5f7ff !important;
      }
    }
  }
</style>
