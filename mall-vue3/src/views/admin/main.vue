<!--
/**
 * 管理系统主界面组件
 * 
 * 该组件实现了电商后台管理系统的整体框架布局，包括：
 * 1. 顶部导航栏：系统标题、管理员信息和操作菜单
 * 2. 侧边栏导航：可折叠的功能菜单栏
 * 3. 内容区域：动态加载各功能页面
 * 4. 页脚信息：版权和系统信息
 * 
 * 组件依赖：
 * - Ant Design Vue 组件库
 * - Vue Router 路由管理
 * - Pinia 状态管理
 * - 各类图标组件
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <a-layout id="admin-layout">
    <!--顶部导航栏区域-->
    <a-layout-header>
      <div class="header">
        <!--左侧 Logo 和系统标题-->
        <div class="header-left">
          <div class="logo-container">
            <!--自定义购物袋 Logo-->
            <div class="shopping-bag">
              <span class="bag-handle"></span>
              <span class="bag-body"></span>
              <span class="shine"></span>
            </div>
            <span class="header-title">品质商城管理系统</span>
          </div>
        </div>
        <!--右侧管理员信息和操作按钮-->
        <div class="header-right">
          <!--前台预览按钮-->
          <a-button type="primary" class="preview-btn" @click="handlePreview">
            <eye-outlined /> 前台预览
          </a-button>
          <!--管理员信息区域-->
          <div class="admin-info">
            <!--管理员头像-->
            <a-avatar class="admin-avatar">
              <template #icon><user-outlined /></template>
            </a-avatar>
            <!--管理员详细信息-->
            <div class="admin-detail">
              <span class="admin-name">{{ userStore.admin_user_name }}</span>
              <span class="admin-role">管理员</span>
            </div>
            <!--管理员操作下拉菜单-->
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
    <!--主体内容区域-->
    <a-layout>
      <!--侧边导航菜单区域-->
      <a-layout-sider 
        v-model="collapsed" 
        collapsible 
        :trigger="null"
        class="custom-sider"
      >
        <!--侧边栏头部区域-->
        <div class="sider-header">
          <!--菜单折叠/展开按钮-->
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
        <!--侧边栏主菜单-->
        <a-menu 
          v-model:selectedKeys="selectedKeys" 
          theme="light" 
          mode="inline" 
          @click="handleClick"
          class="custom-menu"
        >
          <!--数据概览菜单项-->
          <a-menu-item key="overview" class="menu-item">
            <template #icon><dashboard-outlined /></template>
            <span>数据概览</span>
          </a-menu-item>
          <!--客户管理菜单项-->
          <a-menu-item key="user" class="menu-item">
            <template #icon><user-outlined /></template>
            <span>客户管理</span>
          </a-menu-item>
          <!--商品分类菜单项-->
          <a-menu-item key="classification" class="menu-item">
            <template #icon><partition-outlined /></template>
            <span>商品分类</span>
          </a-menu-item>
          <!--商品标签菜单项-->
          <a-menu-item key="tag" class="menu-item">
            <template #icon><tag-outlined /></template>
            <span>商品标签</span>
          </a-menu-item>
          <!--订单中心菜单项-->
          <a-menu-item key="order" class="menu-item">
            <template #icon><shopping-outlined /></template>
            <span>订单中心</span>
          </a-menu-item>
          <!--商品库存菜单项-->
          <a-menu-item key="thing" class="menu-item">
            <template #icon><shopping-cart-outlined /></template>
            <span>商品库存</span>
          </a-menu-item>
          <!--用户评价菜单项-->
          <a-menu-item key="comment" class="menu-item">
            <template #icon><comment-outlined /></template>
            <span>用户评价</span>
          </a-menu-item>
          <!--营销中心子菜单-->
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
          <!--系统日志子菜单-->
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
          <!--系统设置菜单项-->
          <a-menu-item key="sysInfo" class="menu-item">
            <template #icon><setting-outlined /></template>
            <span>系统设置</span>
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
      <!--内容和页脚区域-->
      <a-layout>
        <!--主内容区域-->
        <a-layout-content class="content-area">
          <div class="content-wrapper">
            <!--动态路由视图区，加载不同功能页面-->
            <router-view />
          </div>
        </a-layout-content>
        <!--页脚区域-->
        <a-layout-footer class="footer">
          品质商城管理系统 ©2025 Created by Admin
        </a-layout-footer>
      </a-layout>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
/**
 * 引入必要的依赖
 * - Vue Router：用于路由管理
 * - Vue Composition API：用于组件逻辑
 * - Pinia 用户状态仓库：管理用户状态
 * - Ant Design Vue 图标：UI界面图标
 */
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted } from 'vue';
import { useUserStore } from "/@/store";

// 导入所需的图标组件
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

/**
 * 状态管理
 */
// 用户状态仓库，用于管理用户信息和登录状态
const userStore = useUserStore();
// 当前选中的菜单项，用于高亮显示
const selectedKeys = ref<any[]>([]);
// 侧边栏折叠状态
const collapsed = ref<boolean>(false);

// 路由实例，用于页面导航
const router = useRouter();
// 当前路由信息
const route = useRoute();

/**
 * 处理菜单点击事件
 * 根据点击的菜单项跳转到对应的路由页面
 * 
 * @param {Object} param - 点击事件参数
 * @param {Object} param.item - 菜单项元素
 * @param {string} param.key - 菜单项的key值，对应路由名称
 * @param {Array} param.keyPath - 菜单项的路径
 */
const handleClick = ({item, key, keyPath}) => {
  console.log('点击路由===>', key);
  router.push({
    name: key,
  });
};

/**
 * 处理前台预览
 * 在新窗口打开前台页面
 */
const handlePreview = () => {
  let text = router.resolve({name: 'index'});
  window.open(text.href, '_blank');
};

/**
 * 组件挂载后的初始化操作
 * 设置当前路由对应的菜单项选中状态
 */
onMounted(() => {
  console.log('当前路由===>', route.name);
  selectedKeys.value = [route.name];
});

/**
 * 处理退出登录
 * 调用用户仓库的登出方法，然后跳转到登录页
 */
const handleLogout = () => {
  userStore.adminLogout().then(res => {
    router.push({name: 'adminLogin'});
  });
};
</script>

<style scoped lang="less">
/**
 * 管理系统布局样式
 * 设置整体高度和布局结构
 */
#admin-layout {
  height: 100%;
  
  :deep(.ant-layout) {
    height: 100%;
  }
}

/**
 * 顶部导航栏样式
 * 包含Logo、标题和用户信息区域
 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 100%;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  
  /**
   * 顶部导航栏左侧区域样式
   * 包含Logo和标题
   */
  .header-left {
    display: flex;
    align-items: center;
    
    /**
     * Logo容器样式
     */
    .logo-container {
      display: flex;
      align-items: center;
      
      /**
       * 购物袋Logo样式
       * 自定义绘制的购物袋图形
       */
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
      
      /**
       * 系统标题样式
       */
      .header-title {
        font-size: 18px;
        font-weight: 600;
        color: #333;
        line-height: 64px;
      }
    }
  }
  
  /**
   * 顶部导航栏右侧区域样式
   * 包含前台预览按钮和管理员信息
   */
  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;
    
    /**
     * 前台预览按钮样式
     */
    .preview-btn {
      background: #5a7be0;
      border-radius: 4px;
      border-color: #5a7be0;
      
      &:hover {
        background: #6e89e8;
        border-color: #6e89e8;
      }
    }
    
    /**
     * 管理员信息区域样式
     */
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
      
      /**
       * 管理员头像样式
       */
      .admin-avatar {
        background: #5a7be0;
        color: #fff;
      }
      
      /**
       * 管理员详细信息样式
       */
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
      
      /**
       * 下拉菜单链接样式
       */
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

/**
 * 侧边栏样式
 * 自定义Ant Design侧边栏样式
 */
.custom-sider {
  background: #fff;
  border-right: 1px solid #f0f0f0;
  
  /**
   * 侧边栏头部样式
   * 包含折叠按钮和标题
   */
  .sider-header {
    display: flex;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
    
    /**
     * 折叠/展开触发器样式
     */
    .trigger {
      font-size: 16px;
      color: #999;
      cursor: pointer;
      
      &:hover {
        color: #5a7be0;
      }
    }
    
    /**
     * 侧边栏标题样式
     */
    .sider-title {
      font-size: 14px;
      font-weight: 500;
      color: #333;
      margin-left: 12px;
    }
  }
  
  /**
   * 自定义菜单样式
   * 重写Ant Design Menu组件样式
   */
  .custom-menu {
    border-right: none;
    
    /**
     * 菜单项和子菜单标题样式
     */
    :deep(.ant-menu-item), :deep(.ant-menu-submenu-title) {
      height: 48px;
      line-height: 48px;
      margin: 4px 0;
      padding-left: 24px !important;
      border-radius: 0 24px 24px 0;
      
      &:hover {
        color: #5a7be0;
      }
      
      /**
       * 选中状态样式
       */
      &.ant-menu-item-selected {
        background: #eef2ff;
        color: #5a7be0;
        font-weight: 500;
        
        &::after {
          border-right: 3px solid #5a7be0;
        }
      }
    }
    
    /**
     * 子菜单标题样式
     */
    :deep(.ant-menu-submenu-title) {
      font-weight: 500;
    }
    
    /**
     * 子菜单内容样式
     */
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

/**
 * 内容区域样式
 * 设置内容区域的内边距和背景
 */
.content-area {
  padding: 24px;
  background: #f0f2fa;
  overflow: auto;
  
  /**
   * 内容包装区样式
   * 为内容添加背景和阴影
   */
  .content-wrapper {
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 10px rgba(90, 123, 224, 0.08);
    padding: 24px;
    min-height: calc(100% - 120px);
  }
}

/**
 * 页脚样式
 * 设置页脚文本对齐和边框
 */
.footer {
  text-align: center;
  padding: 16px 50px;
  color: #666;
  font-size: 14px;
  background: #fff;
  border-top: 1px solid #eaeffd;
}

/**
 * 自定义Ant Design组件样式
 * 通过深度选择器修改第三方组件样式
 */
:deep(.ant-layout-header) {
  height: 64px;
  padding: 0;
  line-height: 64px;
  background: #fff;
}

:deep(.ant-layout-sider-trigger) {
  display: none;
}

/**
 * 响应式样式
 * 在小屏幕设备上隐藏部分元素
 */
@media (max-width: 768px) {
  .admin-detail {
    display: none !important;
  }
  
  .header-title {
    display: none;
  }
}
</style>
