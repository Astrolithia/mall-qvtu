<template>
  <div class="order-view">
    <div class="page-header">
      <h2 class="page-title">我的订单</h2>
      <div class="header-actions">
        <a-dropdown>
          <a class="time-filter">{{ timeFilter }} <down-outlined /></a>
          <template #overlay>
            <a-menu @click="handleTimeFilterChange">
              <a-menu-item key="all">全部订单</a-menu-item>
              <a-menu-item key="week">最近一周</a-menu-item>
              <a-menu-item key="month">一个月内</a-menu-item>
              <a-menu-item key="three_month">三个月内</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </div>
    
    <a-tabs
      :activeKey="tabActiveKey"
      @change="onTabChange"
      class="order-tabs">
      <a-tab-pane key="0" tab="全部" />
      <a-tab-pane key="1" tab="待付款" />
      <a-tab-pane key="2" tab="待发货" />
      <a-tab-pane key="3" tab="待收货" />
      <a-tab-pane key="4" tab="待评价" />
      <a-tab-pane key="5" tab="已完成" />
      <a-tab-pane key="7" tab="已取消" />
    </a-tabs>
    
    <div v-if="loading" class="loading-state">
      <a-spin />
    </div>
    
    <div v-else-if="orderList.length === 0" class="empty-state">
      <a-empty description="暂无订单" />
      <a-button type="primary" @click="goToShop" class="go-shop-btn">去购物</a-button>
    </div>
    
    <div v-else class="order-list">
      <div 
        class="order-card" 
        v-for="(item, index) in orderList" 
        :key="index"
        :class="{'order-pending': item.status === '1'}"
      >
        <div class="order-header">
          <div class="order-info">
            <span class="order-time">{{ getFormatTime(item.orderTime, true) }}</span>
            <span class="order-number">订单号：{{ item.orderNumber }}</span>
          </div>
          <div class="order-status">
            <a-tag 
              :color="getStatusColor(item.status)"
              class="status-tag"
            >
              {{ getStatusText(item.status) }}
            </a-tag>
          </div>
        </div>
        
        <div class="order-content">
          <div class="product-info">
            <div class="product-image">
              <img 
                :src="item.cover" 
                :alt="item.title"
                @error="handleImageError"
              >
            </div>
            <div class="product-details">
              <h3 class="product-title" @click="handleDetail(item.thingId)">{{ item.title }}</h3>
              <div class="product-meta">
                <span class="product-price">¥{{ item.price }}</span>
                <span class="product-quantity">x{{ item.count }}</span>
              </div>
            </div>
          </div>
          
          <div class="delivery-info">
            <div class="delivery-section">
              <h4 class="section-title">收货信息</h4>
              <p class="contact-info">{{ item.receiverName }} {{ formatPhone(item.receiverPhone) }}</p>
              <p class="address-info">{{ item.receiverAddress }}</p>
            </div>
            
            <div class="delivery-section" v-if="['3', '4'].includes(item.status)">
              <h4 class="section-title">物流信息</h4>
              <p class="shipping-info">
                <span class="shipping-company">{{ item.shippingCompany || '未知快递' }}</span>
              </p>
              <p class="tracking-info">{{ item.trackingNumber || '暂无物流单号' }}</p>
            </div>
            
            <div class="delivery-section" v-else>
              <h4 class="section-title">快递单号</h4>
              <p class="tracking-info">{{ item.trackingNumber || '暂无物流信息' }}</p>
            </div>
            
            <div class="delivery-section">
              <h4 class="section-title">备注信息</h4>
              <p class="remark-info">{{ item.remark || '无备注' }}</p>
            </div>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-summary">
            <span class="summary-text">共{{ item.count }}件商品</span>
            <span class="divider">|</span>
            <a class="details-link" @click="handleDetail(item.thingId)">商品详情</a>
          </div>
          
          <div class="order-price">
            <div class="price-item">
              <span class="price-label">总计：</span>
              <span class="price-value">¥{{ calculateTotal(item) }}</span>
            </div>
            <div class="price-item">
              <span class="price-label">优惠：</span>
              <span class="price-value discount">¥{{ getDiscount(item) }}</span>
            </div>
            <div class="price-item total">
              <span class="price-label">实付：</span>
              <span class="price-value">¥{{ calculateFinal(item) }}</span>
            </div>
          </div>
          
          <div class="order-actions">
            <a-button 
              v-if="item.status === '1'" 
              type="primary" 
              @click="handlePay(item)"
              class="action-btn pay-btn"
            >
              立即支付
            </a-button>
            <a-popconfirm
              v-if="item.status === '1'"
              title="确定取消此订单？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleCancel(item)"
            >
              <a-button class="action-btn cancel-btn">取消订单</a-button>
            </a-popconfirm>
            <a-button 
              v-if="item.status === '2'" 
              type="primary"
              class="action-btn"
              disabled
            >
              等待发货
            </a-button>
            <a-popconfirm
              v-if="item.status === '2'"
              title="确定取消此订单？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="cancelOrder(item)"
            >
              <a-button class="action-btn cancel-btn">取消订单</a-button>
            </a-popconfirm>
            <a-button 
              v-if="item.status === '3'" 
              type="primary"
              class="action-btn"
              @click="checkShipping(item)"
            >
              查看物流
            </a-button>
            <a-button 
              v-if="item.status === '3'" 
              class="action-btn"
              @click="showConfirmReceiveModal(item)"
            >
              确认收货
            </a-button>
            <a-button 
              v-if="item.status === '4'" 
              type="primary"
              class="action-btn rate-btn"
              @click="showCommentModal(item)"
            >
              评价商品
            </a-button>
            <a-tag v-if="item.status === '5'" color="green" class="rated-tag">
              已完成
            </a-tag>
            <a-tag v-if="item.status === '7'" color="red" class="rated-tag">
              已取消
            </a-tag>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container" v-if="orderList.length > 0">
      <a-pagination 
        v-model="currentPage" 
        :total="totalItems" 
        :pageSize="pageSize"
        @change="handlePageChange"
        showQuickJumper
        showSizeChanger
        :pageSizeOptions="['5', '10', '20']"
        @showSizeChange="handleSizeChange"
      />
    </div>
    
    <!-- 评价模态框 -->
    <a-modal
      v-model:visible="commentModalVisible"
      title="评价商品"
      :footer="null"
      :maskClosable="false"
      :keyboard="false"
      :destroyOnClose="true"
      :getContainer="false"
      width="500px">
      <div class="comment-modal-content">
        <div class="comment-product">
          <img 
            :src="commentForm.cover || defaultImage" 
            @error="handleImageError"
            class="product-img" 
            alt="商品图片"
          />
          <div class="product-info">
            <div class="product-title">{{ commentForm.title }}</div>
            <div class="product-price">¥{{ commentForm.price }}</div>
          </div>
        </div>
        
        <div class="product-review-area">
          <div class="comment-rate-container">
            <span class="rate-label">商品评分：</span>
            <a-rate v-model:value="commentForm.score" />
          </div>
          <div class="comment-input-container">
            <a-textarea
              v-model:value="commentForm.content"
              placeholder="请输入您对商品的评价"
              :rows="4"
              :maxlength="500"
              show-count />
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <a-button @click="cancelComment">取消</a-button>
        <a-button 
          type="primary" 
          @click="submitComment" 
          :disabled="!commentForm.score">
          提交评价
        </a-button>
      </div>
    </a-modal>
    
    <!-- 物流信息模态框 -->
    <a-modal
      v-model:visible="shippingModalVisible"
      title="物流信息查询"
      :footer="null"
      :maskClosable="false"
      :keyboard="false"
      :destroyOnClose="true"
      :getContainer="false"
      width="520px">
      <div class="shipping-modal-content">
        <div class="shipping-header">
          <div class="shipping-product">
            <img 
              :src="currentOrder.cover || defaultImage" 
              @error="handleImageError"
              class="product-img" 
              alt="商品图片"
            />
            <div class="product-info">
              <div class="product-title">{{ currentOrder.title }}</div>
              <div class="order-number">订单号：{{ currentOrder.orderNumber }}</div>
            </div>
          </div>
        </div>
        
        <a-divider>物流详情</a-divider>
        
        <div class="shipping-info">
          <div class="info-item">
            <span class="label">收货人：</span>
            <span class="value">{{ currentOrder.receiverName }} {{ formatPhone(currentOrder.receiverPhone) }}</span>
          </div>
          <div class="info-item">
            <span class="label">收货地址：</span>
            <span class="value">{{ currentOrder.receiverAddress }}</span>
          </div>
          <div class="info-item">
            <span class="label">快递公司：</span>
            <span class="value highlight">{{ currentOrder.shippingCompany || '未知' }}</span>
          </div>
          <div class="info-item">
            <span class="label">快递单号：</span>
            <span class="value highlight">{{ currentOrder.trackingNumber }}</span>
            <a-button 
              size="small" 
              type="link" 
              @click="copyTrackingNumber(currentOrder.trackingNumber)"
            >
              复制
            </a-button>
          </div>
          <div class="info-item shipping-time">
            <span class="label">发货时间：</span>
            <span class="value">{{ currentOrder.shippingTime || '未知' }}</span>
          </div>
        </div>
        
        <a-divider>物流跟踪</a-divider>
        
        <div v-if="shippingLoading" class="tracking-loading">
          <a-spin />
          <p>正在查询物流信息...</p>
        </div>
        <div v-else-if="trackingInfo" class="tracking-result">
          <div class="tracking-list">
            <div class="tracking-item" v-for="(item, index) in mockTrackingInfo" :key="index" :class="{ 'active': index === 0 }">
              <div class="time">{{ item.time }}</div>
              <div class="status">{{ item.status }}</div>
            </div>
          </div>
        </div>
        <div v-else class="tracking-empty">
          <a-empty description="暂无物流跟踪信息" />
          <p class="tracking-tip">物流信息通常在发货后24小时内更新</p>
        </div>
        
        <div class="modal-footer">
          <a-button @click="closeShippingModal">关闭</a-button>
          <a-button 
            v-if="['3'].includes(currentOrder.status)"
            type="primary" 
            @click="confirmReceivedFromModal" 
          >
            确认收货
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { message, Empty, Spin, Modal } from "ant-design-vue";
import { DownOutlined } from '@ant-design/icons-vue';
import { getFormatTime } from '/@/utils/';
import { userOrderListApi, cancelUserOrderApi, confirmReceiptApi, checkShippingApi, updateApi } from '/@/api/order';
import { createApi as createCommentApi } from '/@/api/comment';
import { BASE_URL } from "/@/store/constants";
import { useUserStore } from "/@/store";
import { ref, computed } from 'vue';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const loading = ref(false);
const orderList = ref([]);
const orderStatus = ref('');
const tabActiveKey = ref('0');
const currentPage = ref(1);
const pageSize = ref(5);
const totalItems = ref(0);
const defaultImage = ref('/default-product.jpg'); // 默认图片路径

// 获取状态文本
const getStatusText = (status) => {
  switch(status) {
    case '1': return '待付款';
    case '2': return '待发货';
    case '3': return '待收货';
    case '4': return '待评价';
    case '5': return '已完成';
    case '7': return '已取消';
    default: return '未知状态';
  }
};

// 获取状态颜色
const getStatusColor = (status) => {
  const statusColors = {
    '1': 'orange',   // 待付款
    '2': 'blue',     // 待发货
    '3': 'purple',   // 待收货
    '4': 'cyan',     // 待评价
    '5': 'green',    // 已完成
    '7': 'red'       // 已取消
  };
  return statusColors[status] || 'default';
};

// 格式化手机号码
const formatPhone = (phone) => {
  if (!phone) return '';
  if (phone.length !== 11) return phone;
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

// 计算订单总价
const calculateTotal = (item) => {
  return (item.price * item.count).toFixed(2);
};

// 获取折扣金额
const getDiscount = (item) => {
  return '0.00';
};

// 计算最终支付金额
const calculateFinal = (item) => {
  return (item.price * item.count).toFixed(2);
};

onMounted(() => {
  getOrderList();
});

// 标签页切换
const onTabChange = (key) => {
  tabActiveKey.value = key;
  currentPage.value = 1;
  
  switch(key) {
    case '0':
      orderStatus.value = '';
      break;
    case '1':
      orderStatus.value = '1'; // 待付款
      break;
    case '2':
      orderStatus.value = '2'; // 待发货
      break;
    case '3':
      orderStatus.value = '3'; // 待收货
      break;
    case '4':
      orderStatus.value = '4'; // 待评价
      break;
    case '5':
      orderStatus.value = '5'; // 已完成
      break;
    case '7':
      orderStatus.value = '7'; // 已取消
      break;
  }
  
  getOrderList();
};

// 获取订单列表
const getOrderList = () => {
  loading.value = true;
  const userId = userStore.user_id;
  
  userOrderListApi({
    userId: userId, 
    status: orderStatus.value,
    page: currentPage.value,
    pageSize: pageSize.value
  })
    .then(res => {
      if (res.data && Array.isArray(res.data)) {
        // 处理数据
        const processedData = res.data.map((item) => {
          // 处理封面图片
          if (item.cover) {
            item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
          }
          return item;
        });
        
        // 确保筛选逻辑
        if (orderStatus.value) {
          // 如果有选择状态，只显示对应状态的订单
          orderList.value = processedData.filter(item => item.status === orderStatus.value);
        } else {
          // 全部订单
          orderList.value = processedData;
        }
        
        totalItems.value = res.total || res.data.length;
      } else {
        orderList.value = [];
        totalItems.value = 0;
      }
      loading.value = false;
    })
    .catch(err => {
      console.error(err);
      message.error('获取订单列表失败');
      loading.value = false;
      orderList.value = [];
    });
};

// 处理商品详情点击
const handleDetail = (thingId) => {
  if (!thingId) return;
  // 跳转新页面
  const url = router.resolve({name: 'detail', query: {id: thingId}});
  window.open(url.href, '_blank');
};

// 处理取消订单
const handleCancel = (item) => {
  if (!item || !item.id) {
    message.error('订单信息不完整');
    return;
  }
  
  // 检查订单状态是否为待付款或待发货
  if (item.status !== '1' && item.status !== '2') {
    message.error('只有待付款或待发货的订单可以取消');
    return;
  }
  
  loading.value = true;
  cancelUserOrderApi({
    id: item.id
  })
    .then(res => {
      message.success('订单取消成功');
      getOrderList();
    })
    .catch(err => {
      message.error(err.msg || '取消订单失败');
      loading.value = false;
    });
};

// 处理图片加载失败
const handleImageError = (e) => {
  // 防止死循环：只在图片src不为defaultImage时才设置默认图片
  if (e.target.src.indexOf('default-product.jpg') === -1) {
    e.target.src = 'https://via.placeholder.com/80x80?text=No+Image';
  }
};

// 处理支付
const handlePay = (item) => {
  message.info('支付功能即将上线');
};

// 前往商城
const goToShop = () => {
  router.push({name: 'home'});
};

// 处理页码变化
const handlePageChange = (page) => {
  currentPage.value = page;
  getOrderList();
};

// 处理每页条数变化
const handleSizeChange = (current, size) => {
  pageSize.value = size;
  currentPage.value = 1;
  getOrderList();
};

// 添加评价弹窗组件
const commentModalVisible = ref(false);
const commentForm = ref({
  cover: '',
  title: '',
  price: '',
  thingId: '',
  orderId: '',
  score: 0,
  content: ''
});

// 显示评价弹窗
const showCommentModal = (item) => {
  // 打印完整订单对象以检查结构
  console.log('正在打开评价模态框，订单数据:', item);
  console.log('订单ID:', item.id);
  
  // 重置评价表单
  commentForm.value = {
    cover: item.cover,
    title: item.title,
    price: item.price,
    thingId: item.thingId,
    orderId: item.id,
    score: 5,  // 默认5分
    content: ''
  };
  
  // 延迟打开模态框，避免DOM未就绪
  setTimeout(() => {
    commentModalVisible.value = true;
  }, 100);
};

// 取消评价
const cancelComment = () => {
  commentModalVisible.value = false;
  commentForm.value = {
    cover: '',
    title: '',
    price: '',
    thingId: '',
    orderId: '',
    score: 0,
    content: ''
  };
};

// 提交评价
const submitComment = () => {
  if (!commentForm.value.content.trim()) {
    message.warn('请输入评价内容');
    return;
  }
  
  const userId = userStore.user_id;
  const commentData = {
    content: commentForm.value.content,
    thingId: commentForm.value.thingId,
    userId: userId,
    score: commentForm.value.score,
    orderId: commentForm.value.orderId
  };
  
  console.log('提交评价数据:', commentData);
  
  // 先关闭模态框，避免DOM操作冲突
  commentModalVisible.value = false;
  
  createCommentApi(commentData).then(res => {
    console.log('评价提交结果:', res);
    message.success('评价提交成功');
    
    // 确保我们有有效的订单ID
    if (!commentForm.value.orderId) {
      console.error('订单ID不存在，无法更新订单状态');
      getOrderList(); // 刷新列表
      return;
    }
    
    // 直接更新当前订单的状态
    const formData = new FormData();
    formData.append('id', commentForm.value.orderId);
    formData.append('status', '5'); // 更新为已完成状态
    
    updateApi({}, formData)
      .then((updateRes) => {
        console.log('订单状态更新成功:', updateRes);
        // 刷新订单列表
        getOrderList();
      })
      .catch(err => {
        console.error('更新订单状态失败:', err);
        getOrderList();
      });
  }).catch(err => {
    console.error('评价提交失败:', err);
    message.error('评价提交失败');
    
    // 重新打开模态框，让用户再次尝试
    setTimeout(() => {
      commentModalVisible.value = true;
    }, 500);
  });
};

// 物流查询模态框
const shippingModalVisible = ref(false);
const shippingLoading = ref(false);
const trackingInfo = ref(null);
const currentOrder = ref({});

// 模拟物流数据
const mockTrackingInfo = [
  { time: getFormatTime(new Date(), true), status: '已签收，签收人：本人' },
  { time: getFormatTime(new Date(Date.now() - 86400000), true), status: '派送中，派送员：张师傅，联系电话：135****8899' },
  { time: getFormatTime(new Date(Date.now() - 86400000 * 2), true), status: '已到达【XX市XX区XX街道】' },
  { time: getFormatTime(new Date(Date.now() - 86400000 * 3), true), status: '运输中，已离开【XX市集散中心】' },
  { time: getFormatTime(new Date(Date.now() - 86400000 * 4), true), status: '已到达【XX市集散中心】' },
  { time: getFormatTime(new Date(Date.now() - 86400000 * 5), true), status: '已揽件，揽件员：李师傅' },
  { time: getFormatTime(new Date(Date.now() - 86400000 * 5), true), status: '商家已发货' }
];

// 查看物流
const checkShipping = (item) => {
  if (!item.trackingNumber) {
    message.info('暂无物流信息');
    return;
  }
  
  // 保存当前订单信息
  currentOrder.value = { ...item };
  
  // 显示模态框
  shippingModalVisible.value = true;
  
  // 模拟加载物流信息
  shippingLoading.value = true;
  setTimeout(() => {
    // 这里应该是调用真实的物流API
    trackingInfo.value = mockTrackingInfo;
    shippingLoading.value = false;
  }, 1000);
};

// 复制快递单号
const copyTrackingNumber = (number) => {
  if (!number) {
    message.error('快递单号不存在');
    return;
  }
  
  // 创建临时input元素
  const input = document.createElement('input');
  input.value = number;
  document.body.appendChild(input);
  input.select();
  
  try {
    const successful = document.execCommand('copy');
    if (successful) {
      message.success('复制成功');
    } else {
      message.error('复制失败');
    }
  } catch (err) {
    message.error('复制失败: ' + err);
  }
  
  document.body.removeChild(input);
};

// 关闭物流模态框
const closeShippingModal = () => {
  shippingModalVisible.value = false;
  currentOrder.value = {};
  trackingInfo.value = null;
};

// 从模态框中确认收货
const confirmReceivedFromModal = () => {
  Modal.confirm({
    title: '确认收货',
    content: '确认已收到商品吗？确认后将无法撤销。',
    okText: '确认收货',
    cancelText: '取消',
    onOk: () => {
      confirmReceived(currentOrder.value);
      closeShippingModal();
    }
  });
};

// 确认收货
const confirmReceived = (item) => {
  if (!item || !item.id) {
    message.error('订单信息不完整');
    return;
  }
  
  confirmReceiptApi({
    id: item.id
  })
    .then(res => {
      message.success('确认收货成功');
      getOrderList();
    })
    .catch(err => {
      message.error(err.msg || '确认收货失败');
    });
};

// 显示确认收货的确认框
const showConfirmReceiveModal = (item) => {
  Modal.confirm({
    title: '确认收货',
    content: '确认已收到商品吗？确认后将无法撤销，并且系统会根据订单金额自动为您增加相应积分。',
    okText: '确认收货',
    cancelText: '取消',
    onOk: () => {
      confirmReceived(item);
    }
  });
};

// 取消订单（待发货状态）
const cancelOrder = (item) => {
  if (!item || !item.id) {
    message.error('订单信息不完整');
    return;
  }
  
  cancelUserOrderApi({
    id: item.id
  })
    .then(res => {
      message.success('订单取消成功');
      getOrderList();
    })
    .catch(err => {
      message.error(err.msg || '取消订单失败');
    });
};
</script>

<style scoped lang="less">
.order-view {
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
      
      .time-filter {
        color: #666;
        margin-right: 8px;
      }
    }
  }
  
  .order-tabs {
    margin-bottom: 20px;
  }
  
  .loading-state, .empty-state {
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
  
  .order-list {
    .order-card {
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
      margin-bottom: 20px;
      overflow: hidden;
      transition: all 0.3s;
      
      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      }
      
      &.order-pending {
        border-left: 4px solid #ff9800;
      }
      
      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px;
        background: #f9f9f9;
        border-bottom: 1px solid #eee;
        
        .order-info {
          .order-time {
            color: #666;
            margin-right: 20px;
          }
          
          .order-number {
            color: #333;
            font-weight: 500;
          }
        }
        
        .status-tag {
          border-radius: 4px;
          font-weight: 500;
        }
      }
      
      .order-content {
        padding: 20px;
        display: flex;
        
        .product-info {
          display: flex;
          flex: 1;
          padding-right: 30px;
          
          .product-image {
            width: 80px;
            height: 80px;
            margin-right: 16px;
            border-radius: 4px;
            overflow: hidden;
            background: #f5f5f5;
            
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
          }
          
          .product-details {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            
            .product-title {
              font-size: 16px;
              color: #333;
              margin: 0 0 10px;
              cursor: pointer;
              
              &:hover {
                color: #4684e2;
              }
            }
            
            .product-meta {
              display: flex;
              justify-content: space-between;
              
              .product-price {
                color: #ff6b00;
                font-weight: 500;
              }
              
              .product-quantity {
                color: #999;
              }
            }
          }
        }
        
        .delivery-info {
          flex: 1;
          border-left: 1px dashed #eee;
          padding-left: 30px;
          
          .delivery-section {
            margin-bottom: 16px;
            
            &:last-child {
              margin-bottom: 0;
            }
            
            .section-title {
              color: #666;
              font-size: 14px;
              font-weight: normal;
              margin: 0 0 8px;
            }
            
            p {
              margin: 0;
              color: #333;
              line-height: 1.5;
            }
            
            .contact-info {
              font-weight: 500;
            }
            
            .tracking-info, .remark-info, .shipping-info {
              color: #666;
            }
            
            .shipping-company {
              color: #1890ff;
              font-weight: 500;
              margin-right: 8px;
            }
          }
        }
      }
      
      .order-footer {
        padding: 16px 20px;
        background: #f9f9f9;
        border-top: 1px solid #eee;
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .order-summary {
          color: #666;
          
          .divider {
            margin: 0 10px;
            color: #ddd;
          }
          
          .details-link {
            color: #4684e2;
            cursor: pointer;
            
            &:hover {
              text-decoration: underline;
            }
          }
        }
        
        .order-price {
          display: flex;
          
          .price-item {
            margin-left: 20px;
            
            .price-label {
              color: #666;
            }
            
            .price-value {
              color: #333;
              font-weight: 500;
              
              &.discount {
                color: #ff6b00;
              }
            }
            
            &.total {
              .price-value {
                color: #ff6b00;
                font-weight: 600;
                font-size: 16px;
              }
            }
          }
        }
        
        .order-actions {
          display: flex;
          
          .action-btn {
            margin-left: 12px;
            
            &.pay-btn {
              background: #ff6b00;
              border-color: #ff6b00;
              
              &:hover {
                background: #e96000;
                border-color: #e96000;
              }
            }
            
            &.cancel-btn {
              color: #666;
            }
          }
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

/* 添加评价相关样式 */
.rated-tag {
  margin-left: 12px;
}

.comment-modal-content {
  .comment-product {
    display: flex;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
    
    .product-img {
      width: 80px;
      height: 80px;
      flex-shrink: 0;
      margin-right: 16px;
      border-radius: 4px;
      overflow: hidden;
      background: #f5f5f5;
      object-fit: cover;
    }
    
    .product-info {
      flex: 1;
      min-width: 0; /* 防止子元素溢出 */
      
      .product-title {
        font-size: 16px;
        margin: 0 0 8px;
        color: #333;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .product-price {
        font-size: 16px;
        color: #ff6b00;
        font-weight: 500;
        margin: 0;
      }
    }
  }
  
  .product-review-area {
    .comment-rate-container {
      margin-bottom: 16px;
    }
    
    .comment-input-container {
      margin-bottom: 16px;
    }
  }
}

.order-actions {
  .rate-btn {
    background: #FF9800;
    border-color: #FF9800;
    
    &:hover {
      background: #F57C00;
      border-color: #F57C00;
    }
  }
}

/* 物流模态框样式 */
.shipping-modal-content {
  .shipping-header {
    margin-bottom: 20px;
    
    .shipping-product {
      display: flex;
      align-items: center;
      
      .product-img {
        width: 70px;
        height: 70px;
        border-radius: 4px;
        object-fit: cover;
        margin-right: 16px;
      }
      
      .product-info {
        flex: 1;
        
        .product-title {
          font-size: 16px;
          font-weight: 500;
          color: #333;
          margin-bottom: 8px;
        }
        
        .order-number {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }
  
  .shipping-info {
    margin-bottom: 20px;
    
    .info-item {
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      
      .label {
        color: #666;
        width: 80px;
        flex-shrink: 0;
      }
      
      .value {
        color: #333;
        
        &.highlight {
          color: #1890ff;
          font-weight: 500;
        }
      }
      
      &.shipping-time {
        .value {
          color: #666;
          font-style: italic;
        }
      }
    }
  }
  
  .tracking-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30px 0;
    
    p {
      margin-top: 16px;
      color: #666;
    }
  }
  
  .tracking-result {
    .tracking-list {
      .tracking-item {
        padding: 16px 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        &.active {
          .time, .status {
            color: #1890ff;
            font-weight: 500;
          }
        }
        
        .time {
          font-size: 13px;
          color: #666;
          margin-bottom: 8px;
        }
        
        .status {
          font-size: 14px;
          color: #333;
        }
      }
    }
  }
  
  .tracking-empty {
    padding: 30px 0;
    text-align: center;
    
    .tracking-tip {
      color: #999;
      font-size: 13px;
      margin-top: 16px;
    }
  }
  
  .modal-footer {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
    
    button {
      margin-left: 12px;
    }
  }
}
</style>
