<!--
/**
 * 商品库存管理组件
 * 
 * 该组件实现了电商平台商品的管理功能，包括：
 * 1. 商品列表的展示、检索和筛选
 * 2. 商品的增删改查操作
 * 3. 批量删除功能
 * 4. 富文本编辑器支持的商品详情编辑
 * 5. 图片上传和预览功能
 * 6. 分类和标签管理关联
 * 
 * 组件依赖：
 * - Vue Composition API
 * - Ant Design Vue 组件库
 * - WangEditor 富文本编辑器
 * - 多个API服务（商品、分类、标签、文件上传）
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <div>
    <!--页面区域：包含页面标题、操作按钮和商品列表-->
    <div class="page-view">
      <!--页面标题区域-->
      <div class="header-section">
        <h2 class="page-title">商品库存</h2>
        <p class="page-description">管理商城商品信息、库存和上下架状态</p>
      </div>
      
      <!--表格操作按钮和搜索区域-->
      <div class="table-operations">
        <a-row :gutter="16" align="middle">
          <a-col :span="12" class="operations-left">
            <a-space>
              <!--新增商品按钮-->
              <a-button type="primary" @click="handleAdd">
                <template #icon><plus-outlined /></template>
                新增商品
              </a-button>
              <!--批量删除按钮-->
              <a-button @click="handleBatchDelete">
                <template #icon><delete-outlined /></template>
                批量删除
              </a-button>
            </a-space>
          </a-col>
          <a-col :span="12" class="operations-right">
            <!--商品名称搜索框-->
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
      
      <!--商品列表表格区域-->
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
          <!--自定义表格单元格渲染-->
          <template #bodyCell="{ text, record, index, column }">
            <!--价格列：显示当前价格和原价-->
            <template v-if="column.key === 'price'">
              <span class="price-column">¥{{ text }}</span>
              <span v-if="record.originalPrice && record.originalPrice > record.price" class="original-price">¥{{ record.originalPrice }}</span>
            </template>
            <!--状态列：显示上/下架状态-->
            <template v-if="column.key === 'status'">
              <a-tag :color="text === '0' ? '#5a7be0' : '#bfbfbf'">
                {{ text === '0' ? '上架' : '下架' }}
              </a-tag>
            </template>
            <!--库存列：根据库存量显示不同颜色-->
            <template v-if="column.key === 'repertory'">
              <a-tag :color="getStockColor(text)">{{ text }}</a-tag>
            </template>
            <!--操作列：编辑和删除按钮-->
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

    <!--弹窗区域：用于新增和编辑商品-->
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
          <!--商品表单-->
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <!--商品名称输入-->
              <a-col :span="24">
                <a-form-item label="商品名称" name="title">
                  <a-input placeholder="请输入商品名称" v-model:value="modal.form.title" />
                </a-form-item>
              </a-col>
              <!--商品分类选择-->
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
              <!--商品标签选择-->
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
              <!--商品封面上传-->
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

              <!--商品描述富文本编辑器-->
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
              <!--商品售价输入-->
              <a-col :span="12">
                <a-form-item label="售价(元)" name="price">
                  <a-input-number v-model:value="modal.form.price" :min="0" style="width: 100%" precision="2" />
                </a-form-item>
              </a-col>
              <!--商品原价输入-->
              <a-col :span="12">
                <a-form-item label="原价(元)" name="originalPrice">
                  <a-input-number v-model:value="modal.form.originalPrice" :min="0" style="width: 100%" precision="2" />
                  <span class="form-help-text">原价应大于等于售价，用于显示折扣</span>
                </a-form-item>
              </a-col>
              <!--商品状态选择-->
              <a-col :span="12">
                <a-form-item label="商品状态" name="status">
                  <a-select placeholder="请选择" allowClear v-model:value="modal.form.status" class="status-select">
                    <a-select-option key="0" value="0">上架</a-select-option>
                    <a-select-option key="1" value="1">下架</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <!--库存数量输入-->
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
/**
 * 导入必要的依赖和组件
 * - Ant Design Vue：用于UI组件
 * - API服务：用于与后端交互
 * - 富文本编辑器：用于商品描述编辑
 * - Vue Composition API：响应式编程
 */
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

/**
 * 表格列配置
 * 定义表格的列结构、标题、数据字段和渲染方式
 */
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

/**
 * 图片上传前处理
 * 修改文件名并存储到表单中
 * 
 * @param {File} file - 上传的文件对象
 * @returns {boolean} 返回false阻止默认上传行为
 */
const beforeUpload = (file: File) => {
  // 改文件名
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  console.log(copyFile);
  modal.form.imageFile = copyFile;
  return false;
};

/**
 * 文件列表
 * 存储已上传的文件
 */
const fileList = ref<any[]>([]);

/**
 * 页面数据状态
 * 包含商品列表、加载状态、筛选条件、分页信息等
 */
const data = reactive({
  dataList: [],          // 商品列表数据
  loading: false,        // 加载状态
  keyword: '',           // 搜索关键词
  selectedRowKeys: [] as any[], // 选中行的键值
  pageSize: 10,          // 每页显示条数
  page: 1,               // 当前页码
});

/**
 * 弹窗数据源
 * 包含表单数据、验证规则等
 */
const modal = reactive({
  visile: false,         // 模态框显示状态
  editFlag: false,       // 是否为编辑模式
  title: '',             // 模态框标题
  cData: [],             // 分类数据
  tagData: [{}],         // 标签数据
  form: {                // 表单数据
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
  rules: {               // 表单验证规则
    title: [{ required: true, message: '请输入商品名称', trigger: 'change' }],
    classificationId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
    repertory: [{ required: true, message: '请输入库存数量', trigger: 'change' }],
    price: [{ required: true, message: '请输入商品售价', trigger: 'change' }],
    status: [{ required: true, message: '请选择商品状态', trigger: 'change' }]
  },
});

/**
 * 表单实例引用
 */
const myform = ref<FormInstance>();

/**
 * 编辑器实例
 * 使用shallowRef避免不必要的深层响应式
 */
const editorRef = shallowRef()

/**
 * 工具栏配置
 * 设置富文本编辑器的工具栏选项
 */
const toolbarConfig = {
  excludeKeys: [] // 可以排除某些菜单
}

/**
 * 编辑器配置
 * 设置富文本编辑器的基本配置和自定义上传处理
 */
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

/**
 * 组件销毁时的清理操作
 * 销毁编辑器实例，防止内存泄漏
 */
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

/**
 * 编辑器创建回调
 * 保存编辑器实例引用
 * 
 * @param {Object} editor - 编辑器实例
 */
const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例
}

/**
 * 组件挂载时的初始化操作
 * 获取各类数据
 */
onMounted(() => {
  getDataList();
  getCDataList();
  getTagDataList();
});

/**
 * 获取商品列表数据
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
      data.dataList = res.data;
    })
    .catch((err) => {
      data.loading = false;
      console.log(err);
    });
}

/**
 * 获取分类数据列表
 * 获取商品分类的树形结构数据
 */
const getCDataList = () => {
  // 使用树形结构API获取分类数据
  treeClassificationApi({}).then(res => {
    modal.cData = res.data;
  });
}

/**
 * 获取标签数据列表
 * 获取商品标签数据
 */
const getTagDataList = ()=> {
  listTagApi({}).then(res => {
    res.data.forEach((item, index) => {
      item.index = index + 1
    })
    modal.tagData = res.data
  })
}

/**
 * 搜索关键词变更处理
 * 
 * @param {Event} e - 输入事件对象
 */
const onSearchChange = (e: Event) => {
  const target = e.target as HTMLInputElement;
  data.keyword = target?.value;
  console.log(data.keyword);
};

/**
 * 触发搜索操作
 * 根据关键词搜索商品
 */
const onSearch = () => {
  getDataList();
};

/**
 * 表格行选择配置
 * 处理表格行选择状态变化
 */
const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[], selectedRows: any[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    data.selectedRowKeys = selectedRowKeys;
  },
});

/**
 * 处理新增商品操作
 * 打开新增商品的表单弹窗
 */
const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增商品';
  // 重置表单字段
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  modal.form.cover = undefined;
  modal.form.status = "0"; // 默认上架
};

/**
 * 处理编辑商品操作
 * 
 * @param {Object} record - 要编辑的商品数据记录
 */
const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑商品';
  // 重置表单字段
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  // 填充当前数据
  for (const key in record) {
    if(record[key]) {
      modal.form[key] = record[key];
    }
  }
  // 处理图片URL
  if(modal.form.cover) {
    modal.form.coverUrl = BASE_URL + '/api/staticfiles/image/' + modal.form.cover
    modal.form.cover = undefined
  }
};

/**
 * 确认删除商品
 * 
 * @param {Object} record - 要删除的商品数据记录
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

/**
 * 处理表单提交
 * 验证并提交表单数据，创建或更新商品
 * 
 * @throws {Error} 当表单验证失败或提交失败时抛出错误
 */
const handleOk = () => {
  myform.value
    ?.validate()
    .then(() => {
      // 构建FormData对象用于提交
      const formData = new FormData();
      
      // 针对编辑模式，添加ID
      if(modal.editFlag) {
        formData.append('id', modal.form.id)
      }
      
      // 添加基本信息
      formData.append('title', modal.form.title)
      
      // 添加分类ID
      if (modal.form.classificationId) {
        formData.append('classificationId', modal.form.classificationId)
      }
      
      // 添加标签数组
      if (modal.form.tags) {
        modal.form.tags.forEach(function (value) {
          if(value){
            formData.append('tags[]', value)
          }
        })
      }
      
      // 添加图片文件
      if (modal.form.imageFile) {
        formData.append('imageFile', modal.form.imageFile)
      }
      
      // 添加商品描述
      const htmlContent = modal.form.description || '';
      formData.append('description', htmlContent)
      
      // 添加价格信息
      formData.append('price', modal.form.price || '')
      if (modal.form.originalPrice >= 0) {
        formData.append('originalPrice', modal.form.originalPrice)
      }
      
      // 添加库存信息
      if (modal.form.repertory >= 0) {
        formData.append('repertory', modal.form.repertory)
      }
      
      // 添加商品状态
      if (modal.form.status) {
        formData.append('status', modal.form.status)
      }
      
      // 根据编辑标志判断是更新还是创建
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

/**
 * 处理取消操作
 * 关闭弹窗并重置状态
 */
const handleCancel = () => {
  hideModal();
};

/**
 * 重置表单状态
 * 清空表单数据和上传文件
 */
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

/**
 * 关闭弹窗
 */
const hideModal = () => {
  modal.visile = false;
};

/**
 * 自定义图片上传处理
 * 
 * @param {File} file - 要上传的图片文件
 * @param {Function} insertFn - 插入图片到编辑器的回调函数
 * @throws {Error} 当上传失败时抛出错误
 */
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

/**
 * 自定义视频上传处理
 * 
 * @param {File} file - 要上传的视频文件
 * @param {Function} insertFn - 插入视频到编辑器的回调函数
 * @throws {Error} 当上传失败时抛出错误
 */
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

/**
 * 根据层级设置标签颜色
 * 
 * @param {number} level - 分类层级
 * @returns {string} 颜色代码
 */
const getLevelColor = (level) => {
  const colors = ['#5a7be0', '#52c41a', '#722ed1', '#fa8c16', '#f5222d'];
  return colors[(level - 1) % colors.length];
};

/**
 * 根据库存数量显示不同颜色
 * 
 * @param {number} stock - 库存数量
 * @returns {string} 颜色代码：红色(无库存)、黄色(库存紧张)或绿色(库存充足)
 */
const getStockColor = (stock: number) => {
  if (stock <= 0) return '#ff4d4f';
  if (stock < 10) return '#faad14';
  return '#52c41a';
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
 * 设置按钮和搜索框的布局
 */
.table-operations {
  margin-bottom: 16px;
  
  .operations-left {
    text-align: left;
  }
  
  .operations-right {
    text-align: right;
  }
  
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
  
  /**
   * 搜索按钮样式
   */
  :deep(.ant-input-search-button) {
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

/**
 * 价格列样式
 * 设置价格显示为红色，加粗显示
 */
.price-column {
  color: #f5222d;
  font-weight: 500;
}

/**
 * 原价样式
 * 设置原价为灰色和删除线效果
 */
.original-price {
  margin-left: 8px;
  color: #999;
  text-decoration: line-through;
  font-size: 12px;
}

/**
 * 编辑器容器样式
 * 设置富文本编辑器的边框和层级
 */
.editor-container {
  border: 1px solid #d9d9d9;
  border-radius: 2px;
  z-index: 100;
  margin-bottom: 20px;

  /**
   * 编辑器工具栏样式
   */
  :deep(.w-e-toolbar) {
    z-index: 101 !important;
  }
  
  /**
   * 编辑器内容区样式
   */
  :deep(.w-e-text-container) {
    z-index: 100 !important;
  }
}

/**
 * 表单帮助文本样式
 */
.form-help-text {
  display: block;
  margin-top: 4px;
  color: #999;
  font-size: 12px;
}

/**
 * 封面上传区域样式
 */
.cover-upload {
  :deep(.ant-upload-drag) {
    border-color: #d9d9d9;
    
    &:hover {
      border-color: #5a7be0;
    }
  }
  
  /**
   * 封面预览图样式
   */
  .cover-preview {
    max-width: 150px;
    max-height: 150px;
    object-fit: contain;
  }
}

/**
 * 分类、标签和状态选择器样式
 */
.category-select, .tag-select, .status-select {
  :deep(.ant-select-selector) {
    border-color: #d9d9d9;
    
    &:hover {
      border-color: #5a7be0;
    }
  }
  
  /**
   * 选择器聚焦样式
   */
  &:deep(.ant-select-focused:not(.ant-select-disabled).ant-select:not(.ant-select-customize-input) .ant-select-selector) {
    border-color: #5a7be0;
    box-shadow: 0 0 0 2px rgba(90, 123, 224, 0.2);
  }
}
</style>
