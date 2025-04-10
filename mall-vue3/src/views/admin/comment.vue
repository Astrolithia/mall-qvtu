<!--
/**
 * 评论管理组件
 * 
 * 该组件实现了商品评论的管理功能，包括：
 * 1. 评论列表的展示
 * 2. 评论的删除操作
 * 3. 批量删除功能
 * 
 * 组件依赖：
 * - Ant Design Vue 组件库
 * - Vue Composition API
 * - 评论管理相关API
 * - 工具函数（时间格式化等）
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <div>
    <!--页面区域：包含操作按钮和评论列表-->
    <div class="page-view">
      <!--表格操作按钮区域-->
      <div class="table-operations">
        <a-space>
          <!--新增评论按钮（模拟）-->
          <a-button type="primary" @click="handleAdd">模拟新增</a-button>
          <!--批量删除按钮-->
          <a-button @click="handleBatchDelete">批量删除</a-button>
        </a-space>
      </div>
      
      <!--评论列表表格-->
      <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.list"
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
            <span>
              <a-divider type="vertical"/>
              <!--删除确认弹窗-->
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                <a href="#">删除</a>
              </a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 引入必要的依赖
 * - Ant Design Vue：用于UI组件
 * - 评论管理相关API
 * - 常量配置
 * - 工具函数
 */
import {FormInstance, message} from 'ant-design-vue';
import {createApi, listApi, deleteApi} from '/@/api/comment';
import {BASE_URL} from "/@/store/constants";
import {getFormatTime} from "/@/utils";

/**
 * 表格列配置
 * 定义表格的列结构、标题、数据字段和渲染方式
 */
const columns = reactive([
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    align: 'center'
  },
  {
    title: '用户',
    dataIndex: 'username',
    key: 'username',
    align: 'center'
  },
  {
    title: '商品名称',
    dataIndex: 'title',
    key: 'title',
    align: 'center'
  },
  {
    title: '评分',
    dataIndex: 'score',
    key: 'score',
    align: 'center',
    customRender: ({text}) => {
      return text ? `${text}分` : '暂无评分';
    }
  },
  {
    title: '评论内容',
    dataIndex: 'content',
    key: 'content',
    align: 'center'
  },
  {
    title: '评论时间',
    dataIndex: 'commentTime',
    key: 'commentTime',
    align: 'center',
    customRender: ({text}) => getFormatTime(text, true)
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'operation',
    align: 'center',
    fixed: 'right',
    width: 140,
  },
]);

/**
 * 页面数据状态
 * 包含评论列表、加载状态、分页信息等
 */
const data = reactive({
  list: [],
  loading: false,
  currentAdminUserName: '',
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
    image: undefined,
    link: undefined,
  },
  rules: {
    link: [{required: true, message: '请输入', trigger: 'change'}],
  },
});

/**
 * 组件挂载后的初始化操作
 */
onMounted(() => {
  getList();
});

/**
 * 获取评论列表数据
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
const getList = () => {
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
          // 处理图片URL
          if (item.image) {
            item.image = BASE_URL + item.image
          }
        });
        data.list = res.data;
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
  onChange: (selectedRowKeys: (string | number)[], selectedRows: DataItem[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    data.selectedRowKeys = selectedRowKeys;
  },
});

/**
 * 处理新增操作（模拟）
 */
const handleAdd = () => {
  // createApi({}).then(res => {
  //   message.success("模拟新增成功")
  //   getList()
  // }).catch(err => {
  //
  // })
};

/**
 * 确认删除操作
 * 
 * @param {Object} record - 要删除的记录
 * @throws {Error} 当删除失败时抛出错误
 */
const confirmDelete = (record: any) => {
  console.log('delete', record);
  deleteApi({ids: record.id})
      .then((res) => {
        getList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
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
    message.warn('请勾选删除项');
    return;
  }
  deleteApi({ids: data.selectedRowKeys.join(',')})
      .then((res) => {
        message.success('删除成功');
        data.selectedRowKeys = [];
        getList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
};
</script>

<style scoped lang="less">
/**
 * 页面视图样式
 * 设置整体布局和背景
 */
.page-view {
  min-height: 100%;
  background: #fff;
  padding: 24px;
  display: flex;
  flex-direction: column;
}

/**
 * 表格操作按钮区域样式
 */
.table-operations {
  margin-bottom: 16px;
  text-align: right;
}

.table-operations > button {
  margin-right: 8px;
}
</style>
