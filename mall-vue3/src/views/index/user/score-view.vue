<!--
/**
 * 用户积分中心组件
 * 
 * 该组件实现了电商平台的用户积分管理功能，包括：
 * 1. 积分总额显示
 * 2. 积分获取规则
 * 3. 积分明细历史记录
 * 4. 积分获取途径展示
 * 5. 积分规则弹窗
 * 
 * 组件依赖：
 * - Vue 3.x
 * - Ant Design Vue
 * - Ant Design Icons
 * - Pinia 状态管理
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */
-->

<template>
  <!-- 积分中心主容器 -->
  <div class="score-view">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <h2 class="page-title">我的积分</h2>
    </div>
    
    <!-- 积分卡片：显示当前积分总额和快捷操作 -->
    <div class="score-card">
      <div class="score-info">
        <!-- 积分标签和数值 -->
        <div class="score-label">当前积分</div>
        <div class="score-value">
          <span class="score-num">{{ score }}</span>
          <span class="score-unit">分</span>
        </div>
        <!-- 操作按钮区域 -->
        <div class="score-actions">
          <a-button type="primary" @click="showRules">积分规则</a-button>
          <a-button @click="refreshScore">刷新积分</a-button>
        </div>
      </div>
      
      <!-- 积分使用提示 -->
      <div class="score-tips">
        <a-alert type="info" show-icon>
          <template #message>
            积分可用于抵扣订单金额，100积分=1元
          </template>
        </a-alert>
      </div>
    </div>
    
    <!-- 积分明细记录区域 -->
    <div class="score-history">
      <!-- 标题栏 -->
      <div class="header-with-actions">
        <h3 class="section-title">积分明细</h3>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="historyLoading" class="loading-state">
        <a-spin size="large" />
        <div class="loading-text">加载中...</div>
      </div>
      
      <!-- 空状态 -->
      <div v-else-if="scoreHistory.length === 0" class="empty-state">
        <a-empty description="暂无积分记录" />
      </div>
      
      <!-- 积分记录列表 -->
      <div v-else class="history-list">
        <!-- 单条积分记录 -->
        <div v-for="(item, index) in scoreHistory" :key="index" class="history-item">
          <div class="history-info">
            <!-- 积分类型和描述 -->
            <div class="history-type">
              <span :class="['type-tag', getTypeClass(item.type)]">{{ getTypeText(item.type) }}</span>
              <span class="history-desc">{{ item.description }}</span>
            </div>
            <!-- 积分获取时间 -->
            <div class="history-time">{{ formatTime(item.createTime) }}</div>
          </div>
          <!-- 积分变动数量 -->
          <div class="history-score" :class="{ 'positive': true }">
            +{{ item.score }}
          </div>
        </div>
      </div>
    </div>
    
    <!-- 积分获取方式展示区域 -->
    <div class="score-ways">
      <h3 class="section-title">积分获取途径</h3>
      <div class="ways-list">
        <!-- 购物消费方式 -->
        <div class="way-item">
          <shopping-outlined class="way-icon" />
          <div class="way-info">
            <div class="way-title">购物消费</div>
            <div class="way-desc">每消费1元可获得1积分</div>
          </div>
        </div>
        <!-- 评价商品方式 -->
        <div class="way-item">
          <comment-outlined class="way-icon" />
          <div class="way-info">
            <div class="way-title">评价商品</div>
            <div class="way-desc">评价一次商品获得5积分</div>
          </div>
        </div>
        <!-- 每日签到方式 -->
        <div class="way-item">
          <calendar-outlined class="way-icon" />
          <div class="way-info">
            <div class="way-title">每日签到</div>
            <div class="way-desc">每日签到可获得2积分</div>
          </div>
        </div>
        <!-- 邀请好友方式 -->
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
      <!-- 积分规则内容 -->
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
/**
 * 导入所需的依赖和资源
 */
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
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

/**
 * 获取状态和路由
 */
const router = useRouter();
const userStore = useUserStore();

/**
 * 响应式数据定义
 */
const score = ref(0);                // 用户当前积分总额
const loading = ref(false);          // 积分信息加载状态
const rulesVisible = ref(false);     // 积分规则弹窗可见状态
const scoreHistory = ref([]);        // 积分历史记录
const historyLoading = ref(false);   // 积分历史加载状态

/**
 * 组件挂载后执行
 * 初始化数据，获取用户积分信息和历史记录
 */
onMounted(() => {
  getUserInfo();
  getScoreHistory();
});

/**
 * 获取用户积分信息
 * 
 * @description 从API获取用户的积分总额
 * @throws {Error} 当API请求失败时抛出错误
 */
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

/**
 * 获取积分历史记录
 * 
 * @description 从API获取用户的积分变动历史记录
 * @throws {Error} 当API请求失败时抛出错误
 */
const getScoreHistory = () => {
  historyLoading.value = true;
  const userId = userStore.user_id;
  
  getScoreListApi({ userId: userId })
    .then(res => {
      if (res.data && Array.isArray(res.data)) {
        // 按时间倒序排列积分记录
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

/**
 * 刷新积分信息
 * 
 * @description 重新加载用户积分总额和历史记录
 */
const refreshScore = () => {
  message.info('正在刷新积分...');
  getUserInfo();
  getScoreHistory();
};

/**
 * 显示积分规则弹窗
 * 
 * @description 设置弹窗可见状态为true
 */
const showRules = () => {
  rulesVisible.value = true;
};

/**
 * 格式化时间戳
 * 
 * @description 将时间戳转换为可读格式
 * @param {number} timestamp - 时间戳
 * @returns {string} 格式化后的时间字符串
 */
const formatTime = (timestamp) => {
  if (!timestamp) return '未知时间';
  return getFormatTime(timestamp, true);
};

/**
 * 获取积分类型文本
 * 
 * @description 根据积分类型代码返回对应的中文描述
 * @param {string} type - 积分类型代码
 * @returns {string} 积分类型的中文描述
 */
const getTypeText = (type) => {
  const typeMap = {
    'ORDER_CONFIRM': '确认收货',
    'COMMENT': '评价商品',
    'SIGN_IN': '每日签到',
    'INVITE': '邀请好友'
  };
  return typeMap[type] || '其他';
};

/**
 * 获取积分类型样式类
 * 
 * @description 根据积分类型代码返回对应的CSS类名
 * @param {string} type - 积分类型代码
 * @returns {string} 对应的CSS类名
 */
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
/**
 * 积分中心主容器样式
 * 设置内边距和布局
 */
.score-view {
  padding: 0 20px;
  
  /**
   * 页面标题区域样式
   * 设置下边距
   */
  .page-header {
    margin-bottom: 24px;
    
    /**
     * 页面标题样式
     * 设置字体、颜色和外边距
     */
    .page-title {
      font-size: 22px;
      font-weight: 600;
      color: #222;
      margin: 0;
    }
  }
  
  /**
   * 积分卡片样式
   * 设置渐变背景、圆角和阴影
   */
  .score-card {
    background: linear-gradient(135deg, #4684e2, #7a5af8);
    border-radius: 12px;
    padding: 30px;
    color: white;
    margin-bottom: 24px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    
    /**
     * 积分信息区域样式
     * 设置列式布局
     */
    .score-info {
      display: flex;
      flex-direction: column;
      
      /**
       * 积分标签样式
       * 设置字体大小和透明度
       */
      .score-label {
        font-size: 16px;
        opacity: 0.9;
        margin-bottom: 8px;
      }
      
      /**
       * 积分数值区域样式
       * 设置行式布局和对齐方式
       */
      .score-value {
        display: flex;
        align-items: baseline;
        margin-bottom: 20px;
        
        /**
         * 积分数字样式
         * 设置大字号和粗字重
         */
        .score-num {
          font-size: 42px;
          font-weight: 700;
        }
        
        /**
         * 积分单位样式
         * 设置更小字号和透明度
         */
        .score-unit {
          font-size: 18px;
          margin-left: 4px;
          opacity: 0.9;
        }
      }
      
      /**
       * 积分操作区域样式
       * 设置按钮间隔
       */
      .score-actions {
        display: flex;
        gap: 12px;
      }
    }
    
    /**
     * 积分提示区域样式
     * 设置上边距
     */
    .score-tips {
      margin-top: 20px;
    }
  }
  
  /**
   * 区域标题通用样式
   * 设置字体、颜色和外边距
   */
  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 0 0 16px 0;
  }
  
  /**
   * 积分历史记录区域样式
   * 设置背景、圆角、内边距和阴影
   */
  .score-history {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    
    /**
     * 带操作的标题栏样式
     * 设置行式布局和对齐方式
     */
    .header-with-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
    }
    
    /**
     * 加载状态和空状态共用样式
     * 设置居中布局和内边距
     */
    .loading-state, .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 40px 0;
      
      /**
       * 加载文本样式
       * 设置上边距和颜色
       */
      .loading-text {
        margin-top: 16px;
        color: #999;
      }
    }
    
    /**
     * 历史记录列表样式
     */
    .history-list {
      /**
       * 单条历史记录项样式
       * 设置行式布局、对齐方式和边框
       */
      .history-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 0;
        border-bottom: 1px solid #f0f0f0;
        
        /**
         * 最后一项移除底部边框
         */
        &:last-child {
          border-bottom: none;
        }
        
        /**
         * 历史记录信息区域样式
         * 设置弹性增长
         */
        .history-info {
          flex: 1;
          
          /**
           * 积分类型区域样式
           * 设置行式布局和下边距
           */
          .history-type {
            display: flex;
            align-items: center;
            margin-bottom: 4px;
            
            /**
             * 类型标签基础样式
             * 设置内边距、圆角和字号
             */
            .type-tag {
              padding: 2px 8px;
              border-radius: 4px;
              font-size: 12px;
              margin-right: 8px;
              
              /**
               * 确认收货类型样式
               * 设置蓝色背景和文字
               */
              &.type-order {
                background: #e6f7ff;
                color: #1890ff;
              }
              
              /**
               * 评价商品类型样式
               * 设置绿色背景和文字
               */
              &.type-comment {
                background: #f6ffed;
                color: #52c41a;
              }
              
              /**
               * 每日签到类型样式
               * 设置橙色背景和文字
               */
              &.type-signin {
                background: #fff7e6;
                color: #fa8c16;
              }
              
              /**
               * 邀请好友类型样式
               * 设置紫色背景和文字
               */
              &.type-invite {
                background: #f9f0ff;
                color: #722ed1;
              }
            }
            
            /**
             * 积分描述文本样式
             * 设置字号和颜色
             */
            .history-desc {
              font-size: 14px;
              color: #333;
            }
          }
          
          /**
           * 积分时间样式
           * 设置小字号和浅色
           */
          .history-time {
            font-size: 12px;
            color: #999;
          }
        }
        
        /**
         * 积分数值样式
         * 设置字号和字重
         */
        .history-score {
          font-size: 16px;
          font-weight: 500;
          
          /**
           * 积分增加样式
           * 设置绿色文字
           */
          &.positive {
            color: #52c41a;
          }
          
          /**
           * 积分减少样式
           * 设置红色文字
           */
          &.negative {
            color: #f5222d;
          }
        }
      }
    }
  }
  
  /**
   * 积分获取途径区域样式
   * 设置背景、圆角、内边距和阴影
   */
  .score-ways {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    
    /**
     * 途径列表样式
     * 设置网格布局和间距
     */
    .ways-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
      gap: 16px;
      
      /**
       * 单个途径项样式
       * 设置行式布局、内边距和背景
       */
      .way-item {
        display: flex;
        align-items: center;
        padding: 16px;
        border-radius: 8px;
        background: #f9f9f9;
        transition: all 0.3s;
        
        /**
         * 悬停效果
         * 设置背景变化和上移动画
         */
        &:hover {
          background: #f0f0f0;
          transform: translateY(-2px);
        }
        
        /**
         * 图标样式
         * 设置字号、颜色和右边距
         */
        .way-icon {
          font-size: 24px;
          color: #4684e2;
          margin-right: 12px;
        }
        
        /**
         * 途径信息区域样式
         */
        .way-info {
          /**
           * 途径标题样式
           * 设置字重、字号和颜色
           */
          .way-title {
            font-weight: 500;
            font-size: 15px;
            color: #333;
            margin-bottom: 4px;
          }
          
          /**
           * 途径描述样式
           * 设置字号和颜色
           */
          .way-desc {
            font-size: 13px;
            color: #666;
          }
        }
      }
    }
  }
  
  /**
   * 积分规则弹窗内容样式
   */
  .rules-content {
    /**
     * 规则标题样式
     * 设置外边距和颜色
     */
    h4 {
      margin-top: 16px;
      margin-bottom: 8px;
      color: #333;
    }
    
    /**
     * 规则条目样式
     * 设置外边距和颜色
     */
    p {
      margin: 6px 0;
      color: #666;
    }
  }
}
</style>
