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
    <div class="login-card">
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
          <div class="slogan">购物新体验，品质新生活</div>
          <div class="feature-list">
            <div class="feature-item">
              <div class="feature-icon">
                <i class="feature-icon-inner">⚡</i>
              </div>
              <div class="feature-content">
                <h3>精选优质商品</h3>
                <p>汇聚全球好物，精选品质生活</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <i class="feature-icon-inner">🔒</i>
              </div>
              <div class="feature-content">
                <h3>安全支付保障</h3>
                <p>全程交易担保，资金安全无忧</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <i class="feature-icon-inner">🚚</i>
              </div>
              <div class="feature-content">
                <h3>极速物流配送</h3>
                <p>专业物流服务，准时送达家门</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="login-panel">
        <div class="login-header">
          <h2>欢迎登录</h2>
          <p>登录账号，享受品质购物体验</p>
        </div>
        <div class="form-container">
          <div class="input-wrapper">
            <label class="input-label">账号</label>
            <div class="input-field">
              <span class="input-icon">
                <img :src="MailIcon" />
              </span>
              <input type="text" placeholder="请输入账号" v-model="pageData.loginForm.username" />
            </div>
          </div>
          
          <div class="input-wrapper">
            <label class="input-label">密码</label>
            <div class="input-field">
              <span class="input-icon">
                <img :src="PwdIcon" />
              </span>
              <input type="password" placeholder="请输入密码" v-model="pageData.loginForm.password" />
            </div>
          </div>
          
          <div class="input-wrapper verification-wrapper">
            <label class="input-label">验证码</label>
            <div class="verification-container">
              <div class="input-field verification-input">
                <input type="text" placeholder="验证码" v-model="userInputCode" maxlength="4" />
              </div>
              <div class="captcha-container">
                <div class="captcha-code" @click="refreshCaptcha">{{ captchaCode }}</div>
              </div>
            </div>
          </div>
          
          <div class="additional-options">
            <label class="remember-option">
              <input type="checkbox" class="remember-checkbox" />
              <span class="checkmark"></span>
              <span>记住我</span>
            </label>
            <a class="forgot-link">忘记密码?</a>
          </div>
          
          <button class="login-button" @click="handleLogin">
            <span class="button-text">立即登录</span>
            <span class="button-icon">→</span>
          </button>
          
          <div class="create-account">
            <span>没有账号?</span>
            <a @click="handleCreateUser" class="create-link">注册账号</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '/@/store';
import { message } from 'ant-design-vue';
import MailIcon from '/@/assets/images/mail-icon.svg';
import PwdIcon from '/@/assets/images/pwd-icon.svg';
import { useRouter } from 'vue-router';
import { reactive, ref } from 'vue';

const router = useRouter();
const userStore = useUserStore();

const captchaCode = ref(generateCaptcha());
const userInputCode = ref('');

function generateCaptcha() {
  const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  let result = '';
  for (let i = 0; i < 4; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

function refreshCaptcha() {
  captchaCode.value = generateCaptcha();
  userInputCode.value = '';
}

const pageData = reactive({
  loginForm: {
    username: '',
    password: '',
  },
});

const handleLogin = () => {
  if (!pageData.loginForm.username || !pageData.loginForm.password) {
    message.warning('用户名和密码不能为空');
    return;
  }
  
  if (!userInputCode.value) {
    message.warning('请输入验证码');
    return;
  }
  
  if (userInputCode.value.toUpperCase() !== captchaCode.value) {
    message.error('验证码错误');
    refreshCaptcha();
    return;
  }

  userStore
    .login({
      username: pageData.loginForm.username,
      password: pageData.loginForm.password,
    })
    .then((res) => {
      loginSuccess();
    })
    .catch((err) => {
      // 处理不同的错误情况
      if (err.code === 'USER_NOT_FOUND') {
        message.warn('用户名不存在');
      } else if (err.code === 'INVALID_PASSWORD') {
        message.warn('密码错误');
      } else {
        message.warn(err.msg || '登录失败');
      }
    });
};

const handleCreateUser = () => {
  router.push({ name: 'register' });
};

const loginSuccess = () => {
  router.push({ name: 'portal' });
  message.success('登录成功！');
};
</script>

<style scoped lang="less">
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
  background: linear-gradient(135deg, #4a65d5, #8364c4);
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

.login-card {
  width: 750px;
  min-height: 480px;
  display: flex;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 15px 50px rgba(46, 56, 167, 0.15), 0 5px 15px rgba(0, 0, 0, 0.1);
  background: white;
  position: relative;
  z-index: 1;
}

.brand-panel {
  position: relative;
  width: 40%;
  color: white;
  overflow: hidden;
  background: linear-gradient(135deg, #5a7be0, #8b68e8);
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
  cursor: pointer;
  margin-bottom: 15px;
}

.logo-icon {
  position: relative;
  width: 48px;
  height: 48px;
  margin-right: 15px;
}

.shopping-bag {
  position: relative;
  width: 100%;
  height: 100%;
}

.bag-body {
  position: absolute;
  width: 36px;
  height: 32px;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  border-radius: 4px;
  z-index: 1;
}

.bag-handle {
  position: absolute;
  width: 20px;
  height: 12px;
  border: 3px solid white;
  border-bottom: none;
  border-radius: 10px 10px 0 0;
  top: 6px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 0;
}

.shine {
  position: absolute;
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  bottom: 6px;
  right: 14px;
  z-index: 2;
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
  margin-bottom: 20px;
  
  .input-label {
    display: block;
    font-size: 14px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }
  
  .input-field {
    position: relative;
    height: 48px;
    
    .input-icon {
      position: absolute;
      left: 16px;
      top: 50%;
      transform: translateY(-50%);
      display: flex;
      align-items: center;
      justify-content: center;
      
      img {
        width: 18px;
        height: 18px;
      }
    }
    
    input {
      width: 100%;
      height: 100%;
      border: 1px solid #e0e0e0;
      border-radius: 12px;
      padding: 0 16px 0 48px;
      font-size: 15px;
      transition: all 0.3s;
      
      &:focus {
        border-color: #5a7be0;
        box-shadow: 0 0 0 3px rgba(90, 123, 224, 0.1);
      }
      
      &::placeholder {
        color: #aaa;
      }
    }
  }
}

.verification-wrapper {
  .verification-container {
    display: flex;
    gap: 12px;
    align-items: stretch;
    height: 48px;
    
    .verification-input {
      flex: 1;
    }
    
    .captcha-container {
      display: flex;
      flex-direction: column;
      width: 130px;
      height: 100%;
      border-radius: 12px;
      overflow: hidden;
      border: 1px solid #e0e0e0;
      
      .captcha-code {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f3f4f6;
        font-size: 20px;
        font-weight: 700;
        letter-spacing: 5px;
        color: #555;
        cursor: pointer;
        transition: background 0.2s;
        
        &:hover {
          background: #e9ecef;
        }
      }
    }
  }
}

.additional-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
  
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
        background-color: #5a7be0;
        border-color: #5a7be0;
        
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
    color: #5a7be0;
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
  background: linear-gradient(135deg, #5a7be0, #8b68e8);
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
    box-shadow: 0 6px 15px rgba(90, 123, 224, 0.4);
  }
  
  .button-text {
    margin-right: 8px;
  }
  
  .button-icon {
    font-weight: 300;
    font-size: 18px;
  }
}

.create-account {
  text-align: center;
  
  span {
    font-size: 14px;
    color: #666;
  }
  
  .create-link {
    font-size: 14px;
    color: #5a7be0;
    font-weight: 600;
    margin-left: 6px;
    cursor: pointer;
    
    &:hover {
      text-decoration: underline;
    }
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
