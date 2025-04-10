<!--
/**
 * 地址管理组件
 * 
 * 该组件实现了电商平台的用户地址管理功能，包括：
 * 1. 地址列表展示
 * 2. 新增地址
 * 3. 编辑地址
 * 4. 删除地址
 * 5. 设置默认地址
 * 6. 手机号码脱敏处理
 * 
 * 组件依赖：
 * - Vue 3.x
 * - Ant Design Vue
 * - Vuex/Pinia
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */
-->

<template>
  <!-- 地址管理主容器 -->
  <div class="address-view">
    <!-- 页面标题和新增按钮 -->
    <div class="page-header">
      <h2 class="page-title">地址管理</h2>
      <a-button type="primary" class="add-btn" @click="handleAdd">
        <plus-outlined />
        新建地址
      </a-button>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <a-spin tip="加载中..." />
    </div>
    
    <!-- 空状态 -->
    <div v-else-if="pageData.addressData.length === 0" class="empty-container">
      <a-empty description="暂无地址信息" />
      <a-button type="primary" @click="handleAdd" class="add-empty-btn">
        添加新地址
      </a-button>
    </div>
    
    <!-- 地址列表 -->
    <div v-else class="address-list">
      <!-- 地址卡片 -->
      <div
        v-for="(item, index) in pageData.addressData"
        :key="index"
        class="address-card"
        :class="{ 'is-default': item.def === '1' }"
      >
        <!-- 地址信息内容 -->
        <div class="card-content">
          <!-- 用户信息区域 -->
          <div class="user-info">
            <span class="name">{{ item.name }}</span>
            <span class="phone">{{ formatPhone(item.mobile) }}</span>
            <a-tag v-if="item.def === '1'" color="blue" class="default-tag">默认</a-tag>
          </div>
          
          <!-- 地址详情 -->
          <div class="address-info">
            {{ item.description }}
          </div>
        </div>
        
        <!-- 操作按钮区域 -->
        <div class="card-actions">
          <!-- 编辑按钮 -->
          <a-button type="text" class="edit-btn" @click="handleEdit(item)">
            <edit-outlined />
            编辑
          </a-button>
          
          <!-- 删除按钮（带确认） -->
          <a-popconfirm
            title="确定要删除这个地址吗？"
            ok-text="确定"
            cancel-text="取消"
            @confirm="handleDelete(item)"
          >
            <a-button type="text" class="delete-btn">
              <delete-outlined />
              删除
            </a-button>
          </a-popconfirm>
          
          <!-- 设为默认地址按钮（非默认地址才显示） -->
          <a-button
            v-if="item.def !== '1'"
            type="text"
            class="default-btn"
            @click="setAsDefault(item)"
          >
            设为默认
          </a-button>
        </div>
      </div>
    </div>
    
    <!-- 新增/编辑地址弹窗 -->
    <a-modal
      :visible="modal.visile"
      :title="modal.title"
      :maskClosable="false"
      :destroyOnClose="true"
      ok-text="保存"
      cancel-text="取消"
      @cancel="handleCancel"
      @ok="handleOk"
      class="address-modal"
    >
      <!-- 地址表单 -->
      <a-form
        ref="myform"
        :model="modal.form"
        :rules="rules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
      >
        <!-- 收件人表单项 -->
        <a-form-item label="收件人" name="name">
          <a-input
            v-model:value="modal.form.name"
            placeholder="请输入收件人姓名"
            :maxLength="20"
          />
        </a-form-item>
        
        <!-- 手机号码表单项 -->
        <a-form-item label="手机号码" name="mobile">
          <a-input
            v-model:value="modal.form.mobile"
            placeholder="请输入手机号码"
            :maxLength="11"
          />
        </a-form-item>
        
        <!-- 详细地址表单项 -->
        <a-form-item label="详细地址" name="description">
          <a-textarea
            v-model:value="modal.form.description"
            placeholder="请输入详细地址，包括省/市/区/街道/门牌号等"
            :rows="3"
            :maxLength="100"
          />
        </a-form-item>
        
        <!-- 默认地址表单项 -->
        <a-form-item label="默认地址" name="default">
          <a-switch
            v-model:checked="modal.form.default"
            :checked-children="'是'"
            :un-checked-children="'否'"
          />
          <span class="default-tip">设为默认地址后，下次购物会自动选择该地址</span>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
/**
 * 导入Vue相关依赖
 */
import { FormInstance, message } from 'ant-design-vue';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import { listApi, deleteApi, createApi, updateApi } from '/@/api/address';
import { useUserStore } from "/@/store";
import { ref, reactive, onMounted } from 'vue';

/**
 * 初始化Vue相关实例和状态
 */
const userStore = useUserStore();
const loading = ref(false);

/**
 * 页面数据
 * 包含地址列表信息
 */
const pageData = reactive({
  addressData: []
});

/**
 * 表单验证规则
 * 定义地址相关字段的验证规则
 */
const rules = {
  name: [
    { required: true, message: '请输入收件人姓名', trigger: 'blur' },
    { max: 20, message: '姓名最多20个字符', trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { max: 100, message: '地址最多100个字符', trigger: 'blur' }
  ]
};

/**
 * 弹窗数据源
 * 包含弹窗状态和表单数据
 */
const modal = reactive({
  visile: false,
  editFlag: false,
  title: '',
  form: {
    id: undefined,
    name: undefined,
    mobile: undefined,
    description: undefined,
    default: false
  }
});

/**
 * 表单实例引用
 * 用于表单验证和重置
 */
const myform = ref<FormInstance>();

/**
 * 组件挂载后执行
 * 初始化地址列表数据
 */
onMounted(() => {
  listAddressData();
});

/**
 * 格式化手机号
 * 
 * @description 将手机号中间4位显示为****进行脱敏处理
 * @param {string} phone - 原始手机号码
 * @returns {string} 格式化后的手机号码
 */
const formatPhone = (phone) => {
  if (!phone) return '';
  if (phone.length !== 11) return phone;
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

/**
 * 获取地址列表
 * 
 * @description 从API获取当前用户的地址列表数据
 * @throws {Error} 当API请求失败时抛出错误
 */
const listAddressData = () => {
  loading.value = true;
  const userId = userStore.user_id;
  
  listApi({ userId: userId })
    .then(res => {
      pageData.addressData = res.data || [];
      loading.value = false;
    })
    .catch(err => {
      console.error('获取地址列表失败', err);
      message.error('获取地址列表失败');
      loading.value = false;
    });
};

/**
 * 删除地址
 * 
 * @description 删除指定的地址记录
 * @param {Object} item - 要删除的地址对象
 * @throws {Error} 当API请求失败时抛出错误
 */
const handleDelete = (item) => {
  if (!item || !item.id) {
    message.error('地址信息不完整');
    return;
  }
  
  loading.value = true;
  deleteApi({ ids: item.id })
    .then(res => {
      message.success('删除成功');
      listAddressData();
    })
    .catch(err => {
      console.error('删除地址失败', err);
      message.error('删除地址失败');
      loading.value = false;
    });
};

/**
 * 添加地址
 * 
 * @description 打开添加地址的弹窗并重置表单
 */
const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增地址';
  
  // 重置表单
  modal.form = {
    id: undefined,
    name: undefined,
    mobile: undefined,
    description: undefined,
    default: false
  };
};

/**
 * 编辑地址
 * 
 * @description 打开编辑地址的弹窗并填充数据
 * @param {Object} record - 要编辑的地址对象
 */
const handleEdit = (record) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑地址';
  
  // 填充表单数据
  modal.form = {
    id: record.id,
    name: record.name,
    mobile: record.mobile,
    description: record.description,
    default: record.def === '1'
  };
};

/**
 * 设为默认地址
 * 
 * @description 将指定地址设置为默认地址
 * @param {Object} item - 要设为默认的地址对象
 * @throws {Error} 当API请求失败时抛出错误
 */
const setAsDefault = (item) => {
  if (!item || !item.id) {
    message.error('地址信息不完整');
    return;
  }
  
  loading.value = true;
  const formData = new FormData();
  formData.append('userId', userStore.user_id);
  formData.append('id', item.id);
  formData.append('def', '1');
  formData.append('name', item.name);
  formData.append('mobile', item.mobile);
  formData.append('description', item.description);
  
  updateApi(formData)
    .then(() => {
      message.success('已设为默认地址');
      listAddressData();
    })
    .catch(err => {
      console.error('设置默认地址失败', err);
      message.error('设置默认地址失败');
      loading.value = false;
    });
};

/**
 * 提交表单
 * 
 * @description 验证并提交地址表单数据
 * @throws {Error} 当表单验证失败或API请求失败时抛出错误
 */
const handleOk = () => {
  myform.value?.validate()
    .then(() => {
      // 构建表单数据
      const formData = new FormData();
      formData.append('userId', userStore.user_id);
      formData.append('def', modal.form.default ? '1' : '0');
      
      // 添加ID字段（编辑模式）
      if (modal.form.id) {
        formData.append('id', modal.form.id);
      }
      
      // 添加表单字段
      if (modal.form.name) {
        formData.append('name', modal.form.name);
      }
      
      if (modal.form.mobile) {
        formData.append('mobile', modal.form.mobile);
      }
      
      if (modal.form.description) {
        formData.append('description', modal.form.description);
      }
      
      loading.value = true;
      
      // 根据模式选择API
      if (modal.editFlag) {
        // 编辑模式
        updateApi(formData)
          .then(() => {
            message.success('地址更新成功');
            hideModal();
            listAddressData();
          })
          .catch(err => {
            console.error('更新地址失败', err);
            message.error('更新地址失败');
            loading.value = false;
          });
      } else {
        // 新增模式
        createApi(formData)
          .then(() => {
            message.success('地址添加成功');
            hideModal();
            listAddressData();
          })
          .catch(err => {
            console.error('添加地址失败', err);
            message.error('添加地址失败');
            loading.value = false;
          });
      }
    })
    .catch(() => {
      message.error('请完善表单信息');
    });
};

/**
 * 取消操作
 * 
 * @description 关闭弹窗并取消当前操作
 */
const handleCancel = () => {
  hideModal();
};

/**
 * 重置表单状态
 * 
 * @description 清空表单数据和验证状态
 */
const resetModal = () => {
  myform.value?.resetFields();
};

/**
 * 关闭弹窗
 * 
 * @description 隐藏地址编辑弹窗
 */
const hideModal = () => {
  modal.visile = false;
};
</script>

<style scoped lang="less">
/**
 * 地址管理视图样式
 * 定义整体布局和内边距
 */
.address-view {
  padding: 0 20px;
  
  /**
   * 页面标题区域样式
   * 设置标题和添加按钮的布局
   */
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    /**
     * 页面标题样式
     */
    .page-title {
      font-size: 22px;
      font-weight: 600;
      color: #222;
      margin: 0;
    }
    
    /**
     * 添加按钮样式
     */
    .add-btn {
      display: flex;
      align-items: center;
      
      /**
       * 图标样式
       */
      .anticon {
        margin-right: 4px;
      }
    }
  }
  
  /**
   * 加载中和空状态容器
   * 居中显示加载状态和空数据提示
   */
  .loading-container, .empty-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 0;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    
    /**
     * 空状态下的添加按钮
     */
    .add-empty-btn {
      margin-top: 20px;
    }
  }
  
  /**
   * 地址列表样式
   * 使用网格布局实现响应式多列
   */
  .address-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    
    /**
     * 地址卡片样式
     * 设置卡片基本样式和过渡效果
     */
    .address-card {
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      transition: all 0.3s;
      overflow: hidden;
      position: relative;
      
      /**
       * 卡片悬浮效果
       * 增加阴影并显示操作按钮
       */
      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        
        .card-actions {
          opacity: 1;
          transform: translateY(0);
        }
      }
      
      /**
       * 默认地址样式
       * 为默认地址添加特殊样式
       */
      &.is-default {
        border: 1px solid #4684e2;
        
        /**
         * 默认地址标识
         * 右上角添加小三角标识
         */
        &:before {
          content: '';
          position: absolute;
          top: 0;
          right: 0;
          width: 0;
          height: 0;
          border-style: solid;
          border-width: 0 32px 32px 0;
          border-color: transparent #4684e2 transparent transparent;
          z-index: 1;
        }
      }
      
      /**
       * 卡片内容区样式
       * 设置内边距和布局
       */
      .card-content {
        padding: 20px;
        
        /**
         * 用户信息区域样式
         * 包含姓名和电话
         */
        .user-info {
          margin-bottom: 10px;
          
          /**
           * 姓名样式
           */
          .name {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            margin-right: 10px;
          }
          
          /**
           * 电话号码样式
           */
          .phone {
            color: #666;
          }
          
          /**
           * 默认标签样式
           */
          .default-tag {
            margin-left: 10px;
            font-size: 12px;
          }
        }
        
        /**
         * 地址信息样式
         * 设置文本样式和断行规则
         */
        .address-info {
          color: #333;
          font-size: 14px;
          line-height: 1.6;
          word-break: break-all;
          margin-top: 8px;
        }
      }
      
      /**
       * 卡片操作区样式
       * 定义底部操作按钮的布局
       */
      .card-actions {
        display: flex;
        padding: 12px 20px;
        border-top: 1px solid #f0f0f0;
        background: #fafafa;
        transition: all 0.2s;
        
        /**
         * 操作按钮通用样式
         */
        .edit-btn, .delete-btn, .default-btn {
          display: flex;
          align-items: center;
          margin-right: 16px;
          font-size: 13px;
          
          /**
           * 图标样式
           */
          .anticon {
            margin-right: 4px;
          }
        }
        
        /**
         * 编辑按钮样式
         */
        .edit-btn {
          color: #4684e2;
        }
        
        /**
         * 删除按钮样式
         */
        .delete-btn {
          color: #ff4d4f;
        }
        
        /**
         * 设为默认按钮样式
         */
        .default-btn {
          color: #666;
          margin-left: auto;
          margin-right: 0;
        }
      }
    }
  }
}

/**
 * 地址弹窗样式
 * 使用深度选择器修改弹窗内部样式
 */
:deep(.address-modal) {
  /**
   * 默认地址提示文本样式
   */
  .default-tip {
    margin-left: 12px;
    font-size: 12px;
    color: #999;
  }
}
</style>
