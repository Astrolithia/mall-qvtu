<!--
/**
 * 用户推送设置组件
 * 
 * 该组件实现了电商平台的用户推送通知设置功能，包括：
 * 1. 邮箱设置
 * 2. 邮件接收开关
 * 3. 设置保存功能
 * 
 * 组件依赖：
 * - Vue 3.x
 * - Ant Design Vue
 * - Pinia 状态管理
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */
-->

<template>
  <!-- 推送设置主容器 -->
  <div class="content-list">
    <!-- 页面标题 -->
    <div class="list-title">推送设置</div>
    <!-- 内容区域 -->
    <div class="list-content">
      <div class="push-view">
        <!-- 推送邮箱设置项 -->
        <div class="item flex-view">
          <div class="label">推送邮箱</div>
          <div class="right-box">
            <input type="text" class="input-dom" placeholder="请输入邮箱" v-model="pushEmail">
          </div>
        </div>
        <!-- 邮件开关设置项 -->
        <div class="item flex-view">
          <div class="label">接受邮件消息</div>
          <div class="right-box">
            <a-switch v-model:checked="pushSwitch"/>
          </div>
        </div>
        <!-- 保存按钮 -->
        <button class="save mg" @click="handleSave()">保存</button>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 导入所需的依赖和资源
 */
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { message } from "ant-design-vue";
import { detailApi, updateUserInfoApi } from '/@/api/user';
import { useUserStore } from "/@/store";

/**
 * 获取状态和路由
 */
const router = useRouter();
const userStore = useUserStore();

/**
 * 响应式数据定义
 */
let pushEmail = ref('');       // 用户推送邮箱
let pushSwitch = ref(false);   // 邮件接收开关状态

/**
 * 组件挂载后执行
 * 获取用户推送设置信息
 */
onMounted(() => {
  getUserInfo();
});

/**
 * 获取用户信息
 * 
 * @description 从API获取用户信息并初始化邮箱和开关状态
 * @throws {Error} 当API请求失败时抛出错误
 */
const getUserInfo = () => {
  let userId = userStore.user_id;

  detailApi({userId: userId}).then(res => {
    if (res.data) {
      pushEmail.value = res.data.pushEmail;
      if(res.data.pushSwitch === '1'){
        pushSwitch.value = true;
      }
    }
  }).catch(err => {
    console.log(err);
  });
};

/**
 * 保存设置
 * 
 * @description 验证邮箱格式并将用户推送设置保存到服务器
 * @throws {Error} 当邮箱格式不正确或API请求失败时抛出错误
 */
const handleSave = () => {
  // 邮箱格式正则验证
  const reg = /^[a-zA-Z0-9][a-zA-Z0-9_]+\@[a-zA-Z0-9]+\.[a-zA-Z]{2,5}(\.[a-zA-Z]{2,5})*$/i;

  // 验证邮箱格式
  if (!pushEmail.value.match(reg)) {
    message.warn('请输入正确的邮箱格式');
    return;
  }

  // 准备表单数据
  let userId = userStore.user_id;
  const formData = new FormData();
  formData.append("id", userId);
  if (pushEmail.value) {
    formData.append('pushEmail', pushEmail.value);
  }
  // 将开关状态转换为字符串保存
  formData.append('pushSwitch', pushSwitch.value? '1':'0');
  
  // 提交更新请求
  updateUserInfoApi(formData).then(res => {
    // 刷新用户信息
    getUserInfo();
    message.success('保存成功');
  }).catch(err => {
    console.log(err);
  });
};

</script>
<style scoped lang="less">
/**
 * 进度条基础样式
 */
progress {
  vertical-align: baseline;
}

/**
 * 弹性布局容器样式
 */
.flex-view {
  display: flex;
}

/**
 * 表单元素样式重置
 * 移除边框和轮廓
 */
input, textarea {
  outline: none;
  border-style: none;
}

/**
 * 按钮样式重置
 * 移除内边距
 */
button {
  padding: 0;
}

/**
 * 内容列表容器样式
 * 设置弹性布局和基础样式
 */
.content-list {
  flex: 1;

  /**
   * 列表标题样式
   * 设置字体、颜色、高度和底部边框
   */
  .list-title {
    color: #152844;
    font-weight: 600;
    font-size: 18px;
    //line-height: 24px;
    height: 48px;
    margin-bottom: 4px;
    border-bottom: 1px solid #cedce4;
  }
}

/**
 * 推送设置视图样式
 * 设置整体布局和间距
 */
.push-view {
  /**
   * 单个设置项样式
   * 设置对齐方式和外边距
   */
  .item {
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    margin: 24px 0;

    /**
     * 标签样式
     * 设置宽度、颜色和字体
     */
    .label {
      width: 100px;
      color: #152844;
      font-weight: 600;
      font-size: 14px;
    }

    /**
     * 输入框样式
     * 设置背景、边框、尺寸和字体
     */
    .input-dom {
      background: #f8fafb;
      border-radius: 4px;
      width: 400px;
      height: 40px;
      line-height: 40px;
      font-size: 14px;
      color: #152844;
      padding: 0 12px;
    }
  }

  /**
   * 左侧外边距样式
   * 与标签宽度对齐
   */
  .mg {
    margin-left: 100px;
  }

  /**
   * 保存按钮样式
   * 设置背景、边框、尺寸和字体
   */
  .save {
    cursor: pointer;
    background: #4684e2;
    border-radius: 32px;
    width: 96px;
    height: 32px;
    line-height: 32px;
    font-size: 14px;
    color: #fff;
    border: none;
    outline: none;
  }
}

/**
 * 开关视图样式
 * 设置基础外观和鼠标样式
 */
.switch-view {
  position: relative;
  background: #a1adc5;
  border-radius: 32px;
  width: 48px;
  height: 28px;
  cursor: pointer;

  /**
   * 开关圆形按钮样式
   * 设置位置、尺寸和圆形边框
   */
  .circle {
    position: absolute;
    margin-left: 4px;
    top: 4px;
    width: 20px;
    height: 20px;
    background: #fff;
    border-radius: 50%;
  }

  /**
   * 选中状态下圆形按钮位置
   */
  .selected {
    right: 4px;
  }
}

/**
 * 选中状态样式
 * 设置开关的激活背景颜色
 */
.selected {
  background: #58b251;
}

</style>
