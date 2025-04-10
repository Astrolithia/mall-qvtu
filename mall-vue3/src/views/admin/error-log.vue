<!--
/**
 * 错误日志管理组件
 * 
 * 该组件实现了系统错误日志的管理功能，包括：
 * 1. 错误日志列表的展示
 * 2. 错误日志的编辑和删除
 * 3. 分页和搜索功能
 * 
 * 组件依赖：
 * - Ant Design Vue 组件库
 * - Vue Composition API
 * - 日志管理相关API
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <div>
    <!--页面区域：包含错误日志列表-->
    <div class="page-view">
      <!--错误日志列表表格-->
      <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.dataList"
          :scroll="{ x: 'max-content' }"
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
              <!--编辑按钮-->
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
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
 * - 日志管理相关API
 */
import { FormInstance, message } from 'ant-design-vue';
import { listErrorLogListApi } from '/@/api/log';

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
    title: '请求方式',
    dataIndex: 'method',
    key: 'method',
    align: 'center'
  },
  {
    title: '请求URL',
    dataIndex: 'url',
    key: 'url',
    align: 'center'
  },
  {
    title: '异常信息',
    dataIndex: 'content',
    key: 'content'
  },
  {
    title: '操作IP',
    dataIndex: 'ip',
    key: 'ip',
    align: 'center'
  },
  {
    title: '操作时间',
    dataIndex: 'logTime',
    key: 'logTime',
    align: 'center'
  }
]);

/**
 * 页面数据状态
 * 包含错误日志列表、加载状态、分页信息等
 */
const data = reactive({
  dataList: [],
  loading: false,
  keyword: '',
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
});

/**
 * 组件挂载后的初始化操作
 */
onMounted(() => {
  getDataList();
});

/**
 * 获取错误日志列表数据
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
const getDataList = () => {
  data.loading = true;
  listErrorLogListApi({
    keyword: data.keyword,
  })
      .then((res) => {
        data.loading = false;
        console.log(res);
        // 处理列表数据
        res.data.forEach((item: any, index: any) => {
          item.index = index + 1;
        });
        data.dataList = res.data;
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
 * 定义DataItem接口
 */
interface DataItem {
  id: number | string;
  index?: number;
  method?: string;
  url?: string;
  content?: string;
  ip?: string;
  logTime?: string;
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
