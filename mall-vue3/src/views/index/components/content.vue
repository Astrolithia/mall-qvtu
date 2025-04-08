<template>
  <!-- 内容区域 -->
  <div class="content-view">
    <div class="content-padding">
      <!-- 为你推荐区域 -->
      <div class="recommendation-section">
        <div class="section-header">
          <h2>为你推荐</h2>
          <a class="view-more" @click="viewMoreRecommend">查看更多 <right-outlined /></a>
        </div>
        
        <!-- 商品展示 -->
        <a-spin :spinning="recommendLoading">
          <div class="product-showcase">
            <div
              v-for="item in recommendProducts"
              :key="item.id"
              @click="handleDetail(item)"
              class="product-item"
            >
              <div class="product-image">
                <img :src="item.cover" :alt="item.title" />
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
                
                <!-- 折扣标签 -->
                <div class="discount-badge" v-if="item.originalPrice && item.originalPrice > item.price">
                  {{ calculateDiscount(item.price, item.originalPrice) }}折
                </div>
              </div>
              
              <div class="product-info">
                <div class="product-title">{{ item.title }}</div>
                
                <!-- 分类和标签信息 -->
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
                
                <!-- 价格区域 -->
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
                
                <!-- 商品评分和销量 -->
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
          
          <!-- 空状态提示 -->
          <div v-if="recommendProducts.length <= 0 && !recommendLoading" class="empty-state">
            <a-empty description="暂无推荐商品" />
          </div>
        </a-spin>
      </div>
      
      <!-- 热销榜单区域 -->
      <div class="recommendation-section">
        <div class="section-header">
          <h2>热销榜单</h2>
          <a class="view-more" @click="viewMoreHot">查看更多 <right-outlined /></a>
        </div>
        
        <!-- 商品展示 -->
        <a-spin :spinning="hotLoading">
          <div class="product-showcase">
            <div
              v-for="(item, index) in hotProducts"
              :key="item.id"
              @click="handleDetail(item)"
              class="product-item"
            >
              <!-- 热销排名标签 -->
              <div class="rank-badge" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
              
              <div class="product-image">
                <img :src="item.cover" :alt="item.title" />
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
                
                <!-- 折扣标签 -->
                <div class="discount-badge" v-if="item.originalPrice && item.originalPrice > item.price">
                  {{ calculateDiscount(item.price, item.originalPrice) }}折
                </div>
              </div>
              
              <div class="product-info">
                <div class="product-title">{{ item.title }}</div>
                
                <!-- 分类和标签信息 -->
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
                
                <!-- 价格区域 -->
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
                
                <!-- 销量强调 -->
                <div class="product-stats">
                  <div class="hot-sales">
                    <fire-outlined class="fire-icon" />
                    <span class="hot-count">{{ item.salesCount || 0 }}人已购</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 空状态提示 -->
          <div v-if="hotProducts.length <= 0 && !hotLoading" class="empty-state">
            <a-empty description="暂无热销商品" />
          </div>
        </a-spin>
      </div>
      
      <!-- 新品上市区域 -->
      <div class="recommendation-section">
        <div class="section-header">
          <h2>新品上市</h2>
          <a class="view-more" @click="viewMoreNew">查看更多 <right-outlined /></a>
        </div>
        
        <!-- 商品展示 -->
        <a-spin :spinning="newLoading">
          <div class="product-showcase">
            <div
              v-for="item in newProducts"
              :key="item.id"
              @click="handleDetail(item)"
              class="product-item"
            >
              <!-- 新品标签 -->
              <div class="new-badge">新品</div>
              
              <div class="product-image">
                <img :src="item.cover" :alt="item.title" />
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
                
                <!-- 折扣标签 -->
                <div class="discount-badge" v-if="item.originalPrice && item.originalPrice > item.price">
                  {{ calculateDiscount(item.price, item.originalPrice) }}折
                </div>
              </div>
              
              <div class="product-info">
                <div class="product-title">{{ item.title }}</div>
                
                <!-- 分类和标签信息 -->
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
                
                <!-- 价格区域 -->
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
                
                <!-- 上架时间 -->
                <div class="product-stats">
                  <div class="new-time">
                    <clock-circle-outlined class="time-icon" />
                    <span class="time-text">{{ formatNewTime(item.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 空状态提示 -->
          <div v-if="newProducts.length <= 0 && !newLoading" class="empty-state">
            <a-empty description="暂无新品上市" />
          </div>
        </a-spin>
      </div>
    </div>
  </div>
</template>

<script setup>
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

const userStore = useUserStore();
const cartStore = useCartStore();
const router = useRouter();

// 各区域加载状态
const recommendLoading = ref(false);
const hotLoading = ref(false);
const newLoading = ref(false);

// 各区域商品数据
const recommendProducts = ref([]);
const hotProducts = ref([]);
const newProducts = ref([]);

// 页面加载时获取所有数据
onMounted(() => {
  getRecommendProducts();
  getHotProducts();
  getNewProducts();
});

// 计算折扣率
const calculateDiscount = (price, originalPrice) => {
  if (!originalPrice || originalPrice <= 0) return null;
  const discount = (price / originalPrice * 10).toFixed(1);
  return discount;
};

// 跳转到商品详情
const handleDetail = (item) => {
  // 使用新页面打开详情
  let url = router.resolve({
    name: 'detail',
    query: { id: item.id }
  });
  window.open(url.href, '_blank');
};

// 格式化新品上架时间
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

// 加入购物车
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

// 查看更多链接
const viewMoreRecommend = () => {
  router.push({ name: 'search', query: { sort: 'recommend' } });
};

const viewMoreHot = () => {
  router.push({ name: 'search', query: { sort: 'hot' } });
};

const viewMoreNew = () => {
  router.push({ name: 'search', query: { sort: 'new' } });
};

// 格式化分类路径
const formatCategoryPath = (path) => {
  if (!path) return '';
  return path.split('>').join(' > ');
};

// 获取推荐商品
const getRecommendProducts = () => {
  recommendLoading.value = true;
  
  listThingList({ sort: 'recommend', limit: 4 }).then(res => {
    // 处理返回的商品数据
    res.data.forEach(item => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
      }
      
      // 确保评分和价格信息正确显示
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

// 获取热销商品
const getHotProducts = () => {
  hotLoading.value = true;
  
  listThingList({ sort: 'hot', limit: 4 }).then(res => {
    // 处理返回的商品数据
    res.data.forEach(item => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
      }
      
      // 确保评分和价格信息正确显示
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

// 获取新品商品
const getNewProducts = () => {
  newLoading.value = true;
  
  listThingList({ sort: 'recent', limit: 4 }).then(res => {
    // 处理返回的商品数据
    res.data.forEach(item => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
      }
      
      // 确保评分和价格信息正确显示
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
/* 内容区域样式 */
.content-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-padding {
  padding-top: 90px;
}

/* 公共部分 - 区域标题 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h2 {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin: 0;
    position: relative;
    
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

/* 推荐区域 */
.recommendation-section {
  background-color: white;
  border-radius: 10px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

/* 商品展示区 */
.product-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(270px, 1fr));
  gap: 24px;
  margin-top: 20px;
  
  .product-item {
    border-radius: 8px;
    background-color: white;
    overflow: hidden;
    transition: all 0.3s ease;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
    cursor: pointer;
    position: relative;
    
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
      
      &.top-three {
        background: linear-gradient(135deg, #ff9a9e, #ff6b00);
        width: 30px;
        height: 30px;
        font-size: 16px;
      }
    }
    
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
    
    .product-image {
      position: relative;
      height: 220px;
      overflow: hidden;
      background-color: #f8f8f8;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.5s ease;
      }
      
      .hover-overlay {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        display: flex;
        opacity: 0;
        transition: opacity 0.3s ease;
        
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
          
          &.cart-button {
            background-color: rgba(255, 107, 0, 0.9);
            
            &:hover {
              background-color: rgba(255, 107, 0, 1);
            }
          }
        }
      }
      
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
    
    .product-info {
      padding: 16px;
      
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
      
      .tags-container {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
        margin-bottom: 12px;
        
        .category-tag, .product-tag {
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 12px;
        }
        
        .category-tag {
          background-color: #e6f7ff;
          color: #1890ff;
        }
        
        .product-tag {
          background-color: #f5f5f5;
          color: #666;
        }
      }
      
      .price-area {
        display: flex;
        align-items: baseline;
        margin-bottom: 10px;
        
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
      
      .product-stats {
        display: flex;
        align-items: center;
        font-size: 12px;
        color: #999;
        
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
        
        .sold {
          display: flex;
          align-items: center;
          
          &:before {
            content: '|';
            margin-right: 8px;
            color: #ddd;
          }
        }
        
        .no-rating {
          .sold:before {
            content: '';
            margin-right: 0;
          }
        }
        
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

/* 空状态样式 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}
</style>
