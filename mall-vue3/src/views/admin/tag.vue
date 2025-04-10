<!--
/**
 * 商品标签管理组件
 * 
 * 该组件实现了电商平台商品标签的管理功能，包括：
 * 1. 标签列表的展示和检索
 * 2. 标签的增删改查操作
 * 3. 批量删除功能
 * 
 * 组件依赖：
 * - Vue Composition API
 * - Ant Design Vue 组件库
 * - 标签管理API服务
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <div>
    <!--页面区域：包含页面标题、操作按钮和标签列表-->
    <div class="page-view">
      <!--页面标题区域-->
      <div class="header-section">
        <h2 class="page-title">商品标签</h2>
        <p class="page-description">管理商品标签，用于商品分类和筛选</p>
      </div>
      
      <!--表格操作按钮区域-->
      <div class="table-operations">
        <a-space>
          <!--新增标签按钮-->
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            新增标签
          </a-button>
          <!--批量删除按钮-->
          <a-button @click="handleBatchDelete">
            <template #icon><delete-outlined /></template>
            批量删除
          </a-button>
        </a-space>
      </div>
      
      <!--标签列表表格区域-->
      <a-card class="table-card">
        <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.tagList"
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
            <!--操作列：编辑和删除按钮-->
            <template v-if="column.key === 'operation'">
              <a-space>
                <a-button type="link" size="small" @click="handleEdit(record)">
                  <template #icon><edit-outlined /></template>
                  编辑
                </a-button>
                <a-divider type="vertical" />
                <a-popconfirm title="确定删除此标签?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
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

    <!--弹窗区域：用于新增和编辑标签-->
    <div>
      <a-modal
        :visible="modal.visile"
        :forceRender="true"
        :title="modal.title"
        ok-text="确认"
        cancel-text="取消"
        @cancel="handleCancel"
        @ok="handleOk"
        width="500px"
        :maskClosable="false"
      >
        <div>
          <!--标签表单-->
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item label="标签名称" name="title">
                  <a-input 
                    placeholder="请输入标签名称" 
                    v-model:value="modal.form.title"
                    :maxLength="20"
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
 * - Vue Composition API：响应式编程
 */
import { FormInstance, message } from 'ant-design-vue';
import { createApi, listApi, updateApi, deleteApi } from '/@/api/tag';
import { 
  PlusOutlined, 
  DeleteOutlined, 
  EditOutlined
} from '@ant-design/icons-vue';

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
    title: '标签名称',
    dataIndex: 'title',
    key: 'title',
    align: 'center'
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
 * 包含标签列表、加载状态、筛选条件、分页信息等
 */
const data = reactive({
  tagList: [],          // 标签列表数据
  loading: false,       // 加载状态
  keyword: '',          // 搜索关键词
  selectedRowKeys: [] as any[], // 选中行的键值
  pageSize: 10,         // 每页显示条数
  page: 1,              // 当前页码
});

/**
 * 弹窗数据源
 * 包含表单数据、验证规则等
 */
const modal = reactive({
  visile: false,        // 模态框显示状态
  editFlag: false,      // 是否为编辑模式
  title: '',            // 模态框标题
  form: {               // 表单数据
    id: undefined,
    title: undefined,
  },
  rules: {              // 表单验证规则
    title: [{ required: true, message: '请输入标签名称', trigger: 'change' }],
  },
});

/**
 * 表单实例引用
 */
const myform = ref<FormInstance>();

/**
 * 组件挂载时的初始化操作
 * 获取标签列表数据
 */
onMounted(() => {
  getDataList();
});

/**
 * 获取标签列表数据
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
      res.data.forEach((item: any, index: any) => {
        item.index = index + 1;
      });
      data.tagList = res.data;
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
 * 根据关键词搜索标签
 */
const onSearch = () => {
  getDataList();
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
 * 处理新增标签操作
 * 打开新增标签的表单弹窗
 */
const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增标签';
  // 重置表单字段
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
};

/**
 * 处理编辑标签操作
 * 
 * @param {Object} record - 要编辑的标签数据记录
 */
const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑标签';
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
 * 确认删除标签
 * 
 * @param {Object} record - 要删除的标签数据记录
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
  if (data.selectedRowKeys.length <= 0) {
    console.log('hello');
    message.warn('请勾选要删除的标签');
    return;
  }
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
 * 验证并提交表单数据，创建或更新标签
 * 
 * @throws {Error} 当表单验证失败或提交失败时抛出错误
 */
const handleOk = () => {
  myform.value
    ?.validate()
    .then(() => {
      if (modal.editFlag) {
        updateApi({ id: modal.form.id }, modal.form)
          .then((res) => {
            message.success('更新成功');
            hideModal();
            getDataList();
          })
          .catch((err) => {
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

/**
 * 定义DataItem接口
 * 用于类型检查和代码提示
 */
interface DataItem {
  id: number | string;
  title: string;
  index?: number;
  [key: string]: any;
}
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
  
  /**
   * 表格单元格样式
   */
  :deep(.ant-table-cell) {
    padding: 12px 16px;
  }
}
</style>
