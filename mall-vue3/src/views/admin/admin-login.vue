<!-- eslint-disable prettier/prettier -->
<template>
  <div class="container">
    <div class="animated-background">
      <div class="wave wave1"></div>
      <div class="wave wave2"></div>
      <div class="wave wave3"></div>
      <div class="wave wave4"></div>
    </div>
    <div class="back-home-corner">
      <a @click="router.push({ name: 'portal' })" class="back-link">
        <span class="back-icon">←</span>
        <span>返回首页</span>
      </a>
    </div>
    <div class="admin-login-card">
      <div class="brand-panel">
        <div class="brand-content">
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
          <div class="admin-badge">管理后台</div>
          <div class="slogan">安全高效的电商管理系统</div>
          <div class="feature-list">
            <div class="feature-item">
              <div class="feature-icon">
                <i class="feature-icon-inner">🛒</i>
              </div>
              <div class="feature-content">
                <h3>商品管理</h3>
                <p>高效管理商品信息及库存</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <i class="feature-icon-inner">📊</i>
              </div>
              <div class="feature-content">
                <h3>数据分析</h3>
                <p>实时掌握销售趋势及用户行为</p>
              </div>
            </div>
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
      <div class="login-panel">
        <div class="login-header">
          <h2>管理员登录</h2>
          <p>安全管理，高效运营</p>
        </div>
        <div class="form-container">
          <a-form
            ref="myform"
            layout="vertical"
            :model="data.loginForm"
            :rules="data.rules"
            :hideRequiredMark="true"
            class="admin-form"
          >
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
            
            <div class="additional-options">
              <label class="remember-option">
                <input type="checkbox" class="remember-checkbox" v-model="checked" />
                <span class="checkmark"></span>
                <span>记住我</span>
              </label>
              <a class="forgot-link">忘记密码?</a>
            </div>
            
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
import { useUserStore } from '/@/store';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { reactive, ref, onMounted } from 'vue';

const router = useRouter();
const userStore = useUserStore();

const myform = ref();
const loginBtn = ref<Boolean>(false);
const checked = ref<Boolean>(false);

const data = reactive({
  loginForm: {
    username: '',
    password: '',
  },
  rules: {
    username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
    password: [{ required: true, message: '请输入管理员密码', trigger: 'blur' }],
  },
});

const handleSubmit = () => {
  console.log('提交数据:', data.loginForm);  // 添加调试信息
  
  if (!data.loginForm.username || !data.loginForm.password) {
    console.log('验证失败:', data.loginForm.username, data.loginForm.password);  // 添加调试信息
    message.warn('请填写完整登录信息');
    return;
  }
  
  loginBtn.value = true;
  handleLogin();
};

const handleLogin = () => {
  userStore
    .adminLogin({
      username: data.loginForm.username,
      password: data.loginForm.password,
    })
    .then((res) => {
      loginSuccess();
    })
    .catch((err) => {
      loginBtn.value = false;
      message.warn(err.msg || '登录失败');
    });
};

const loginSuccess = () => {
  router.push({ path: '/admin' });
  message.success('登录成功！');
};
</script>

<style lang="less" scoped>
.container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

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

.wave1 {
  animation: rotate 12s linear infinite;
  transform-origin: 50% 48%;
}

.wave2 {
  animation: rotate 16s linear infinite;
  opacity: 0.2;
  transform-origin: 49% 52%;
}

.wave3 {
  animation: rotate 20s linear infinite reverse;
  opacity: 0.15;
  transform-origin: 51% 48%;
}

.wave4 {
  animation: rotate 24s linear infinite;
  opacity: 0.1;
  transform-origin: 52% 50%;
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

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

.brand-panel {
  position: relative;
  width: 40%;
  color: white;
  overflow: hidden;
  background: linear-gradient(135deg, #3c5a9a, #6e479c);
}

.brand-content {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 40px 30px;
  height: 100%;
  z-index: 4;
}

.logo-container {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.logo-icon {
  position: relative;
  width: 48px;
  height: 48px;
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}

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

.admin-logo {
  width: 40px;
  height: 40px;
  filter: brightness(0) invert(1);
}

.logo-brand {
  position: relative;
}

.logo-text {
  font-size: 28px;
  font-weight: 600;
  color: white;
  letter-spacing: 0.5px;
}

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

.slogan {
  font-size: 15px;
  opacity: 0.9;
  margin-bottom: 50px;
  font-weight: 300;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

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

.login-panel {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
}

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

.form-container {
  width: 100%;
  max-width: 340px;
}

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
