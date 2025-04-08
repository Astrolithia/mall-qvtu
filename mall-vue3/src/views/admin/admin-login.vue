<!--
/**
 * 管理员登录组件
 * 
 * 该组件实现了管理员登录功能，包括：
 * 1. 管理员账号密码登录
 * 2. 记住登录状态
 * 3. 登录状态验证和错误处理
 * 4. 登录成功后跳转
 * 
 * 组件依赖：
 * - Ant Design Vue 组件库
 * - Vue Router 路由管理
 * - Vuex 状态管理
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <!--页面容器：包含背景动画和登录卡片-->
  <div class="container">
    <!--动态背景：使用CSS动画创建波浪效果-->
    <div class="animated-background">
      <div class="wave wave1"></div>
      <div class="wave wave2"></div>
      <div class="wave wave3"></div>
      <div class="wave wave4"></div>
    </div>
    
    <!--返回首页按钮：固定在左上角-->
    <div class="back-home-corner">
      <a @click="router.push({ name: 'portal' })" class="back-link">
        <span class="back-icon">←</span>
        <span>返回首页</span>
      </a>
    </div>
    
    <!--登录卡片：包含品牌展示和登录表单-->
    <div class="admin-login-card">
      <!--品牌展示面板：展示系统品牌和功能特点-->
      <div class="brand-panel">
        <div class="brand-content">
          <!--品牌Logo区域-->
          <div class="logo-container">
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
          <!--管理后台标识-->
          <div class="admin-badge">管理后台</div>
          <!--系统标语-->
          <div class="slogan">安全高效的电商管理系统</div>
          <!--功能特点列表-->
          <div class="feature-list">
            <!--商品管理功能-->
            <div class="feature-item">
              <div class="feature-icon">
                <i class="feature-icon-inner">🛒</i>
              </div>
              <div class="feature-content">
                <h3>商品管理</h3>
                <p>高效管理商品信息及库存</p>
              </div>
            </div>
            <!--数据分析功能-->
            <div class="feature-item">
              <div class="feature-icon">
                <i class="feature-icon-inner">📊</i>
              </div>
              <div class="feature-content">
                <h3>数据分析</h3>
                <p>实时掌握销售趋势及用户行为</p>
              </div>
            </div>
            <!--用户管理功能-->
            <div class="feature-item">
              <div class="feature-icon">
                <i class="feature-icon-inner">👥</i>
              </div>
              <div class="feature-content">
                <h3>用户管理</h3>
                <p>便捷管理用户信息及订单</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!--登录表单面板-->
      <div class="login-panel">
        <!--登录表单标题-->
        <div class="login-header">
          <h2>管理员登录</h2>
          <p>安全管理，高效运营</p>
        </div>
        
        <!--登录表单容器-->
        <div class="form-container">
          <!--使用Ant Design Vue的表单组件-->
          <a-form
            ref="myform"
            layout="vertical"
            :model="data.loginForm"
            :rules="data.rules"
            :hideRequiredMark="true"
            class="admin-form"
          >
            <!--用户名输入框-->
            <div class="input-wrapper">
              <label class="input-label">账号</label>
              <a-form-item name="username" :colon="false">
                <a-input
                  size="large"
                  placeholder="请输入管理员账号"
                  v-model:value="data.loginForm.username"
                  @pressEnter="handleSubmit"
                  class="admin-input"
                />
              </a-form-item>
            </div>
            
            <!--密码输入框-->
            <div class="input-wrapper">
              <label class="input-label">密码</label>
              <a-form-item name="password" :colon="false">
                <a-input
                  size="large"
                  type="password"
                  placeholder="请输入管理员密码"
                  v-model:value="data.loginForm.password"
                  @pressEnter="handleSubmit"
                  class="admin-input"
                />
              </a-form-item>
            </div>
            
            <!--登录选项：记住密码和忘记密码-->
            <div class="additional-options">
              <label class="remember-option">
                <input type="checkbox" class="remember-checkbox" v-model="checked" />
                <span class="checkmark"></span>
                <span>记住我</span>
              </label>
              <a class="forgot-link">忘记密码?</a>
            </div>
            
            <!--登录按钮-->
            <a-form-item>
              <button
                class="login-button"
                :loading="loginBtn"
                @click="handleSubmit"
              >
                <span class="button-text">管理员登录</span>
                <span class="button-icon">→</span>
              </button>
            </a-form-item>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 引入必要的依赖
 * - Vue Router：用于页面导航
 * - Vuex Store：用于状态管理
 * - Ant Design Vue：用于UI组件
 * - Vue Composition API：用于组件逻辑
 */
import { useUserStore } from '/@/store';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { reactive, ref, onMounted } from 'vue';

/**
 * 初始化路由和状态管理
 */
const router = useRouter();
const userStore = useUserStore();

/**
 * 组件引用和状态变量
 */
const myform = ref(); // 表单引用
const loginBtn = ref<Boolean>(false); // 登录按钮加载状态
const checked = ref<Boolean>(false); // 记住密码状态

/**
 * 响应式数据对象
 * 包含登录表单数据和验证规则
 */
const data = reactive({
  // 登录表单数据
  loginForm: {
    username: '',
    password: '',
  },
  // 表单验证规则
  rules: {
    username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
    password: [{ required: true, message: '请输入管理员密码', trigger: 'blur' }],
  },
});

/**
 * 处理表单提交
 * 
 * 验证表单数据并触发登录流程
 * 
 * @throws {Error} 当表单验证失败时抛出错误
 */
const handleSubmit = () => {
  console.log('提交数据:', data.loginForm);
  
  // 验证表单数据
  if (!data.loginForm.username || !data.loginForm.password) {
    console.log('验证失败:', data.loginForm.username, data.loginForm.password);
    message.warn('请填写完整登录信息');
    return;
  }
  
  // 设置登录按钮加载状态
  loginBtn.value = true;
  // 执行登录操作
  handleLogin();
};

/**
 * 执行登录操作
 * 
 * 调用Vuex action进行登录验证
 * 
 * @throws {Error} 当登录失败时抛出错误
 */
const handleLogin = () => {
  userStore
    .adminLogin({
      username: data.loginForm.username,
      password: data.loginForm.password,
    })
    .then((res) => {
      // 登录成功处理
      loginSuccess();
    })
    .catch((err) => {
      // 登录失败处理
      loginBtn.value = false;
      message.warn(err.msg || '登录失败');
    });
};

/**
 * 登录成功处理
 * 
 * 跳转到管理后台首页并显示成功提示
 */
const loginSuccess = () => {
  router.push({ path: '/admin' });
  message.success('登录成功！');
};
</script>

<style lang="less" scoped>
/**
 * 页面容器样式
 * 设置整体布局和背景
 */
.container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

/**
 * 动态背景样式
 * 创建渐变背景和波浪动画效果
 */
.animated-background {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background: linear-gradient(135deg, #2d4b8e, #664687);
  overflow: hidden;
  z-index: -1;
}

/**
 * 波浪动画样式
 * 定义波浪形状和动画效果
 */
.wave {
  position: absolute;
  width: 200%;
  height: 220%;
  opacity: 0.3;
  border-radius: 45%;
  background: rgba(255, 255, 255, 0.1);
  bottom: -120%;
  left: -50%;
}

/**
 * 波浪动画关键帧
 * 定义旋转动画效果
 */
@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/**
 * 登录卡片样式
 * 设置卡片布局和阴影效果
 */
.admin-login-card {
  width: 750px;
  min-height: 480px;
  display: flex;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 15px 50px rgba(25, 32, 74, 0.2), 0 5px 15px rgba(0, 0, 0, 0.1);
  background: white;
  position: relative;
  z-index: 1;
}

/**
 * 品牌面板样式
 * 设置左侧品牌展示区域样式
 */
.brand-panel {
  position: relative;
  width: 40%;
  color: white;
  overflow: hidden;
  background: linear-gradient(135deg, #3c5a9a, #6e479c);
}

/**
 * 品牌内容样式
 * 设置品牌展示区域内部布局
 */
.brand-content {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 40px 30px;
  height: 100%;
  z-index: 4;
}

/**
 * Logo容器样式
 * 设置Logo和品牌名称的布局
 */
.logo-container {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

/**
 * Logo图标样式
 * 创建购物袋图标效果
 */
.logo-icon {
  position: relative;
  width: 48px;
  height: 48px;
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/**
 * 购物袋图标样式
 * 使用CSS创建购物袋形状
 */
.shopping-bag {
  position: relative;
  width: 36px;
  height: 36px;
  
  .bag-handle {
    position: absolute;
    width: 20px;
    height: 8px;
    border: 2px solid #fff;
    border-bottom: none;
    border-radius: 6px 6px 0 0;
    top: 0;
    left: 50%;
    transform: translateX(-50%);
  }
  
  .bag-body {
    position: absolute;
    width: 32px;
    height: 28px;
    background-color: #fff;
    border-radius: 6px;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
  }
  
  .shine {
    position: absolute;
    width: 8px;
    height: 8px;
    background-color: rgba(255, 255, 255, 0.7);
    border-radius: 50%;
    top: 14px;
    left: 12px;
  }
}

/**
 * 品牌名称样式
 * 设置品牌名称的字体和颜色
 */
.logo-brand {
  position: relative;
}

.logo-text {
  font-size: 28px;
  font-weight: 600;
  color: white;
  letter-spacing: 0.5px;
}

/**
 * 管理后台标识样式
 * 设置半透明背景和模糊效果
 */
.admin-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.15);
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
  backdrop-filter: blur(5px);
}

/**
 * 系统标语样式
 * 设置标语文本的字体和透明度
 */
.slogan {
  font-size: 15px;
  opacity: 0.9;
  margin-bottom: 50px;
  font-weight: 300;
}

/**
 * 功能特点列表样式
 * 设置功能展示区域的布局和间距
 */
.feature-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/**
 * 功能项样式
 * 设置单个功能项的布局和样式
 */
.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  
  .feature-icon {
    flex-shrink: 0;
    width: 48px;
    height: 48px;
    border-radius: 12px;
    background: rgba(255,255,255,0.15);
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(5px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    
    .feature-icon-inner {
      font-size: 22px;
      font-style: normal;
    }
  }
  
  .feature-content {
    h3 {
      font-size: 16px;
      font-weight: 600;
      margin: 0 0 4px 0;
    }
    
    p {
      font-size: 13px;
      opacity: 0.9;
      margin: 0;
      font-weight: 300;
    }
  }
}

/**
 * 登录面板样式
 * 设置右侧登录表单区域的布局
 */
.login-panel {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
}

/**
 * 登录标题样式
 * 设置登录表单标题的字体和颜色
 */
.login-header {
  margin-bottom: 24px;
  
  h2 {
    font-size: 26px;
    color: #333;
    margin: 0 0 6px 0;
    font-weight: 700;
  }
  
  p {
    font-size: 15px;
    color: #666;
    margin: 0;
  }
}

/**
 * 表单容器样式
 * 设置表单区域的宽度和布局
 */
.form-container {
  width: 100%;
  max-width: 340px;
}

/**
 * 输入框包装器样式
 * 设置输入框标签和输入框的布局
 */
.input-wrapper {
  margin-bottom: 10px;
  
  .input-label {
    display: block;
    font-size: 14px;
    font-weight: 600;
    color: #333;
    margin-bottom: 6px;
  }
}

/**
 * 管理员表单样式
 * 自定义Ant Design Vue表单组件的样式
 */
.admin-form {
  :deep(.ant-form-item) {
    margin-bottom: 20px;
  }
  
  :deep(.ant-input) {
    height: 48px;
    border-radius: 12px;
    padding: 0 16px;
    font-size: 15px;
    border: 1px solid #e0e0e0;
    
    &:focus {
      border-color: #3c5a9a;
      box-shadow: 0 0 0 3px rgba(60, 90, 154, 0.1);
    }
    
    &::placeholder {
      color: #aaa;
    }
  }
}

/**
 * 登录选项样式
 * 设置记住密码和忘记密码的布局
 */
.additional-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  
  .remember-option {
    position: relative;
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #666;
    cursor: pointer;
    
    input {
      position: absolute;
      opacity: 0;
      height: 0;
      width: 0;
      
      &:checked ~ .checkmark {
        background-color: #3c5a9a;
        border-color: #3c5a9a;
        
        &:after {
          display: block;
        }
      }
    }
    
    .checkmark {
      position: relative;
      height: 18px;
      width: 18px;
      margin-right: 8px;
      background-color: white;
      border: 1.5px solid #ccc;
      border-radius: 4px;
      transition: all 0.3s;
      
      &:after {
        content: "";
        position: absolute;
        display: none;
        left: 6px;
        top: 2px;
        width: 4px;
        height: 9px;
        border: solid white;
        border-width: 0 2px 2px 0;
        transform: rotate(45deg);
      }
    }
  }
  
  .forgot-link {
    font-size: 14px;
    color: #3c5a9a;
    cursor: pointer;
    font-weight: 500;
    
    &:hover {
      text-decoration: underline;
    }
  }
}

/**
 * 登录按钮样式
 * 设置登录按钮的渐变背景和悬停效果
 */
.login-button {
  width: 100%;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #3c5a9a, #6e479c);
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  margin-bottom: 24px;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 15px rgba(60, 90, 154, 0.4);
  }
  
  .button-text {
    margin-right: 8px;
  }
  
  .button-icon {
    font-weight: 300;
    font-size: 18px;
  }
}

/**
 * 返回首页按钮样式
 * 设置左上角返回按钮的样式和动画效果
 */
.back-home-corner {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 5;
  background-color: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(5px);
  padding: 8px 16px;
  border-radius: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:hover {
    background-color: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
  }
  
  .back-link {
    font-size: 14px;
    color: white;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    
    .back-icon {
      margin-right: 6px;
      font-weight: bold;
    }
    
    &:hover {
      text-decoration: none;
    }
  }
}
</style>
