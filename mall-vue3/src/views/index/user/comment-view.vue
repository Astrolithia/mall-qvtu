<template>
  <div class="comment-view">
    <div class="page-header">
      <h2 class="page-title">我的评论</h2>
      <div class="header-actions">
        <a-select
          v-model:value="filterType"
          placeholder="所有评论"
          class="filter-select"
          @change="handleFilterChange"
        >
          <a-select-option value="all">所有评论</a-select-option>
          <a-select-option value="recent">最近一周</a-select-option>
          <a-select-option value="older">一个月前</a-select-option>
        </a-select>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <a-spin tip="加载中..." />
    </div>
    
    <!-- 空状态 -->
    <div v-else-if="commentData.length === 0" class="empty-container">
      <a-empty description="暂无评论记录" />
      <a-button type="primary" @click="goToShop" class="go-shop-btn">
        去购物
      </a-button>
    </div>
    
    <!-- 评论列表 -->
    <div v-else class="comment-list">
      <div 
        v-for="(item, index) in commentData" 
        :key="index"
        class="comment-card"
      >
        <div class="comment-header">
          <div class="product-info" @click="handleClickTitle(item)">
            <img 
              :src="item.cover" 
              :alt="item.title" 
              class="product-img"
              @error="handleImageError"
            />
            <div class="product-title">
              《{{ item.title }}》
            </div>
          </div>
          
          <div class="comment-meta">
            <div class="rating-display">
              <span class="rating-stars">
                <span v-for="i in 5" :key="i" class="star" :class="{ active: i <= (item.score || 5) }">★</span>
              </span>
              <a-tag color="blue">{{ getCommentRating(item) }}</a-tag>
            </div>
            <span class="comment-time">{{ getFormatTime(item.commentTime, true) }}</span>
          </div>
        </div>
        
        <div class="comment-content">
          <p class="content-text">{{ item.content }}</p>
          
          <div v-if="item.images && item.images.length" class="comment-images">
            <div
              v-for="(img, imgIndex) in parseImages(item.images)"
              :key="imgIndex"
              class="image-item"
              @click="previewImage(img, parseImages(item.images))"
            >
              <img :src="img" :alt="`评论图片 ${imgIndex + 1}`" />
            </div>
          </div>
        </div>
        
        <div class="comment-footer">
          <div class="likes-info">
            <like-outlined class="icon" />
            <span>{{ item.likes || 0 }}</span>
          </div>
          
          <div class="reply-info" v-if="item.replyContent">
            <message-outlined class="icon" />
            <span>商家回复</span>
          </div>
          
          <div class="action-btns">
            <!-- 注释掉编辑按钮 
            <a-button type="text" class="edit-btn" @click="editComment(item)">
              <edit-outlined />
              编辑
            </a-button>
            -->
            <!-- 注释掉删除按钮 
            <a-popconfirm
              title="确定要删除这条评论吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="deleteComment(item)"
            >
              <a-button type="text" class="delete-btn">
                <delete-outlined />
                删除
              </a-button>
            </a-popconfirm>
            -->
          </div>
        </div>
        
        <!-- 商家回复区域 -->
        <div v-if="item.replyContent" class="merchant-reply">
          <div class="reply-header">
            <shop-outlined class="shop-icon" />
            <span class="shop-name">商家回复：</span>
            <span class="reply-time">{{ getFormatTime(item.replyTime, true) }}</span>
          </div>
          <p class="reply-content">{{ item.replyContent }}</p>
        </div>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container" v-if="commentData.length > 0">
      <a-pagination 
        v-model:current="currentPage" 
        :total="totalItems" 
        :pageSize="pageSize"
        @change="handlePageChange"
        showQuickJumper
        :pageSizeOptions="['5', '10', '20']"
        @showSizeChange="handleSizeChange"
      />
    </div>
    
    <!-- 图片预览 -->
    <a-image-preview-group>
      <a-image
        v-for="(img, idx) in previewImages"
        :key="idx"
        :src="img"
        style="display: none;"
      />
    </a-image-preview-group>
  </div>
</template>

<script setup>
import { message } from 'ant-design-vue';
import { 
  LikeOutlined, 
  MessageOutlined,
  EditOutlined,
  DeleteOutlined,
  ShopOutlined
} from '@ant-design/icons-vue';
import { useUserStore } from "/@/store";
import { listUserCommentsApi, deleteApi } from '/@/api/comment';
import { BASE_URL } from "/@/store/constants";
import { getFormatTime } from '/@/utils';
import { ref, reactive, onMounted } from 'vue';

const router = useRouter();
const userStore = useUserStore();

// 页面状态
const loading = ref(false);
const commentData = ref([]);
const filterType = ref('all');
const currentPage = ref(1);
const pageSize = ref(10);
const totalItems = ref(0);
const defaultImage = ref('/default-product.jpg');
const previewImages = ref([]);
const previewVisible = ref(false);
const previewIndex = ref(0);

onMounted(() => {
  getCommentList();
});

// 处理点击商品标题
const handleClickTitle = (record) => {
  if (!record || !record.thingId) return;
  
  const url = router.resolve({name: 'detail', query: {id: record.thingId}});
  window.open(url.href, '_blank');
};

// 获取评论列表
const getCommentList = () => {
  loading.value = true;
  const userId = userStore.user_id;
  
  listUserCommentsApi({
    userId: userId,
    page: currentPage.value,
    pageSize: pageSize.value,
    filter: filterType.value
  })
    .then(res => {
      if (res.data && Array.isArray(res.data)) {
        res.data.forEach(item => {
          if (item.cover) {
            item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
          }
        });
        commentData.value = res.data;
        totalItems.value = res.total || res.data.length;
      } else {
        commentData.value = [];
        totalItems.value = 0;
      }
      loading.value = false;
    })
    .catch(err => {
      console.error('获取评论列表失败', err);
      message.error(err.msg || '获取评论列表失败');
      loading.value = false;
    });
};

// 处理图片加载失败
const handleImageError = (e) => {
  e.target.src = defaultImage.value;
};

// 解析评论图片
const parseImages = (imagesStr) => {
  if (!imagesStr) return [];
  
  try {
    if (typeof imagesStr === 'string') {
      const images = JSON.parse(imagesStr);
      return Array.isArray(images) 
        ? images.map(img => BASE_URL + '/api/staticfiles/image/' + img)
        : [];
    }
    return Array.isArray(imagesStr) 
      ? imagesStr.map(img => BASE_URL + '/api/staticfiles/image/' + img)
      : [];
  } catch (e) {
    return [];
  }
};

// 预览图片
const previewImage = (image, images) => {
  previewImages.value = images;
  previewVisible.value = true;
  previewIndex.value = images.findIndex(img => img === image);
};

// 获取评分显示
const getCommentRating = (item) => {
  const score = item.score || 5;
  return `${score}分 ${getRatingText(score)}`;
};

// 获取评分文本
const getRatingText = (score) => {
  if (score >= 5) return '非常满意';
  if (score >= 4) return '满意';
  if (score >= 3) return '一般';
  if (score >= 2) return '不满意';
  return '非常不满意';
};

// 编辑评论
const editComment = (item) => {
  message.info('编辑评论功能即将上线');
};

// 删除评论
const deleteComment = (item) => {
  if (!item || !item.id) {
    message.error('评论信息不完整');
    return;
  }
  
  loading.value = true;
  deleteApi({ id: item.id })
    .then(() => {
      message.success('评论删除成功');
      getCommentList();
    })
    .catch(err => {
      console.error('删除评论失败', err);
      message.error(err.msg || '删除评论失败');
      loading.value = false;
    });
};

// 筛选变化
const handleFilterChange = () => {
  currentPage.value = 1;
  getCommentList();
};

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page;
  getCommentList();
};

// 每页数量变化
const handleSizeChange = (current, size) => {
  pageSize.value = size;
  currentPage.value = 1;
  getCommentList();
};

// 去购物
const goToShop = () => {
  router.push({ name: 'home' });
};
</script>

<style scoped lang="less">
.comment-view {
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
    
    .header-actions {
      display: flex;
      align-items: center;
      
      .filter-select {
        width: 120px;
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
    
    .go-shop-btn {
      margin-top: 20px;
    }
  }
  
  .comment-list {
    .comment-card {
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      padding: 20px;
      margin-bottom: 16px;
      transition: all 0.3s;
      
      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      }
      
      .comment-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        .product-info {
          display: flex;
          align-items: center;
          cursor: pointer;
          
          .product-img {
            width: 40px;
            height: 40px;
            border-radius: 4px;
            object-fit: cover;
            margin-right: 12px;
            background: #f5f5f5;
          }
          
          .product-title {
            font-size: 15px;
            font-weight: 500;
            color: #333;
            
            &:hover {
              color: #4684e2;
            }
          }
        }
        
        .comment-meta {
          .rating-display {
            display: flex;
            align-items: center;
            margin-bottom: 5px;
            
            .rating-stars {
              display: flex;
              margin-right: 10px;
              
              .star {
                color: #DDDDDD;
                font-size: 16px;
                margin-right: 2px;
                
                &.active {
                  color: #FF6700;
                }
              }
            }
          }
          .comment-time {
            color: #999;
          }
        }
      }
      
      .comment-content {
        .content-text {
          margin: 0;
          font-size: 14px;
          line-height: 1.6;
          color: #333;
          word-break: break-word;
        }
        
        .comment-images {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          margin-top: 12px;
          
          .image-item {
            width: 80px;
            height: 80px;
            border-radius: 4px;
            overflow: hidden;
            cursor: pointer;
            
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
              transition: transform 0.3s;
              
              &:hover {
                transform: scale(1.05);
              }
            }
          }
        }
      }
      
      .comment-footer {
        display: flex;
        margin-top: 16px;
        align-items: center;
        
        .likes-info, .reply-info {
          display: flex;
          align-items: center;
          margin-right: 16px;
          color: #666;
          
          .icon {
            font-size: 16px;
            margin-right: 4px;
          }
        }
        
        .action-btns {
          margin-left: auto;
          display: flex;
          
          .edit-btn, .delete-btn {
            display: flex;
            align-items: center;
            margin-left: 12px;
            
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
        }
      }
      
      .merchant-reply {
        margin-top: 16px;
        background: #f9f9f9;
        border-radius: 4px;
        padding: 12px 16px;
        
        .reply-header {
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          
          .shop-icon {
            color: #ff6b00;
            margin-right: 4px;
          }
          
          .shop-name {
            font-weight: 500;
            color: #666;
          }
          
          .reply-time {
            color: #999;
            font-size: 12px;
            margin-left: 8px;
          }
        }
        
        .reply-content {
          margin: 0;
          font-size: 13px;
          color: #666;
          line-height: 1.6;
        }
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 30px;
    margin-bottom: 20px;
  }
}
</style>
