<!--
/**
 * 评论管理组件
 * 
 * 该组件实现了电商平台的用户评论管理功能，包括：
 * 1. 评论列表展示
 * 2. 评论筛选（全部/最近/较早）
 * 3. 评论分页
 * 4. 商品跳转
 * 5. 评论图片查看
 * 6. 商家回复展示
 * 
 * 组件依赖：
 * - Vue 3.x
 * - Ant Design Vue
 * - Vuex/Pinia
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */
-->

<template>
  <!-- 评论管理主容器 -->
  <div class="comment-view">
    <!-- 页面标题和筛选区 -->
    <div class="page-header">
      <h2 class="page-title">我的评论</h2>
      <div class="header-actions">
        <!-- 评论筛选下拉框 -->
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
      <!-- 单个评论卡片 -->
      <div 
        v-for="(item, index) in commentData" 
        :key="index"
        class="comment-card"
      >
        <!-- 评论卡片头部信息 -->
        <div class="comment-header">
          <!-- 商品信息 -->
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
          
          <!-- 评分和时间信息 -->
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
        
        <!-- 评论内容 -->
        <div class="comment-content">
          <p class="content-text">{{ item.content }}</p>
          
          <!-- 评论图片区域 -->
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
        
        <!-- 评论底部操作区 -->
        <div class="comment-footer">
          <!-- 点赞信息 -->
          <div class="likes-info">
            <like-outlined class="icon" />
            <span>{{ item.likes || 0 }}</span>
          </div>
          
          <!-- 回复信息 -->
          <div class="reply-info" v-if="item.replyContent">
            <message-outlined class="icon" />
            <span>商家回复</span>
          </div>
          
          <!-- 操作按钮区 -->
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
    
    <!-- 分页控件 -->
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
    
    <!-- 图片预览组件 -->
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
/**
 * 导入Vue相关依赖
 */
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
import { useRouter } from 'vue-router';

/**
 * 初始化Vue相关实例
 */
const router = useRouter();
const userStore = useUserStore();

/**
 * 响应式数据定义
 * 包含页面状态、评论数据、分页信息等
 */
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

/**
 * 组件挂载后执行
 * 获取用户评论列表
 */
onMounted(() => {
  getCommentList();
});

/**
 * 处理点击商品标题
 * 
 * @description 点击标题跳转到商品详情页
 * @param {Object} record - 评论关联的商品数据对象
 */
const handleClickTitle = (record) => {
  if (!record || !record.thingId) return;
  
  const url = router.resolve({name: 'detail', query: {id: record.thingId}});
  window.open(url.href, '_blank');
};

/**
 * 获取评论列表
 * 
 * @description 根据筛选条件获取用户评论列表
 * @throws {Error} 当API请求失败时抛出错误
 */
const getCommentList = () => {
  loading.value = true;
  
  // 确保用户已登录
  if (!userStore.user_token) {
    message.warning('请先登录');
    loading.value = false;
    return;
  }
  
  // 调试信息
  console.log('用户信息:', {
    userId: userStore.user_id,
    token: userStore.user_token
  });
  
  // 构建请求参数
  const params = {
    userId: userStore.user_id,
    page: currentPage.value,
    pageSize: pageSize.value
  };
  
  // 仅当选择了非"all"选项时才添加filter参数
  if (filterType.value !== 'all') {
    params.filter = filterType.value;
  }
  
  console.log('请求参数:', params);
  
  // 发起API请求
  listUserCommentsApi(params)
    .then(res => {
      console.log('API响应成功:', res);
      if (res.data && Array.isArray(res.data)) {
        // 处理评论图片路径
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
      console.error('获取评论列表失败 - 详细错误:', err);
      
      // 尝试处理不同类型的错误
      if (err.response) {
        const status = err.response.status;
        console.error(`状态码: ${status}`);
        console.error('响应数据:', err.response.data);
        
        if (status === 401) {
          message.error('登录已过期，请重新登录');
          // 可能需要执行登出操作
          // userStore.logout();
        } else if (status === 500) {
          message.error('服务器内部错误，请稍后再试');
        } else {
          message.error(`服务器响应错误: ${status}`);
        }
      } else if (err.request) {
        message.error('无法连接到服务器，请检查网络');
      } else {
        message.error('请求评论列表时出错: ' + (err.message || '未知错误'));
      }
      
      loading.value = false;
    });
};

/**
 * 处理图片加载错误
 * 
 * @description 当商品图片加载失败时显示默认图片
 * @param {Event} e - 图片加载错误事件
 */
const handleImageError = (e) => {
  e.target.src = defaultImage.value;
};

/**
 * 解析评论图片
 * 
 * @description 将评论图片字符串解析为图片URL数组
 * @param {string|Array} imagesStr - 图片字符串或数组
 * @returns {Array} 图片URL数组
 */
const parseImages = (imagesStr) => {
  if (!imagesStr) return [];
  
  try {
    // 处理字符串格式的图片数据
    if (typeof imagesStr === 'string') {
      const images = JSON.parse(imagesStr);
      return Array.isArray(images) 
        ? images.map(img => BASE_URL + '/api/staticfiles/image/' + img)
        : [];
    }
    // 处理数组格式的图片数据
    return Array.isArray(imagesStr) 
      ? imagesStr.map(img => BASE_URL + '/api/staticfiles/image/' + img)
      : [];
  } catch (e) {
    return [];
  }
};

/**
 * 预览图片
 * 
 * @description 打开图片预览
 * @param {string} image - 要预览的图片URL
 * @param {Array} images - 所有图片URL数组
 */
const previewImage = (image, images) => {
  previewImages.value = images;
  previewVisible.value = true;
  previewIndex.value = images.findIndex(img => img === image);
};

/**
 * 获取评分显示文本
 * 
 * @description 根据评分获取评分显示文本
 * @param {Object} item - 评论对象
 * @returns {string} 评分显示文本
 */
const getCommentRating = (item) => {
  const score = item.score || 5;
  return `${score}分 ${getRatingText(score)}`;
};

/**
 * 获取评分描述文本
 * 
 * @description 根据评分值获取对应的描述文本
 * @param {number} score - 评分值
 * @returns {string} 评分描述文本
 */
const getRatingText = (score) => {
  if (score >= 5) return '非常满意';
  if (score >= 4) return '满意';
  if (score >= 3) return '一般';
  if (score >= 2) return '不满意';
  return '非常不满意';
};

/**
 * 编辑评论
 * 
 * @description 编辑评论功能（当前未实现）
 * @param {Object} item - 要编辑的评论对象
 */
const editComment = (item) => {
  message.info('编辑评论功能即将上线');
};

/**
 * 删除评论
 * 
 * @description 删除指定的评论
 * @param {Object} item - 要删除的评论对象
 * @throws {Error} 当API请求失败时抛出错误
 */
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

/**
 * 处理筛选变化
 * 
 * @description 当筛选条件变化时重新加载数据
 */
const handleFilterChange = () => {
  currentPage.value = 1;
  getCommentList();
};

/**
 * 处理页码变化
 * 
 * @description 切换页码时重新加载数据
 * @param {number} page - 目标页码
 */
const handlePageChange = (page) => {
  currentPage.value = page;
  getCommentList();
};

/**
 * 处理每页数量变化
 * 
 * @description 修改每页显示数量时重新加载数据
 * @param {number} current - 当前页码
 * @param {number} size - 每页数量
 */
const handleSizeChange = (current, size) => {
  pageSize.value = size;
  currentPage.value = 1;
  getCommentList();
};

/**
 * 跳转到商城首页
 * 
 * @description 跳转到商城首页（购物页）
 */
const goToShop = () => {
  router.push({ name: 'home' });
};
</script>

<style scoped lang="less">
/**
 * 评论管理视图样式
 * 定义整体布局和内边距
 */
.comment-view {
  padding: 0 20px;
  
  /**
   * 页面标题区域样式
   * 设置标题和操作区的布局
   */
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    /**
     * 页面标题样式
     */
    .page-title {
      font-size: 22px;
      font-weight: 600;
      color: #222;
      margin: 0;
    }
    
    /**
     * 头部操作区样式
     */
    .header-actions {
      display: flex;
      align-items: center;
      
      /**
       * 筛选下拉框样式
       */
      .filter-select {
        width: 120px;
      }
    }
  }
  
  /**
   * 加载中和空状态容器
   * 居中显示加载状态和空数据提示
   */
  .loading-container, .empty-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 0;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    
    /**
     * 空状态下的购物按钮
     */
    .go-shop-btn {
      margin-top: 20px;
    }
  }
  
  /**
   * 评论列表样式
   */
  .comment-list {
    /**
     * 评论卡片样式
     * 设置卡片基本样式和过渡效果
     */
    .comment-card {
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      padding: 20px;
      margin-bottom: 16px;
      transition: all 0.3s;
      
      /**
       * 卡片悬浮效果
       */
      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      }
      
      /**
       * 评论卡片头部样式
       * 设置商品信息和评分的布局
       */
      .comment-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        /**
         * 商品信息样式
         */
        .product-info {
          display: flex;
          align-items: center;
          cursor: pointer;
          
          /**
           * 商品图片样式
           */
          .product-img {
            width: 40px;
            height: 40px;
            border-radius: 4px;
            object-fit: cover;
            margin-right: 12px;
            background: #f5f5f5;
          }
          
          /**
           * 商品标题样式
           */
          .product-title {
            font-size: 15px;
            font-weight: 500;
            color: #333;
            
            /**
             * 商品标题悬浮效果
             */
            &:hover {
              color: #4684e2;
            }
          }
        }
        
        /**
         * 评论元数据样式
         * 包含评分和时间
         */
        .comment-meta {
          /**
           * 评分显示样式
           */
          .rating-display {
            display: flex;
            align-items: center;
            margin-bottom: 5px;
            
            /**
             * 星级评分样式
             */
            .rating-stars {
              display: flex;
              margin-right: 10px;
              
              /**
               * 单个星星样式
               */
              .star {
                color: #DDDDDD;
                font-size: 16px;
                margin-right: 2px;
                
                /**
                 * 激活的星星样式
                 */
                &.active {
                  color: #FF6700;
                }
              }
            }
          }
          
          /**
           * 评论时间样式
           */
          .comment-time {
            color: #999;
          }
        }
      }
      
      /**
       * 评论内容样式
       */
      .comment-content {
        /**
         * 评论文本样式
         */
        .content-text {
          margin: 0;
          font-size: 14px;
          line-height: 1.6;
          color: #333;
          word-break: break-word;
        }
        
        /**
         * 评论图片区域样式
         */
        .comment-images {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          margin-top: 12px;
          
          /**
           * 单个图片项样式
           */
          .image-item {
            width: 80px;
            height: 80px;
            border-radius: 4px;
            overflow: hidden;
            cursor: pointer;
            
            /**
             * 图片样式及悬浮效果
             */
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
      
      /**
       * 评论底部样式
       * 包含点赞数和操作按钮
       */
      .comment-footer {
        display: flex;
        margin-top: 16px;
        align-items: center;
        
        /**
         * 点赞和回复信息样式
         */
        .likes-info, .reply-info {
          display: flex;
          align-items: center;
          margin-right: 16px;
          color: #666;
          
          /**
           * 图标样式
           */
          .icon {
            font-size: 16px;
            margin-right: 4px;
          }
        }
        
        /**
         * 操作按钮区域样式
         */
        .action-btns {
          margin-left: auto;
          display: flex;
          
          /**
           * 编辑和删除按钮样式
           */
          .edit-btn, .delete-btn {
            display: flex;
            align-items: center;
            margin-left: 12px;
            
            /**
             * 按钮图标样式
             */
            .anticon {
              margin-right: 4px;
            }
          }
          
          /**
           * 编辑按钮样式
           */
          .edit-btn {
            color: #4684e2;
          }
          
          /**
           * 删除按钮样式
           */
          .delete-btn {
            color: #ff4d4f;
          }
        }
      }
      
      /**
       * 商家回复区域样式
       */
      .merchant-reply {
        margin-top: 16px;
        background: #f9f9f9;
        border-radius: 4px;
        padding: 12px 16px;
        
        /**
         * 回复头部样式
         */
        .reply-header {
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          
          /**
           * 商店图标样式
           */
          .shop-icon {
            color: #ff6b00;
            margin-right: 4px;
          }
          
          /**
           * 商店名称样式
           */
          .shop-name {
            font-weight: 500;
            color: #666;
          }
          
          /**
           * 回复时间样式
           */
          .reply-time {
            color: #999;
            font-size: 12px;
            margin-left: 8px;
          }
        }
        
        /**
         * 回复内容样式
         */
        .reply-content {
          margin: 0;
          font-size: 13px;
          color: #666;
          line-height: 1.6;
        }
      }
    }
  }
  
  /**
   * 分页容器样式
   */
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 30px;
    margin-bottom: 20px;
  }
}
</style>
