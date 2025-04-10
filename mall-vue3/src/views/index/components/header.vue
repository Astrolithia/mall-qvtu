<!--
/**
 * 头部导航组件
 * 
 * 该组件实现了电商平台的顶部导航功能，包括：
 * 1. 网站Logo展示和跳转
 * 2. 商品分类导航（支持三级分类）
 * 3. 热门标签展示
 * 4. 主导航菜单
 * 5. 购物车入口
 * 6. 用户登录/注册入口
 * 7. 用户中心下拉菜单
 * 8. 消息通知中心
 * 
 * 组件依赖：
 * - Vue 3.x
 * - Vue Router
 * - Vuex/Pinia
 * - Ant Design Vue
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <!-- 主导航栏容器 -->
  <div class="main-bar-view">
    <!-- 头部内容容器 -->
    <div class="header-container">
      <!-- Logo区域 -->
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
        <!-- 商品分类触发器 -->
        <div class="categories-trigger" @mouseenter="showCategoryPanel = true">
          <i class="category-icon"></i>
          <span>全部商品分类</span>
          <i class="down-arrow"></i>
          
          <!-- 多级分类面板 -->
          <div class="category-panel" v-show="showCategoryPanel" @mouseenter="showCategoryPanel = true" @mouseleave="showCategoryPanel = false">
            <div class="category-panel-inner">
              <!-- 一级分类列表 -->
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
        
        <!-- 主导航菜单 -->
        <div class="main-nav">
          <router-link to="/" class="nav-item" :class="{ active: route.name === 'portal' || route.path === '/' }">首页</router-link>
          <router-link :to="{ name: 'search', query: { sort: 'new' } }" class="nav-item" :class="{ active: route.query.sort === 'new' }">新品上市</router-link>
          <router-link :to="{ name: 'search', query: { sort: 'hot' } }" class="nav-item" :class="{ active: route.query.sort === 'hot' }">热卖商品</router-link>
          <router-link :to="{ name: 'search', query: { sort: 'recommend' } }" class="nav-item" :class="{ active: route.query.sort === 'recommend' }">限时特惠</router-link>
        </div>
      </div>
      
      <!-- 间距 -->
      <div class="spacer"></div>
      
      <!-- 右侧功能区 -->
      <div class="right-view">
        <!-- 购物车图标 -->
        <div class="cart-icon" @click="$router.push({name: 'cart'})">
          <img src="../../../assets/images/cart-icon.svg" />
          <span class="cart-count" v-if="cartStore.cartCount > 0">{{ cartStore.cartCount }}</span>
        </div>
        
        <!-- 用户登录状态 -->
        <template v-if="userStore.user_token">
          <!-- 用户下拉菜单 -->
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
        <!-- 未登录状态 -->
        <template v-else>
          <button class="login btn hidden-sm" @click="goLogin()">登录</button>
        </template>

        <!-- 消息通知图标 -->
        <div class="right-icon" @click="msgVisible = true">
          <img :src="MessageIcon" />
          <span class="msg-point" style=""></span>
        </div>
        <!-- 消息通知抽屉 -->
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
/**
 * 导入Vue相关依赖
 */
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

/**
 * 初始化Vue相关实例
 */
const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const cartStore = useCartStore();

/**
 * 响应式数据定义
 */
let loading = ref(false)
let msgVisible = ref(false)
let msgData = ref([] as any)
let categoryList = ref([] as any)
let tagList = ref([] as any)
let showCategoryPanel = ref(false)
let showTagsPanel = ref(false)
let currentCategoryIndex = ref(-1)

/**
 * 标签颜色数组
 */
const tagColors = ['#4684e2', '#7a5af8', '#ff6b00', '#2cc58c', '#ff4d94', '#00b7c3', '#f7b500']

/**
 * 组件挂载后执行
 */
onMounted(()=>{
  getMessageList()
  getCategoryTree()
  getTagList()
})

/**
 * 获取消息列表
 * 
 * @description 从服务器获取用户消息列表
 * @throws {Error} 当API请求失败时抛出错误
 */
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

/**
 * 获取分类树数据
 * 
 * @description 从服务器获取商品分类的树形结构数据
 * @throws {Error} 当API请求失败时抛出错误
 */
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

/**
 * 获取标签列表
 * 
 * @description 从服务器获取热门标签列表
 * @throws {Error} 当API请求失败时抛出错误
 */
const getTagList = () => {
  listTagApi({}).then(res => {
    tagList.value = res.data
  }).catch(err => {
    console.error('获取标签失败:', err)
  })
}

/**
 * 获取标签颜色
 * 
 * @description 根据标签ID获取对应的颜色
 * @param {number} id - 标签ID
 * @returns {string} 标签颜色值
 */
const getTagColor = (id) => {
  const index = id % tagColors.length
  return tagColors[index]
}

/**
 * 导航到分类页面
 * 
 * @description 根据分类ID跳转到对应的商品列表页面
 * @param {number} categoryId - 分类ID
 */
const navigateToCategory = (categoryId) => {
  // 查找分类数据
  let categoryName = '分类';
  let foundCategory = false;
  
  // 查找一级分类
  for (const category of categoryList.value) {
    if (category.id === categoryId) {
      categoryName = category.title;
      foundCategory = true;
      break;
    }
    
    // 如果有子分类，查找二级分类
    if (category.children && category.children.length > 0) {
      for (const subCategory of category.children) {
        if (subCategory.id === categoryId) {
          categoryName = subCategory.title;
          foundCategory = true;
          break;
        }
        
        // 如果有孙分类，查找三级分类
        if (subCategory.children && subCategory.children.length > 0) {
          for (const thirdCategory of subCategory.children) {
            if (thirdCategory.id === categoryId) {
              categoryName = thirdCategory.title;
              foundCategory = true;
              break;
            }
          }
        }
        
        if (foundCategory) break;
      }
    }
    
    if (foundCategory) break;
  }
  
  // 路由跳转，添加分类名称参数
  router.push({
    name: 'search',
    query: { 
      c: categoryId,
      category_name: categoryName 
    }
  });
  
  showCategoryPanel.value = false;
}

/**
 * 导航到标签页面
 * 
 * @description 根据标签ID跳转到对应的商品列表页面
 * @param {number} tagId - 标签ID
 */
const navigateToTag = (tagId) => {
  router.push({
    name: 'search',
    query: { tag: tagId }
  })
  showTagsPanel.value = false
}

/**
 * 导航到所有标签页面
 * 
 * @description 跳转到所有标签的展示页面
 */
const navigateToAllTags = () => {
  router.push({
    name: 'search'
  })
  showTagsPanel.value = false
}

/**
 * 跳转到登录页面
 * 
 * @description 导航到用户登录页面
 */
const goLogin = () => {
  router.push({name: 'login'})
}

/**
 * 跳转到用户中心
 * 
 * @description 根据菜单名称导航到用户中心对应页面
 * @param {string} menuName - 菜单名称
 */
const goUserCenter = (menuName) => {
  router.push({name: menuName})
}

/**
 * 退出登录
 * 
 * @description 执行用户退出登录操作
 * @throws {Error} 当退出登录失败时抛出错误
 */
const quit= () => {
  userStore.logout().then(res => {
    router.push({name: 'portal'})
  })
}

/**
 * 关闭消息抽屉
 * 
 * @description 关闭消息通知抽屉面板
 */
const onClose = () => {
  msgVisible.value = false;
}

/**
 * 处理头像加载错误
 * 
 * @description 当用户头像加载失败时使用默认头像
 * @param {Event} e - 图片加载错误事件
 */
const handleAvatarError = (e) => {
  console.log('Avatar load error:', e);
  e.target.src = AvatarIcon;
};

/**
 * 获取头像URL
 * 
 * @description 根据头像文件名生成完整的头像URL
 * @param {string} avatar - 头像文件名
 * @returns {string} 完整的头像URL
 */
const getAvatarUrl = (avatar) => {
  if (!avatar) return AvatarIcon;
  return `/api/staticfiles/avatar/${avatar}`;
};
</script>

<style scoped lang="less">
/**
 * 主导航栏基础样式
 * 设置固定定位、背景色和阴影效果
 */
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

/**
 * 头部容器样式
 * 设置高度、布局和内边距
 */
.header-container {
  height: 70px;
  display: flex;
  align-items: center;
  width: 1200px;
  max-width: 100%;
  margin: 0 auto;
  padding: 0 24px;
}

/**
 * Logo区域样式
 */
.logo {
  margin-right: 30px;
  
  /**
   * Logo容器样式
   */
  .logo-container {
    display: flex;
    align-items: center;
    cursor: pointer;
    
    /**
     * Logo图标样式
     */
    .logo-icon {
      position: relative;
      width: 38px;
      height: 38px;
      margin-right: 10px;
      
      /**
       * 购物袋图标样式
       */
      .shopping-bag {
        position: relative;
        width: 100%;
        height: 100%;
        
        /**
         * 购物袋主体样式
         */
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
        
        /**
         * 购物袋提手样式
         */
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
        
        /**
         * 高光效果样式
         */
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
    
    /**
     * Logo品牌文字样式
     */
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
    
    /**
     * Logo悬浮效果
     */
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

/**
 * 导航区域样式
 */
.menu-container {
  display: flex;
  align-items: center;
  height: 40px;
}

/**
 * 分类触发器样式
 */
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
  
  /**
   * 悬浮效果
   */
  &:hover, &.active {
    color: #4684e2;
  }
  
  /**
   * 分类图标样式
   */
  .category-icon {
    display: inline-block;
    width: 16px;
    height: 16px;
    margin-right: 8px;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23333"><path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/></svg>') center/cover no-repeat;
  }
  
  /**
   * 下拉箭头样式
   */
  .down-arrow {
    display: inline-block;
    width: 12px;
    height: 12px;
    margin-left: 4px;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23666"><path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z"/></svg>') center/cover no-repeat;
  }
}

/**
 * 标签容器样式
 */
.tags-container {
  position: relative;
  margin-right: 20px;
}

/**
 * 标签触发器样式
 */
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
  
  /**
   * 悬浮效果
   */
  &:hover {
    color: #4684e2;
  }
  
  /**
   * 标签图标样式
   */
  .tag-icon {
    display: inline-block;
    width: 16px;
    height: 16px;
    margin-right: 8px;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23333"><path d="M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41 0-.55-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z"/></svg>') center/cover no-repeat;
  }
  
  /**
   * 下拉箭头样式
   */
  .down-arrow {
    display: inline-block;
    width: 12px;
    height: 12px;
    margin-left: 4px;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23666"><path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z"/></svg>') center/cover no-repeat;
  }
}

/**
 * 主导航菜单样式
 */
.main-nav {
  display: flex;
  height: 40px;
  
  /**
   * 导航项样式
   */
  .nav-item {
    padding: 0 18px;
    line-height: 40px;
    font-weight: 500;
    color: #333;
    cursor: pointer;
    position: relative;
    text-decoration: none;
    
    /**
     * 悬浮效果
     */
    &:hover {
      color: #4684e2;
    }
    
    /**
     * 激活状态样式
     */
    &.active {
      color: #4684e2;
      font-weight: 600;
      
      /**
       * 激活状态指示器
       */
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

/**
 * 间距样式
 */
.spacer {
  flex: 1;
}

/**
 * 右侧功能区样式
 */
.right-view {
  display: flex;
  align-items: center;
  
  /**
   * 快速链接样式
   */
  .quick-links {
    display: flex;
    margin-right: 20px;
    
    /**
     * 快速链接项样式
     */
    .quick-link {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin: 0 12px;
      text-decoration: none;
      
      /**
       * 图标样式
       */
      img {
        width: 20px;
        height: 20px;
        opacity: 0.7;
        transition: opacity 0.2s;
      }
      
      /**
       * 文字样式
       */
      span {
        font-size: 12px;
        color: #666;
        margin-top: 4px;
        transition: color 0.2s;
      }
      
      /**
       * 悬浮效果
       */
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
  
  /**
   * 购物车图标样式
   */
  .cart-icon {
    position: relative;
    margin: 0 20px;
    cursor: pointer;
    
    /**
     * 图标样式
     */
    img {
      width: 24px;
      height: 24px;
      opacity: 0.7;
      transition: opacity 0.2s;
    }
    
    /**
     * 悬浮效果
     */
    &:hover img {
      opacity: 1;
    }
    
    /**
     * 购物车数量样式
     */
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
  
  /**
   * 用户头像样式
   */
  .self-img {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    cursor: pointer;
    margin-right: 8px;
    border: 2px solid transparent;
    transition: all 0.2s;
    
    /**
     * 悬浮效果
     */
    &:hover {
      border-color: #4684e2;
    }
  }
  
  /**
   * 用户名样式
   */
  .user-name {
    font-size: 14px;
    color: #333;
    margin-right: 4px;
  }
  
  /**
   * 下拉箭头样式
   */
  .dropdown-arrow {
    font-size: 12px;
    color: #666;
  }
  
  /**
   * 用户菜单样式
   */
  :deep(.user-menu) {
    min-width: 160px;
    padding: 4px 0;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    
    /**
     * 菜单项样式
     */
    .menu-item {
      padding: 8px 16px;
      margin: 2px 0;
      
      /**
       * 链接样式
       */
      a {
        display: flex;
        align-items: center;
        color: #333;
        
        /**
         * 菜单图标样式
         */
        .menu-icon {
          font-size: 16px;
          margin-right: 8px;
          color: #666;
        }
        
        /**
         * 悬浮效果
         */
        &:hover {
          color: #4684e2;
          
          .menu-icon {
            color: #4684e2;
          }
        }
      }
    }
    
    /**
     * 菜单项悬浮效果
     */
    .ant-dropdown-menu-item:hover {
      background-color: #f5f7fa;
    }
  }
  
  /**
   * 右侧图标样式
   */
  .right-icon {
    position: relative;
    margin-left: 10px;
    cursor: pointer;
    
    /**
     * 图标样式
     */
    img {
      width: 22px;
      height: 22px;
      opacity: 0.7;
      transition: opacity 0.2s;
    }
    
    /**
     * 悬浮效果
     */
    &:hover img {
      opacity: 1;
    }
    
    /**
     * 消息提示点样式
     */
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
  
  /**
   * 登录按钮样式
   */
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
    
    /**
     * 悬浮效果
     */
    &:hover {
      background: #3670c5;
      transform: translateY(-2px);
      box-shadow: 0 4px 10px rgba(70, 132, 226, 0.3);
    }
  }
}

/**
 * 消息列表内容样式
 */
.list-content {
  .notification-view {
    .list {
      /**
       * 消息项样式
       */
      .notification-item {
        cursor: default;
        padding: 16px 0;
        border-bottom: 1px solid #f0f0f0;
        
        /**
         * 最后一项样式
         */
        &:last-child {
          border-bottom: none;
        }
        
        /**
         * 内容框样式
         */
        .content-box {
          /**
           * 头部样式
           */
          .header {
            margin-bottom: 12px;
            
            /**
             * 标题样式
             */
            .title-txt {
              font-weight: 600;
              color: #333;
              font-size: 16px;
            }
            
            /**
             * 时间样式
             */
            .time {
              color: #999;
              font-size: 12px;
            }
          }
          
          /**
           * 内容样式
           */
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

/**
 * 分类面板样式
 */
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
  
  /**
   * 分类面板内部样式
   */
  .category-panel-inner {
    display: flex;
    height: 480px;
    position: relative;
  }
}

/**
 * 一级分类列表样式
 */
.primary-categories {
  width: 200px;
  padding: 0;
  margin: 0;
  list-style: none;
  border-right: 1px solid rgba(240, 240, 240, 0.7);
  height: 100%;
  overflow-y: auto;
  
  /**
   * 一级分类项样式
   */
  .primary-category-item {
    position: relative;
    padding: 12px 15px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    cursor: pointer;
    transition: all 0.2s;
    
    /**
     * 悬浮和激活状态样式
     */
    &:hover, &.active {
      background-color: rgba(247, 247, 247, 0.8);
      color: #4684e2;
    }
    
    /**
     * 分类名称样式
     */
    .category-name {
      flex: 1;
    }
    
    /**
     * 箭头图标样式
     */
    .arrow-icon {
      width: 12px;
      height: 12px;
      background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23999"><path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/></svg>') center/cover no-repeat;
    }
  }
}

/**
 * 二级分类容器样式
 */
.sub-categories-container {
  position: absolute;
  left: 200px;
  top: 0;
  width: 600px;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  padding: 15px;
  
  /**
   * 面板内容样式
   */
  .panel-content {
    flex: 1;
    padding-right: 15px;
    overflow-y: auto;
    max-height: 450px;
  }
  
  /**
   * 二级分类区域样式
   */
  .sub-category-section {
    margin-bottom: 15px;
  }
  
  /**
   * 二级分类头部样式
   */
  .sub-category-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 0;
    border-bottom: 1px solid rgba(240, 240, 240, 0.8);
    margin-bottom: 10px;
    cursor: pointer;
    
    /**
     * 二级分类名称样式
     */
    .sub-category-name {
      font-size: 14px;
      font-weight: 500;
      color: #333;
    }
    
    /**
     * 右箭头图标样式
     */
    .arrow-right {
      width: 12px;
      height: 12px;
      background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23999"><path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/></svg>') center/cover no-repeat;
    }
    
    /**
     * 悬浮效果
     */
    &:hover {
      .sub-category-name {
        color: #4684e2;
      }
    }
  }

  /**
   * 三级分类列表样式
   */
  .third-categories {
    display: flex;
    flex-wrap: wrap;
    
    /**
     * 三级分类项样式
     */
    .third-category-item {
      padding: 5px 10px;
      margin: 0 10px 10px 0;
      background: rgba(245, 245, 245, 0.8);
      border-radius: 4px;
      font-size: 12px;
      color: #666;
      cursor: pointer;
      transition: all 0.2s;
      
      /**
       * 悬浮效果
       */
      &:hover {
        background: rgba(230, 247, 255, 0.9);
        color: #4684e2;
      }
    }
  }
}

/**
 * 标签面板样式
 */
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

/**
 * 标签列表样式
 */
.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  
  /**
   * 标签项样式
   */
  .tag-item {
    padding: 5px 12px;
    border-radius: 15px;
    font-size: 12px;
    color: white;
    cursor: pointer;
    transition: all 0.3s;
    
    /**
     * 悬浮效果
     */
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
    }
  }
  
  /**
   * 查看更多样式
   */
  .view-more {
    padding: 5px 12px;
    border-radius: 15px;
    font-size: 12px;
    color: #666;
    background-color: #f5f5f5;
    cursor: pointer;
    transition: all 0.3s;
    
    /**
     * 悬浮效果
     */
    &:hover {
      color: #4684e2;
      background-color: #e6f7ff;
      transform: translateY(-2px);
    }
  }
}
</style>
