<template>
  <div class="product-detail">
    <Header />
    <div class="container">
      <div class="breadcrumb"><a href="/">首页</a> / <span>商品详情</span></div>
      <div class="product-container">
        <div class="product-gallery">
          <div class="main-image">
            <img :src="detailData.cover" alt="商品图片" />
              </div>
                </div>
        <div class="product-info">
          <div class="product-status">
            <div class="status-badge" v-if="detailData.repertory > 0">有货</div>
            <div class="status-badge out-of-stock" v-else>缺货</div>
                </div>
          <h1 class="product-title">{{ detailData.title }}</h1>
          <div class="product-rating">
            <div class="stars">
              <span v-for="i in 5" :key="i" class="star" :class="{ active: i <= Math.round(avgRating) }">★</span>
                </div>
            <div class="rating-count">{{ avgRating.toFixed(1) }}分 ({{ commentData.length }}条评价)</div>
            <div class="view-count"><i class="icon-view"></i> 浏览量: {{ detailData.viewCount || 0 }}</div>
              </div>
          <div class="price-box">
            <div class="current-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ detailData.price }}</span>
            </div>
            <div class="original-price" v-if="detailData.originalPrice && detailData.originalPrice > detailData.price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ detailData.originalPrice }}</span>
                </div>
            <div class="discount-tag" v-if="discountInfo && detailData.originalPrice > detailData.price">
              <div class="discount-badge">{{ discountInfo.discountText }}</div>
              <div class="saved-amount">{{ discountInfo.savedText }}</div>
                  </div>
                  </div>
          <div class="product-meta">
            <div class="meta-item">
              <span class="label">分类：</span>
              <span class="value category-path">
                <!-- 显示多级分类信息，用 > 分隔 -->
                <template v-if="categoryPath && categoryPath.length > 0">
                  <span 
                    v-for="(cat, index) in categoryPath" 
                    :key="cat.id"
                    class="category-item"
                  >
                    <span class="cat-name" @click="navigateToCategory(cat.id)">{{ cat.title }}</span>
                    <span v-if="index < categoryPath.length - 1" class="separator"> > </span>
                  </span>
                </template>
                <template v-else>
                  {{ detailData.classification_title || '未分类' }}
                </template>
              </span>
                </div>
            <div class="meta-item tags-item" v-if="detailData.tagList && detailData.tagList.length > 0">
              <span class="label">标签：</span>
              <span class="value">
                <a-tag 
                  v-for="tag in detailData.tagList" 
                  :key="tag.id" 
                  color="blue" 
                  class="product-tag"
                  @click="navigateToTag(tag.id)"
                >
                  {{ tag.title }}
                </a-tag>
              </span>
              </div>
            <div class="meta-item">
              <span class="label">库存：</span>
              <span class="value">{{ detailData.repertory }} 件</span>
                </div>
            <div class="meta-item">
              <span class="label">销量：</span>
              <span class="value">{{ detailData.salesCount || 0 }} 件</span>
                  </div>
                  </div>
          <div class="product-actions">
            <div class="quantity-selector">
              <button class="qty-btn" @click="quantity > 1 && quantity--">-</button>
              <input type="number" v-model="quantity" min="1" :max="detailData.repertory || 999" />
              <button class="qty-btn" @click="quantity < (detailData.repertory || 999) && quantity++">+</button>
                </div>
            <div class="action-buttons">
              <button class="cart-btn" @click="addToCart(detailData)">
                <i class="icon-cart"></i>
                <span>加入购物车</span>
              </button>
              <button class="buy-btn" @click="handleOrder(detailData)">
                <i class="icon-buy"></i>
                <span>立即购买</span>
              </button>
              </div>
                </div>
          <div class="product-social">
            <div class="social-item" @click="addToWish()">
              <i class="icon-wish"></i>
              <span>加入心愿单 ({{ detailData.wishCount || 0 }})</span>
                  </div>
            <div class="social-item" @click="collect()">
              <i class="icon-collect"></i>
              <span>收藏 ({{ detailData.collectCount || 0 }})</span>
                  </div>
            <div class="social-item" @click="share()">
              <i class="icon-share"></i>
              <span>分享</span>
                </div>
              </div>
            </div>
          </div>
      <div class="product-details">
        <div class="tabs">
          <div 
            class="tab"
            v-for="(item, index) in tabData"
                :key="index"
            :class="{ active: selectTabIndex === index }"
            @click="selectTab(index)"
          >
            {{ item }}
            </div>
        </div>
        <div class="tab-content">
          <!-- 简介 -->
          <div class="product-description" v-show="selectTabIndex === 0">
            <div v-html="detailData.description"></div>
            </div>

          <!-- 评论 -->
          <div class="product-comments" v-show="selectTabIndex === 1">
            <div class="comment-section">
              <div class="comment-tips">
                <div class="tips-header">
                  <i class="icon-info"></i>
                  <span>评价须知</span>
              </div>
                <div class="tips-content">
                  <p>1. 只有购买过此商品的用户才能发表评价</p>
                  <p>2. 购买商品并确认收货后，可在"我的订单"中对商品进行评价</p>
                  <p>3. 评价应客观公正，请勿包含违法违规、侮辱他人的内容</p>
                </div>
              </div>
            </div>
            
            <div class="comment-list-header">
              <div class="comment-count">共有 {{ commentData.length }} 条评论</div>
              <div class="comment-sort" v-if="commentData.length > 0">
                <span :class="{ active: sortIndex === 0 }" @click="sortCommentList('recent')">最新</span>
                <span class="divider">|</span>
                <span :class="{ active: sortIndex === 1 }" @click="sortCommentList('hot')">热门</span>
              </div>
            </div>
            
            <div class="comment-list">
              <div class="comment-item" v-for="item in commentData" :key="item.id">
                <div class="comment-header">
                  <img :src="AvatarIcon" class="avatar" />
                  <div class="comment-info">
                    <div class="username">{{ item.username }}</div>
                      <div class="time">{{ item.commentTime }}</div>
                    </div>
                  <div class="comment-score">
                    <span class="star-text">评分：</span>
                    <div class="stars-small">
                      <span v-for="i in 5" :key="i" class="star" :class="{ active: i <= Math.round(item.score || 0) }">★</span>
                    </div>
                  </div>
                  <div class="like-btn" @click="like(item.id)">
                    <i class="icon-like"></i>
                    <span>{{ item.likeCount }}</span>
                </div>
                </div>
                <div class="comment-body">
                  {{ item.content }}
                </div>
              </div>
              
              <div class="no-more" v-if="commentData.length > 0">
                      <p>没有更多了</p>
                    </div>
              
              <div class="no-comments" v-else>
                <p>暂无评论</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

      <div class="related-products" v-if="recommendData.length > 0">
        <h2 class="section-title">热门推荐</h2>
        <div class="product-grid">
          <div class="product-card" v-for="item in recommendData" :key="item.id" @click="handleDetail(item)">
            <div class="product-image">
              <img :src="item.cover" :alt="item.title" />
          </div>
            <div class="product-card-body">
              <h3 class="product-card-title">{{ item.title.substring(0, 20) }}</h3>
              <div class="product-card-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ item.price }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    <Footer />
  </div>
</template>

<script setup>
import {message} from "ant-design-vue";
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import AvatarIcon from '/@/assets/images/avatar.jpg';
import {
  detailApi,
  listApi as listThingList,
  updateViewsApi
} from '/@/api/thing'
import {listThingCommentsApi, createApi as createCommentApi, likeApi} from '/@/api/comment'
import {wishApi} from '/@/api/thingWish'
import {collectApi} from '/@/api/thingCollect'
import {BASE_URL} from "/@/store/constants";
import {useRoute, useRouter} from "vue-router/dist/vue-router";
import {useUserStore, useCartStore} from "/@/store";
import {getFormatTime} from "/@/utils";
import axios from "axios";

const router = useRouter()
const route = useRoute()
const userStore = useUserStore();
const cartStore = useCartStore();

let thingId = ref('')
let detailData = ref({})
let tabData = ref(['简介', '评论'])
let selectTabIndex = ref(0)
let commentData = ref([])
let recommendData = ref([])
let sortIndex = ref(0)
let order = ref('recent') // 默认排序最新
let commentRef = ref()
let discountInfo = ref(null)
let quantity = ref(1)
let avgRating = ref(5) // 默认平均评分为5分

// 分类路径数据
const categoryPath = ref([]);

onMounted(()=>{
  thingId.value = route.query.id.trim()
  getThingDetail()
  getRecommendThing()
  getCommentList()
})

const selectTab = (index) => {
  selectTabIndex.value = index
}

const getThingDetail = () => {
  detailApi({id: thingId.value}).then(res => {
    detailData.value = res.data
    detailData.value.cover = BASE_URL + '/api/staticfiles/image/' + detailData.value.cover
    
    // 更新浏览量
    updateViewsApi({id: thingId.value}).then(() => {
      console.log('浏览量+1')
    }).catch(err => {
      console.error('更新浏览量失败', err)
    })
    
    // 获取分类路径
    if (res.data.classificationId) {
      fetchCategoryPath(res.data.classificationId);
    }
  }).catch(err => {
    message.error('获取详情失败')
  })
}

const addToWish = () => {
  let userId = userStore.user_id
  if (userId) {
    wishApi({thingId: thingId.value, userId: userId}).then(res => {
      message.success(res.msg)
      getThingDetail()
    }).catch(err => {
      console.log('操作失败')
    })
  } else {
    message.warn('请先登录')
  }
}

const collect = () => {
  let userId = userStore.user_id
  if (userId) {
    collectApi({thingId: thingId.value, userId: userId}).then(res => {
      message.success(res.msg)
      getThingDetail()
    }).catch(err => {
      console.log('收藏失败')
    })
  } else {
    message.warn('请先登录')
  }
}

const share = () => {
  let content = '分享一个非常好玩的网站 ' + window.location.href
  let shareHref = 'http://service.weibo.com/share/share.php?title=' + content
  window.open(shareHref)
}

const handleOrder = (detailData) => {
  console.log(detailData)
  const userId = userStore.user_id
  router.push({
    name: 'confirm',
    query: {
          id: detailData.id,
          title: detailData.title,
          cover: detailData.cover,
          price: detailData.price
}
  })
}

const getRecommendThing = () => {
  listThingList({sort: 'recommend'}).then(res => {
    res.data.forEach((item, index) => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover
      }
    })
    recommendData.value = res.data.slice(0, 4)
  }).catch(err => {
    console.log(err)
  })
}

const handleDetail = (item) => {
  // 跳转新页面
  let text = router.resolve({name: 'detail', query: {id: item.id}})
  window.open(text.href, '_blank')
}

const like = (commentId) => {
  likeApi({id: commentId}).then(res => {
    getCommentList()
  }).catch(err => {
    console.log(err)
  })
}

const getCommentList = () => {
  listThingCommentsApi({thingId: thingId.value, order: order.value}).then(res => {
    console.log('评论数据:', res.data);
    res.data.forEach(item => {
      item.commentTime = getFormatTime(item.commentTime, true);
      console.log('单条评论:', item, '评分:', item.score);
    })
    commentData.value = res.data
    
    // 计算平均评分
    if (commentData.value.length > 0) {
      const totalScore = commentData.value.reduce((sum, comment) => {
        return sum + (comment.score || 5);
      }, 0);
      avgRating.value = totalScore / commentData.value.length;
    } else {
      avgRating.value = 5; // 默认5分
    }
  }).catch(err => {
    console.log(err)
  })
}

const sortCommentList = (sortType) => {
  sortIndex.value = sortType === 'recent' ? 0 : 1
  order.value = sortType
  getCommentList()
}

// 添加到购物车
const addToCart = (item) => {
  if (!item || !item.id) {
    message.error('商品信息有误');
    return;
  }
  
  cartStore.addToCart({
    id: item.id,
    title: item.title,
    cover: item.cover,
    price: Number(item.price),
    count: quantity.value
  });
  
  message.success('已添加到购物车');
};

// 在详情数据加载完成后获取折扣信息
watch(() => detailData.value, (newVal) => {
  if (newVal && newVal.id) {
    fetchDiscountInfo(newVal.id)
  }
}, { immediate: true })

const fetchDiscountInfo = (id) => {
  axios.get(`${BASE_URL}/api/thing/discount?id=${id}`)
    .then(res => {
      if (res.data.code === 200) {
        discountInfo.value = res.data.data;
      }
    })
    .catch(err => {
      console.error('获取折扣信息失败', err);
    });
}

// 解析标签数据
const parseTags = (tagsData) => {
  if (!tagsData) return [];
  
  try {
    if (typeof tagsData === 'string') {
      // 尝试解析JSON字符串
      return JSON.parse(tagsData);
    }
    // 确保是标签数组，如果是ID数组则从标签数据中获取标签对象
    if (Array.isArray(tagsData)) {
      if (tagsData.length > 0 && typeof tagsData[0] === 'number') {
        // 这是ID数组，需要从标签数据中查找完整信息
        return tagsData.map(id => ({
          id: id,
          title: `标签${id}` // 这里可以从全局标签列表中查找，但简单起见直接构造
        }));
      }
      return tagsData;
    }
    return [];
  } catch (e) {
    console.error('解析标签失败', e);
    return [];
  }
};

// 获取分类路径
const fetchCategoryPath = (classificationId) => {
  // 如果没有分类ID，直接返回
  if (!classificationId) return;
  
  // 调用接口获取指定ID的分类信息及其父级分类
  axios.get(`${BASE_URL}/api/classification/path?id=${classificationId}`)
    .then(res => {
      if (res.data && res.data.code === 200) {
        categoryPath.value = res.data.data || [];
      }
    })
    .catch(err => {
      console.log('获取分类路径失败:', err);
    });
};

// 导航到分类页面
const navigateToCategory = (categoryId) => {
  router.push({
    name: 'search',
    query: { c: categoryId }
  });
};

// 导航到标签页面
const navigateToTag = (tagId) => {
  router.push({
    name: 'search',
    query: { tag: tagId }
  });
};
</script>

<style scoped lang="less">
/* 仿淘宝京东商品详情页样式 */
.product-detail {
  background-color: #f2f2f2;
  min-height: 100vh;
  color: #333;
  font-size: 14px;
  padding-top: 40px; /* 增加与头部的距离 */
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 10px 15px;
  margin-top: 20px; /* 保持已有的顶部边距 */
}

.breadcrumb {
  margin: 10px 0 20px; /* 增加底部间距 */
      font-size: 12px;
  color: #999;
  padding-bottom: 10px; /* 添加底部内边距 */
  border-bottom: 1px solid #eee; /* 添加底部边框进一步分隔 */
  
  a {
    color: #666;
    text-decoration: none;
    
    &:hover {
      color: #FF0036;
    }
  }
}

.product-container {
  display: flex;
  margin-bottom: 20px;
  background: white;
  border-radius: 2px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  
  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.product-gallery {
  flex: 0 0 420px;
  padding: 20px;
  border-right: 1px solid #f2f2f2;
  
  .main-image {
    height: 400px;
  display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 10px;
    border: 1px solid #f2f2f2;
    
    img {
      max-width: 100%;
      max-height: 400px;
      object-fit: contain;
    }
  }
}

.product-info {
    flex: 1;
  padding: 20px;
  position: relative;
}

.product-status {
  position: absolute;
  top: 20px;
  right: 20px;
  
  .status-badge {
    display: inline-block;
    padding: 2px 8px;
    background-color: #FF0036;
    color: white;
      border-radius: 2px;
      font-size: 12px;
  }
  
  .out-of-stock {
    background-color: #999;
  }
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  color: #000;
  margin-bottom: 10px;
  line-height: 1.5;
  padding-right: 60px;
}

.product-rating {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  
  .stars {
    display: flex;
    margin-right: 8px;
    
    .star {
      color: #DDDDDD;
      font-size: 18px;
      margin-right: 2px;
      
      &.active {
        color: #FF6700; /* 使用橙色，更容易看到 */
      }
    }
  }
  
  .rating-count {
    color: #666;
    font-size: 14px;
    margin-right: 15px;
  }
  
  .view-count {
    color: #666;
      font-size: 14px;
    display: flex;
    align-items: center;
    
    .icon-view {
      display: inline-block;
      width: 16px;
      height: 16px;
      background: url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDI0IDEwMjQiPjxwYXRoIGZpbGw9IiM2NjY2NjYiIGQ9Ik01MTIgMjU2YzE3NC40IDAgMzM0LjUgMTA1LjQgNDI2LjcgMjU2LTkyLjIgMTUwLjYtMjUyLjMgMjU2LTQyNi43IDI1Ni0xNzQuNCAwLTMzNC41LTEwNS40LTQyNi43LTI1NiA5Mi4yLTE1MC42IDI1Mi4zLTI1NiA0MjYuNy0yNTZ6bTAgNjRjLTEwNiAwLTE5NCA4Ni0xOTQgMTkyIDAgMTA2IDg4IDE5NCAxOTQgMTk0czE5NC04OCAxOTQtMTk0YzAtMTA2LTg4LTE5Mi0xOTQtMTkyek01MTIgMzg0Yy0xNC44IDAtMjYuOTkgMTEuOTgtMjYuOTkgMjYuODFWNTEyYzAgMTQuOTQgMTIuMDcgMjcuMDEgMjYuOTkgMjcuMDEgMTQuOTQgMCAyNy4wMS0xMi4wNyAyNy4wMS0yNy4wMVY0MTAuODFjMC0xNC44My0xMi4wNy0yNi44MS0yNy4wMS0yNi44MXoiPjwvcGF0aD48L3N2Zz4=') no-repeat center;
      margin-right: 4px;
    }
  }
}

.price-box {
  padding: 15px;
  margin: -5px -15px 15px -15px; /* 负边距使背景色延伸 */
  background-color: #FFF9F9; /* 更淡的红色背景 */
  
  .current-price {
    font-size: 26px;
    font-weight: bold;
    color: #FF0036;
    display: inline-block;
    
    .price-symbol {
      font-size: 16px;
      font-weight: normal;
      margin-right: 2px;
    }
  }
  
  .original-price {
    display: inline-block;
    margin-left: 10px;
    text-decoration: line-through;
    color: #999;
      font-size: 14px;
  }
  
  .discount-tag {
    display: inline-block;
    margin-left: 10px;
    vertical-align: middle;
    
    .discount-badge {
      display: inline-block;
      background-color: #FF0036;
      color: white;
      padding: 1px 3px;
      border-radius: 2px;
      font-size: 12px;
      font-weight: normal;
    }
    
    .saved-amount {
      display: inline-block;
      margin-left: 5px;
      font-size: 12px;
      color: #FF0036;
    }
  }
}

.product-meta {
  margin: 24px 0;
  
  .meta-item {
    margin-bottom: 12px;
    display: flex;
    
    .label {
      width: 70px;
      color: #666;
      flex-shrink: 0;
    }
    
    .value {
      color: #333;
      flex: 1;
      
      &.category-path {
        .category-item {
          display: inline-block;
          
          .cat-name {
            color: #1890ff;
    cursor: pointer;
            position: relative;
            padding: 2px 4px;
            border-radius: 4px;
            transition: all 0.2s;
            
            &:hover {
              background-color: #e6f7ff;
              color: #096dd9;
            }
          }
          
          .separator {
            color: #999;
            margin: 0 4px;
          }
        }
      }
    }
    
    &.tags-item {
      .product-tag {
        margin-right: 8px;
        cursor: pointer;
        transition: all 0.2s;
        
        &:hover {
          opacity: 0.85;
          transform: translateY(-2px);
        }
      }
    }
  }
}

.product-actions {
  margin-bottom: 20px;
  
  .quantity-selector {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    
    input {
      width: 40px;
      height: 30px;
      text-align: center;
      border: 1px solid #e5e5e5;
      border-left: none;
      border-right: none;
    }
    
    .qty-btn {
      width: 30px;
      height: 30px;
      background: white;
      border: 1px solid #e5e5e5;
  font-size: 14px;
      display: flex;
    align-items: center;
      justify-content: center;
    cursor: pointer;
      
      &:hover {
        color: #FF0036;
      }
    }
  }
  
  .action-buttons {
    display: flex;
    gap: 10px;
    
    .cart-btn, .buy-btn {
      height: 40px;
      padding: 0 30px;
      border-radius: 2px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
    cursor: pointer;
      transition: all 0.2s;
      font-weight: bold;
      
      i {
        margin-right: 5px;
      }
    }
    
    .cart-btn {
      background-color: #FFEDED;
      color: #FF0036;
      border: 1px solid #FF0036;
      
      &:hover {
        background-color: #FFD6D6;
  }
}

.buy-btn {
      background-color: #FF0036;
      color: white;
      border: 1px solid #FF0036;
      
      &:hover {
        background-color: #E00030;
      }
    }
  }
}

.product-social {
  display: flex;
  gap: 15px;
  margin-top: 20px;
  
  .social-item {
    display: flex;
    align-items: center;
    color: #999;
  cursor: pointer;

    i {
      margin-right: 5px;
  font-size: 14px;
    }
    
    &:hover {
      color: #FF0036;
    }
  }
}

.product-details {
  background: white;
  border-radius: 2px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  margin-top: 15px;
}

.tabs {
  display: flex;
  border-bottom: 1px solid #e5e5e5;
  background-color: #FAFAFA;
  
  .tab {
    padding: 12px 20px;
    font-size: 14px;
    color: #666;
    cursor: pointer;
    position: relative;
    
    &.active {
      font-weight: bold;
      color: #FF0036;
      background: white;
      border-top: 2px solid #FF0036;
      margin-top: -1px;
    }
    
    &:hover {
      color: #FF0036;
    }
  }
}

.tab-content {
  padding: 20px;
}

.product-description {
  line-height: 1.8;
  color: #666;
  
  p {
    margin-bottom: 15px;
  }
  
  img {
    max-width: 100%;
    margin: 10px 0;
  }
  
  :deep(img) {
    max-width: 100%;
    margin: 10px 0;
  }
  
  :deep(a) {
    color: #FF0036;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
    margin: 15px 0;
    font-weight: bold;
    color: #333;
  }
  
  :deep(ul), :deep(ol) {
    padding-left: 20px;
    margin: 10px 0;
  }
  
  :deep(table) {
    border-collapse: collapse;
    width: 100%;
    margin: 15px 0;
    
    td, th {
      border: 1px solid #e5e5e5;
      padding: 8px;
    }
    
    th {
      background-color: #f9f9f9;
    }
  }
}

.comment-section {
  margin-bottom: 20px;
}

.comment-tips {
  background-color: #f8f8f8;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  border-left: 4px solid #4684e2;

  .tips-header {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    font-weight: 600;
    color: #333;

    .icon-info {
      margin-right: 8px;
      color: #4684e2;
    }
  }

  .tips-content {
    color: #666;
    font-size: 14px;

    p {
      margin: 5px 0;
      line-height: 1.5;
    }
  }
}

.comment-list-header {
  display: flex;
  justify-content: space-between;
  padding-bottom: 10px;
  border-bottom: 1px solid #e5e5e5;
  margin-bottom: 15px;
  
  .comment-count {
    color: #333;
    font-weight: bold;
  }
  
  .comment-sort {
    span {
      color: #999;
      cursor: pointer;
          font-size: 12px;
      
      &.active {
        color: #FF0036;
        font-weight: bold;
      }
      
      &:hover {
        color: #FF0036;
      }
    }
    
    .divider {
      margin: 0 8px;
      color: #e5e5e5;
    }
  }
}

.comment-list {
  .comment-item {
    padding: 15px 0;
    border-bottom: 1px solid #f2f2f2;
    
    .comment-header {
      display: flex;
    align-items: center;
      margin-bottom: 10px;
      
      .avatar {
      width: 40px;
      height: 40px;
        border-radius: 50%;
        margin-right: 10px;
    }

      .comment-info {
      flex: 1;
        
        .username {
          font-weight: bold;
          color: #333;
        }

        .time {
          font-size: 12px;
          color: #999;
        }
      }
      
      .comment-score {
        display: flex;
    align-items: center;
        margin-right: 15px;
        
        .star-text {
          font-size: 12px;
          color: #999;
          margin-right: 5px;
        }
        
        .stars-small {
          display: flex;
          
          .star {
            color: #DDD;
            font-size: 14px;
            margin-right: 2px;
            
            &.active {
              color: #FF6700;
            }
          }
        }
      }
      
      .like-btn {
        display: flex;
        align-items: center;
        color: #999;
        cursor: pointer;
        
        i {
          margin-right: 3px;
        }
        
        &:hover {
          color: #FF0036;
        }
      }
    }
    
    .comment-body {
      margin-left: 50px;
      color: #333;
      line-height: 1.6;
    }
  }
  
  .no-more, .no-comments {
    text-align: center;
    color: #999;
    padding: 15px 0;
    font-size: 12px;
  }
}

.related-products {
  margin-bottom: 30px;
  margin-top: 15px;
  
  .section-title {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin-bottom: 15px;
    padding-left: 10px;
    border-left: 4px solid #FF0036;
  }
  
  .product-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 15px;
    
    @media (max-width: 992px) {
      grid-template-columns: repeat(4, 1fr);
    }
    
    @media (max-width: 768px) {
      grid-template-columns: repeat(3, 1fr);
    }
    
    @media (max-width: 576px) {
      grid-template-columns: repeat(2, 1fr);
      gap: 10px;
    }
  }
  
  .product-card {
    background: white;
    transition: all 0.2s;
        cursor: pointer;
    
    &:hover {
      box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);
    }
    
    .product-image {
      height: 200px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 10px;
      
      img {
        max-width: 100%;
        max-height: 180px;
        object-fit: contain;
      }
    }
    
    .product-card-body {
      padding: 8px 10px;
      border-top: 1px solid #f2f2f2;
      
      .product-card-title {
        margin-bottom: 5px;
        font-size: 12px;
        color: #333;
        height: 36px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
      
      .product-card-price {
        color: #FF0036;
        font-weight: bold;
        font-size: 14px;
        
        .price-symbol {
          font-size: 12px;
        }
      }
    }
  }
}

/* 模拟图标 */
.icon-cart, .icon-buy, .icon-wish, .icon-collect, .icon-share, .icon-like {
  display: inline-block;
  width: 16px;
  height: 16px;
  background-color: currentColor;
  mask-repeat: no-repeat;
  mask-position: center;
  mask-size: contain;
  -webkit-mask-repeat: no-repeat;
  -webkit-mask-position: center;
  -webkit-mask-size: contain;
}

.icon-cart {
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z"/></svg>');
  -webkit-mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z"/></svg>');
}

.icon-buy {
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/></svg>');
  -webkit-mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/></svg>');
}

.icon-wish {
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>');
  -webkit-mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>');
}

.icon-collect {
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M17 3H7c-1.1 0-1.99.9-1.99 2L5 21l7-3 7 3V5c0-1.1-.9-2-2-2z"/></svg>');
  -webkit-mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M17 3H7c-1.1 0-1.99.9-1.99 2L5 21l7-3 7 3V5c0-1.1-.9-2-2-2z"/></svg>');
}

.icon-share {
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92 1.61 0 2.92-1.31 2.92-2.92s-1.31-2.92-2.92-2.92z"/></svg>');
  -webkit-mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92 1.61 0 2.92-1.31 2.92-2.92s-1.31-2.92-2.92-2.92z"/></svg>');
}

.icon-like {
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-1.91l-.01-.01L23 10z"/></svg>');
  -webkit-mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-1.91l-.01-.01L23 10z"/></svg>');
}

.stars-small {
  display: inline-flex;
  margin-right: 8px;
  
  .star {
    color: #DDDDDD;
    font-size: 14px;
    margin-right: 2px;
    
    &.active {
      color: #FF6700;
    }
  }
}

.comment-score {
  display: flex;
  align-items: center;
  margin-right: 15px;
  font-size: 12px;
  color: #999;
  
  .star-text {
    margin-right: 4px;
  }
}

// 产品标签样式
.product-tag {
  margin-right: 6px;
  cursor: pointer;
  
  &:hover {
    opacity: 0.8;
  }
}
</style>
