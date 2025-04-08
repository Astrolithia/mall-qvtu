<template>
  <div class="score-view">
    <div class="page-header">
      <h2 class="page-title">我的积分</h2>
    </div>
    
    <!-- 积分卡片 -->
    <div class="score-card">
      <div class="score-info">
        <div class="score-label">当前积分</div>
        <div class="score-value">
          <span class="score-num">{{ score }}</span>
          <span class="score-unit">分</span>
        </div>
        <div class="score-actions">
          <a-button type="primary" @click="showRules">积分规则</a-button>
          <a-button @click="refreshScore">刷新积分</a-button>
        </div>
      </div>
      
      <div class="score-tips">
        <a-alert type="info" show-icon>
          <template #message>
            积分可用于抵扣订单金额，100积分=1元
          </template>
        </a-alert>
      </div>
    </div>
    
    <!-- 积分明细记录 -->
    <div class="score-history">
      <div class="header-with-actions">
        <h3 class="section-title">积分明细</h3>
      </div>
      
      <div v-if="historyLoading" class="loading-state">
        <a-spin size="large" />
        <div class="loading-text">加载中...</div>
      </div>
      
      <div v-else-if="scoreHistory.length === 0" class="empty-state">
        <a-empty description="暂无积分记录" />
      </div>
      
      <div v-else class="history-list">
        <div v-for="(item, index) in scoreHistory" :key="index" class="history-item">
          <div class="history-info">
            <div class="history-type">
              <span :class="['type-tag', getTypeClass(item.type)]">{{ getTypeText(item.type) }}</span>
              <span class="history-desc">{{ item.description }}</span>
            </div>
            <div class="history-time">{{ formatTime(item.createTime) }}</div>
          </div>
          <div class="history-score" :class="{ 'positive': true }">
            +{{ item.score }}
          </div>
        </div>
      </div>
    </div>
    
    <!-- 积分获取方式 -->
    <div class="score-ways">
      <h3 class="section-title">积分获取途径</h3>
      <div class="ways-list">
        <div class="way-item">
          <shopping-outlined class="way-icon" />
          <div class="way-info">
            <div class="way-title">购物消费</div>
            <div class="way-desc">每消费1元可获得1积分</div>
          </div>
        </div>
        <div class="way-item">
          <comment-outlined class="way-icon" />
          <div class="way-info">
            <div class="way-title">评价商品</div>
            <div class="way-desc">评价一次商品获得5积分</div>
          </div>
        </div>
        <div class="way-item">
          <calendar-outlined class="way-icon" />
          <div class="way-info">
            <div class="way-title">每日签到</div>
            <div class="way-desc">每日签到可获得2积分</div>
          </div>
        </div>
        <div class="way-item">
          <user-add-outlined class="way-icon" />
          <div class="way-info">
            <div class="way-title">邀请好友</div>
            <div class="way-desc">成功邀请好友获得20积分</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 积分规则弹窗 -->
    <a-modal
      v-model:visible="rulesVisible"
      title="积分规则"
      :footer="null"
      width="600px"
    >
      <div class="rules-content">
        <h4>一、积分获取</h4>
        <p>1. 购物消费：每消费1元可获得1积分</p>
        <p>2. 评价商品：每次有效评价可获得5积分</p>
        <p>3. 每日签到：每日登录签到可获得2积分</p>
        <p>4. 邀请好友：每成功邀请一位好友注册可获得20积分</p>
        
        <h4>二、积分使用</h4>
        <p>1. 积分可在购物结算时抵扣订单金额，100积分=1元</p>
        <p>2. 积分不可兑换现金</p>
        <p>3. 积分有效期为一年，请及时使用</p>
        
        <h4>三、其他说明</h4>
        <p>1. 积分仅限本人使用，不可转赠他人</p>
        <p>2. 如有积分相关问题，请联系客服</p>
        <p>3. 本平台保留对积分规则的最终解释权</p>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { 
  ShoppingOutlined, 
  CommentOutlined, 
  CalendarOutlined, 
  UserAddOutlined 
} from '@ant-design/icons-vue';
import { detailApi } from '/@/api/user';
import { getScoreListApi, getTotalScoreApi } from '/@/api/userScore';
import { useUserStore } from "/@/store";
import { getFormatTime } from '/@/utils';

const router = useRouter();
const userStore = useUserStore();

// 页面状态
const score = ref(0);
const loading = ref(false);
const rulesVisible = ref(false);
const scoreHistory = ref([]);
const historyLoading = ref(false);

onMounted(() => {
  getUserInfo();
  getScoreHistory();
});

// 获取用户信息
const getUserInfo = () => {
  loading.value = true;
  const userId = userStore.user_id;
  
  getTotalScoreApi({ userId: userId })
    .then(res => {
      if (res.data !== undefined) {
        score.value = res.data;
      }
      loading.value = false;
    })
    .catch(err => {
      console.error('获取用户积分失败', err);
      message.error(err.msg || '获取用户积分失败');
      loading.value = false;
    });
};

// 获取积分历史
const getScoreHistory = () => {
  historyLoading.value = true;
  const userId = userStore.user_id;
  
  getScoreListApi({ userId: userId })
    .then(res => {
      if (res.data && Array.isArray(res.data)) {
        scoreHistory.value = res.data.sort((a, b) => b.createTime - a.createTime);
      } else {
        scoreHistory.value = [];
      }
      historyLoading.value = false;
    })
    .catch(err => {
      console.error('获取积分历史失败', err);
      message.error(err.msg || '获取积分历史失败');
      historyLoading.value = false;
      scoreHistory.value = [];
    });
};

// 刷新积分
const refreshScore = () => {
  message.info('正在刷新积分...');
  getUserInfo();
  getScoreHistory();
};

// 显示积分规则
const showRules = () => {
  rulesVisible.value = true;
};

// 格式化时间戳
const formatTime = (timestamp) => {
  if (!timestamp) return '未知时间';
  return getFormatTime(timestamp, true);
};

// 获取积分类型文本
const getTypeText = (type) => {
  const typeMap = {
    'ORDER_CONFIRM': '确认收货',
    'COMMENT': '评价商品',
    'SIGN_IN': '每日签到',
    'INVITE': '邀请好友'
  };
  return typeMap[type] || '其他';
};

// 获取积分类型样式类
const getTypeClass = (type) => {
  const classMap = {
    'ORDER_CONFIRM': 'type-order',
    'COMMENT': 'type-comment',
    'SIGN_IN': 'type-signin',
    'INVITE': 'type-invite'
  };
  return classMap[type] || '';
};
</script>

<style scoped lang="less">
.score-view {
  padding: 0 20px;
  
  .page-header {
    margin-bottom: 24px;
    
    .page-title {
      font-size: 22px;
      font-weight: 600;
      color: #222;
      margin: 0;
    }
  }
  
  .score-card {
    background: linear-gradient(135deg, #4684e2, #7a5af8);
    border-radius: 12px;
    padding: 30px;
    color: white;
    margin-bottom: 24px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    
    .score-info {
      display: flex;
      flex-direction: column;
      
      .score-label {
        font-size: 16px;
        opacity: 0.9;
        margin-bottom: 8px;
      }
      
      .score-value {
        display: flex;
        align-items: baseline;
        margin-bottom: 20px;
        
        .score-num {
          font-size: 42px;
          font-weight: 700;
        }
        
        .score-unit {
          font-size: 18px;
          margin-left: 4px;
          opacity: 0.9;
        }
      }
      
      .score-actions {
        display: flex;
        gap: 12px;
      }
    }
    
    .score-tips {
      margin-top: 20px;
    }
  }
  
  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 0 0 16px 0;
  }
  
  .score-history {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    
    .header-with-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
    }
    
    .loading-state, .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 40px 0;
      
      .loading-text {
        margin-top: 16px;
        color: #999;
      }
    }
    
    .history-list {
      .history-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .history-info {
          flex: 1;
          
          .history-type {
            display: flex;
            align-items: center;
            margin-bottom: 4px;
            
            .type-tag {
              padding: 2px 8px;
              border-radius: 4px;
              font-size: 12px;
              margin-right: 8px;
              
              &.type-order {
                background: #e6f7ff;
                color: #1890ff;
              }
              
              &.type-comment {
                background: #f6ffed;
                color: #52c41a;
              }
              
              &.type-signin {
                background: #fff7e6;
                color: #fa8c16;
              }
              
              &.type-invite {
                background: #f9f0ff;
                color: #722ed1;
              }
            }
            
            .history-desc {
              font-size: 14px;
              color: #333;
            }
          }
          
          .history-time {
            font-size: 12px;
            color: #999;
          }
        }
        
        .history-score {
          font-size: 16px;
          font-weight: 500;
          
          &.positive {
            color: #52c41a;
          }
          
          &.negative {
            color: #f5222d;
          }
        }
      }
    }
  }
  
  .score-ways {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    
    .ways-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
      gap: 16px;
      
      .way-item {
        display: flex;
        align-items: center;
        padding: 16px;
        border-radius: 8px;
        background: #f9f9f9;
        transition: all 0.3s;
        
        &:hover {
          background: #f0f0f0;
          transform: translateY(-2px);
        }
        
        .way-icon {
          font-size: 24px;
          color: #4684e2;
          margin-right: 12px;
        }
        
        .way-info {
          .way-title {
            font-weight: 500;
            font-size: 15px;
            color: #333;
            margin-bottom: 4px;
          }
          
          .way-desc {
            font-size: 13px;
            color: #666;
          }
        }
      }
    }
  }
  
  .rules-content {
    h4 {
      margin-top: 16px;
      margin-bottom: 8px;
      color: #333;
    }
    
    p {
      margin: 6px 0;
      color: #666;
    }
  }
}
</style>
