<!--
/**
 * 页脚组件
 * 
 * 该组件实现了电商平台的页脚展示功能，包括：
 * 1. 服务保障信息展示
 * 2. 购物指南导航
 * 3. 配送方式说明
 * 4. 支付方式展示
 * 5. 售后服务信息
 * 6. 联系方式和支付方式
 * 7. 版权信息展示
 * 
 * 组件依赖：
 * - Vue 2.x
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <!-- 页脚容器 -->
  <div class="footer-view">
    <!-- 页脚内容容器 -->
    <div class="footer-container">
      <!-- 服务保障区域 -->
      <div class="footer-service">
        <!-- 正品保障服务项 -->
        <div class="service-item">
          <div class="service-icon">
            <img src="../../../assets/images/genuine-icon.svg" alt="正品保障" />
          </div>
          <div class="service-info">
            <h4>正品保障</h4>
            <p>品质护航 购物无忧</p>
          </div>
        </div>
        <!-- 极速物流服务项 -->
        <div class="service-item">
          <div class="service-icon">
            <img src="../../../assets/images/express-icon.svg" alt="极速物流" />
          </div>
          <div class="service-info">
            <h4>极速物流</h4>
            <p>多仓直发 极速配送</p>
          </div>
        </div>
        <!-- 无忧退换服务项 -->
        <div class="service-item">
          <div class="service-icon">
            <img src="../../../assets/images/return-icon.svg" alt="无忧退换" />
          </div>
          <div class="service-info">
            <h4>无忧退换</h4>
            <p>7天无理由退换</p>
          </div>
        </div>
        <!-- 贴心服务项 -->
        <div class="service-item">
          <div class="service-icon">
            <img src="../../../assets/images/service-icon.svg" alt="贴心服务" />
          </div>
          <div class="service-info">
            <h4>贴心服务</h4>
            <p>全年无休 贴心服务</p>
          </div>
        </div>
      </div>
      
      <!-- 页脚导航区域 -->
      <div class="footer-top">
        <!-- 购物指南列 -->
        <div class="footer-column">
          <h3>购物指南</h3>
          <ul>
            <li><a href="#">购物流程</a></li>
            <li><a href="#">会员介绍</a></li>
            <li><a href="#">常见问题</a></li>
            <li><a href="#">联系客服</a></li>
          </ul>
        </div>
        <!-- 配送方式列 -->
        <div class="footer-column">
          <h3>配送方式</h3>
          <ul>
            <li><a href="#">上门自提</a></li>
            <li><a href="#">211限时达</a></li>
            <li><a href="#">配送服务查询</a></li>
            <li><a href="#">配送费收取标准</a></li>
          </ul>
        </div>
        <!-- 支付方式列 -->
        <div class="footer-column">
          <h3>支付方式</h3>
          <ul>
            <li><a href="#">货到付款</a></li>
            <li><a href="#">在线支付</a></li>
            <li><a href="#">分期付款</a></li>
            <li><a href="#">公司转账</a></li>
          </ul>
        </div>
        <!-- 售后服务列 -->
        <div class="footer-column">
          <h3>售后服务</h3>
          <ul>
            <li><a href="#">售后政策</a></li>
            <li><a href="#">价格保护</a></li>
            <li><a href="#">退款说明</a></li>
            <li><a href="#">返修/退换货</a></li>
          </ul>
        </div>
        <!-- 联系方式列 -->
        <div class="footer-column">
          <h3>关注我们</h3>
          <!-- 客服信息 -->
          <div class="contact-info">
            <p>客服热线：400-123-4567</p>
            <p>周一至周日 9:00-22:00</p>
          </div>
          <!-- 支付方式展示 -->
          <div class="payment-methods">
            <div class="payment-title">支付方式</div>
            <div class="payment-icons">
              <span class="payment-icon">支付宝</span>
              <span class="payment-icon">微信</span>
              <span class="payment-icon">银联</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 页脚底部区域 -->
      <div class="footer-bottom">
        <!-- 底部链接 -->
        <div class="foot-link-box">
          <a href="#" class="foot-link">关于我们</a>
          <div class="link-split"></div>
          <a href="#" class="foot-link">帮助中心</a>
          <div class="link-split"></div>
          <a href="#" class="foot-link">隐私政策</a>
          <div class="link-split"></div>
          <a href="#" class="foot-link">用户协议</a>
          <div class="link-split"></div>
          <a href="/#/admin" class="foot-link">后台管理</a>
        </div>
        <!-- 版权信息 -->
        <div class="copyright">2025 © 品质商城 · All Rights Reserved</div>
      </div>
    </div>
  </div>
</template>

<script>
/**
 * 组件定义
 * 
 * @component Footer
 * @description 页脚组件，展示网站底部信息
 */
export default {
  /**
   * 组件名称
   */
  name: 'Footer',
  
  /**
   * 组件数据
   * 
   * @returns {Object} 组件数据对象
   */
  data () {
    return {}
  },
  
  /**
   * 组件方法
   */
  methods: {}
}
</script>

<style scoped lang="less">
/**
 * 页脚基础样式
 * 设置背景色、阴影、内边距和上边距
 */
.footer-view {
  background-color: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  padding: 60px 0 40px;
  margin-top: 40px;
}

/**
 * 页脚容器样式
 * 设置最大宽度、居中对齐和内边距
 */
.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/**
 * 服务保障区域样式
 * 使用弹性布局展示服务项
 */
.footer-service {
  display: flex;
  justify-content: space-between;
  margin-bottom: 50px;
  padding-bottom: 40px;
  border-bottom: 1px solid #eaeaea;
  
  /**
   * 服务项样式
   */
  .service-item {
    display: flex;
    align-items: center;
    transition: transform 0.3s;
    
    /**
     * 悬浮效果
     */
    &:hover {
      transform: translateY(-5px);
    }
    
    /**
     * 服务图标样式
     */
    .service-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background: linear-gradient(135deg, #f5f8ff, #eef2f7);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
      box-shadow: 0 6px 15px rgba(70, 132, 226, 0.1);
      
      img {
        width: 35px;
        height: 35px;
      }
    }
    
    /**
     * 服务信息样式
     */
    .service-info {
      h4 {
        font-size: 18px;
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
      }
      
      p {
        font-size: 14px;
        color: #666;
      }
    }
  }
}

/**
 * 页脚导航区域样式
 * 使用弹性布局展示导航列
 */
.footer-top {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 50px;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 40px;
}

/**
 * 导航列样式
 */
.footer-column {
  flex: 1;
  min-width: 180px;
  margin-bottom: 20px;
  
  /**
   * 列标题样式
   */
  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
    position: relative;
    padding-bottom: 10px;
    
    /**
     * 标题下划线装饰
     */
    &:after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      width: 30px;
      height: 2px;
      background: #4684e2;
    }
  }
  
  /**
   * 导航列表样式
   */
  ul {
    list-style: none;
    padding: 0;
    margin: 0;
    
    li {
      margin-bottom: 10px;
      
      a {
        color: #666;
        font-size: 14px;
        transition: color 0.2s;
        
        /**
         * 链接悬浮效果
         */
        &:hover {
          color: #4684e2;
        }
      }
    }
  }
  
  /**
   * 联系信息样式
   */
  .contact-info {
    margin-bottom: 20px;
    
    p {
      margin: 5px 0;
      color: #666;
      font-size: 14px;
    }
  }
  
  /**
   * 支付方式样式
   */
  .payment-methods {
    .payment-title {
      font-size: 14px;
      color: #666;
      margin-bottom: 10px;
    }
    
    .payment-icons {
      display: flex;
      gap: 10px;
      
      .payment-icon {
        padding: 4px 8px;
        background: #f5f7fa;
        border-radius: 4px;
        font-size: 12px;
        color: #666;
      }
    }
  }
}

/**
 * 页脚底部样式
 */
.footer-bottom {
  text-align: center;
  
  /**
   * 底部链接容器样式
   */
  .foot-link-box {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
    flex-wrap: wrap;
    
    /**
     * 链接样式
     */
    .foot-link {
      margin: 0 16px;
      color: #4684e2;
      font-size: 14px;
      transition: color 0.2s;
      
      /**
       * 链接悬浮效果
       */
      &:hover {
        color: #3670c5;
        text-decoration: underline;
      }
    }
    
    /**
     * 链接分隔符样式
     */
    .link-split {
      width: 1px;
      height: 14px;
      background-color: #ddd;
    }
  }

  /**
   * 版权信息样式
   */
  .copyright {
    font-size: 13px;
    color: #999;
    line-height: 1.5;
  }
}
</style>
