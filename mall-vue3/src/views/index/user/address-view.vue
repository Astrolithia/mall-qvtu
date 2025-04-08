<template>
  <div class="address-view">
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
      <div
        v-for="(item, index) in pageData.addressData"
        :key="index"
        class="address-card"
        :class="{ 'is-default': item.def === '1' }"
      >
        <div class="card-content">
          <div class="user-info">
            <span class="name">{{ item.name }}</span>
            <span class="phone">{{ formatPhone(item.mobile) }}</span>
            <a-tag v-if="item.def === '1'" color="blue" class="default-tag">默认</a-tag>
          </div>
          
          <div class="address-info">
            {{ item.description }}
          </div>
        </div>
        
        <div class="card-actions">
          <a-button type="text" class="edit-btn" @click="handleEdit(item)">
            <edit-outlined />
            编辑
          </a-button>
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
      <a-form
        ref="myform"
        :model="modal.form"
        :rules="rules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
      >
        <a-form-item label="收件人" name="name">
          <a-input
            v-model:value="modal.form.name"
            placeholder="请输入收件人姓名"
            :maxLength="20"
          />
        </a-form-item>
        
        <a-form-item label="手机号码" name="mobile">
          <a-input
            v-model:value="modal.form.mobile"
            placeholder="请输入手机号码"
            :maxLength="11"
          />
        </a-form-item>
        
        <a-form-item label="详细地址" name="description">
          <a-textarea
            v-model:value="modal.form.description"
            placeholder="请输入详细地址，包括省/市/区/街道/门牌号等"
            :rows="3"
            :maxLength="100"
          />
        </a-form-item>
        
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
import { FormInstance, message } from 'ant-design-vue';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import { listApi, deleteApi, createApi, updateApi } from '/@/api/address';
import { useUserStore } from "/@/store";
import { ref, reactive, onMounted } from 'vue';

const userStore = useUserStore();
const loading = ref(false);

// 页面数据
const pageData = reactive({
  addressData: []
});

// 表单验证规则
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

// 弹窗数据源
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

const myform = ref<FormInstance>();

onMounted(() => {
  listAddressData();
});

// 格式化手机号，中间4位显示为****
const formatPhone = (phone) => {
  if (!phone) return '';
  if (phone.length !== 11) return phone;
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

// 获取地址列表
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

// 删除地址
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

// 添加地址
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

// 编辑地址
const handleEdit = (record) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑地址';
  
  // 重置表单
  modal.form = {
    id: record.id,
    name: record.name,
    mobile: record.mobile,
    description: record.description,
    default: record.def === '1'
  };
};

// 设为默认地址
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

// 提交表单
const handleOk = () => {
  myform.value?.validate()
    .then(() => {
      const formData = new FormData();
      formData.append('userId', userStore.user_id);
      formData.append('def', modal.form.default ? '1' : '0');
      
      if (modal.form.id) {
        formData.append('id', modal.form.id);
      }
      
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
      
      if (modal.editFlag) {
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

// 取消操作
const handleCancel = () => {
  hideModal();
};

// 重置表单状态
const resetModal = () => {
  myform.value?.resetFields();
};

// 关闭弹窗
const hideModal = () => {
  modal.visile = false;
};
</script>

<style scoped lang="less">
.address-view {
  padding: 0 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .page-title {
      font-size: 22px;
      font-weight: 600;
      color: #222;
      margin: 0;
    }
    
    .add-btn {
      display: flex;
      align-items: center;
      
      .anticon {
        margin-right: 4px;
      }
    }
  }
  
  .loading-container, .empty-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 0;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    
    .add-empty-btn {
      margin-top: 20px;
    }
  }
  
  .address-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    
    .address-card {
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      transition: all 0.3s;
      overflow: hidden;
      position: relative;
      
      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        
        .card-actions {
          opacity: 1;
          transform: translateY(0);
        }
      }
      
      &.is-default {
        border: 1px solid #4684e2;
        
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
      
      .card-content {
        padding: 20px;
        
        .user-info {
          margin-bottom: 10px;
          
          .name {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            margin-right: 10px;
          }
          
          .phone {
            color: #666;
          }
          
          .default-tag {
            margin-left: 10px;
            font-size: 12px;
          }
        }
        
        .address-info {
          color: #333;
          font-size: 14px;
          line-height: 1.6;
          word-break: break-all;
          margin-top: 8px;
        }
      }
      
      .card-actions {
        display: flex;
        padding: 12px 20px;
        border-top: 1px solid #f0f0f0;
        background: #fafafa;
        transition: all 0.2s;
        
        .edit-btn, .delete-btn, .default-btn {
          display: flex;
          align-items: center;
          margin-right: 16px;
          font-size: 13px;
          
          .anticon {
            margin-right: 4px;
          }
        }
        
        .edit-btn {
          color: #4684e2;
        }
        
        .delete-btn {
          color: #ff4d4f;
        }
        
        .default-btn {
          color: #666;
          margin-left: auto;
          margin-right: 0;
        }
      }
    }
  }
}

:deep(.address-modal) {
  .default-tip {
    margin-left: 12px;
    font-size: 12px;
    color: #999;
  }
}
</style>
