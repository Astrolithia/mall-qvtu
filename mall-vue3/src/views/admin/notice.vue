<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="header-section">
        <h2 class="page-title">平台公告</h2>
        <p class="page-description">管理商城平台的通知公告信息</p>
      </div>
      
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            新增公告
          </a-button>
          <a-button @click="handleBatchDelete">
            <template #icon><delete-outlined /></template>
            批量删除
          </a-button>
        </a-space>
      </div>
      
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
          <template #bodyCell="{ text, record, index, column }">
            <template v-if="column.key === 'title'">
              <span class="notice-title">{{ text }}</span>
            </template>
            <template v-if="column.key === 'content'">
              <div class="notice-content">
                {{ text?.substring(0, 40) }}{{ text?.length > 40 ? '...' : '' }}
              </div>
            </template>
            <template v-if="column.key === 'createTime'">
              <span class="create-time">
                {{ text ? new Date(parseInt(text)).toLocaleString() : '--' }}
              </span>
            </template>
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

    <!--弹窗区域-->
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
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item label="公告标题" name="title">
                  <a-input 
                    placeholder="请输入公告标题" 
                    v-model:value="modal.form.title"
                    :maxLength="100"
                  />
                </a-form-item>
              </a-col>
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
import { FormInstance, message } from 'ant-design-vue';
import { createApi, listApi, updateApi, deleteApi } from '/@/api/notice';
import { 
  PlusOutlined, 
  DeleteOutlined, 
  EditOutlined, 
  NotificationOutlined 
} from '@ant-design/icons-vue';
import { ref, reactive, onMounted } from 'vue';

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

// 页面数据
const data = reactive({
  noticeList: [],
  loading: false,
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

const myform = ref<FormInstance>();

onMounted(() => {
  getDataList();
});

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
      data.noticeList = res.data;
    })
    .catch((err) => {
      data.loading = false;
      console.log(err);
    });
};

const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[], selectedRows: any[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    data.selectedRowKeys = selectedRowKeys;
  },
});

const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增公告';
  // 重置
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
};

const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑公告';
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
      getDataList();
    })
    .catch((err) => {
      message.error(err.msg || '删除失败');
    });
};

const handleBatchDelete = () => {
  console.log(data.selectedRowKeys);
  if (data.selectedRowKeys.length <= 0) {
    message.warn('请勾选要删除的公告');
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

const handleOk = () => {
  myform.value
    ?.validate()
    .then(() => {
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

.notice-title {
  font-weight: 500;
  color: #333;
}

.notice-content {
  color: #666;
  line-height: 1.5;
}

.create-time {
  color: #999;
  font-size: 13px;
}

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
