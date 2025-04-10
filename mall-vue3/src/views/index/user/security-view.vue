<!--
/**
 * 用户账号安全组件
 * 
 * 该组件实现了电商平台的用户账号安全管理功能，包括：
 * 1. 账号安全等级展示
 * 2. 手机绑定管理
 * 3. 密码修改功能
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
  <!-- 账号安全主容器 -->
  <div class="content-list">
    <!-- 页面标题 -->
    <div class="list-title">帐号安全</div>
    <!-- 内容区域 -->
    <div class="list-content">
      <div class="safe-view">
        <!-- 安全信息区域 -->
        <div class="safe-info-box">
          <!-- 安全等级展示 -->
          <div class="item flex-view">
            <div class="label">账号安全等级</div>
            <div class="right-box flex-center flex-view">
              <div class="safe-text">低风险</div>
              <progress max="3" class="safe-line" value="2">
              </progress>
            </div>
          </div>
          <!-- 手机绑定信息 -->
          <div class="item flex-view">
            <div class="label">绑定手机</div>
            <div class="right-box">
              <input class="input-dom" placeholder="请输入手机号">
              <a-button type="link" @click="handleBindMobile()">更换</a-button>
            </div>
          </div>
        </div>
        <!-- 密码修改区域 -->
        <div class="edit-pwd-box" style="display;">
          <div class="pwd-edit">
            <!-- 当前密码输入 -->
            <div class="item flex-view">
              <div class="label">当前密码</div>
              <div class="right-box">
                <a-input-password placeholder="输入当前密码" v-model:value="password"/>
              </div>
            </div>
            <!-- 新密码输入 -->
            <div class="item flex-view">
              <div class="label">新密码</div>
              <div class="right-box">
                <a-input-password placeholder="输入新密码" v-model:value="newPassword1"/>
              </div>
            </div>
            <!-- 确认新密码 -->
            <div class="item flex-view">
              <div class="label">确认新密码</div>
              <div class="right-box">
                <a-input-password placeholder="重复输入密码" v-model:value="newPassword2"/>
              </div>
            </div>
            <!-- 提交按钮 -->
            <div class="item flex-view">
              <div class="label">
              </div>
              <div class="right-box">
                <a-button type="primary" @click="handleUpdatePwd()">修改密码</a-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 导入所需的依赖和资源
 */
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { message } from "ant-design-vue";
import { updateUserPwdApi } from '/@/api/user';
import { useUserStore } from "/@/store";

/**
 * 获取状态和路由
 */
const router = useRouter();
const userStore = useUserStore();

/**
 * 响应式数据定义
 */
let password = ref('');         // 当前密码
let newPassword1 = ref('');     // 新密码
let newPassword2 = ref('');     // 确认新密码

/**
 * 处理绑定手机事件
 * 
 * @description 触发手机绑定流程（当前为开发中提示）
 */
const handleBindMobile = () => {
  message.info('功能开发中');
};

/**
 * 处理密码更新
 * 
 * @description 验证并提交密码更新请求
 * @throws {Error} 当输入验证失败或API请求失败时抛出错误
 */
const handleUpdatePwd = () => {
  // 验证输入不为空
  if (!password.value || !newPassword1.value || !newPassword2.value) {
    message.warn('不能为空');
    return;
  }
  
  // 验证两次输入的新密码一致
  if (newPassword1.value !== newPassword2.value) {
    message.warn('密码不一致');
    return;
  }

  // 提交密码更新请求
  let userId = userStore.user_id;
  updateUserPwdApi({
    userId: userId,
    password: password.value,
    newPassword: newPassword1.value,
  }).then(res => {
    message.success('修改成功');
  }).catch(err => {
    message.error(err.msg);
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
 * 安全视图样式
 * 设置整体布局和间距
 */
.safe-view {
  /**
   * 设置项样式
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
     * 居中对齐样式
     * 用于内容垂直居中
     */
    .flex-center {
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
    }

    /**
     * 安全等级文本样式
     * 设置警示红色和字体
     */
    .safe-text {
      color: #f62a2a;
      font-weight: 600;
      font-size: 14px;
      margin-right: 18px;
    }

    /**
     * 安全等级进度条样式
     * 设置背景、圆角和尺寸
     */
    .safe-line {
      background: #d3dce6;
      border-radius: 8px;
      width: 280px;
      height: 8px;
      overflow: hidden;
      color: #f6982a;
    }

    /**
     * 输入框样式
     * 设置背景、边框、尺寸和字体
     */
    .input-dom {
      background: #f8fafb;
      border-radius: 4px;
      width: 240px;
      height: 40px;
      line-height: 40px;
      font-size: 14px;
      color: #5f77a6;
      padding: 0 12px;
      margin-right: 16px;
    }

    /**
     * 更改按钮样式
     * 设置颜色和鼠标样式
     */
    .change-btn {
      color: #4684e2;
      font-size: 14px;
      border: none;
      outline: none;
      cursor: pointer;
    }

    /**
     * 微信文本样式
     * 设置颜色和字体
     */
    .wx-text {
      color: #5f77a6;
      font-size: 14px;
      margin-right: 16px;
    }

    /**
     * 修改密码按钮样式
     * 设置颜色和鼠标样式
     */
    .edit-pwd-btn {
      color: #4684e2;
      font-size: 14px;
      cursor: pointer;
    }
  }
}
</style>
