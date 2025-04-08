<template>
  <div class="mine-infos-view">
    <!-- 用户信息头部 -->
    <div class="info-header">
      <div class="info-box">
        <img 
          :src="userStore.user_avatar ? `${BASE_URL}/api/staticfiles/avatar/${userStore.user_avatar}` : AvatarIcon" 
          class="avatar-img" 
          @error="handleAvatarError"
          alt="用户头像"
        />
        <div class="name-box">
          <h2 class="nick">{{ userStore.user_name }}</h2>
          <div class="age">
            <span>活跃{{ activeDays }}天</span>
          </div>
        </div>
      </div>
      
      <!-- 收藏和心愿单统计 -->
      <div class="counts-view">
        <div class="counts">
          <div class="count-item" @click="handleCollectClick">
            <div class="num">{{ collectCount }}</div>
            <div class="text">收藏</div>
          </div>
          <div class="count-item" @click="handleWishClick">
            <div class="num">{{ wishCount }}</div>
            <div class="text">心愿单</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 主标签页 -->
    <div class="main-tabs">
      <div class="tab" :class="{ active: currentTab === 'order' }" @click="switchTab('order')">
        订单中心
      </div>
      <div class="tab" :class="{ active: currentTab === 'personal' }" @click="switchTab('personal')">
        个人设置
      </div>
    </div>
    
    <!-- 菜单图标 -->
    <div class="menu-icons">
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
    
    <!-- 内容显示区域 - 通过路由视图 -->
    <div class="content-area">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup lang="ts">
import AvatarIcon from '../../../assets/images/avatar.jpg';
import MyOrderImg from '../../../assets/images/order-icon.svg';
import CommentIconImg from '../../../assets/images/order-thing-icon.svg';
import AddressIconImage from '../../../assets/images/order-address-icon.svg';
import PointIconImage from '../../../assets/images/order-point-icon.svg';
import SettingIconImage from '../../../assets/images/setting-icon.svg';
import SafeIconImage from '../../../assets/images/setting-safe-icon.svg';
import PushIconImage from '../../../assets/images/setting-push-icon.svg';
import MessageIconImage from '../../../assets/images/setting-msg-icon.svg';
import { BASE_URL } from "/@/store/constants";

import { userCollectListApi } from '../../../api/thingCollect';
import { userWishListApi } from '../../../api/thingWish';
import { useUserStore } from '../../../store';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();

const collectCount = ref(0);
const wishCount = ref(0);
const currentTab = ref('order');
const activeMenuItem = ref('');
const activeDays = ref(1);

// 菜单项配置
const orderMenuItems = [
  { id: 'orderView', name: '我的订单', icon: MyOrderImg },
  { id: 'commentView', name: '我的评论', icon: CommentIconImg },
  { id: 'addressView', name: '地址管理', icon: AddressIconImage },
  { id: 'scoreView', name: '我的积分', icon: PointIconImage }
];

const personalMenuItems = [
  { id: 'userInfoEditView', name: '编辑资料', icon: SettingIconImage },
  { id: 'securityView', name: '账号安全', icon: SafeIconImage },
  { id: 'pushView', name: '推送设置', icon: PushIconImage },
  { id: 'messageView', name: '消息管理', icon: MessageIconImage }
];

// 处理头像加载错误
const handleAvatarError = (e) => {
  e.target.src = AvatarIcon;
};

// 计算活跃天数
const calculateActiveDays = () => {
  const lastActiveTime = localStorage.getItem('last_active_time');
  if (!lastActiveTime) {
    localStorage.setItem('last_active_time', new Date().toISOString());
    return 1;
  }

  const lastActive = new Date(lastActiveTime);
  const now = new Date();
  const diffTime = Math.abs(now - lastActive);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  // 如果超过24小时，更新最后活跃时间
  if (diffDays >= 1) {
    localStorage.setItem('last_active_time', new Date().toISOString());
  }

  return diffDays;
};

onMounted(() => {
  getCollectThingList();
  getWishThingList();
  activeDays.value = calculateActiveDays();
});

// 主标签页切换
const switchTab = (tab) => {
  currentTab.value = tab;
  activeMenuItem.value = '';
};

// 菜单项点击
const selectMenuItem = (id) => {
  activeMenuItem.value = id;
  router.push({ name: id });
};

// 收藏点击
const handleCollectClick = () => {
  router.push({ name: 'collectThingView' });
};

// 心愿单点击
const handleWishClick = () => {
  router.push({ name: 'wishThingView' });
};

// 获取收藏数据
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

// 获取心愿单数据
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
.mine-infos-view {
  width: 100%;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  
  .info-header {
    background: linear-gradient(135deg, #4684e2, #7a5af8);
    padding: 24px;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .info-box {
      display: flex;
      align-items: center;
      
      .avatar-img {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        border: 3px solid rgba(255, 255, 255, 0.3);
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        margin-right: 16px;
      }
      
      .name-box {
        .nick {
          color: white;
          font-weight: 600;
          font-size: 20px;
          margin: 0 0 4px 0;
        }
        
        .age {
          opacity: 0.8;
          font-size: 13px;
        }
      }
    }
    
    .counts-view {
      .counts {
        display: flex;
        gap: 24px;
        
        .count-item {
          text-align: center;
          cursor: pointer;
          transition: transform 0.2s;
          
          &:hover {
            transform: translateY(-3px);
          }
          
          .num {
            font-size: 24px;
            font-weight: 600;
            color: white;
          }
          
          .text {
            font-size: 13px;
            opacity: 0.8;
          }
        }
      }
    }
  }
  
  // 主标签页
  .main-tabs {
    display: flex;
    border-bottom: 1px solid #eaeaea;
    
    .tab {
      padding: 12px 24px;
      position: relative;
      font-weight: 500;
      font-size: 16px;
      color: #666;
      cursor: pointer;
      
      &.active {
        color: #4684e2;
        font-weight: 600;
        
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
      
      &:hover:not(.active) {
        color: #4684e2;
        opacity: 0.7;
      }
    }
  }
  
  // 菜单图标
  .menu-icons {
    display: flex;
    padding: 16px;
    flex-wrap: wrap;
    
    .menu-icon-item {
      padding: 12px 24px;
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-3px);
      }
      
      &.active {
        .icon-wrapper {
          background: linear-gradient(135deg, #4684e2, #7a5af8);
          box-shadow: 0 6px 15px rgba(122, 90, 248, 0.2);
          
          img {
            filter: brightness(0) invert(1);
          }
        }
        
        .icon-name {
          color: #4684e2;
          font-weight: 500;
        }
      }
      
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
        
        img {
          width: 24px;
          height: 24px;
        }
      }
      
      .icon-name {
        font-size: 14px;
        color: #666;
      }
    }
  }
  
  // 内容区域
  .content-area {
    padding: 20px;
    background-color: #fafafa;
    border-radius: 8px;
    margin: 16px;
  }
}
</style>
