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
    <div class="register-card">
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
      <div class="register-panel">
        <div class="register-header">
          <h2>快速注册</h2>
          <p>创建账号，开启品质购物之旅</p>
        </div>
        <div class="form-container">
          <div class="input-wrapper">
            <label class="input-label">账号</label>
            <div class="input-field">
              <span class="input-icon">
                <img :src="MailIcon" />
              </span>
              <input type="text" placeholder="请输入账号" v-model="pageData.username" />
            </div>
          </div>
          
          <div class="input-wrapper">
            <label class="input-label">手机号</label>
            <div class="input-field">
              <span class="input-icon">
                <img :src="MailIcon" />
              </span>
              <input type="text" placeholder="请输入手机号" v-model="pageData.mobile" />
            </div>
          </div>
          
          <div class="input-wrapper">
            <label class="input-label">密码</label>
            <div class="input-field">
              <span class="input-icon">
                <img :src="PwdIcon" />
              </span>
              <input type="password" placeholder="请输入密码" v-model="pageData.password" />
            </div>
          </div>
          
          <div class="input-wrapper">
            <label class="input-label">确认密码</label>
            <div class="input-field">
              <span class="input-icon">
                <img :src="PwdIcon" />
              </span>
              <input type="password" placeholder="再次输入密码" v-model="pageData.confirmPassword" />
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
            <label class="terms-option">
              <input type="checkbox" class="terms-checkbox" v-model="pageData.agreeTerms" />
              <span class="checkmark"></span>
              <span>我已阅读并同意 <a class="terms-link">用户协议</a> 和 <a class="terms-link">隐私政策</a></span>
            </label>
          </div>
          
          <button class="register-button" @click="handleRegister">
            <span class="button-text">完成注册</span>
            <span class="button-icon">→</span>
          </button>
          
          <div class="login-account">
            <span>已有账号?</span>
            <a @click="$router.push({ name: 'login' })" class="login-link">去登录</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { userRegisterApi } from '/@/api/user';
import { message } from 'ant-design-vue';
import MailIcon from '/@/assets/images/mail-icon.svg';
import PwdIcon from '/@/assets/images/pwd-icon.svg';
import { useRouter } from 'vue-router';
import { reactive, ref } from 'vue';

const router = useRouter();

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
  username: '',
  password: '',
  confirmPassword: '',
  mobile: '',
  agreeTerms: false
});

const handleRegister = () => {
  if (!pageData.username) {
    message.warning('请输入账号');
    return;
  }
  
  if (!pageData.password) {
    message.warning('请输入密码');
    return;
  }
  
  if (!pageData.confirmPassword) {
    message.warning('请输入确认密码');
    return;
  }
  
  if (!pageData.mobile) {
    message.warning('请输入手机号');
    return;
  }
  
  if (!pageData.agreeTerms) {
    message.warning('请同意用户协议和隐私政策');
    return;
  }
  
  // 3. 验证两次密码输入是否一致
  if (pageData.password !== pageData.confirmPassword) {
    message.warning('提示两次密码输入不一致');
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
  
  userRegisterApi({
    username: pageData.username,
    password: pageData.password,
    rePassword: pageData.confirmPassword,
    mobile: pageData.mobile,
    code: userInputCode.value
  })
    .then((res) => {
      // 4. 注册成功提示
      message.success('注册成功');
      router.push({ name: 'login' });
    })
    .catch((err) => {
      // 2. 检查是否用户名已存在的错误
      if (err.code === 'USER_EXISTS' || (err.msg && err.msg.includes('已存在'))) {
        message.error('提示用户名已存在');
      } else {
        message.error(err.msg || '注册失败');
      }
    });
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

.register-card {
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

.register-panel {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
}

.register-header {
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
  margin-bottom: 28px;
  
  .terms-option {
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
      min-width: 18px;
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
    
    .terms-link {
      color: #5a7be0;
      cursor: pointer;
      font-weight: 500;
      
      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.register-button {
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

.login-account {
  text-align: center;
  
  span {
    font-size: 14px;
    color: #666;
  }
  
  .login-link {
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
