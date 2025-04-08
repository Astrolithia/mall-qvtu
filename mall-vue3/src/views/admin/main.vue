<template>
  <a-layout id="admin-layout">
    <a-layout-header>
      <div class="header">
        <div class="header-left">
          <div class="logo-container">
            <div class="shopping-bag">
              <span class="bag-handle"></span>
              <span class="bag-body"></span>
              <span class="shine"></span>
            </div>
            <span class="header-title">品质商城管理系统</span>
          </div>
        </div>
        <div class="header-right">
          <a-button type="primary" class="preview-btn" @click="handlePreview">
            <eye-outlined /> 前台预览
          </a-button>
          <div class="admin-info">
            <a-avatar class="admin-avatar">
              <template #icon><user-outlined /></template>
            </a-avatar>
            <div class="admin-detail">
              <span class="admin-name">{{ userStore.admin_user_name }}</span>
              <span class="admin-role">管理员</span>
            </div>
            <a-dropdown>
              <a class="dropdown-link">
                <down-outlined />
              </a>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="profile">
                    <user-outlined /> 个人资料
                  </a-menu-item>
                  <a-menu-item key="settings">
                    <setting-outlined /> 系统设置
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout" @click="handleLogout">
                    <logout-outlined /> 退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </div>
      </div>
    </a-layout-header>
    <a-layout>
      <a-layout-sider 
        v-model="collapsed" 
        collapsible 
        :trigger="null"
        class="custom-sider"
      >
        <div class="sider-header">
          <menu-unfold-outlined
            v-if="collapsed"
            class="trigger"
            @click="collapsed = !collapsed"
          />
          <menu-fold-outlined
            v-else
            class="trigger"
            @click="collapsed = !collapsed"
          />
          <span v-if="!collapsed" class="sider-title">管理菜单</span>
        </div>
        <a-menu 
          v-model:selectedKeys="selectedKeys" 
          theme="light" 
          mode="inline" 
          @click="handleClick"
          class="custom-menu"
        >
          <a-menu-item key="overview" class="menu-item">
            <template #icon><dashboard-outlined /></template>
            <span>数据概览</span>
          </a-menu-item>
          <a-menu-item key="user" class="menu-item">
            <template #icon><user-outlined /></template>
            <span>客户管理</span>
          </a-menu-item>
          <a-menu-item key="classification" class="menu-item">
            <template #icon><partition-outlined /></template>
            <span>商品分类</span>
          </a-menu-item>
          <a-menu-item key="tag" class="menu-item">
            <template #icon><tag-outlined /></template>
            <span>商品标签</span>
          </a-menu-item>
          <a-menu-item key="order" class="menu-item">
            <template #icon><shopping-outlined /></template>
            <span>订单中心</span>
          </a-menu-item>
          <a-menu-item key="thing" class="menu-item">
            <template #icon><shopping-cart-outlined /></template>
            <span>商品库存</span>
          </a-menu-item>
          <a-menu-item key="comment" class="menu-item">
            <template #icon><comment-outlined /></template>
            <span>用户评价</span>
          </a-menu-item>
          <a-sub-menu class="menu-subitem">
            <template #icon><appstore-outlined /></template>
            <template #title>营销中心</template>
            <a-menu-item key="ad">
              <template #icon><fund-outlined /></template>
              <span>推广广告</span>
            </a-menu-item>
            <a-menu-item key="banner">
              <template #icon><picture-outlined /></template>
              <span>首页轮播</span>
            </a-menu-item>
            <a-menu-item key="notice">
              <template #icon><notification-outlined /></template>
              <span>平台公告</span>
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu class="menu-subitem">
            <template #icon><file-search-outlined /></template>
            <template #title>系统日志</template>
            <a-menu-item key="loginLog">
              <template #icon><login-outlined /></template>
              <span>登录记录</span>
            </a-menu-item>
            <a-menu-item key="opLog">
              <template #icon><interaction-outlined /></template>
              <span>操作记录</span>
            </a-menu-item>
            <a-menu-item key="errorLog">
              <template #icon><warning-outlined /></template>
              <span>异常日志</span>
            </a-menu-item>
          </a-sub-menu>
          <a-menu-item key="sysInfo" class="menu-item">
            <template #icon><setting-outlined /></template>
            <span>系统设置</span>
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
      <a-layout>
        <a-layout-content class="content-area">
          <div class="content-wrapper">
            <router-view />
          </div>
        </a-layout-content>
        <a-layout-footer class="footer">
          品质商城管理系统 ©2025 Created by Admin
        </a-layout-footer>
      </a-layout>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted } from 'vue';
import { useUserStore } from "/@/store";

import {
  HomeOutlined,
  AppstoreOutlined,
  FolderOutlined,
  UserOutlined,
  CommentOutlined,
  InfoCircleOutlined,
  TagOutlined,
  PieChartOutlined,
  DollarOutlined,
  LayoutOutlined,
  DatabaseOutlined,
  PictureOutlined,
  EyeOutlined,
  SettingOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  ShoppingOutlined,
  ShoppingCartOutlined,
  PartitionOutlined,
  FileSearchOutlined,
  LoginOutlined,
  InteractionOutlined,
  WarningOutlined,
  DownOutlined,
  NotificationOutlined,
  FundOutlined
} from '@ant-design/icons-vue';

const userStore = useUserStore();
const selectedKeys = ref<any[]>([]);
const collapsed = ref<boolean>(false);

const router = useRouter();
const route = useRoute();

const handleClick = ({item, key, keyPath}) => {
  console.log('点击路由===>', key);
  router.push({
    name: key,
  });
};

const handlePreview = () => {
  let text = router.resolve({name: 'index'});
  window.open(text.href, '_blank');
};

onMounted(() => {
  console.log('当前路由===>', route.name);
  selectedKeys.value = [route.name];
});

const handleLogout = () => {
  userStore.adminLogout().then(res => {
    router.push({name: 'adminLogin'});
  });
};
</script>

<style scoped lang="less">
#admin-layout {
  height: 100%;
  
  :deep(.ant-layout) {
    height: 100%;
  }
}

// Header styles
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 100%;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  
  .header-left {
    display: flex;
    align-items: center;
    
    .logo-container {
      display: flex;
      align-items: center;
      
      .shopping-bag {
        position: relative;
        width: 32px;
        height: 32px;
        margin-right: 12px;
        
        .bag-handle {
          position: absolute;
          width: 16px;
          height: 6px;
          border: 2px solid #5a7be0;
          border-bottom: none;
          border-radius: 4px 4px 0 0;
          top: 0;
          left: 50%;
          transform: translateX(-50%);
        }
        
        .bag-body {
          position: absolute;
          width: 24px;
          height: 24px;
          background-color: #5a7be0;
          border-radius: 4px;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
        }
        
        .shine {
          position: absolute;
          width: 6px;
          height: 6px;
          background-color: rgba(255, 255, 255, 0.7);
          border-radius: 50%;
          top: 12px;
          left: 10px;
        }
      }
      
      .header-title {
        font-size: 18px;
        font-weight: 600;
        color: #333;
        line-height: 64px;
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .preview-btn {
      background: #5a7be0;
      border-radius: 4px;
      border-color: #5a7be0;
      
      &:hover {
        background: #6e89e8;
        border-color: #6e89e8;
      }
    }
    
    .admin-info {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 0 8px;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        background: #f5f7fa;
      }
      
      .admin-avatar {
        background: #5a7be0;
        color: #fff;
      }
      
      .admin-detail {
        display: flex;
        flex-direction: column;
        line-height: 1.2;
        
        .admin-name {
          font-size: 14px;
          font-weight: 500;
          color: #333;
        }
        
        .admin-role {
          font-size: 12px;
          color: #999;
        }
      }
      
      .dropdown-link {
        color: #999;
        padding: 0 4px;
        
        &:hover {
          color: #333;
        }
      }
    }
  }
}

// Sider styles
.custom-sider {
  background: #fff;
  border-right: 1px solid #f0f0f0;
  
  .sider-header {
    display: flex;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
    
    .trigger {
      font-size: 16px;
      color: #999;
      cursor: pointer;
      
      &:hover {
        color: #5a7be0;
      }
    }
    
    .sider-title {
      font-size: 14px;
      font-weight: 500;
      color: #333;
      margin-left: 12px;
    }
  }
  
  .custom-menu {
    border-right: none;
    
    :deep(.ant-menu-item), :deep(.ant-menu-submenu-title) {
      height: 48px;
      line-height: 48px;
      margin: 4px 0;
      padding-left: 24px !important;
      border-radius: 0 24px 24px 0;
      
      &:hover {
        color: #5a7be0;
      }
      
      &.ant-menu-item-selected {
        background: #eef2ff;
        color: #5a7be0;
        font-weight: 500;
        
        &::after {
          border-right: 3px solid #5a7be0;
        }
      }
    }
    
    :deep(.ant-menu-submenu-title) {
      font-weight: 500;
    }
    
    :deep(.ant-menu-sub) {
      background: #fafafa;
      
      .ant-menu-item {
        height: 40px;
        line-height: 40px;
        padding-left: 60px !important;
        
        &::after {
          left: 0;
        }
      }
    }
  }
}

// Content styles
.content-area {
  padding: 24px;
  background: #f0f2fa;
  overflow: auto;
  
  .content-wrapper {
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 10px rgba(90, 123, 224, 0.08);
    padding: 24px;
    min-height: calc(100% - 120px);
  }
}

// Footer styles
.footer {
  text-align: center;
  padding: 16px 50px;
  color: #666;
  font-size: 14px;
  background: #fff;
  border-top: 1px solid #eaeffd;
}

:deep(.ant-layout-header) {
  height: 64px;
  padding: 0;
  line-height: 64px;
  background: #fff;
}

:deep(.ant-layout-sider-trigger) {
  display: none;
}

@media (max-width: 768px) {
  .admin-detail {
    display: none !important;
  }
  
  .header-title {
    display: none;
  }
}
</style>
