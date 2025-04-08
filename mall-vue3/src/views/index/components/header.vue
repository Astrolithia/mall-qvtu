<template>
  <div class="main-bar-view">
    <div class="header-container">
      <div class="logo">
        <div class="logo-container" @click="$router.push({ name: 'portal' })">
          <div class="logo-icon">
            <div class="shopping-bag">
              <span class="bag-handle"></span>
              <span class="bag-body"></span>
              <span class="shine"></span>
            </div>
          </div>
          <div class="logo-brand">
            <span class="logo-text">品质商城</span>
          </div>
        </div>
      </div>
      
      <!-- 导航区域 -->
      <div class="menu-container">
        <!-- 更简洁的分类触发器 -->
        <div class="categories-trigger" @mouseenter="showCategoryPanel = true">
          <i class="category-icon"></i>
          <span>全部商品分类</span>
          <i class="down-arrow"></i>
          
          <!-- 多级分类面板 -->
          <div class="category-panel" v-show="showCategoryPanel" @mouseenter="showCategoryPanel = true" @mouseleave="showCategoryPanel = false">
            <div class="category-panel-inner">
              <ul class="primary-categories">
                <li 
                  v-for="(category, index) in categoryList" 
                  :key="category.id"
                  class="primary-category-item"
                  :class="{ active: currentCategoryIndex === index }"
                  @mouseenter="currentCategoryIndex = index"
                  @click="navigateToCategory(category.id)"
                >
                  <span class="category-name">{{ category.title }}</span>
                  <i class="arrow-icon" v-if="category.children && category.children.length > 0"></i>
                </li>
              </ul>
              
              <!-- 二级和三级分类内容区 -->
              <div class="sub-categories-container" v-if="currentCategoryIndex >= 0 && categoryList[currentCategoryIndex] && categoryList[currentCategoryIndex].children && categoryList[currentCategoryIndex].children.length > 0">
                <div class="panel-content">
                  <div 
                    v-for="subCategory in categoryList[currentCategoryIndex].children" 
                    :key="subCategory.id" 
                    class="sub-category-section"
                  >
                    <div class="sub-category-header" @click="navigateToCategory(subCategory.id)">
                      <span class="sub-category-name">{{ subCategory.title }}</span>
                      <i class="arrow-right" v-if="subCategory.children && subCategory.children.length > 0"></i>
                    </div>
                    
                    <!-- 三级分类列表 -->
                    <div class="third-categories" v-if="subCategory.children && subCategory.children.length > 0">
                      <a 
                        v-for="thirdCategory in subCategory.children" 
                        :key="thirdCategory.id"
                        class="third-category-item"
                        @click.stop="navigateToCategory(thirdCategory.id)"
                      >
                        {{ thirdCategory.title }}
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 热门标签展示 -->
        <div class="tags-container" @mouseenter="showTagsPanel = true" @mouseleave="showTagsPanel = false">
          <div class="tags-trigger">
            <i class="tag-icon"></i>
            <span>热门标签</span>
            <i class="down-arrow"></i>
          </div>
          
          <!-- 标签下拉面板 -->
          <div class="tags-panel" v-show="showTagsPanel">
            <div class="tags-list">
              <div 
                v-for="tag in tagList" 
                :key="tag.id"
                class="tag-item"
                :style="{ backgroundColor: getTagColor(tag.id) }"
                @click="navigateToTag(tag.id)"
              >
                {{ tag.title }}
              </div>
              <div class="view-more" @click="navigateToAllTags">查看全部</div>
            </div>
          </div>
        </div>
        
        <!-- 主导航菜单 - 更简洁的样式 -->
        <div class="main-nav">
          <router-link to="/" class="nav-item" :class="{ active: route.name === 'portal' || route.path === '/' }">首页</router-link>
          <router-link :to="{ name: 'search', query: { sort: 'new' } }" class="nav-item" :class="{ active: route.query.sort === 'new' }">新品上市</router-link>
          <router-link :to="{ name: 'search', query: { sort: 'hot' } }" class="nav-item" :class="{ active: route.query.sort === 'hot' }">热卖商品</router-link>
          <router-link :to="{ name: 'search', query: { sort: 'recommend' } }" class="nav-item" :class="{ active: route.query.sort === 'recommend' }">限时特惠</router-link>
        </div>
      </div>
      
      <div class="spacer"></div>
      
      <div class="right-view">
        <div class="cart-icon" @click="$router.push({name: 'cart'})">
          <img src="../../../assets/images/cart-icon.svg" />
          <span class="cart-count" v-if="cartStore.cartCount > 0">{{ cartStore.cartCount }}</span>
        </div>
        
        <template v-if="userStore.user_token">
          <a-dropdown>
            <a class="ant-dropdown-link" @click="e => e.preventDefault()">
              <img 
                :src="userStore.user_avatar ? `${BASE_URL}/api/staticfiles/avatar/${userStore.user_avatar}` : AvatarIcon" 
                class="self-img" 
                @error="handleAvatarError"
                alt="用户头像"
              />
              <span class="user-name">{{ userStore.user_nickname || '用户未设置昵称' }}</span>
              <down-outlined class="dropdown-arrow" />
            </a>
            <template #overlay>
              <a-menu class="user-menu">
                <a-menu-item class="menu-item">
                  <a @click="goUserCenter('userInfoEditView')">
                    <user-outlined class="menu-icon" />
                    <span>个人中心</span>
                  </a>
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item class="menu-item">
                  <a @click="quit()">
                    <logout-outlined class="menu-icon" />
                    <span>退出登录</span>
                  </a>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </template>
        <template v-else>
          <button class="login btn hidden-sm" @click="goLogin()">登录</button>
        </template>

        <div class="right-icon" @click="msgVisible = true">
          <img :src="MessageIcon" />
          <span class="msg-point" style=""></span>
        </div>
        <div>
          <a-drawer
            title="我的消息"
            placement="right"
            :closable="true"
            :maskClosable="true"
            :visible="msgVisible"
            @close="onClose"
          >
            <a-spin :spinning="loading" style="min-height: 200px;">
              <div class="list-content">
                <div class="notification-view">
                  <div class="list">
                    <div class="notification-item flex-view" v-for="item in msgData" :key="item.id">
                      <!---->
                      <div class="content-box">
                        <div class="header">
                          <span class="title-txt">{{ item.title }}</span>
                          <br />
                          <span class="time">{{ item.create_time }}</span>
                        </div>
                        <div class="head-text">
                        </div>
                        <div class="content">
                          <p>{{ item.content }}</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </a-spin>
          </a-drawer>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { listApi } from '../../../api/notice'
import { treeApi } from '../../../api/classification'
import { listApi as listTagApi } from '../../../api/tag'
import { useUserStore, useCartStore } from "../../../store";
import AvatarIcon from '../../../assets/images/avatar.jpg';
import MessageIcon from '../../../assets/images/message-icon.svg';
import { UserOutlined, LogoutOutlined, DownOutlined } from '@ant-design/icons-vue';
import { BASE_URL } from "/@/store/constants";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const cartStore = useCartStore();

let loading = ref(false)
let msgVisible = ref(false)
let msgData = ref([] as any)
let categoryList = ref([] as any)
let tagList = ref([] as any)
let showCategoryPanel = ref(false)
let showTagsPanel = ref(false)
let currentCategoryIndex = ref(-1)

// 标签颜色数组
const tagColors = ['#4684e2', '#7a5af8', '#ff6b00', '#2cc58c', '#ff4d94', '#00b7c3', '#f7b500']

onMounted(()=>{
  getMessageList()
  getCategoryTree()
  getTagList()
})

const getMessageList = ()=> {
  loading.value = true
  listApi({}).then(res => {
    msgData.value = res.data
    loading.value = false
  }).catch(err => {
    console.log(err)
    loading.value = false
  })
}

const getCategoryTree = () => {
  treeApi({}).then(res => {
    if (res.data && res.data.length > 0) {
      console.log('分类树数据:', res.data);
      categoryList.value = res.data;
    } else {
      console.log('分类树数据为空');
      categoryList.value = [];
    }
  }).catch(err => {
    console.log('获取分类树失败:', err);
    categoryList.value = [];
  });
}

// 获取标签列表
const getTagList = () => {
  listTagApi({}).then(res => {
    tagList.value = res.data
  }).catch(err => {
    console.error('获取标签失败:', err)
  })
}

// 随机获取标签颜色
const getTagColor = (id) => {
  const index = id % tagColors.length
  return tagColors[index]
}

const navigateToCategory = (categoryId) => {
  router.push({
    name: 'search',
    query: { c: categoryId }
  });
  showCategoryPanel.value = false;
}

const navigateToTag = (tagId) => {
  router.push({
    name: 'search',
    query: { tag: tagId }
  })
  showTagsPanel.value = false
}

const navigateToAllTags = () => {
  router.push({
    name: 'search'
  })
  showTagsPanel.value = false
}

const goLogin = () => {
  router.push({name: 'login'})
}

const goUserCenter = (menuName) => {
  router.push({name: menuName})
}
const quit= () => {
  userStore.logout().then(res => {
    router.push({name: 'portal'})
  })
}
const onClose = () => {
  msgVisible.value = false;
}

// 添加头像加载错误处理函数
const handleAvatarError = (e) => {
  console.log('Avatar load error:', e);
  e.target.src = AvatarIcon;
};

// 添加头像URL处理函数
const getAvatarUrl = (avatar) => {
  if (!avatar) return AvatarIcon;
  return `/api/staticfiles/avatar/${avatar}`;
};
</script>

<style scoped lang="less">
.main-bar-view {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 100;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.06);
}

.header-container {
  height: 70px;
  display: flex;
  align-items: center;
  width: 1200px;
  max-width: 100%;
  margin: 0 auto;
  padding: 0 24px;
}

.logo {
  margin-right: 30px;
  
  .logo-container {
    display: flex;
    align-items: center;
    cursor: pointer;
    
    .logo-icon {
      position: relative;
      width: 38px;
      height: 38px;
      margin-right: 10px;
      
      .shopping-bag {
        position: relative;
        width: 100%;
        height: 100%;
        
        .bag-body {
          position: absolute;
          width: 28px;
          height: 26px;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          background: linear-gradient(135deg, #4684e2, #7a5af8);
          border-radius: 4px;
          z-index: 1;
        }
        
        .bag-handle {
          position: absolute;
          width: 16px;
          height: 10px;
          border: 3px solid #4684e2;
          border-bottom: none;
          border-radius: 10px 10px 0 0;
          top: 4px;
          left: 50%;
          transform: translateX(-50%);
          z-index: 0;
        }
        
        .shine {
          position: absolute;
          width: 8px;
          height: 8px;
          background: #fff;
          border-radius: 50%;
          bottom: 5px;
          right: 12px;
          opacity: 0.6;
          z-index: 2;
        }
      }
    }
    
    .logo-brand {
      position: relative;
      
      .logo-text {
        font-size: 22px;
        font-weight: 600;
        background: linear-gradient(90deg, #4684e2, #7a5af8);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        letter-spacing: 0.5px;
      }
    }
    
    &:hover {
      .logo-icon .shopping-bag {
        .bag-body {
          background: linear-gradient(135deg, #5a93e8, #8c6ff9);
        }
        
        .bag-handle {
          border-color: #5a93e8;
        }
      }
    }
  }
}

/* 整合导航区域 */
.menu-container {
  display: flex;
  align-items: center;
  height: 40px;
}

/* 简洁的分类触发器 */
.categories-trigger {
  display: flex;
  align-items: center;
  height: 40px;
  padding: 0 15px;
  margin-right: 20px;
  color: #333;
  font-weight: 500;
  cursor: pointer;
  position: relative;
  transition: color 0.3s;
  
  &:hover, &.active {
    color: #4684e2;
  }
  
  .category-icon {
    display: inline-block;
    width: 16px;
    height: 16px;
    margin-right: 8px;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23333"><path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/></svg>') center/cover no-repeat;
  }
  
  .down-arrow {
    display: inline-block;
    width: 12px;
    height: 12px;
    margin-left: 4px;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23666"><path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z"/></svg>') center/cover no-repeat;
  }
}

/* 热门标签样式 */
.tags-container {
  position: relative;
  margin-right: 20px;
}

.tags-trigger {
  display: flex;
  align-items: center;
  height: 40px;
  padding: 0 15px;
  color: #333;
  font-weight: 500;
  cursor: pointer;
  position: relative;
  transition: color 0.3s;
  
  &:hover {
    color: #4684e2;
  }
  
  .tag-icon {
    display: inline-block;
    width: 16px;
    height: 16px;
    margin-right: 8px;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23333"><path d="M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41 0-.55-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z"/></svg>') center/cover no-repeat;
  }
  
  .down-arrow {
    display: inline-block;
    width: 12px;
    height: 12px;
    margin-left: 4px;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23666"><path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z"/></svg>') center/cover no-repeat;
  }
}

/* 主导航菜单 */
.main-nav {
  display: flex;
  height: 40px;
  
  .nav-item {
    padding: 0 18px;
    line-height: 40px;
    font-weight: 500;
    color: #333;
    cursor: pointer;
    position: relative;
    text-decoration: none;
    
    &:hover {
      color: #4684e2;
    }
    
    &.active {
      color: #4684e2;
      font-weight: 600;
      
      &::after {
        content: '';
        position: absolute;
        left: 50%;
        bottom: 0;
        width: 20px;
        height: 2px;
        background-color: #4684e2;
        transform: translateX(-50%);
      }
    }
  }
}

.spacer {
  flex: 1;
}

.right-view {
  display: flex;
  align-items: center;
  
  .quick-links {
    display: flex;
    margin-right: 20px;
    
    .quick-link {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin: 0 12px;
      text-decoration: none;
      
      img {
        width: 20px;
        height: 20px;
        opacity: 0.7;
        transition: opacity 0.2s;
      }
      
      span {
        font-size: 12px;
        color: #666;
        margin-top: 4px;
        transition: color 0.2s;
      }
      
      &:hover {
        img {
          opacity: 1;
        }
        
        span {
          color: #4684e2;
        }
      }
    }
  }
  
  .cart-icon {
    position: relative;
    margin: 0 20px;
    cursor: pointer;
    
    img {
      width: 24px;
      height: 24px;
      opacity: 0.7;
      transition: opacity 0.2s;
    }
    
    &:hover img {
      opacity: 1;
    }
    
    .cart-count {
      position: absolute;
      top: -8px;
      right: -8px;
      background-color: #ff6b6b;
      color: white;
      font-size: 12px;
      font-weight: 500;
      width: 18px;
      height: 18px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
    }
  }
  
  .self-img {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    cursor: pointer;
    margin-right: 8px;
    border: 2px solid transparent;
    transition: all 0.2s;
    
    &:hover {
      border-color: #4684e2;
    }
  }
  
  .user-name {
    font-size: 14px;
    color: #333;
    margin-right: 4px;
  }
  
  .dropdown-arrow {
    font-size: 12px;
    color: #666;
  }
  
  :deep(.user-menu) {
    min-width: 160px;
    padding: 4px 0;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    
    .menu-item {
      padding: 8px 16px;
      margin: 2px 0;
      
      a {
        display: flex;
        align-items: center;
        color: #333;
        
        .menu-icon {
          font-size: 16px;
          margin-right: 8px;
          color: #666;
        }
        
        &:hover {
          color: #4684e2;
          
          .menu-icon {
            color: #4684e2;
          }
        }
      }
    }
    
    .ant-dropdown-menu-item:hover {
      background-color: #f5f7fa;
    }
  }
  
  .right-icon {
    position: relative;
    margin-left: 10px;
    cursor: pointer;
    
    img {
      width: 22px;
      height: 22px;
      opacity: 0.7;
      transition: opacity 0.2s;
    }
    
    &:hover img {
      opacity: 1;
    }
    
    .msg-point {
      position: absolute;
      top: -2px;
      right: -2px;
      width: 8px;
      height: 8px;
      background: #ff6b6b;
      border-radius: 50%;
    }
  }
  
  .login {
    background: #4684e2;
    color: white;
    border: none;
    border-radius: 20px;
    height: 36px;
    padding: 0 20px;
    margin: 0 15px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      background: #3670c5;
      transform: translateY(-2px);
      box-shadow: 0 4px 10px rgba(70, 132, 226, 0.3);
    }
  }
}

.list-content {
  .notification-view {
    .list {
      .notification-item {
        cursor: default;
        padding: 16px 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .content-box {
          .header {
            margin-bottom: 12px;
            
            .title-txt {
              font-weight: 600;
              color: #333;
              font-size: 16px;
            }
            
            .time {
              color: #999;
              font-size: 12px;
            }
          }
          
          .content {
            color: #666;
            font-size: 14px;
            line-height: 1.5;
          }
        }
      }
    }
  }
}

.category-panel {
  position: absolute;
  top: 40px;
  left: 0;
  width: 800px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(8px);
  border-radius: 0 0 4px 4px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  z-index: 1000;
  padding: 10px 0;
  
  .category-panel-inner {
    display: flex;
    height: 480px;
    position: relative;
  }
}

.primary-categories {
  width: 200px;
  padding: 0;
  margin: 0;
  list-style: none;
  border-right: 1px solid rgba(240, 240, 240, 0.7);
  height: 100%;
  overflow-y: auto;
  
  .primary-category-item {
    position: relative;
    padding: 12px 15px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover, &.active {
      background-color: rgba(247, 247, 247, 0.8);
      color: #4684e2;
    }
    
    .category-name {
      flex: 1;
    }
    
    .arrow-icon {
      width: 12px;
      height: 12px;
      background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23999"><path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/></svg>') center/cover no-repeat;
    }
  }
}

.sub-categories-container {
  position: absolute;
  left: 200px;
  top: 0;
  width: 600px;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  padding: 15px;
  
  .panel-content {
    flex: 1;
    padding-right: 15px;
    overflow-y: auto;
    max-height: 450px;
  }
  
  .sub-category-section {
    margin-bottom: 15px;
  }
  
  .sub-category-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 0;
    border-bottom: 1px solid rgba(240, 240, 240, 0.8);
    margin-bottom: 10px;
    cursor: pointer;
    
    .sub-category-name {
      font-size: 14px;
      font-weight: 500;
      color: #333;
    }
    
    .arrow-right {
      width: 12px;
      height: 12px;
      background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23999"><path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/></svg>') center/cover no-repeat;
    }
    
    &:hover {
      .sub-category-name {
        color: #4684e2;
      }
    }
  }

  .third-categories {
    display: flex;
    flex-wrap: wrap;
    
    .third-category-item {
      padding: 5px 10px;
      margin: 0 10px 10px 0;
      background: rgba(245, 245, 245, 0.8);
      border-radius: 4px;
      font-size: 12px;
      color: #666;
      cursor: pointer;
      transition: all 0.2s;
      
      &:hover {
        background: rgba(230, 247, 255, 0.9);
        color: #4684e2;
      }
    }
  }
}

.tags-panel {
  position: absolute;
  top: 40px;
  left: 0;
  min-width: 400px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(8px);
  border-radius: 0 0 4px 4px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  z-index: 1000;
  padding: 15px;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  
  .tag-item {
    padding: 5px 12px;
    border-radius: 15px;
    font-size: 12px;
    color: white;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
    }
  }
  
  .view-more {
    padding: 5px 12px;
    border-radius: 15px;
    font-size: 12px;
    color: #666;
    background-color: #f5f5f5;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      color: #4684e2;
      background-color: #e6f7ff;
      transform: translateY(-2px);
    }
  }
}
</style>
