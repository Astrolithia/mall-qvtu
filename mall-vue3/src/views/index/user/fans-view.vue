<!--
/**
 * 粉丝列表组件
 * 
 * 该组件实现了电商平台的用户粉丝管理功能，包括：
 * 1. 粉丝列表展示
 * 2. 粉丝信息展示
 * 
 * 组件依赖：
 * - Vue 3.x
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */
-->

<template>
  <!-- 粉丝页面主容器 -->
  <div class="content-list">
    <!-- 页面标题 -->
    <div class="list-title">我的粉丝</div>
    
    <!-- 粉丝列表容器 -->
    <div class="my-follow-view">
      <!-- 粉丝列表 -->
      <div class="follow-list flex-view">
        <!-- 单个粉丝项 -->
        <div class="follow-item-view flex-view" v-for="item in fansData">
          <!-- 粉丝头像 -->
          <img src="" alt="">
          
          <!-- 粉丝信息 -->
          <div class="infos flex-view">
            <!-- 粉丝名称 -->
            <h3 class="name">Python_C</h3>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
/**
 * 粉丝视图组件
 * 
 * @description 显示用户的粉丝列表
 */
export default {
  /**
   * 组件名称
   */
  name: 'FansView',
  
  /**
   * 组件数据
   * 
   * @returns {Object} 组件响应式数据
   */
  data () {
    return {
      /**
       * 粉丝数据数组
       * 注: 当前为测试数据，实际应用中需替换为真实数据
       */
      fansData: ['','','','','',]
    }
  }
}
</script>
<style scoped lang="less">
/**
 * 弹性布局容器样式
 */
.flex-view {
  display: flex;
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
 * 粉丝视图样式
 * 设置布局和外边距
 */
.my-follow-view {
  -webkit-box-pack: justify;
  -ms-flex-pack: justify;
  justify-content: space-between;
  margin-top: 16px;

  /**
   * 粉丝列表样式
   * 设置弹性布局和换行
   */
  .follow-list {
    -ms-flex-wrap: wrap;
    flex-wrap: wrap;

    /**
     * 单个粉丝项样式
     * 设置宽度、外边距和鼠标样式
     */
    .follow-item-view {
      padding-right: 10px;
      overflow: hidden;
      margin-bottom: 24px;
      width: 33.3%;
      cursor: pointer;

      /**
       * 粉丝头像样式
       * 设置尺寸和圆形边框
       */
      img {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        margin-right: 12px;
      }

      /**
       * 粉丝信息容器样式
       * 设置溢出处理和对齐方式
       */
      .infos {
        overflow: hidden;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
        -ms-flex-wrap: wrap;
        flex-wrap: wrap;
      }

      /**
       * 粉丝名称样式
       * 设置字体、颜色和溢出处理
       */
      h3 {
        color: #141a24;
        font-weight: 600;
        font-size: 14px;
        line-height: 22px;
        height: 22px;
        text-overflow: ellipsis;
        overflow: hidden;
        width: 100%;
      }

      /**
       * 操作按钮样式
       * 设置鼠标样式、边框和颜色
       */
      button {
        cursor: pointer;
        margin-top: 4px;
        border: none;
        outline: none;
        color: #f62a2a;
        font-size: 14px;
        line-height: 22px;
        height: 22px;
      }
    }
  }
}
</style>
