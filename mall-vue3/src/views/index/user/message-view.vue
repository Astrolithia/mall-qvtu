<!--
/**
 * 消息中心组件
 * 
 * 该组件实现了电商平台的用户消息中心功能，包括：
 * 1. 系统消息列表展示
 * 2. 消息内容查看
 * 3. 消息发送时间显示
 * 4. 加载状态管理
 * 
 * 组件依赖：
 * - Vue 3.x
 * - Ant Design Vue
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */
-->

<template>
  <!-- 消息中心主容器 -->
  <div class="content-list">
    <!-- 页面标题 -->
    <div class="list-title">我的消息</div>
    
    <!-- 消息列表加载状态 -->
    <a-spin :spinning="loading" style="min-height: 200px;">
      <!-- 消息列表内容区域 -->
      <div class="list-content">
        <div class="notification-view">
          <!-- 消息列表 -->
          <div class="list">
            <!-- 单个消息项 -->
            <div class="notification-item flex-view" v-for="item in msgData">
              <!-- 消息图标 -->
              <img
                src="data:image/png;base64,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"
                class="avatar">
              
              <!-- 消息内容区 -->
              <div class="content-box">
                <!-- 消息标题和时间 -->
                <div class="header">
                  <span class="title-txt">{{item.title}}</span>
                  <span class="time">{{ item.create_time }}</span>
                </div>
                
                <!-- 消息发送者（隐藏） -->
                <div class="head-text">
                  <span class="name" style="display: none;">
                  </span>
                </div>
                
                <!-- 消息内容 -->
                <div class="content">
                  <p>{{ item.content }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup>
/**
 * 导入Vue相关依赖
 */
import { ref, onMounted } from 'vue';
import { listApi } from '/@/api/notice'

/**
 * 响应式数据定义
 * 包含加载状态和消息数据
 */
let loading = ref(false)
let msgData = ref([])

/**
 * 组件挂载后执行
 * 获取用户消息列表
 */
onMounted(()=>{
  getMessageList()
})

/**
 * 获取消息列表
 * 
 * @description 从API获取用户消息列表数据
 * @throws {Error} 当API请求失败时抛出错误
 */
const getMessageList =()=> {
  loading.value = true
  listApi().then(res => {
    msgData.value = res.data
    loading.value = false
  }).catch(err => {
    console.log(err)
    loading.value = false
  })
}

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
 * 消息项样式
 * 设置内边距和布局
 */
.notification-item {
  padding-top: 16px;

  /**
   * 头像样式
   * 设置尺寸和圆形边框
   */
  .avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    margin-right: 8px;
  }

  /**
   * 内容容器样式
   * 设置弹性布局和底部边框
   */
  .content-box {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    border-bottom: 1px dashed #e9e9e9;
    padding: 4px 0 16px;
  }

  /**
   * 消息标题区样式
   * 设置下边距
   */
  .header {
    margin-bottom: 12px;
  }

  /**
   * 标题文本样式
   * 设置颜色、字重和字号
   */
  .title-txt {
    color: #315c9e;
    font-weight: 500;
    font-size: 14px;
  }

  /**
   * 时间样式
   * 设置颜色、字号和左边距
   */
  .time {
    color: #a1adc5;
    font-size: 14px;
    margin-left: 16px;
  }

  /**
   * 发送者信息样式
   * 设置颜色、字重和字号
   */
  .head-text {
    color: #152844;
    font-weight: 500;
    font-size: 14px;
    line-height: 22px;

    /**
     * 发送者名称样式
     * 设置右边距
     */
    .name {
      margin-right: 8px;
    }
  }

  /**
   * 消息内容样式
   * 设置上边距、颜色和字号
   */
  .content {
    margin-top: 4px;
    color: #484848;
    font-size: 14px;
    line-height: 22px;
  }
}
</style>
