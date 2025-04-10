<!--
/**
 * 用户个人中心组件
 * 
 * 该组件实现了电商平台的用户个人中心功能，包括：
 * 1. 用户基本信息展示（头像、昵称、活跃天数）
 * 2. 用户收藏和心愿单统计
 * 3. 双标签页导航（订单中心和个人设置）
 * 4. 图标菜单导航
 * 5. 子页面内容区域
 * 
 * 组件依赖：
 * - Vue 3.x
 * - Vue Router
 * - Pinia 状态管理
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */
-->

<template>
  <!-- 个人中心主容器 -->
  <div class="mine-infos-view">
    <!-- 用户信息头部区域 -->
    <div class="info-header">
      <!-- 用户基本信息区 -->
      <div class="info-box">
        <!-- 用户头像，带错误处理 -->
        <img 
          :src="userStore.user_avatar ? `${BASE_URL}/api/staticfiles/avatar/${userStore.user_avatar}` : AvatarIcon" 
          class="avatar-img" 
          @error="handleAvatarError"
          alt="用户头像"
        />
        <!-- 用户名称和活跃信息 -->
        <div class="name-box">
          <h2 class="nick">{{ userStore.user_name }}</h2>
          <div class="age">
            <span>活跃{{ activeDays }}天</span>
          </div>
        </div>
      </div>
      
      <!-- 收藏和心愿单统计区域 -->
      <div class="counts-view">
        <div class="counts">
          <!-- 收藏数量 -->
          <div class="count-item" @click="handleCollectClick">
            <div class="num">{{ collectCount }}</div>
            <div class="text">收藏</div>
          </div>
          <!-- 心愿单数量 -->
          <div class="count-item" @click="handleWishClick">
            <div class="num">{{ wishCount }}</div>
            <div class="text">心愿单</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 主导航标签页 -->
    <div class="main-tabs">
      <!-- 订单中心标签 -->
      <div class="tab" :class="{ active: currentTab === 'order' }" @click="switchTab('order')">
        订单中心
      </div>
      <!-- 个人设置标签 -->
      <div class="tab" :class="{ active: currentTab === 'personal' }" @click="switchTab('personal')">
        个人设置
      </div>
    </div>
    
    <!-- 菜单图标区域 - 根据当前标签页显示不同菜单 -->
    <div class="menu-icons">
      <!-- 订单中心相关菜单 -->
      <template v-if="currentTab === 'order'">
        <div 
          v-for="(item, index) in orderMenuItems" 
          :key="index"
          class="menu-icon-item" 
          :class="{ active: activeMenuItem === item.id }"
          @click="selectMenuItem(item.id)"
        >
          <div class="icon-wrapper">
            <img :src="item.icon" :alt="item.name" />
          </div>
          <div class="icon-name">{{ item.name }}</div>
        </div>
      </template>
      
      <!-- 个人设置相关菜单 -->
      <template v-else>
        <div 
          v-for="(item, index) in personalMenuItems" 
          :key="index"
          class="menu-icon-item" 
          :class="{ active: activeMenuItem === item.id }"
          @click="selectMenuItem(item.id)"
        >
          <div class="icon-wrapper">
            <img :src="item.icon" :alt="item.name" />
          </div>
          <div class="icon-name">{{ item.name }}</div>
        </div>
      </template>
    </div>
    
    <!-- 内容显示区域 - 通过路由视图加载子组件 -->
    <div class="content-area">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 导入所需的依赖和资源
 */
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../../store';
import { BASE_URL } from "/@/store/constants";
import { userCollectListApi } from '../../../api/thingCollect';
import { userWishListApi } from '../../../api/thingWish';

/**
 * 导入图标资源
 */
import AvatarIcon from '../../../assets/images/avatar.jpg';
import MyOrderImg from '../../../assets/images/order-icon.svg';
import CommentIconImg from '../../../assets/images/order-thing-icon.svg';
import AddressIconImage from '../../../assets/images/order-address-icon.svg';
import PointIconImage from '../../../assets/images/order-point-icon.svg';
import SettingIconImage from '../../../assets/images/setting-icon.svg';
import SafeIconImage from '../../../assets/images/setting-safe-icon.svg';
import PushIconImage from '../../../assets/images/setting-push-icon.svg';
import MessageIconImage from '../../../assets/images/setting-msg-icon.svg';

/**
 * 获取状态和路由
 */
const userStore = useUserStore();
const router = useRouter();

/**
 * 响应式数据定义
 */
const collectCount = ref(0);          // 用户收藏数量
const wishCount = ref(0);             // 用户心愿单数量
const currentTab = ref('order');      // 当前活动标签页
const activeMenuItem = ref('');       // 当前活动菜单项
const activeDays = ref(1);            // 用户活跃天数

/**
 * 订单中心菜单项配置
 * @type {Array<{id: string, name: string, icon: string}>}
 */
const orderMenuItems = [
  { id: 'orderView', name: '我的订单', icon: MyOrderImg },
  { id: 'commentView', name: '我的评论', icon: CommentIconImg },
  { id: 'addressView', name: '地址管理', icon: AddressIconImage },
  { id: 'scoreView', name: '我的积分', icon: PointIconImage }
];

/**
 * 个人设置菜单项配置
 * @type {Array<{id: string, name: string, icon: string}>}
 */
const personalMenuItems = [
  { id: 'userInfoEditView', name: '编辑资料', icon: SettingIconImage },
  { id: 'securityView', name: '账号安全', icon: SafeIconImage },
  { id: 'pushView', name: '推送设置', icon: PushIconImage },
  { id: 'messageView', name: '消息管理', icon: MessageIconImage }
];

/**
 * 处理头像加载错误
 * 
 * @description 当用户头像加载失败时，显示默认头像
 * @param {Event} e - 图片加载错误事件
 */
const handleAvatarError = (e: Event) => {
  const target = e.target as HTMLImageElement;
  target.src = AvatarIcon;
};

/**
 * 计算用户活跃天数
 * 
 * @description 根据上次活跃时间计算用户连续活跃天数
 * @returns {number} 活跃天数
 */
const calculateActiveDays = () => {
  const lastActiveTime = localStorage.getItem('last_active_time');
  if (!lastActiveTime) {
    localStorage.setItem('last_active_time', new Date().toISOString());
    return 1;
  }

  const lastActive = new Date(lastActiveTime);
  const now = new Date();
  const diffTime = Math.abs(now.getTime() - lastActive.getTime());
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  // 如果超过24小时，更新最后活跃时间
  if (diffDays >= 1) {
    localStorage.setItem('last_active_time', new Date().toISOString());
  }

  return diffDays;
};

/**
 * 组件挂载后执行
 * 初始化数据、获取收藏和心愿单数据、计算活跃天数
 */
onMounted(() => {
  getCollectThingList();
  getWishThingList();
  activeDays.value = calculateActiveDays();
});

/**
 * 切换主标签页
 * 
 * @description 在"订单中心"和"个人设置"之间切换
 * @param {string} tab - 目标标签页名称
 */
const switchTab = (tab: string) => {
  currentTab.value = tab;
  activeMenuItem.value = '';
};

/**
 * 选择菜单项
 * 
 * @description 设置当前活动菜单项并导航到对应路由
 * @param {string} id - 菜单项ID，同时作为路由名称
 */
const selectMenuItem = (id: string) => {
  activeMenuItem.value = id;
  router.push({ name: id });
};

/**
 * 处理收藏点击事件
 * 
 * @description 导航到用户收藏页面
 */
const handleCollectClick = () => {
  router.push({ name: 'collectThingView' });
};

/**
 * 处理心愿单点击事件
 * 
 * @description 导航到用户心愿单页面
 */
const handleWishClick = () => {
  router.push({ name: 'wishThingView' });
};

/**
 * 获取用户收藏列表
 * 
 * @description 从API获取用户收藏数据并更新计数
 * @throws {Error} 当API请求失败时抛出错误
 */
const getCollectThingList = () => {
  const userId = userStore.user_id;
  userCollectListApi({ userId: userId })
    .then((res) => {
      collectCount.value = res.data.length;
    })
    .catch((err) => {
      console.log(err.msg);
    });
};

/**
 * 获取用户心愿单列表
 * 
 * @description 从API获取用户心愿单数据并更新计数
 * @throws {Error} 当API请求失败时抛出错误
 */
const getWishThingList = () => {
  const userId = userStore.user_id;
  userWishListApi({ userId: userId })
    .then((res) => {
      wishCount.value = res.data.length;
    })
    .catch((err) => {
      console.log(err.msg);
    });
};
</script>

<style scoped lang="less">
/**
 * 个人中心主容器样式
 * 设置基本外观和阴影效果
 */
.mine-infos-view {
  width: 100%;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  
  /**
   * 信息头部区域样式
   * 设置渐变背景和布局
   */
  .info-header {
    background: linear-gradient(135deg, #4684e2, #7a5af8);
    padding: 24px;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    /**
     * 用户信息区样式
     * 设置头像和文本布局
     */
    .info-box {
      display: flex;
      align-items: center;
      
      /**
       * 头像样式
       * 设置圆形边框和阴影效果
       */
      .avatar-img {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        border: 3px solid rgba(255, 255, 255, 0.3);
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        margin-right: 16px;
      }
      
      /**
       * 用户名称区域样式
       */
      .name-box {
        /**
         * 昵称样式
         * 设置字重和字号
         */
        .nick {
          color: white;
          font-weight: 600;
          font-size: 20px;
          margin: 0 0 4px 0;
        }
        
        /**
         * 活跃天数样式
         * 设置半透明效果
         */
        .age {
          opacity: 0.8;
          font-size: 13px;
        }
      }
    }
    
    /**
     * 统计数据视图样式
     */
    .counts-view {
      /**
       * 统计数据容器样式
       * 设置横向布局和间距
       */
      .counts {
        display: flex;
        gap: 24px;
        
        /**
         * 单个统计项样式
         * 设置鼠标悬停效果
         */
        .count-item {
          text-align: center;
          cursor: pointer;
          transition: transform 0.2s;
          
          /**
           * 悬停动画效果
           */
          &:hover {
            transform: translateY(-3px);
          }
          
          /**
           * 数字样式
           * 设置字号和字重
           */
          .num {
            font-size: 24px;
            font-weight: 600;
            color: white;
          }
          
          /**
           * 文本标签样式
           * 设置半透明效果
           */
          .text {
            font-size: 13px;
            opacity: 0.8;
          }
        }
      }
    }
  }
  
  /**
   * 主标签页样式
   * 设置横向布局和底部边框
   */
  .main-tabs {
    display: flex;
    border-bottom: 1px solid #eaeaea;
    
    /**
     * 单个标签页样式
     * 设置基本外观和鼠标样式
     */
    .tab {
      padding: 12px 24px;
      position: relative;
      font-weight: 500;
      font-size: 16px;
      color: #666;
      cursor: pointer;
      
      /**
       * 激活状态样式
       * 设置颜色和下划线指示器
       */
      &.active {
        color: #4684e2;
        font-weight: 600;
        
        /**
         * 激活指示器
         * 设置渐变色下划线
         */
        &:after {
          content: '';
          position: absolute;
          left: 0;
          bottom: -1px;
          width: 100%;
          height: 2px;
          background: linear-gradient(to right, #4684e2, #7a5af8);
        }
      }
      
      /**
       * 非激活状态悬停效果
       */
      &:hover:not(.active) {
        color: #4684e2;
        opacity: 0.7;
      }
    }
  }
  
  /**
   * 菜单图标区域样式
   * 设置弹性布局和内边距
   */
  .menu-icons {
    display: flex;
    padding: 16px;
    flex-wrap: wrap;
    
    /**
     * 单个菜单图标项样式
     * 设置垂直布局和过渡效果
     */
    .menu-icon-item {
      padding: 12px 24px;
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;
      transition: all 0.3s;
      
      /**
       * 悬停动画效果
       */
      &:hover {
        transform: translateY(-3px);
      }
      
      /**
       * 激活状态样式
       * 设置图标背景和文字颜色
       */
      &.active {
        /**
         * 激活状态图标容器
         * 设置渐变背景和阴影
         */
        .icon-wrapper {
          background: linear-gradient(135deg, #4684e2, #7a5af8);
          box-shadow: 0 6px 15px rgba(122, 90, 248, 0.2);
          
          /**
           * 激活状态图标
           * 设置反色效果
           */
          img {
            filter: brightness(0) invert(1);
          }
        }
        
        /**
         * 激活状态文字
         * 设置颜色和字重
         */
        .icon-name {
          color: #4684e2;
          font-weight: 500;
        }
      }
      
      /**
       * 图标容器样式
       * 设置圆形背景和居中对齐
       */
      .icon-wrapper {
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        background: #f7f9fd;
        margin-bottom: 8px;
        transition: all 0.3s;
        
        /**
         * 图标样式
         * 设置尺寸限制
         */
        img {
          width: 24px;
          height: 24px;
        }
      }
      
      /**
       * 图标名称样式
       * 设置字号和颜色
       */
      .icon-name {
        font-size: 14px;
        color: #666;
      }
    }
  }
  
  /**
   * 内容区域样式
   * 设置内边距和背景色
   */
  .content-area {
    padding: 20px;
    background-color: #fafafa;
    border-radius: 8px;
    margin: 16px;
  }
}
</style>
