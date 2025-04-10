<!--
/**
 * 用户管理组件
 * 
 * 该组件实现了电商平台用户的管理功能，包括：
 * 1. 用户列表的展示和检索
 * 2. 用户的增删改查操作
 * 3. 用户角色和状态管理
 * 4. 批量删除功能
 * 
 * 组件依赖：
 * - Vue Composition API
 * - Ant Design Vue 组件库
 * - 用户管理API服务
 * - 时间格式化工具
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <div>
    <!--页面区域：包含页面标题、操作按钮和用户列表-->
    <div class="page-view">
      <!--页面标题区域-->
      <div class="header-section">
        <h2 class="page-title">客户管理</h2>
        <p class="page-description">管理系统用户信息、权限与状态</p>
      </div>
      
      <!--表格操作按钮区域-->
      <div class="table-operations">
        <a-space>
          <!--新增用户按钮-->
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            新增用户
          </a-button>
          <!--批量删除按钮-->
          <a-button @click="handleBatchDelete">
            <template #icon><delete-outlined /></template>
            批量删除
          </a-button>
          <!--用户搜索框-->
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
      
      <!--用户列表表格区域-->
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
          <!--自定义表格单元格渲染-->
          <template #bodyCell="{ text, record, index, column }">
            <!--角色列：显示角色标签-->
            <template v-if="column.key === 'role'">
              <a-tag :color="getRoleColor(text)">
                <span v-if="text === '1'">普通用户</span>
                <span v-if="text === '2'">演示帐号</span>
                <span v-if="text === '3'">管理员</span>
              </a-tag>
            </template>
            <!--状态列：显示状态标签-->
            <template v-if="column.key === 'status'">
              <a-badge :status="text === '0' ? 'success' : 'error'" :text="text === '0' ? '正常' : '封号'" />
            </template>
            <!--操作列：编辑和删除按钮-->
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

    <!--弹窗区域：用于新增和编辑用户-->
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
          <!--用户表单-->
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <!--用户名输入框-->
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
              <!--密码输入框（仅在新增时显示）-->
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
              <!--昵称输入框-->
              <a-col :span="12">
                <a-form-item label="昵称" name="nickname">
                  <a-input 
                    placeholder="请输入昵称" 
                    v-model:value="modal.form.nickname" 
                    allowClear 
                  />
                </a-form-item>
              </a-col>
              <!--角色选择框-->
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
              <!--状态选择框-->
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
              <!--邮箱输入框-->
              <a-col :span="12">
                <a-form-item label="邮箱" name="email">
                  <a-input 
                    placeholder="请输入邮箱地址" 
                    v-model:value="modal.form.email" 
                    allowClear 
                  />
                </a-form-item>
              </a-col>
              <!--手机号输入框-->
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
/**
 * 导入必要的依赖和组件
 * - Ant Design Vue：用于UI组件和表单验证
 * - API服务：用于与后端交互
 * - 工具函数：用于时间格式化
 * - Vue Composition API：响应式编程
 */
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

/**
 * 表格列配置
 * 定义表格的列结构、标题、数据字段和渲染方式
 */
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

/**
 * 获取角色对应的标签颜色
 * 
 * @param {string} role - 角色标识
 * @return {string} 对应的颜色值
 */
const getRoleColor = (role) => {
  switch (role) {
    case '1': return '#5a7be0'; // 普通用户
    case '2': return '#52c41a'; // 演示帐号
    case '3': return '#fa541c'; // 管理员
    default: return 'default';
  }
};

/**
 * 页面数据状态
 * 包含用户列表、加载状态、筛选条件、分页信息等
 */
const data = reactive({
  userList: [],          // 用户列表数据
  loading: false,        // 加载状态
  currentAdminUserName: '', // 当前管理员用户名
  keyword: '',           // 搜索关键词
  selectedRowKeys: [] as any[], // 选中行的键值
  pageSize: 10,          // 每页显示条数
  page: 1,               // 当前页码
});

/**
 * 弹窗数据源
 * 包含表单数据、验证规则、角色选项等
 */
const modal = reactive({
  visile: false,         // 模态框显示状态
  editFlag: false,       // 是否为编辑模式
  title: '',             // 模态框标题
  roleData: [            // 角色选项数据
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
  form: {                // 表单数据
    id: undefined,
    username: undefined,
    password: undefined,
    role: undefined,
    status: undefined,
    nickname: undefined,
    email: undefined,
    mobile: undefined,
  },
  rules: {               // 表单验证规则
    username: [{ required: true, message: '请输入用户名', trigger: 'change' }],
    password: [{ required: true, message: '请输入密码', trigger: 'change' }],
    role: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
    status: [{ required: true, message: '请选择用户状态', trigger: 'change' }],
  },
});

/**
 * 表单实例引用
 */
const myform = ref<FormInstance>();

/**
 * 组件挂载时的初始化操作
 * 获取用户列表数据
 */
onMounted(() => {
  getUserList();
});

/**
 * 获取用户列表数据
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
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

/**
 * 搜索关键词变更处理
 * 
 * @param {Event} e - 输入事件对象
 */
const onSearchChange = (e: Event) => {
  data.keyword = e?.target?.value;
  console.log(data.keyword);
};

/**
 * 触发搜索操作
 * 根据关键词搜索用户
 */
const onSearch = () => {
  getUserList();
};

/**
 * 表格行选择配置
 * 处理表格行选择状态变化
 */
const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[], selectedRows: DataItem[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    data.selectedRowKeys = selectedRowKeys;
  },
});

/**
 * 处理新增用户操作
 * 打开新增用户的表单弹窗
 */
const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增用户';
  // 重置表单字段
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
};

/**
 * 处理编辑用户操作
 * 
 * @param {Object} record - 要编辑的用户数据记录
 */
const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑用户信息';
  // 重置表单字段
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  // 填充当前数据
  for (const key in record) {
    modal.form[key] = record[key];
  }
};

/**
 * 确认删除用户
 * 
 * @param {Object} record - 要删除的用户数据记录
 * @throws {Error} 当删除失败时抛出错误
 */
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

/**
 * 处理批量删除操作
 * 
 * @throws {Error} 当删除失败时抛出错误
 */
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

/**
 * 处理表单提交
 * 验证并提交表单数据，创建或更新用户
 * 
 * @throws {Error} 当表单验证失败或提交失败时抛出错误
 */
const handleOk = () => {
  myform.value
    ?.validate()
    .then(() => {
      const formData = new FormData();
      // 添加表单数据到FormData对象
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
      // 根据编辑模式调用不同的API
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

/**
 * 处理取消操作
 * 关闭弹窗并重置状态
 */
const handleCancel = () => {
  hideModal();
};

/**
 * 重置表单状态
 * 清空表单数据
 */
const resetModal = () => {
  myform.value?.resetFields();
};

/**
 * 关闭弹窗
 */
const hideModal = () => {
  modal.visile = false;
};
</script>

<style scoped lang="less">
/**
 * 页面整体样式
 * 设置页面布局、背景颜色和内边距
 */
.page-view {
  min-height: 100%;
  background: #f0f2fa;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/**
 * 页面标题区域样式
 * 设置标题和描述文字的样式与间距
 */
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

/**
 * 表格操作区域样式
 * 设置按钮的布局和样式
 */
.table-operations {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  
  /**
   * 主按钮样式
   */
  :deep(.ant-btn-primary) {
    background-color: #5a7be0;
    border-color: #5a7be0;
    
    &:hover {
      background-color: #6e89e8;
      border-color: #6e89e8;
    }
  }
}

/**
 * 表格卡片样式
 * 设置表格的圆角、阴影和表头样式
 */
.table-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
  
  /**
   * 表格头部样式
   */
  :deep(.ant-table-thead > tr > th) {
    background-color: #f5f7ff;
    color: #333;
    font-weight: 500;
  }
  
  /**
   * 分页器激活项样式
   */
  :deep(.ant-pagination-item-active) {
    border-color: #5a7be0;
    
    a {
      color: #5a7be0;
    }
  }
  
  /**
   * 表格行悬浮样式
   */
  :deep(.ant-table-row:hover) {
    td {
      background-color: #f5f7ff !important;
    }
  }
}
</style>
