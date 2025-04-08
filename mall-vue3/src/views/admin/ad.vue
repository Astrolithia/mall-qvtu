<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="header-section">
        <h2 class="page-title">推广广告</h2>
        <p class="page-description">管理商城推广横幅广告展示</p>
      </div>
      
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            新增广告
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
          <template #bodyCell="{ text, record, index, column }">
            <template v-if="column.key === 'imageUrl'">
              <div class="ad-image-wrapper">
                <img :src="text" class="ad-image" />
              </div>
            </template>
            <template v-if="column.key === 'link'">
              <a-tooltip :title="text" placement="topLeft">
                <a :href="text" target="_blank" class="ad-link">
                  {{ formatLink(text) }}
                </a>
              </a-tooltip>
            </template>
            <template v-if="column.key === 'operation'">
              <a-space>
                <a-button type="link" size="small" @click="handleEdit(record)">
                  <template #icon><edit-outlined /></template>
                  编辑
                </a-button>
                <a-divider type="vertical" />
                <a-popconfirm title="确定删除此广告?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
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
        width="600px"
        :maskClosable="false"
      >
        <div>
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item label="广告图片" name="imageFile">
                  <a-upload-dragger 
                    name="file" 
                    accept="image/*" 
                    :multiple="false" 
                    :before-upload="beforeUpload"
                    class="ad-upload"
                  >
                    <p class="ant-upload-drag-icon">
                      <template v-if="modal.form.imageUrl">
                        <img :src="modal.form.imageUrl" class="preview-image" />
                      </template>
                      <template v-else>
                        <picture-outlined class="upload-icon" />
                      </template>
                    </p>
                    <p class="ant-upload-text">
                      点击或拖拽上传广告图片
                    </p>
                    <p class="ant-upload-hint">
                      建议尺寸：1200px × 120px，横条广告
                    </p>
                  </a-upload-dragger>
                </a-form-item>
              </a-col>
              <a-col :span="24">
                <a-form-item label="跳转链接" name="link">
                  <a-input 
                    placeholder="请输入广告点击跳转的链接" 
                    v-model:value="modal.form.link"
                    :maxLength="255"
                  />
                  <div class="form-help-text">例如: https://www.example.com/product/123</div>
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
import { createApi, listApi, updateApi, deleteApi } from '/@/api/ad';
import { BASE_URL } from '/@/store/constants';
import { 
  PlusOutlined, 
  DeleteOutlined, 
  EditOutlined,
  PictureOutlined,
  LinkOutlined
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
    title: '广告图片',
    dataIndex: 'imageUrl',
    key: 'imageUrl',
    align: 'center',
    width: 180
  },
  {
    title: '跳转链接',
    dataIndex: 'link',
    key: 'link',
    align: 'left',
    ellipsis: true
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

const formatLink = (link: string) => {
  if (!link) return '无链接';
  
  // 如果链接过长，截取显示
  if (link.length > 50) {
    return link.substring(0, 47) + '...';
  }
  return link;
};

const beforeUpload = (file: File) => {
  // 改文件名
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  console.log(copyFile);
  modal.form.imageFile = copyFile;
  
  // 创建预览
  const reader = new FileReader();
  reader.readAsDataURL(file);
  reader.onload = () => {
    modal.form.imageUrl = reader.result as string;
  };
  
  return false;
};

const fileList = ref([]);

// 页面数据
const data = reactive({
  list: [],
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
  form: {
    id: undefined,
    image: undefined,
    imageFile: undefined,
    imageUrl: undefined,
    link: undefined,
  },
  rules: {
    link: [{ required: true, message: '请输入跳转链接', trigger: 'change' }],
    imageFile: [{ 
      required: true, 
      message: '请上传广告图片', 
      trigger: 'change',
      validator: (rule, value) => {
        if (modal.editFlag && modal.form.image) {
          return Promise.resolve();
        }
        return modal.form.imageFile ? Promise.resolve() : Promise.reject('请上传广告图片');
      }
    }],
  },
});

const myform = ref<FormInstance>();

onMounted(() => {
  getList();
});

const getList = () => {
  data.loading = true;
  listApi({
    keyword: data.keyword,
  })
    .then((res) => {
      data.loading = false;
      console.log(res);
      res.data.forEach((item: any, index: any) => {
        item.index = index + 1;
        if (item.image) {
          item.imageUrl = BASE_URL + '/api/staticfiles/image/' + item.image;
        }
      });
      data.list = res.data;
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
  modal.title = '新增广告';
  // 重置
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
};

const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑广告';
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
      getList();
    })
    .catch((err) => {
      message.error(err.msg || '删除失败');
    });
};

const handleBatchDelete = () => {
  console.log(data.selectedRowKeys);
  if (data.selectedRowKeys.length <= 0) {
    message.warn('请勾选要删除的广告');
    return;
  }
  deleteApi({ ids: data.selectedRowKeys.join(',') })
    .then((res) => {
      message.success('批量删除成功');
      data.selectedRowKeys = [];
      getList();
    })
    .catch((err) => {
      message.error(err.msg || '删除失败');
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
      if (modal.form.link) {
        formData.append('link', modal.form.link);
      }
      if (modal.form.imageFile) {
        formData.append('imageFile', modal.form.imageFile);
      }
      if (modal.editFlag) {
        updateApi(formData)
          .then((res) => {
            message.success('更新成功');
            hideModal();
            getList();
          })
          .catch((err) => {
            console.log(err);
            message.error(err.msg || '操作失败');
          });
      } else {
        createApi(formData)
          .then((res) => {
            message.success('创建成功');
            hideModal();
            getList();
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

.ad-image-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
  
  .ad-image {
    width: 150px;
    height: 40px;
    object-fit: cover;
    border-radius: 4px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.08);
    transition: transform 0.3s;
    
    &:hover {
      transform: scale(1.05);
    }
  }
}

.ad-link {
  color: #5a7be0;
  text-decoration: none;
  
  &:hover {
    text-decoration: underline;
  }
}

.ad-upload {
  :deep(.ant-upload-drag) {
    border-color: #d9d9d9;
    border-radius: 4px;
    
    &:hover {
      border-color: #5a7be0;
    }
  }
  
  .preview-image {
    max-width: 300px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
  }
  
  .upload-icon {
    font-size: 32px;
    color: #999;
  }
}

.form-help-text {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}
</style>
