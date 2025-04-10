<!--
/**
 * 商品分类管理组件
 * 
 * 该组件实现了商品分类的管理功能，包括：
 * 1. 分类的增删改查
 * 2. 多级分类的树形展示
 * 3. 分类的层级管理
 * 4. 批量删除操作
 * 
 * 组件依赖：
 * - Ant Design Vue 组件库
 * - Vue Composition API
 * - 分类管理相关API
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <div>
    <!--页面区域：包含标题、操作按钮和分类展示-->
    <div class="page-view">
      <!--页面标题区域-->
      <div class="header-section">
        <h2 class="page-title">商品分类</h2>
        <p class="page-description">管理产品分类架构，支持多级分类设置</p>
      </div>
      
      <!--表格操作按钮区域-->
      <div class="table-operations">
        <a-space>
          <!--新增分类按钮-->
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            新增分类
          </a-button>
          <!--批量删除按钮-->
          <a-button @click="handleBatchDelete">
            <template #icon><delete-outlined /></template>
            批量删除
          </a-button>
          <!--视图切换开关-->
          <a-switch 
            v-model:checked="data.showTreeMode" 
            checked-children="树形视图" 
            un-checked-children="列表视图"
            @change="toggleViewMode"
            class="view-switch"
          />
        </a-space>
      </div>
      
      <!--树形视图：用于展示分类的层级结构-->
      <a-card class="tree-card" v-if="data.showTreeMode">
        <a-spin :spinning="data.loading">
          <a-tree
            :tree-data="data.treeData"
            :field-names="{ title: 'title', key: 'id', children: 'children' }"
            show-line
            block-node
            class="custom-tree"
          >
            <!--自定义树节点渲染-->
            <template #title="{ title, id, level }">
              <div class="tree-node">
                <span class="node-title">{{ title }}</span>
                <!--分类层级标签-->
                <a-tag :color="getLevelColor(level)" class="level-tag">{{ getLevelText(level) }}</a-tag>
                <!--节点操作按钮-->
                <span class="tree-node-actions">
                  <a-button type="link" size="small" @click.stop="handleEdit(findClassificationById(id))">
                    <template #icon><edit-outlined /></template>
                    编辑
                  </a-button>
                  <a-divider type="vertical" />
                  <a-popconfirm title="确定删除此分类及其所有子分类?" ok-text="是" cancel-text="否" @confirm.stop="confirmDelete(findClassificationById(id))">
                    <a-button type="link" danger size="small" @click.stop>
                      <template #icon><delete-outlined /></template>
                      删除
                    </a-button>
                  </a-popconfirm>
                </span>
              </div>
            </template>
          </a-tree>
        </a-spin>
      </a-card>
      
      <!--表格视图：用于展示分类的列表形式-->
      <a-card class="table-card" v-else>
        <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.classificationList"
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
            <!--操作列-->
            <template v-if="column.key === 'operation'">
              <a-space>
                <a-button type="link" size="small" @click="handleEdit(record)">
                  <template #icon><edit-outlined /></template>
                  编辑
                </a-button>
                <a-divider type="vertical" />
                <a-popconfirm title="确定删除此分类及其所有子分类?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                  <a-button type="link" danger size="small">
                    <template #icon><delete-outlined /></template>
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
            <!--层级列-->
            <template v-if="column.key === 'level'">
              <a-tag :color="getLevelColor(record.level)">{{ getLevelText(record.level) }}</a-tag>
            </template>
            <!--父级分类列-->
            <template v-if="column.key === 'parentTitle'">
              <span>{{ record.parentTitle || '无' }}</span>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>

    <!--弹窗区域：用于新增和编辑分类-->
    <div>
      <a-modal
        :visible="modal.visile"
        :forceRender="true"
        :title="modal.title"
        ok-text="确认"
        cancel-text="取消"
        @cancel="handleCancel"
        @ok="handleOk"
        width="600px"
        :maskClosable="false"
      >
        <div>
          <!--分类表单-->
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <!--分类名称输入-->
              <a-col :span="24">
                <a-form-item label="分类名称" name="title">
                  <a-input 
                    placeholder="请输入分类名称" 
                    v-model:value="modal.form.title" 
                    :maxLength="30"
                  />
                </a-form-item>
              </a-col>
              <!--父级分类选择-->
              <a-col :span="24">
                <a-form-item label="父级分类" name="parentId">
                  <a-tree-select
                    v-model:value="modal.form.parentId"
                    placeholder="请选择父级分类，不选则为一级分类"
                    style="width: 100%"
                    :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                    :tree-data="parentTreeData"
                    :field-names="{ children: 'children', label: 'title', value: 'id' }"
                    tree-default-expand-all
                    allow-clear
                    show-search
                    :tree-node-filter-prop="'title'"
                    class="parent-tree-select"
                  >
                    <!--自定义树节点标题渲染-->
                    <template #title="{ value, title, level }">
                      <span>{{ getIndentText(level) }}{{ title }}</span>
                    </template>
                  </a-tree-select>
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
 * - Vue Composition API：用于组件逻辑
 * - Ant Design Vue：用于UI组件
 * - 分类管理相关API
 */
import { computed, ref, reactive, onMounted } from 'vue';
import { FormInstance, message } from 'ant-design-vue';
import { createApi, listApi, treeApi, updateApi, deleteApi } from '/@/api/classification';
import { 
  PlusOutlined, 
  DeleteOutlined, 
  EditOutlined, 
  ExclamationCircleOutlined,
  UnorderedListOutlined,
  ApartmentOutlined
} from '@ant-design/icons-vue';

/**
 * 表格列配置
 * 定义表格的列结构、标题、数据字段和渲染方式
 */
const columns = reactive([
  {
    title: '分类名称',
    dataIndex: 'title',
    key: 'title',
  },
  {
    title: '层级',
    dataIndex: 'level',
    key: 'level',
    width: 120,
  },
  {
    title: '父级分类',
    dataIndex: 'parentTitle',
    key: 'parentTitle',
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
 * 包含分类列表、树形数据、加载状态等
 */
const data = reactive({
  classificationList: [],
  allClassifications: [],
  treeData: [] as any[],
  loading: false,
  currentAdminUserName: '',
  keyword: '',
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
  showTreeMode: true, // 默认显示树形结构
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
    key: undefined,
    title: undefined,
    parentId: undefined as number | undefined,
  },
  rules: {
    title: [{ required: true, message: '请输入分类名称', trigger: 'change' }],
  },
});

/**
 * 表单实例引用
 */
const myform = ref<FormInstance>();

/**
 * 父级分类树形数据
 * 计算属性，用于生成父级分类选择器的树形数据
 */
const parentTreeData = computed(() => {
  const rootNode = {
    id: 0,
    title: '无 (作为一级分类)',
    value: 0,
    level: 0,
    children: []
  };
  
  // 处理当前编辑的分类，它不能选择自己作为父级
  const editingId = modal.form.id;
  
  // 过滤掉当前正在编辑的分类
  const filteredClassifications = data.allClassifications.filter(item => 
    !editingId || item.id !== editingId
  );
  
  // 构建树形结构
  const treeData = buildTreeData(filteredClassifications);
  
  return [rootNode, ...treeData];
});

/**
 * 组件挂载后的初始化操作
 */
onMounted(() => {
  getDataList();
  getClassificationTree();
});

/**
 * 获取分类列表数据
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
        // 处理列表数据
        res.data.forEach((item: any, index: any) => {
          item.index = index + 1;
          // 查找父分类名称
          if (item.parentId && item.parentId !== 0) {
            const parent = data.allClassifications.find((p) => p.id === item.parentId);
            item.parentTitle = parent ? parent.title : '未知';
          } else {
            item.parentTitle = '无';
          }
        });
        data.classificationList = res.data;
        data.allClassifications = res.data;
      })
      .catch((err) => {
        data.loading = false;
        console.log(err);
      });
};

/**
 * 获取分类树形数据
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
const getClassificationTree = () => {
  data.loading = true;
  treeApi({})
      .then((res) => {
        data.loading = false;
        data.treeData = res.data;
      })
      .catch((err) => {
        data.loading = false;
        console.log(err);
      });
};

/**
 * 构建树形结构数据
 * 
 * @param {Array} items - 分类数据列表
 * @returns {Array} 树形结构数据
 */
const buildTreeData = (items) => {
  const result = [];
  const itemMap = {};
  
  // 先创建一个映射表
  items.forEach(item => {
    itemMap[item.id] = {
      ...item,
      children: []
    };
  });
  
  // 然后构建树形结构
  items.forEach(item => {
    if (item.parentId && item.parentId !== 0 && itemMap[item.parentId]) {
      // 如果有父级且父级存在于映射表中，则添加为其子节点
      itemMap[item.parentId].children.push(itemMap[item.id]);
    } else {
      // 否则作为根节点
      result.push(itemMap[item.id]);
    }
  });
  
  return result;
};

/**
 * 根据ID查找分类
 * 
 * @param {number} id - 分类ID
 * @returns {Object} 分类对象
 */
const findClassificationById = (id) => {
  return data.allClassifications.find(item => item.id === id);
};

/**
 * 切换视图模式
 */
const toggleViewMode = () => {
  if (data.showTreeMode) {
    getClassificationTree();
  } else {
    getDataList();
  }
};

/**
 * 获取层级对应的颜色
 * 
 * @param {number} level - 分类层级
 * @returns {string} 颜色值
 */
const getLevelColor = (level) => {
  const colors = ['#5a7be0', '#52c41a', '#722ed1', '#fa8c16', '#f5222d'];
  return colors[(level - 1) % colors.length];
};

/**
 * 获取层级对应的文本
 * 
 * @param {number} level - 分类层级
 * @returns {string} 层级文本
 */
const getLevelText = (level) => {
  return `${level}级分类`;
};

/**
 * 获取缩进文本
 * 
 * @param {number} level - 层级
 * @returns {string} 缩进文本
 */
const getIndentText = (level) => {
  // 确保level是数字且大于等于1
  const indentLevel = typeof level === 'number' && level > 0 ? level - 1 : 0;
  return '├ '.repeat(indentLevel);
};

/**
 * 表格行选择配置
 */
const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[], selectedRows: DataItem[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    data.selectedRowKeys = selectedRowKeys;
  },
});

/**
 * 处理新增操作
 */
const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增分类';
  // 重置表单数据
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  // 默认设置为一级分类
  modal.form.parentId = 0;
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
  modal.title = '编辑分类';
  // 重置表单数据
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  // 复制记录数据到表单
  for (const key in record) {
    modal.form[key] = record[key];
  }
  // 如果parentId不存在，设为0
  if (!modal.form.parentId) {
    modal.form.parentId = 0;
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
        getClassificationTree();
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
    message.warn('请勾选要删除的分类');
    return;
  }
  deleteApi({ ids: data.selectedRowKeys.join(',') })
      .then((res) => {
        message.success('批量删除成功');
        data.selectedRowKeys = [];
        getDataList();
        getClassificationTree();
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
        if (modal.editFlag) {
          updateApi({ id: modal.form.id }, modal.form)
              .then((res) => {
                message.success('更新成功');
                hideModal();
                getDataList();
                getClassificationTree();
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
                getClassificationTree();
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

/**
 * 定义DataItem接口
 */
interface DataItem {
  id: number | string;
  title: string;
  level?: number;
  parentId?: number;
  parentTitle?: string;
  [key: string]: any;
}
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
  
  .view-switch {
    :deep(.ant-switch-checked) {
      background-color: #5a7be0;
    }
  }
}

/**
 * 树形卡片和表格卡片样式
 */
.tree-card, .table-card {
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
 * 自定义树形组件样式
 */
.custom-tree {
  padding: 8px;
  
  :deep(.ant-tree-treenode) {
    padding: 8px 0;
    border-radius: 4px;
    
    &:hover {
      background-color: #f5f7ff;
    }
  }
  
  :deep(.ant-tree-node-content-wrapper.ant-tree-node-selected) {
    background-color: rgba(90, 123, 224, 0.1);
  }
}

/**
 * 树节点样式
 */
.tree-node {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 4px 0;
  
  .node-title {
    font-weight: 500;
  }
  
  .level-tag {
    margin: 0 10px;
  }
  
  .tree-node-actions {
    margin-left: auto;
    opacity: 0.2;
    transition: opacity 0.3s;
  }
  
  &:hover {
    .tree-node-actions {
      opacity: 1;
    }
  }
}

/**
 * 父级分类选择器样式
 */
.parent-tree-select {
  :deep(.ant-select-selector) {
    border-color: #d9d9d9;
    
    &:hover {
      border-color: #5a7be0;
    }
  }
  
  &:deep(.ant-select-focused:not(.ant-select-disabled).ant-select:not(.ant-select-customize-input) .ant-select-selector) {
    border-color: #5a7be0;
    box-shadow: 0 0 0 2px rgba(90, 123, 224, 0.2);
  }
}
</style>
