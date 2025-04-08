<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="header-section">
        <h2 class="page-title">商品库存</h2>
        <p class="page-description">管理商城商品信息、库存和上下架状态</p>
      </div>
      
      <div class="table-operations">
        <a-row :gutter="16" align="middle">
          <a-col :span="12" class="operations-left">
            <a-space>
              <a-button type="primary" @click="handleAdd">
                <template #icon><plus-outlined /></template>
                新增商品
              </a-button>
              <a-button @click="handleBatchDelete">
                <template #icon><delete-outlined /></template>
                批量删除
              </a-button>
            </a-space>
          </a-col>
          <a-col :span="12" class="operations-right">
            <a-input-search 
              placeholder="输入商品名称搜索" 
              enter-button 
              @search="onSearch" 
              @change="onSearchChange" 
              style="max-width: 300px;"
            />
          </a-col>
        </a-row>
      </div>
      
      <a-card class="table-card">
        <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.dataList"
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
            <template v-if="column.key === 'price'">
              <span class="price-column">¥{{ text }}</span>
              <span v-if="record.originalPrice && record.originalPrice > record.price" class="original-price">¥{{ record.originalPrice }}</span>
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="text === '0' ? '#5a7be0' : '#bfbfbf'">
                {{ text === '0' ? '上架' : '下架' }}
              </a-tag>
            </template>
            <template v-if="column.key === 'repertory'">
              <a-tag :color="getStockColor(text)">{{ text }}</a-tag>
            </template>
            <template v-if="column.key === 'operation'">
              <a-space>
                <a-button type="link" size="small" @click="handleEdit(record)">
                  <template #icon><edit-outlined /></template>
                  编辑
                </a-button>
                <a-divider type="vertical" />
                <a-popconfirm title="确定删除此商品?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
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
        width="880px"
        ok-text="确认"
        cancel-text="取消"
        @cancel="handleCancel"
        @ok="handleOk"
        :maskClosable="false"
      >
        <div>
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item label="商品名称" name="title">
                  <a-input placeholder="请输入商品名称" v-model:value="modal.form.title" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="商品分类" name="classificationId">
                  <a-tree-select
                    placeholder="请选择商品分类"
                    allowClear
                    show-search
                    :tree-data="modal.cData"
                    :replaceFields="{ title: 'title', key: 'id', value: 'id' }"
                    :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                    v-model:value="modal.form.classificationId"
                    :tree-node-filter-prop="'title'"
                    class="category-select"
                  >
                    <template #title="{ title, level }">
                      <span v-if="level">
                        {{ '├ '.repeat(level-1) }}{{ title }}
                        <a-tag size="small" :color="getLevelColor(level)">{{ level }}级</a-tag>
                      </span>
                      <span v-else>{{ title }}</span>
                    </template>
                  </a-tree-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="商品标签">
                  <a-select 
                    mode="multiple" 
                    placeholder="请选择商品标签" 
                    allowClear 
                    v-model:value="modal.form.tags"
                    class="tag-select"
                  >
                    <template v-for="item in modal.tagData" :key="item.id">
                      <a-select-option :value="item.id">{{item.title}}</a-select-option>
                    </template>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="24">
                <a-form-item label="商品封面">
                  <a-upload-dragger
                    name="file"
                    accept="image/*"
                    :multiple="false"
                    :before-upload="beforeUpload"
                    v-model:file-list="fileList"
                    class="cover-upload"
                  >
                    <p class="ant-upload-drag-icon">
                      <template v-if="modal.form.coverUrl">
                        <img :src="modal.form.coverUrl" class="cover-preview" />
                      </template>
                      <template v-else>
                        <file-image-outlined />
                      </template>
                    </p>
                    <p class="ant-upload-text">
                      点击或拖拽上传商品封面图片
                    </p>
                    <p class="ant-upload-hint">
                      建议尺寸: 800×800px，支持jpg、png格式
                    </p>
                  </a-upload-dragger>
                </a-form-item>
              </a-col>

              <a-col :span="24">
                <a-form-item label="商品描述">
                  <div class="editor-container">
                    <Toolbar
                      style="border-bottom: 1px solid #d9d9d9"
                      :editor="editorRef"
                      :defaultConfig="toolbarConfig"
                      mode="default"
                    />
                    <Editor
                      style="height: 300px; overflow-y: hidden;"
                      :modelValue="modal.form.description"
                      @update:modelValue="modal.form.description = $event"
                      :defaultConfig="editorConfig"
                      mode="default"
                      @onCreated="handleCreated"
                    />
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="售价(元)" name="price">
                  <a-input-number v-model:value="modal.form.price" :min="0" style="width: 100%" precision="2" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="原价(元)" name="originalPrice">
                  <a-input-number v-model:value="modal.form.originalPrice" :min="0" style="width: 100%" precision="2" />
                  <span class="form-help-text">原价应大于等于售价，用于显示折扣</span>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="商品状态" name="status">
                  <a-select placeholder="请选择" allowClear v-model:value="modal.form.status" class="status-select">
                    <a-select-option key="0" value="0">上架</a-select-option>
                    <a-select-option key="1" value="1">下架</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="库存数量" name="repertory">
                  <a-input-number placeholder="请输入" :min="0" v-model:value="modal.form.repertory" style="width: 100%;" />
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
import { FormInstance, message, SelectProps } from 'ant-design-vue';
import { createApi, listApi, updateApi, deleteApi } from '/@/api/thing';
import {listApi as listClassificationApi, treeApi as treeClassificationApi} from '/@/api/classification'
import {listApi as listTagApi} from '/@/api/tag'
import {BASE_URL} from "/@/store/constants";
import { FileImageOutlined, PlusOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons-vue';
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { uploadImageApi, uploadVideoApi } from '/@/api/file';
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount } from 'vue';

const columns = reactive([
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 60
  },
  {
    title: '名称',
    dataIndex: 'title',
    key: 'title'
  },
  {
    title: '价格',
    dataIndex: 'price',
    key: 'price'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: '库存',
    dataIndex: 'repertory',
    key: 'repertory'
  },
  {
    title: '简介',
    dataIndex: 'description',
    key: 'description',
    customRender: ({ text, record, index, column }) => text ? text.substring(0, 10) + '...' : '--'
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
  return false;
};

// 文件列表
const fileList = ref<any[]>([]);

// 页面数据
const data = reactive({
  dataList: [],
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
  cData: [],
  tagData: [{}],
  form: {
    id: undefined,
    title: undefined,
    classificationId: undefined,
    tags: [],
    repertory: undefined,
    price: undefined,
    status: undefined,
    cover: undefined,
    coverUrl: undefined,
    imageFile: undefined,
    originalPrice: undefined,
    description: undefined,
  },
  rules: {
    title: [{ required: true, message: '请输入商品名称', trigger: 'change' }],
    classificationId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
    repertory: [{ required: true, message: '请输入库存数量', trigger: 'change' }],
    price: [{ required: true, message: '请输入商品售价', trigger: 'change' }],
    status: [{ required: true, message: '请选择商品状态', trigger: 'change' }]
  },
});

const myform = ref<FormInstance>();

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

// 工具栏配置
const toolbarConfig = {
  excludeKeys: [] // 可以排除某些菜单
}

// 编辑器配置
const editorConfig = {
  placeholder: '请输入商品详细描述...',
  MENU_CONF: {
    uploadImage: {
      customUpload: handleUploadImage
    },
    uploadVideo: {
      customUpload: handleUploadVideo
    }
  }
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

// 编辑器回调函数
const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例
}

onMounted(() => {
  getDataList();
  getCDataList();
  getTagDataList();
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
      data.dataList = res.data;
    })
    .catch((err) => {
      data.loading = false;
      console.log(err);
    });
}

const getCDataList = () => {
  // 使用树形结构API获取分类数据
  treeClassificationApi({}).then(res => {
    modal.cData = res.data;
  });
}

const getTagDataList = ()=> {
  listTagApi({}).then(res => {
    res.data.forEach((item, index) => {
      item.index = index + 1
    })
    modal.tagData = res.data
  })
}

const onSearchChange = (e: Event) => {
  const target = e.target as HTMLInputElement;
  data.keyword = target?.value;
  console.log(data.keyword);
};

const onSearch = () => {
  getDataList();
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
  modal.title = '新增商品';
  // 重置
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  modal.form.cover = undefined;
  modal.form.status = "0"; // 默认上架
};

const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑商品';
  // 重置
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  for (const key in record) {
    if(record[key]) {
      modal.form[key] = record[key];
    }
  }
  if(modal.form.cover) {
    modal.form.coverUrl = BASE_URL + '/api/staticfiles/image/' + modal.form.cover
    modal.form.cover = undefined
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
    console.log('hello');
    message.warn('请勾选要删除的商品');
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
      const formData = new FormData();
      if(modal.editFlag) {
        formData.append('id', modal.form.id)
      }
      formData.append('title', modal.form.title)
      if (modal.form.classificationId) {
        formData.append('classificationId', modal.form.classificationId)
      }
      if (modal.form.tags) {
        modal.form.tags.forEach(function (value) {
          if(value){
            formData.append('tags[]', value)
          }
        })
      }
      if (modal.form.imageFile) {
        formData.append('imageFile', modal.form.imageFile)
      }
      
      // 确保富文本内容被正确提交
      const htmlContent = modal.form.description || '';
      formData.append('description', htmlContent)
      
      formData.append('price', modal.form.price || '')
      if (modal.form.originalPrice >= 0) {
        formData.append('originalPrice', modal.form.originalPrice)
      }
      if (modal.form.repertory >= 0) {
        formData.append('repertory', modal.form.repertory)
      }
      if (modal.form.status) {
        formData.append('status', modal.form.status)
      }
      if (modal.editFlag) {
        updateApi(formData)
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
        createApi(formData)
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
  fileList.value = []
  modal.form.id = undefined
  modal.form.title = undefined
  modal.form.price = undefined
  modal.form.originalPrice = undefined
  modal.form.description = undefined
  modal.form.status = "0"
  modal.form.repertory = undefined
  modal.form.tags = []
  modal.form.classificationId = undefined
  modal.form.cover = undefined
  modal.form.coverUrl = undefined
  modal.form.imageFile = undefined
};

// 关闭弹窗
const hideModal = () => {
  modal.visile = false;
};

// 自定义图片上传
async function handleUploadImage(file: File, insertFn: any) {
  try {
    const res = await uploadImageApi(file);
    if (res.code === 200 && res.data && res.data.url) {
      console.log('图片上传成功:', res.data);
      // 确保图片URL是完整的绝对路径
      const imageUrl = res.data.url.startsWith('http') 
        ? res.data.url
        : BASE_URL + res.data.url;
      insertFn(imageUrl, res.data.alt || file.name, res.data.href || imageUrl);
    } else {
      message.error('图片上传失败');
    }
  } catch (error) {
    console.error('图片上传错误', error);
    message.error('图片上传失败');
  }
}

// 自定义视频上传
async function handleUploadVideo(file: File, insertFn: any) {
  try {
    const res = await uploadVideoApi(file);
    if (res.code === 200 && res.data && res.data.url) {
      console.log('视频上传成功:', res.data);
      // 确保视频URL是完整的绝对路径
      const videoUrl = res.data.url.startsWith('http') 
        ? res.data.url
        : BASE_URL + res.data.url;
      insertFn(videoUrl);
    } else {
      message.error('视频上传失败');
    }
  } catch (error) {
    console.error('视频上传错误', error);
    message.error('视频上传失败');
  }
}

// 根据层级设置标签颜色
const getLevelColor = (level) => {
  const colors = ['#5a7be0', '#52c41a', '#722ed1', '#fa8c16', '#f5222d'];
  return colors[(level - 1) % colors.length];
};

// 根据库存数量显示不同颜色
const getStockColor = (stock: number) => {
  if (stock <= 0) return '#ff4d4f';
  if (stock < 10) return '#faad14';
  return '#52c41a';
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
  
  .operations-left {
    text-align: left;
  }
  
  .operations-right {
    text-align: right;
  }
  
  :deep(.ant-btn-primary) {
    background-color: #5a7be0;
    border-color: #5a7be0;
    
    &:hover {
      background-color: #6e89e8;
      border-color: #6e89e8;
    }
  }
  
  :deep(.ant-input-search-button) {
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

.price-column {
  color: #f5222d;
  font-weight: 500;
}

.original-price {
  margin-left: 8px;
  color: #999;
  text-decoration: line-through;
  font-size: 12px;
}

.editor-container {
  border: 1px solid #d9d9d9;
  border-radius: 2px;
  z-index: 100;
  margin-bottom: 20px;

  :deep(.w-e-toolbar) {
    z-index: 101 !important;
  }
  
  :deep(.w-e-text-container) {
    z-index: 100 !important;
  }
}

.form-help-text {
  display: block;
  margin-top: 4px;
  color: #999;
  font-size: 12px;
}

.cover-upload {
  :deep(.ant-upload-drag) {
    border-color: #d9d9d9;
    
    &:hover {
      border-color: #5a7be0;
    }
  }
  
  .cover-preview {
    max-width: 150px;
    max-height: 150px;
    object-fit: contain;
  }
}

.category-select, .tag-select, .status-select {
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
