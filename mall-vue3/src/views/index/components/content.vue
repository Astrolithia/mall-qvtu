<!--
/**
 * 首页内容展示组件
 * 
 * 该组件实现了电商平台首页的商品展示功能，包括：
 * 1. 为你推荐商品展示
 * 2. 热销榜单商品展示
 * 3. 新品上市商品展示
 * 4. 商品详情跳转
 * 5. 购物车添加功能
 * 
 * 组件依赖：
 * - Vue Composition API
 * - Ant Design Vue 组件库
 * - Vue Router
 * - Vuex Store
 * - 商品管理API服务
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <!-- 内容区域：包含所有商品展示区块 -->
  <div class="content-view">
    <div class="content-padding">
      <!-- 为你推荐区域：展示个性化推荐商品 -->
      <div class="recommendation-section">
        <!-- 区域标题和查看更多链接 -->
        <div class="section-header">
          <h2>为你推荐</h2>
          <a class="view-more" @click="viewMoreRecommend">查看更多 <right-outlined /></a>
        </div>
        
        <!-- 商品展示区域：使用加载状态包装 -->
        <a-spin :spinning="recommendLoading">
          <!-- 商品列表网格布局 -->
          <div class="product-showcase">
            <!-- 单个商品卡片 -->
            <div
              v-for="item in recommendProducts"
              :key="item.id"
              @click="handleDetail(item)"
              class="product-item"
            >
              <!-- 商品图片区域 -->
              <div class="product-image">
                <img :src="item.cover" :alt="item.title" />
                <!-- 悬浮操作按钮 -->
                <div class="hover-overlay">
                  <div class="action-button">
                    <eye-outlined />
                    <span>查看详情</span>
                  </div>
                  <div class="action-button cart-button" @click.stop="handleAddToCart(item)">
                    <shopping-cart-outlined />
                    <span>加入购物车</span>
                  </div>
                </div>
                
                <!-- 折扣标签：显示商品折扣信息 -->
                <div class="discount-badge" v-if="item.originalPrice && item.originalPrice > item.price">
                  {{ calculateDiscount(item.price, item.originalPrice) }}折
                </div>
              </div>
              
              <!-- 商品信息区域 -->
              <div class="product-info">
                <!-- 商品标题 -->
                <div class="product-title">{{ item.title }}</div>
                
                <!-- 分类和标签信息：显示商品分类和标签 -->
                <div class="tags-container">
                  <span class="category-tag" v-if="item.classification_title">
                    <template v-if="item.classification_path">
                      {{ formatCategoryPath(item.classification_path) }}
                    </template>
                    <template v-else>
                      {{ item.classification_title }}
                    </template>
                  </span>
                  <span 
                    class="product-tag" 
                    v-for="tag in item.tagList && item.tagList.length > 0 ? item.tagList.slice(0, 2) : []" 
                    :key="tag.id"
                  >
                    {{ tag.title }}
                  </span>
                </div>
                
                <!-- 价格区域：显示当前价格和原价 -->
                <div class="price-area">
                  <div class="current-price">
                    <span class="symbol">¥</span>
                    <span class="amount">{{ item.price }}</span>
                  </div>
                  <div class="original-price" v-if="item.originalPrice && item.originalPrice > item.price">
                    <span class="symbol">¥</span>
                    <span class="amount">{{ item.originalPrice }}</span>
                  </div>
                </div>
                
                <!-- 商品评分和销量信息 -->
                <div class="product-stats">
                  <div class="rating" v-if="item.score > 0">
                    <span class="stars">★★★★★</span>
                    <span class="score">{{ Number(item.score).toFixed(1) }}</span>
                    <span class="sold">{{ item.salesCount || 0 }}人已购</span>
                  </div>
                  <div class="no-rating" v-else>
                    <span class="sold">{{ item.salesCount || 0 }}人已购</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 空状态提示：当没有商品时显示 -->
          <div v-if="recommendProducts.length <= 0 && !recommendLoading" class="empty-state">
            <a-empty description="暂无推荐商品" />
          </div>
        </a-spin>
      </div>
      
      <!-- 热销榜单区域：展示热销商品 -->
      <div class="recommendation-section">
        <!-- 区域标题和查看更多链接 -->
        <div class="section-header">
          <h2>热销榜单</h2>
          <a class="view-more" @click="viewMoreHot">查看更多 <right-outlined /></a>
        </div>
        
        <!-- 商品展示区域：使用加载状态包装 -->
        <a-spin :spinning="hotLoading">
          <!-- 商品列表网格布局 -->
          <div class="product-showcase">
            <!-- 单个商品卡片 -->
            <div
              v-for="(item, index) in hotProducts"
              :key="item.id"
              @click="handleDetail(item)"
              class="product-item"
            >
              <!-- 热销排名标签：显示商品排名 -->
              <div class="rank-badge" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
              
              <!-- 商品图片区域 -->
              <div class="product-image">
                <img :src="item.cover" :alt="item.title" />
                <!-- 悬浮操作按钮 -->
                <div class="hover-overlay">
                  <div class="action-button">
                    <eye-outlined />
                    <span>查看详情</span>
                  </div>
                  <div class="action-button cart-button" @click.stop="handleAddToCart(item)">
                    <shopping-cart-outlined />
                    <span>加入购物车</span>
                  </div>
                </div>
                
                <!-- 折扣标签：显示商品折扣信息 -->
                <div class="discount-badge" v-if="item.originalPrice && item.originalPrice > item.price">
                  {{ calculateDiscount(item.price, item.originalPrice) }}折
                </div>
              </div>
              
              <!-- 商品信息区域 -->
              <div class="product-info">
                <!-- 商品标题 -->
                <div class="product-title">{{ item.title }}</div>
                
                <!-- 分类和标签信息：显示商品分类和标签 -->
                <div class="tags-container">
                  <span class="category-tag" v-if="item.classification_title">
                    <template v-if="item.classification_path">
                      {{ formatCategoryPath(item.classification_path) }}
                    </template>
                    <template v-else>
                      {{ item.classification_title }}
                    </template>
                  </span>
                  <span 
                    class="product-tag" 
                    v-for="tag in item.tagList && item.tagList.length > 0 ? item.tagList.slice(0, 2) : []" 
                    :key="tag.id"
                  >
                    {{ tag.title }}
                  </span>
                </div>
                
                <!-- 价格区域：显示当前价格和原价 -->
                <div class="price-area">
                  <div class="current-price">
                    <span class="symbol">¥</span>
                    <span class="amount">{{ item.price }}</span>
                  </div>
                  <div class="original-price" v-if="item.originalPrice && item.originalPrice > item.price">
                    <span class="symbol">¥</span>
                    <span class="amount">{{ item.originalPrice }}</span>
                  </div>
                </div>
                
                <!-- 销量信息：强调热销数据 -->
                <div class="product-stats">
                  <div class="hot-sales">
                    <fire-outlined class="fire-icon" />
                    <span class="hot-count">{{ item.salesCount || 0 }}人已购</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 空状态提示：当没有商品时显示 -->
          <div v-if="hotProducts.length <= 0 && !hotLoading" class="empty-state">
            <a-empty description="暂无热销商品" />
          </div>
        </a-spin>
      </div>
      
      <!-- 新品上市区域：展示新上架商品 -->
      <div class="recommendation-section">
        <!-- 区域标题和查看更多链接 -->
        <div class="section-header">
          <h2>新品上市</h2>
          <a class="view-more" @click="viewMoreNew">查看更多 <right-outlined /></a>
        </div>
        
        <!-- 商品展示区域：使用加载状态包装 -->
        <a-spin :spinning="newLoading">
          <!-- 商品列表网格布局 -->
          <div class="product-showcase">
            <!-- 单个商品卡片 -->
            <div
              v-for="item in newProducts"
              :key="item.id"
              @click="handleDetail(item)"
              class="product-item"
            >
              <!-- 新品标签 -->
              <div class="new-badge">新品</div>
              
              <!-- 商品图片区域 -->
              <div class="product-image">
                <img :src="item.cover" :alt="item.title" />
                <!-- 悬浮操作按钮 -->
                <div class="hover-overlay">
                  <div class="action-button">
                    <eye-outlined />
                    <span>查看详情</span>
                  </div>
                  <div class="action-button cart-button" @click.stop="handleAddToCart(item)">
                    <shopping-cart-outlined />
                    <span>加入购物车</span>
                  </div>
                </div>
                
                <!-- 折扣标签：显示商品折扣信息 -->
                <div class="discount-badge" v-if="item.originalPrice && item.originalPrice > item.price">
                  {{ calculateDiscount(item.price, item.originalPrice) }}折
                </div>
              </div>
              
              <!-- 商品信息区域 -->
              <div class="product-info">
                <!-- 商品标题 -->
                <div class="product-title">{{ item.title }}</div>
                
                <!-- 分类和标签信息：显示商品分类和标签 -->
                <div class="tags-container">
                  <span class="category-tag" v-if="item.classification_title">
                    <template v-if="item.classification_path">
                      {{ formatCategoryPath(item.classification_path) }}
                    </template>
                    <template v-else>
                      {{ item.classification_title }}
                    </template>
                  </span>
                  <span 
                    class="product-tag" 
                    v-for="tag in item.tagList && item.tagList.length > 0 ? item.tagList.slice(0, 2) : []" 
                    :key="tag.id"
                  >
                    {{ tag.title }}
                  </span>
                </div>
                
                <!-- 价格区域：显示当前价格和原价 -->
                <div class="price-area">
                  <div class="current-price">
                    <span class="symbol">¥</span>
                    <span class="amount">{{ item.price }}</span>
                  </div>
                  <div class="original-price" v-if="item.originalPrice && item.originalPrice > item.price">
                    <span class="symbol">¥</span>
                    <span class="amount">{{ item.originalPrice }}</span>
                  </div>
                </div>
                
                <!-- 上架时间信息 -->
                <div class="product-stats">
                  <div class="new-time">
                    <clock-circle-outlined class="time-icon" />
                    <span class="time-text">{{ formatNewTime(item.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 空状态提示：当没有商品时显示 -->
          <div v-if="newProducts.length <= 0 && !newLoading" class="empty-state">
            <a-empty description="暂无新品上市" />
          </div>
        </a-spin>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 导入必要的依赖和组件
 * - Vue Composition API：响应式编程
 * - Vue Router：路由管理
 * - API服务：商品列表接口
 * - 常量：基础URL
 * - Store：用户和购物车状态管理
 * - UI组件：消息提示和空状态
 * - 图标组件：各种功能图标
 */
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { listApi as listThingList } from '../../../api/thing';
import { BASE_URL } from "../../../store/constants";
import { useUserStore } from "../../../store";
import { useCartStore } from "../../../store/modules/cart";
import { message } from 'ant-design-vue';
import { Empty } from 'ant-design-vue';
import { 
  EyeOutlined, ShoppingCartOutlined, RightOutlined,
  FireOutlined, ClockCircleOutlined
} from '@ant-design/icons-vue';

/**
 * 初始化Store和Router实例
 */
const userStore = useUserStore();
const cartStore = useCartStore();
const router = useRouter();

/**
 * 加载状态管理
 * 控制各个区域的加载动画显示
 */
const recommendLoading = ref(false);  // 推荐商品加载状态
const hotLoading = ref(false);        // 热销商品加载状态
const newLoading = ref(false);        // 新品商品加载状态

/**
 * 商品数据管理
 * 存储各个区域的商品列表
 */
const recommendProducts = ref([]);    // 推荐商品列表
const hotProducts = ref([]);          // 热销商品列表
const newProducts = ref([]);          // 新品商品列表

/**
 * 组件挂载时的初始化操作
 * 获取所有商品数据
 */
onMounted(() => {
  getRecommendProducts();
  getHotProducts();
  getNewProducts();
});

/**
 * 计算商品折扣率
 * 
 * @param {number} price - 当前价格
 * @param {number} originalPrice - 原价
 * @return {string|null} 折扣率，保留一位小数，如无折扣返回null
 */
const calculateDiscount = (price, originalPrice) => {
  if (!originalPrice || originalPrice <= 0) return null;
  const discount = (price / originalPrice * 10).toFixed(1);
  return discount;
};

/**
 * 处理商品详情跳转
 * 在新窗口打开商品详情页
 * 
 * @param {Object} item - 商品数据对象
 */
const handleDetail = (item) => {
  // 使用新页面打开详情
  let url = router.resolve({
    name: 'detail',
    query: { id: item.id }
  });
  window.open(url.href, '_blank');
};

/**
 * 格式化新品上架时间
 * 根据时间差显示不同的时间描述
 * 
 * @param {string|number} timestamp - 时间戳
 * @return {string} 格式化后的时间描述
 */
const formatNewTime = (timestamp) => {
  if (!timestamp) return '刚刚上架';
  
  const now = new Date();
  const createTime = new Date(timestamp);
  const diffDays = Math.floor((now - createTime) / (1000 * 60 * 60 * 24));
  
  if (diffDays === 0) {
    return '今日上新';
  } else if (diffDays <= 7) {
    return `${diffDays}天前上新`;
  } else {
    return createTime.toLocaleDateString();
  }
};

/**
 * 处理加入购物车操作
 * 验证用户登录状态并添加商品到购物车
 * 
 * @param {Object} item - 商品数据对象
 * @throws {Error} 当商品信息无效或添加失败时抛出错误
 */
const handleAddToCart = (item) => {
  if (!userStore.user_token) {
    message.warning('请先登录');
    return;
  }
  
  if (!item || !item.id) {
    message.error('商品信息有误');
    return;
  }
  
  try {
    cartStore.addToCart({
      id: item.id,
      title: item.title,
      cover: item.cover,
      price: Number(item.price),
      count: 1
    });
    message.success(`已将 ${item.title} 添加到购物车`);
  } catch (error) {
    console.error('添加购物车失败:', error);
    message.error('添加购物车失败');
  }
};

/**
 * 查看更多推荐商品
 * 跳转到搜索页面并设置排序参数
 */
const viewMoreRecommend = () => {
  router.push({ name: 'search', query: { sort: 'recommend' } });
};

/**
 * 查看更多热销商品
 * 跳转到搜索页面并设置排序参数
 */
const viewMoreHot = () => {
  router.push({ name: 'search', query: { sort: 'hot' } });
};

/**
 * 查看更多新品
 * 跳转到搜索页面并设置排序参数
 */
const viewMoreNew = () => {
  router.push({ name: 'search', query: { sort: 'new' } });
};

/**
 * 格式化分类路径
 * 将分类路径字符串转换为更易读的格式
 * 
 * @param {string} path - 分类路径字符串
 * @return {string} 格式化后的分类路径
 */
const formatCategoryPath = (path) => {
  if (!path) return '';
  return path.split('>').join(' > ');
};

/**
 * 获取推荐商品列表
 * 调用API获取推荐商品并处理数据
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
const getRecommendProducts = () => {
  recommendLoading.value = true;
  
  listThingList({ sort: 'recommend', limit: 4 }).then(res => {
    // 处理返回的商品数据
    res.data.forEach(item => {
      // 处理商品图片URL
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
      }
      
      // 确保数值类型正确
      if (item.originalPrice) {
        item.originalPrice = parseFloat(item.originalPrice);
      }
      if (item.price) {
        item.price = parseFloat(item.price);
      }
      if (item.score) {
        item.score = parseFloat(item.score);
      }
      
      // 计算折扣信息
      if (item.originalPrice && item.originalPrice > item.price) {
        item.discountRate = calculateDiscount(item.price, item.originalPrice);
        item.saveAmount = (item.originalPrice - item.price).toFixed(2);
      }
    });
    
    recommendProducts.value = res.data;
    recommendLoading.value = false;
  }).catch(err => {
    console.log(err);
    recommendLoading.value = false;
  });
};

/**
 * 获取热销商品列表
 * 调用API获取热销商品并处理数据
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
const getHotProducts = () => {
  hotLoading.value = true;
  
  listThingList({ sort: 'hot', limit: 4 }).then(res => {
    // 处理返回的商品数据
    res.data.forEach(item => {
      // 处理商品图片URL
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
      }
      
      // 确保数值类型正确
      if (item.originalPrice) {
        item.originalPrice = parseFloat(item.originalPrice);
      }
      if (item.price) {
        item.price = parseFloat(item.price);
      }
      if (item.score) {
        item.score = parseFloat(item.score);
      }
      
      // 计算折扣信息
      if (item.originalPrice && item.originalPrice > item.price) {
        item.discountRate = calculateDiscount(item.price, item.originalPrice);
        item.saveAmount = (item.originalPrice - item.price).toFixed(2);
      }
    });
    
    hotProducts.value = res.data;
    hotLoading.value = false;
  }).catch(err => {
    console.log(err);
    hotLoading.value = false;
  });
};

/**
 * 获取新品商品列表
 * 调用API获取新品商品并处理数据
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
const getNewProducts = () => {
  newLoading.value = true;
  
  listThingList({ sort: 'recent', limit: 4 }).then(res => {
    // 处理返回的商品数据
    res.data.forEach(item => {
      // 处理商品图片URL
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
      }
      
      // 确保数值类型正确
      if (item.originalPrice) {
        item.originalPrice = parseFloat(item.originalPrice);
      }
      if (item.price) {
        item.price = parseFloat(item.price);
      }
      if (item.score) {
        item.score = parseFloat(item.score);
      }
      
      // 计算折扣信息
      if (item.originalPrice && item.originalPrice > item.price) {
        item.discountRate = calculateDiscount(item.price, item.originalPrice);
        item.saveAmount = (item.originalPrice - item.price).toFixed(2);
      }
    });
    
    newProducts.value = res.data;
    newLoading.value = false;
  }).catch(err => {
    console.log(err);
    newLoading.value = false;
  });
};
</script>

<style scoped lang="less">
/**
 * 内容区域基础样式
 * 设置最大宽度、居中对齐和内边距
 */
.content-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/**
 * 内容区域顶部内边距
 */
.content-padding {
  padding-top: 90px;
}

/**
 * 区域标题样式
 * 包含标题文字和查看更多链接的布局
 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  /**
   * 标题文字样式
   */
  h2 {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin: 0;
    position: relative;
    
    /**
     * 标题下划线装饰
     */
    &:after {
      content: '';
      position: absolute;
      left: 0;
      bottom: -8px;
      width: 40px;
      height: 3px;
      background: linear-gradient(to right, #4684e2, #5c8bea);
      border-radius: 3px;
    }
  }
  
  /**
   * 查看更多链接样式
   */
  .view-more {
    font-size: 14px;
    color: #666;
    cursor: pointer;
    display: flex;
    align-items: center;
    
    &:hover {
      color: #4684e2;
    }
    
    .anticon {
      margin-left: 4px;
      font-size: 12px;
    }
  }
}

/**
 * 推荐区域样式
 * 设置背景、圆角、阴影和内边距
 */
.recommendation-section {
  background-color: white;
  border-radius: 10px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

/**
 * 商品展示区样式
 * 使用网格布局展示商品卡片
 */
.product-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(270px, 1fr));
  gap: 24px;
  margin-top: 20px;
  
  /**
   * 商品卡片样式
   */
  .product-item {
    border-radius: 8px;
    background-color: white;
    overflow: hidden;
    transition: all 0.3s ease;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
    cursor: pointer;
    position: relative;
    
    /**
     * 卡片悬浮效果
     */
    &:hover {
      transform: translateY(-6px);
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
      
      .product-image {
        img {
          transform: scale(1.05);
        }
        
        .hover-overlay {
          opacity: 1;
        }
      }
    }
    
    /**
     * 热销排名标签样式
     */
    .rank-badge {
      position: absolute;
      top: 10px;
      left: 10px;
      width: 26px;
      height: 26px;
      border-radius: 50%;
      background: rgba(0, 0, 0, 0.6);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      font-size: 14px;
      z-index: 10;
      
      /**
       * 前三名特殊样式
       */
      &.top-three {
        background: linear-gradient(135deg, #ff9a9e, #ff6b00);
        width: 30px;
        height: 30px;
        font-size: 16px;
      }
    }
    
    /**
     * 新品标签样式
     */
    .new-badge {
      position: absolute;
      top: 10px;
      left: 10px;
      padding: 3px 8px;
      border-radius: 4px;
      background: linear-gradient(135deg, #7a5af8, #4684e2);
      color: white;
      font-size: 12px;
      font-weight: bold;
      z-index: 10;
    }
    
    /**
     * 商品图片区域样式
     */
    .product-image {
      position: relative;
      height: 220px;
      overflow: hidden;
      background-color: #f8f8f8;
      
      /**
       * 商品图片样式
       */
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.5s ease;
      }
      
      /**
       * 悬浮操作按钮区域
       */
      .hover-overlay {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        display: flex;
        opacity: 0;
        transition: opacity 0.3s ease;
        
        /**
         * 操作按钮样式
         */
        .action-button {
          flex: 1;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: rgba(70, 132, 226, 0.9);
          color: white;
          font-size: 14px;
          transition: background-color 0.2s;
          
          &:hover {
            background-color: rgba(70, 132, 226, 1);
          }
          
          span {
            margin-left: 6px;
          }
          
          /**
           * 购物车按钮特殊样式
           */
          &.cart-button {
            background-color: rgba(255, 107, 0, 0.9);
            
            &:hover {
              background-color: rgba(255, 107, 0, 1);
            }
          }
        }
      }
      
      /**
       * 折扣标签样式
       */
      .discount-badge {
        position: absolute;
        top: 10px;
        right: 10px;
        background: #ff4d4f;
        color: white;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 13px;
        font-weight: bold;
        box-shadow: 0 2px 6px rgba(255, 77, 79, 0.3);
      }
    }
    
    /**
     * 商品信息区域样式
     */
    .product-info {
      padding: 16px;
      
      /**
       * 商品标题样式
       */
      .product-title {
        font-size: 16px;
        font-weight: 500;
        color: #333;
        line-height: 1.4;
        height: 44px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        margin-bottom: 10px;
      }
      
      /**
       * 标签容器样式
       */
      .tags-container {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
        margin-bottom: 12px;
        
        /**
         * 分类和商品标签样式
         */
        .category-tag, .product-tag {
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 12px;
        }
        
        /**
         * 分类标签特殊样式
         */
        .category-tag {
          background-color: #e6f7ff;
          color: #1890ff;
        }
        
        /**
         * 商品标签特殊样式
         */
        .product-tag {
          background-color: #f5f5f5;
          color: #666;
        }
      }
      
      /**
       * 价格区域样式
       */
      .price-area {
        display: flex;
        align-items: baseline;
        margin-bottom: 10px;
        
        /**
         * 当前价格样式
         */
        .current-price {
          color: #ff4d4f;
          margin-right: 8px;
          
          .symbol {
            font-size: 14px;
            margin-right: 2px;
          }
          
          .amount {
            font-size: 20px;
            font-weight: bold;
          }
        }
        
        /**
         * 原价样式
         */
        .original-price {
          color: #999;
          text-decoration: line-through;
          
          .symbol {
            font-size: 12px;
          }
          
          .amount {
            font-size: 14px;
          }
        }
      }
      
      /**
       * 商品统计信息样式
       */
      .product-stats {
        display: flex;
        align-items: center;
        font-size: 12px;
        color: #999;
        
        /**
         * 评分样式
         */
        .rating {
          display: flex;
          align-items: center;
          
          .stars {
            color: #fadb14;
            margin-right: 4px;
          }
          
          .score {
            color: #ff6b00;
            font-weight: 500;
            margin-right: 8px;
          }
        }
        
        /**
         * 销量样式
         */
        .sold {
          display: flex;
          align-items: center;
          
          &:before {
            content: '|';
            margin-right: 8px;
            color: #ddd;
          }
        }
        
        /**
         * 无评分时的销量样式
         */
        .no-rating {
          .sold:before {
            content: '';
            margin-right: 0;
          }
        }
        
        /**
         * 热销样式
         */
        .hot-sales {
          display: flex;
          align-items: center;
          
          .fire-icon {
            color: #ff6b00;
            margin-right: 4px;
          }
          
          .hot-count {
            color: #ff6b00;
            font-weight: 500;
          }
        }
        
        /**
         * 新品时间样式
         */
        .new-time {
          display: flex;
          align-items: center;
          
          .time-icon {
            color: #4684e2;
            margin-right: 4px;
          }
          
          .time-text {
            color: #4684e2;
          }
        }
      }
    }
  }
}

/**
 * 空状态样式
 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}
</style>
