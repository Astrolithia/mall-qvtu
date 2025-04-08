<!--
/**
 * 广告管理组件
 * 
 * 该组件实现了广告的完整管理功能，包括：
 * 1. 广告列表展示（带分页、排序和筛选）
 * 2. 广告的创建、更新和删除
 * 3. 广告图片上传和预览
 * 4. 批量删除广告
 * 
 * 组件依赖：
 * - Ant Design Vue 组件库
 * - 广告管理API接口
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <div>
    <!--页面区域：包含标题、操作按钮和广告数据表格-->
    <div class="page-view">
      <!--页面标题区域：显示页面标题和说明-->
      <div class="header-section">
        <h2 class="page-title">推广广告</h2>
        <p class="page-description">管理商城推广横幅广告展示</p>
      </div>
      
      <!--表格操作区域：包含新增和批量删除按钮-->
      <div class="table-operations">
        <a-space>
          <!--新增广告按钮-->
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            新增广告
          </a-button>
          <!--批量删除按钮-->
          <a-button @click="handleBatchDelete">
            <template #icon><delete-outlined /></template>
            批量删除
          </a-button>
        </a-space>
      </div>
      
      <!--广告数据表格区域：显示广告列表数据-->
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
          <!--表格单元格自定义渲染-->
          <template #bodyCell="{ text, record, index, column }">
            <!--广告图片单元格：显示图片预览-->
            <template v-if="column.key === 'imageUrl'">
              <div class="ad-image-wrapper">
                <img :src="text" class="ad-image" />
              </div>
            </template>
            <!--广告链接单元格：显示链接并提供tooltip-->
            <template v-if="column.key === 'link'">
              <a-tooltip :title="text" placement="topLeft">
                <a :href="text" target="_blank" class="ad-link">
                  {{ formatLink(text) }}
                </a>
              </a-tooltip>
            </template>
            <!--操作按钮单元格：提供编辑和删除功能-->
            <template v-if="column.key === 'operation'">
              <a-space>
                <!--编辑按钮-->
                <a-button type="link" size="small" @click="handleEdit(record)">
                  <template #icon><edit-outlined /></template>
                  编辑
                </a-button>
                <a-divider type="vertical" />
                <!--删除按钮（带确认框）-->
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

    <!--弹窗区域：用于新增和编辑广告的表单弹窗-->
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
          <!--广告表单：包含图片上传和链接输入-->
          <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
            <a-row :gutter="24">
              <a-col :span="24">
                <!--广告图片上传区域-->
                <a-form-item label="广告图片" name="imageFile">
                  <a-upload-dragger 
                    name="file" 
                    accept="image/*" 
                    :multiple="false" 
                    :before-upload="beforeUpload"
                    class="ad-upload"
                  >
                    <p class="ant-upload-drag-icon">
                      <!--条件渲染：有图片则显示预览，无图片则显示上传图标-->
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
                <!--广告链接输入框-->
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
/**
 * 引入组件、API和工具
 */
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

/**
 * 表格列定义
 * 
 * 定义广告列表的表头和数据展示格式：
 * - 序号列：展示行号
 * - 图片列：展示广告图片
 * - 链接列：展示跳转链接
 * - 操作列：提供编辑和删除功能
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

/**
 * 格式化链接显示
 * 
 * 对过长的链接进行截断处理，以便于在表格中美观展示
 * 
 * @param link 需要格式化的链接字符串
 * @return 格式化后的链接字符串
 */
const formatLink = (link: string) => {
  // 无链接处理
  if (!link) return '无链接';
  
  // 如果链接过长，截取显示
  if (link.length > 50) {
    return link.substring(0, 47) + '...';
  }
  return link;
};

/**
 * 图片上传前处理
 * 
 * 处理图片上传前的预览和文件名生成，不实际上传文件
 * 而是将文件对象存储到表单中，提交时再一起上传
 * 
 * @param file 用户选择的文件对象
 * @return false 阻止自动上传行为
 */
const beforeUpload = (file: File) => {
  // 改文件名：使用时间戳作为文件名，避免重名
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  console.log(copyFile);
  modal.form.imageFile = copyFile;
  
  // 创建预览：将文件转为base64用于预览
  const reader = new FileReader();
  reader.readAsDataURL(file);
  reader.onload = () => {
    modal.form.imageUrl = reader.result as string;
  };
  
  // 返回false阻止组件默认上传行为
  return false;
};

/**
 * 文件列表引用
 * 
 * 用于存储上传组件的文件列表
 */
const fileList = ref([]);

/**
 * 页面数据状态
 * 
 * 存储页面的主要数据状态，包括：
 * - 广告列表数据
 * - 加载状态
 * - 筛选条件
 * - 选中项
 * - 分页信息
 */
const data = reactive({
  // 广告列表数据
  list: [],
  // 加载状态标志
  loading: false,
  // 当前管理员用户名
  currentAdminUserName: '',
  // 搜索关键词
  keyword: '',
  // 表格选中行的key数组
  selectedRowKeys: [] as any[],
  // 每页显示条数
  pageSize: 10,
  // 当前页码
  page: 1,
});

/**
 * 弹窗相关数据
 * 
 * 存储弹窗的状态和表单数据，包括：
 * - 弹窗可见性
 * - 编辑标志
 * - 弹窗标题
 * - 表单数据
 * - 表单验证规则
 */
const modal = reactive({
  // 弹窗可见性标志
  visile: false,
  // 是否为编辑模式
  editFlag: false,
  // 弹窗标题
  title: '',
  // 表单数据对象
  form: {
    id: undefined,
    image: undefined,
    imageFile: undefined,
    imageUrl: undefined,
    link: undefined,
  },
  // 表单验证规则
  rules: {
    // 链接验证规则：必填
    link: [{ required: true, message: '请输入跳转链接', trigger: 'change' }],
    // 图片验证规则：必填，自定义验证器
    imageFile: [{ 
      required: true, 
      message: '请上传广告图片', 
      trigger: 'change',
      // 自定义验证：编辑模式下如有原图则通过验证
      validator: (rule, value) => {
        if (modal.editFlag && modal.form.image) {
          return Promise.resolve();
        }
        return modal.form.imageFile ? Promise.resolve() : Promise.reject('请上传广告图片');
      }
    }],
  },
});

/**
 * 表单实例引用
 * 
 * 用于获取表单实例，以便调用表单方法
 */
const myform = ref<FormInstance>();

/**
 * 生命周期钩子 - 组件挂载后
 * 
 * 组件挂载到DOM后执行的操作，初始化数据
 */
onMounted(() => {
  // 组件挂载后加载广告列表数据
  getList();
});

/**
 * 获取广告列表
 * 
 * 从服务器获取广告列表数据，并处理返回结果
 * 包括显示加载状态、处理图片URL等
 */
const getList = () => {
  // 显示加载状态
  data.loading = true;
  // 调用API获取列表
  listApi({
    keyword: data.keyword,
  })
    .then((res) => {
      // 隐藏加载状态
      data.loading = false;
      console.log(res);
      // 处理返回数据：添加序号、处理图片URL
      res.data.forEach((item: any, index: any) => {
        // 添加序号
        item.index = index + 1;
        // 处理图片URL：添加基础路径
        if (item.image) {
          item.imageUrl = BASE_URL + '/api/staticfiles/image/' + item.image;
        }
      });
      // 更新列表数据
      data.list = res.data;
    })
    .catch((err) => {
      // 错误处理：隐藏加载状态并记录错误
      data.loading = false;
      console.log(err);
    });
};

/**
 * 表格行选择配置
 * 
 * 配置表格的行选择功能，用于批量操作
 */
const rowSelection = ref({
  // 选择项变化时的回调函数
  onChange: (selectedRowKeys: (string | number)[], selectedRows: any[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    // 更新选中项状态
    data.selectedRowKeys = selectedRowKeys;
  },
});

/**
 * 处理添加广告
 * 
 * 点击"新增广告"按钮时触发，准备表单和弹窗
 */
const handleAdd = () => {
  // 重置表单状态
  resetModal();
  // 显示弹窗
  modal.visile = true;
  // 设置为新增模式
  modal.editFlag = false;
  // 设置弹窗标题
  modal.title = '新增广告';
  // 重置表单数据
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
};

/**
 * 处理编辑广告
 * 
 * 点击"编辑"按钮时触发，准备表单和弹窗，并填充当前数据
 * 
 * @param record 当前行的广告数据对象
 */
const handleEdit = (record: any) => {
  // 重置表单状态
  resetModal();
  // 显示弹窗
  modal.visile = true;
  // 设置为编辑模式
  modal.editFlag = true;
  // 设置弹窗标题
  modal.title = '编辑广告';
  // 重置表单数据
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  // 将当前行数据填充到表单
  for (const key in record) {
    modal.form[key] = record[key];
  }
};

/**
 * 确认删除广告
 * 
 * 用户确认删除后执行的操作，调用删除API
 * 
 * @param record 需要删除的广告数据对象
 */
const confirmDelete = (record: any) => {
  console.log('delete', record);
  // 调用删除API
  deleteApi({ ids: record.id })
    .then((res) => {
      // 删除成功提示
      message.success('删除成功');
      // 重新加载列表
      getList();
    })
    .catch((err) => {
      // 删除失败提示
      message.error(err.msg || '删除失败');
    });
};

/**
 * 处理批量删除
 * 
 * 点击"批量删除"按钮时触发，删除所有选中的广告
 */
const handleBatchDelete = () => {
  console.log(data.selectedRowKeys);
  // 检查是否有选中项
  if (data.selectedRowKeys.length <= 0) {
    message.warn('请勾选要删除的广告');
    return;
  }
  // 调用删除API，使用逗号分隔的ID列表
  deleteApi({ ids: data.selectedRowKeys.join(',') })
    .then((res) => {
      // 删除成功提示
      message.success('批量删除成功');
      // 清空选中项
      data.selectedRowKeys = [];
      // 重新加载列表
      getList();
    })
    .catch((err) => {
      // 删除失败提示
      message.error(err.msg || '删除失败');
    });
};

/**
 * 处理弹窗确认
 * 
 * 点击弹窗"确认"按钮时触发，验证表单并提交数据
 */
const handleOk = () => {
  // 验证表单
  myform.value
    ?.validate()
    .then(() => {
      // 创建FormData对象用于文件上传
      const formData = new FormData();
      // 添加ID字段（编辑模式）
      if (modal.form.id) {
        formData.append('id', modal.form.id);
      }
      // 添加链接字段
      if (modal.form.link) {
        formData.append('link', modal.form.link);
      }
      // 添加图片文件
      if (modal.form.imageFile) {
        formData.append('imageFile', modal.form.imageFile);
      }
      
      // 根据模式选择API：编辑或新增
      if (modal.editFlag) {
        // 编辑模式：调用更新API
        updateApi(formData)
          .then((res) => {
            // 更新成功提示
            message.success('更新成功');
            // 关闭弹窗
            hideModal();
            // 重新加载列表
            getList();
          })
          .catch((err) => {
            console.log(err);
            // 更新失败提示
            message.error(err.msg || '操作失败');
          });
      } else {
        // 新增模式：调用创建API
        createApi(formData)
          .then((res) => {
            // 创建成功提示
            message.success('创建成功');
            // 关闭弹窗
            hideModal();
            // 重新加载列表
            getList();
          })
          .catch((err) => {
            console.log(err);
            // 创建失败提示
            message.error(err.msg || '操作失败');
          });
      }
    })
    .catch((err) => {
      // 表单验证失败日志
      console.log('表单验证失败');
    });
};

/**
 * 处理弹窗取消
 * 
 * 点击弹窗"取消"按钮或关闭图标时触发
 */
const handleCancel = () => {
  // 关闭弹窗
  hideModal();
};

/**
 * 重置弹窗表单
 * 
 * 重置表单字段为初始状态
 */
const resetModal = () => {
  // 调用表单的重置方法
  myform.value?.resetFields();
};

/**
 * 隐藏弹窗
 * 
 * 设置弹窗为不可见状态
 */
const hideModal = () => {
  // 设置弹窗不可见
  modal.visile = false;
};
</script>

<style scoped lang="less">
/**
 * 页面布局样式
 * 设置整体页面的布局、背景和间距
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
 * 设置标题和描述文本的样式
 */
.header-section {
  margin-bottom: 8px;
  
  /* 页面主标题样式 */
  .page-title {
    margin: 0 0 8px 0;
    font-size: 24px;
    font-weight: 500;
    color: #333;
  }
  
  /* 页面描述文本样式 */
  .page-description {
    margin: 0;
    color: #666;
    font-size: 14px;
  }
}

/**
 * 表格操作区域样式
 * 设置操作按钮区域的对齐和按钮样式
 */
.table-operations {
  margin-bottom: 16px;
  text-align: right;
  
  /* 主要按钮样式自定义 */
  :deep(.ant-btn-primary) {
    background-color: #5a7be0;
    border-color: #5a7be0;
    
    /* 主要按钮悬停效果 */
    &:hover {
      background-color: #6e89e8;
      border-color: #6e89e8;
    }
  }
}

/**
 * 表格卡片样式
 * 设置表格容器的圆角、阴影和表头样式
 */
.table-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
  
  /* 表头样式自定义 */
  :deep(.ant-table-thead > tr > th) {
    background-color: #f5f7ff;
    color: #333;
    font-weight: 500;
  }
  
  /* 分页当前页样式 */
  :deep(.ant-pagination-item-active) {
    border-color: #5a7be0;
    
    a {
      color: #5a7be0;
    }
  }
  
  /* 表格行悬停效果 */
  :deep(.ant-table-row:hover) {
    td {
      background-color: #f5f7ff !important;
    }
  }
}

/**
 * 广告图片样式
 * 设置表格中广告图片的尺寸和效果
 */
.ad-image-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
  
  /* 广告图片样式 */
  .ad-image {
    width: 150px;
    height: 40px;
    object-fit: cover;
    border-radius: 4px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.08);
    transition: transform 0.3s;
    
    /* 图片悬停放大效果 */
    &:hover {
      transform: scale(1.05);
    }
  }
}

/**
 * 广告链接样式
 * 设置表格中广告链接的颜色和效果
 */
.ad-link {
  color: #5a7be0;
  text-decoration: none;
  
  /* 链接悬停效果 */
  &:hover {
    text-decoration: underline;
  }
}

/**
 * 广告上传区域样式
 * 设置图片上传区域的边框和预览效果
 */
.ad-upload {
  /* 拖拽上传区域样式 */
  :deep(.ant-upload-drag) {
    border-color: #d9d9d9;
    border-radius: 4px;
    
    /* 拖拽区域悬停效果 */
    &:hover {
      border-color: #5a7be0;
    }
  }
  
  /* 图片预览样式 */
  .preview-image {
    max-width: 300px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
  }
  
  /* 上传图标样式 */
  .upload-icon {
    font-size: 32px;
    color: #999;
  }
}

/**
 * 表单帮助文本样式
 * 设置表单下方的提示文本样式
 */
.form-help-text {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}
</style>
