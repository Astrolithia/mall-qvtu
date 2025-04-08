<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="header-section">
        <h2 class="page-title">首页轮播</h2>
        <p class="page-description">管理商城首页的轮播图展示</p>
      </div>
      
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            新增轮播
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
              <div class="banner-image-wrapper">
                <img :src="text" class="banner-image" />
              </div>
            </template>
            <template v-if="column.key === 'thingId'">
              <a-tag v-if="record.thingId" color="#5a7be0">{{ getProductName(record.thingId) }}</a-tag>
              <a-tag v-else color="#faad14">未关联商品</a-tag>
            </template>
            <template v-if="column.key === 'operation'">
              <a-space>
                <a-button type="link" size="small" @click="handleEdit(record)">
                  <template #icon><edit-outlined /></template>
                  编辑
                </a-button>
                <a-divider type="vertical" />
                <a-popconfirm title="确定删除此轮播图?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
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
        width="800px"
        :maskClosable="false"
      >
        <div>
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item label="轮播图片" name="imageFile">
                  <a-upload-dragger 
                    name="file" 
                    accept="image/*" 
                    :multiple="false" 
                    :before-upload="beforeUpload"
                    class="banner-upload"
                  >
                    <p class="ant-upload-drag-icon">
                      <template v-if="modal.form.imageUrl">
                        <img :src="modal.form.imageUrl" class="preview-image" />
                      </template>
                      <template v-else>
                        <file-image-outlined />
                      </template>
                    </p>
                    <p class="ant-upload-text">
                      点击或拖拽上传轮播图片
                    </p>
                    <p class="ant-upload-hint">
                      建议尺寸：1200px × 400px，横幅比例3:1
                    </p>
                  </a-upload-dragger>
                </a-form-item>
              </a-col>
              <a-col :span="24">
                <a-form-item label="关联商品" name="thingId">
                  <a-select
                    placeholder="请选择关联的商品"
                    allowClear
                    :options="productOptions"
                    :field-names="{ label: 'title', value: 'id' }"
                    v-model:value="modal.form.thingId"
                    :filter-option="filterOption"
                    show-search
                    class="product-select"
                  >
                  </a-select>
                  <span class="form-help-text">关联商品后，点击轮播图会跳转到对应商品详情页</span>
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
import { reactive, ref, onMounted, computed } from 'vue';
import { FormInstance, message } from 'ant-design-vue';
import { createApi, listApi, updateApi, deleteApi } from '/@/api/banner';
import { listApi as listProductApi } from '/@/api/thing';
import { BASE_URL } from '/@/store/constants';
import { 
  FileImageOutlined, 
  PlusOutlined, 
  DeleteOutlined, 
  EditOutlined,
  PictureOutlined
} from '@ant-design/icons-vue';

const columns = reactive([
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    align: 'center',
    width: 80,
  },
  {
    title: '轮播图片',
    dataIndex: 'imageUrl',
    key: 'imageUrl',
    align: 'center',
  },
  {
    title: '关联商品',
    dataIndex: 'thingId',
    key: 'thingId',
    align: 'center',
    width: 180,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    align: 'center',
    width: 180,
    customRender: ({ text }) => {
      if (!text) return '--';
      return new Date(parseInt(text)).toLocaleString();
    }
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

const filterOption = (input: string, option: any) => {
  return option.title.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

// 获取商品名称通过ID
const getProductName = (id: string | number) => {
  const product = productOptions.value.find(item => item.id === id);
  return product ? product.title : id;
};

// 商品选项
const productOptions = ref([]);

// 页面数据
const data = reactive({
  list: [],
  loading: false,
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
    thingId: undefined,
  },
  rules: {
    imageFile: [{ 
      required: true, 
      message: '请上传轮播图片', 
      trigger: 'change',
      validator: (rule, value) => {
        if (modal.editFlag && modal.form.image) {
          return Promise.resolve();
        }
        return modal.form.imageFile ? Promise.resolve() : Promise.reject('请上传轮播图片');
      }
    }],
  },
});

const myform = ref<FormInstance>();

onMounted(() => {
  getList();
  getProductList();
});

const getList = () => {
  data.loading = true;
  listApi({})
    .then((res) => {
      data.loading = false;
      console.log(res);
      res.data.forEach((item: any, index: any) => {
        item.index = index + 1;
        if (item.image) {
          item.imageUrl = BASE_URL + '/api/staticfiles/banner/' + item.image;
        }
      });
      data.list = res.data;
    })
    .catch((err) => {
      data.loading = false;
      console.log(err);
    });
};

const getProductList = () => {
  listProductApi({})
    .then((res) => {
      productOptions.value = res.data;
    })
    .catch((err) => {
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
  modal.title = '新增轮播图';
};

const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑轮播图';
  
  // 复制记录数据
  for (const key in record) {
    if (record[key]) {
      modal.form[key] = record[key];
    }
  }
  
  // 设置图片URL
  if (modal.form.image) {
    modal.form.imageUrl = BASE_URL + '/api/staticfiles/banner/' + modal.form.image;
  }
};

const confirmDelete = (record: any) => {
  console.log('delete', record);
  deleteApi({ ids: record.id })
    .then(() => {
      message.success('删除成功');
      getList();
    })
    .catch((err) => {
      message.error(err.msg || '删除失败');
    });
};

const handleBatchDelete = () => {
  if (data.selectedRowKeys.length <= 0) {
    message.warn('请勾选要删除的轮播图');
    return;
  }
  
  deleteApi({ ids: data.selectedRowKeys.join(',') })
    .then(() => {
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
      
      if (modal.editFlag) {
        formData.append('id', modal.form.id);
      }
      
      if (modal.form.imageFile) {
        formData.append('imageFile', modal.form.imageFile);
      }
      
      if (modal.form.thingId) {
        formData.append('thingId', modal.form.thingId);
      }
      
      if (modal.editFlag) {
        updateApi(formData)
          .then(() => {
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
          .then(() => {
            message.success('添加成功');
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
      console.log('表单验证失败', err);
    });
};

const handleCancel = () => {
  hideModal();
};

// 恢复表单初始状态
const resetModal = () => {
  myform.value?.resetFields();
  
  modal.form.id = undefined;
  modal.form.image = undefined;
  modal.form.imageFile = undefined;
  modal.form.imageUrl = undefined;
  modal.form.thingId = undefined;
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

.banner-image-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
  
  .banner-image {
    width: 280px;
    height: 100px;
    object-fit: cover;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: transform 0.3s;
    
    &:hover {
      transform: scale(1.02);
    }
  }
}

.ant-upload-drag-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}

.banner-upload {
  :deep(.ant-upload-drag) {
    border-color: #d9d9d9;
    border-radius: 4px;
    
    &:hover {
      border-color: #5a7be0;
    }
  }
  
  .preview-image {
    width: 280px;
    height: 100px;
    object-fit: cover;
    border-radius: 4px;
  }
}

.product-select {
  width: 100%;
  
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

.form-help-text {
  display: block;
  margin-top: 4px;
  color: #999;
  font-size: 12px;
}
</style> 