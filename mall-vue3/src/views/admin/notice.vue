<!--
/**
 * 平台公告管理组件
 * 
 * 该组件实现了商城平台公告的管理功能，包括：
 * 1. 公告列表的展示
 * 2. 公告的增删改查
 * 3. 批量删除功能
 * 4. 表单验证和提交
 * 
 * 组件依赖：
 * - Ant Design Vue 组件库
 * - Vue Composition API
 * - 公告管理相关API
 * - 图标组件
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <div>
    <!--页面区域：包含页面标题、操作按钮和公告列表-->
    <div class="page-view">
      <!--页面标题区域-->
      <div class="header-section">
        <h2 class="page-title">平台公告</h2>
        <p class="page-description">管理商城平台的通知公告信息</p>
      </div>
      
      <!--表格操作按钮区域-->
      <div class="table-operations">
        <a-space>
          <!--新增公告按钮-->
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            新增公告
          </a-button>
          <!--批量删除按钮-->
          <a-button @click="handleBatchDelete">
            <template #icon><delete-outlined /></template>
            批量删除
          </a-button>
        </a-space>
      </div>
      
      <!--公告列表表格区域-->
      <a-card class="table-card">
        <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.noticeList"
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
            <!--公告标题列-->
            <template v-if="column.key === 'title'">
              <span class="notice-title">{{ text }}</span>
            </template>
            <!--公告内容列：限制显示长度，超出部分显示省略号-->
            <template v-if="column.key === 'content'">
              <div class="notice-content">
                {{ text?.substring(0, 40) }}{{ text?.length > 40 ? '...' : '' }}
              </div>
            </template>
            <!--发布时间列：格式化时间戳-->
            <template v-if="column.key === 'createTime'">
              <span class="create-time">
                {{ text ? new Date(parseInt(text)).toLocaleString() : '--' }}
              </span>
            </template>
            <!--操作列：编辑和删除按钮-->
            <template v-if="column.key === 'operation'">
              <a-space>
                <a-button type="link" size="small" @click="handleEdit(record)">
                  <template #icon><edit-outlined /></template>
                  编辑
                </a-button>
                <a-divider type="vertical" />
                <a-popconfirm title="确定删除此公告?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
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

    <!--弹窗区域：用于新增和编辑公告-->
    <div>
      <a-modal
        :visible="modal.visile"
        :forceRender="true"
        :title="modal.title"
        ok-text="确认"
        cancel-text="取消"
        @cancel="handleCancel"
        @ok="handleOk"
        width="700px"
        :maskClosable="false"
      >
        <div>
          <!--公告表单-->
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <!--公告标题输入-->
              <a-col :span="24">
                <a-form-item label="公告标题" name="title">
                  <a-input 
                    placeholder="请输入公告标题" 
                    v-model:value="modal.form.title"
                    :maxLength="100"
                  />
                </a-form-item>
              </a-col>
              <!--公告内容输入-->
              <a-col :span="24">
                <a-form-item label="公告内容" name="content">
                  <a-textarea 
                    placeholder="请输入公告内容，支持多行文本" 
                    :rows="8" 
                    v-model:value="modal.form.content"
                    :maxLength="2000"
                    show-count
                    class="notice-textarea"
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
 * 引入必要的依赖
 * - Ant Design Vue：用于UI组件
 * - 公告管理相关API
 * - 图标组件
 * - Vue Composition API
 */
import { FormInstance, message } from 'ant-design-vue';
import { createApi, listApi, updateApi, deleteApi } from '/@/api/notice';
import { 
  PlusOutlined, 
  DeleteOutlined, 
  EditOutlined, 
  NotificationOutlined 
} from '@ant-design/icons-vue';
import { ref, reactive, onMounted } from 'vue';

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
    title: '公告标题',
    dataIndex: 'title',
    key: 'title',
    align: 'left',
    ellipsis: true
  },
  {
    title: '公告内容',
    dataIndex: 'content',
    key: 'content',
    align: 'left',
    width: '40%',
  },
  {
    title: '发布时间',
    dataIndex: 'createTime',
    key: 'createTime',
    align: 'center',
    width: 180,
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'operation',
    align: 'center',
    fixed: 'right',
    width: 180,
  },
]);

/**
 * 页面数据状态
 * 包含公告列表、加载状态、分页信息等
 */
const data = reactive({
  noticeList: [],
  loading: false,
  keyword: '',
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
});

/**
 * 弹窗数据状态
 * 包含表单数据、验证规则等
 */
const modal = reactive({
  visile: false,
  editFlag: false,
  title: '',
  form: {
    id: undefined,
    title: undefined,
    content: undefined,
  },
  rules: {
    title: [{ required: true, message: '请输入公告标题', trigger: 'change' }],
    content: [{ required: true, message: '请输入公告内容', trigger: 'change' }],
  },
});

/**
 * 表单实例引用
 */
const myform = ref<FormInstance>();

/**
 * 组件挂载后的初始化操作
 */
onMounted(() => {
  getDataList();
});

/**
 * 获取公告列表数据
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
const getDataList = () => {
  data.loading = true;
  listApi({
    keyword: data.keyword,
  })
    .then((res) => {
      data.loading = false;
      console.log(res);
      // 处理列表数据，添加序号
      res.data.forEach((item: any, index: any) => {
        item.index = index + 1;
      });
      data.noticeList = res.data;
    })
    .catch((err) => {
      data.loading = false;
      console.log(err);
    });
};

/**
 * 表格行选择配置
 */
const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[], selectedRows: any[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    data.selectedRowKeys = selectedRowKeys;
  },
});

/**
 * 处理新增操作
 * 打开新增公告弹窗并重置表单
 */
const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增公告';
  // 重置表单数据
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
};

/**
 * 处理编辑操作
 * 
 * @param {Object} record - 当前行数据
 */
const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑公告';
  // 重置表单数据
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  // 复制记录数据到表单
  for (const key in record) {
    modal.form[key] = record[key];
  }
};

/**
 * 确认删除操作
 * 
 * @param {Object} record - 要删除的记录
 * @throws {Error} 当删除失败时抛出错误
 */
const confirmDelete = (record: any) => {
  console.log('delete', record);
  deleteApi({ ids: record.id })
    .then((res) => {
      message.success('删除成功');
      getDataList();
    })
    .catch((err) => {
      message.error(err.msg || '删除失败');
    });
};

/**
 * 处理批量删除操作
 * 
 * @throws {Error} 当删除失败时抛出错误
 */
const handleBatchDelete = () => {
  console.log(data.selectedRowKeys);
  // 判断是否选择了要删除的项
  if (data.selectedRowKeys.length <= 0) {
    message.warn('请勾选要删除的公告');
    return;
  }
  // 批量删除接口调用
  deleteApi({ ids: data.selectedRowKeys.join(',') })
    .then((res) => {
      message.success('批量删除成功');
      data.selectedRowKeys = [];
      getDataList();
    })
    .catch((err) => {
      message.error(err.msg || '删除失败');
    });
};

/**
 * 处理表单提交
 * 
 * @throws {Error} 当表单验证失败或提交失败时抛出错误
 */
const handleOk = () => {
  myform.value
    ?.validate()
    .then(() => {
      // 根据编辑标志判断是更新还是创建
      if (modal.editFlag) {
        updateApi(modal.form)
          .then((res) => {
            message.success('更新成功');
            hideModal();
            getDataList();
          })
          .catch((err) => {
            console.log(err);
            message.error(err.msg || '操作失败');
          });
      } else {
        createApi(modal.form)
          .then((res) => {
            message.success('创建成功');
            hideModal();
            getDataList();
          })
          .catch((err) => {
            console.log(err);
            message.error(err.msg || '操作失败');
          });
      }
    })
    .catch((err) => {
      console.log('表单验证失败');
    });
};

/**
 * 处理取消操作
 */
const handleCancel = () => {
  hideModal();
};

/**
 * 重置表单状态
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
 * 页面视图样式
 * 设置整体布局和背景
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
 * 表格操作按钮区域样式
 */
.table-operations {
  margin-bottom: 16px;
  text-align: right;
  
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
 */
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

/**
 * 公告标题样式
 */
.notice-title {
  font-weight: 500;
  color: #333;
}

/**
 * 公告内容样式
 */
.notice-content {
  color: #666;
  line-height: 1.5;
}

/**
 * 发布时间样式
 */
.create-time {
  color: #999;
  font-size: 13px;
}

/**
 * 公告文本域样式
 */
.notice-textarea {
  :deep(.ant-input) {
    font-size: 14px;
    line-height: 1.6;
  }
  
  :deep(.ant-input-data-count) {
    color: #999;
    font-size: 12px;
  }
}
</style>
