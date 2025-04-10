<!--
/**
 * 收藏商品组件
 * 
 * 该组件实现了电商平台的用户收藏商品管理功能，包括：
 * 1. 收藏商品列表展示
 * 2. 移除收藏商品
 * 3. 跳转至商品详情页
 * 4. 作者与译者信息展示
 * 
 * 组件依赖：
 * - Vue 3.x
 * - Ant Design Vue
 * - Vuex/Pinia
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */
-->

<template>
  <!-- 收藏内容主容器 -->
  <div class="content-list">
    <!-- 页面标题 -->
    <div class="list-title">我的收藏</div>
    <!-- 标签页导航区域 -->
    <div role="tablist" class="list-tabs-view flex-view">
    </div>
    <!-- 收藏内容列表 -->
    <div class="list-content">
      <div class="collect-thing-view">
        <!-- 收藏商品网格 -->
        <div class="thing-list flex-view">
          <!-- 单个收藏商品项 -->
          <div class="thing-item item-column-3" v-for="(item,index) in pageData.collectData" :key="index">
            <!-- 移除按钮 -->
            <div class="remove" @click="handleRemove(item)">移出</div>
            <!-- 商品图片区域 -->
            <div class="img-view" @click="handleClickItem(item)">
              <img :src="item.cover">
            </div>
            <!-- 商品信息区域 -->
            <div class="info-view">
              <!-- 商品名称 -->
              <h3 class="thing-name">{{item.title}}</h3>
              <!-- 作者信息（如果存在） -->
              <p class="authors" v-if="item.author">{{item.author}}（作者)</p>
              <!-- 译者信息（如果存在） -->
              <p class="translators" v-if="item.translator">{{item.translator}}（译者）</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 导入Vue相关依赖
 */
import {message} from 'ant-design-vue';
import {userCollectListApi, unCollectApi} from '/@/api/thingCollect'
import {BASE_URL} from "/@/store/constants";
import {useUserStore} from "/@/store";
import { useRouter } from 'vue-router';
import { reactive, onMounted } from 'vue';

/**
 * 初始化Vue相关实例
 */
const router = useRouter();
const userStore = useUserStore();

/**
 * 响应式数据定义
 * 包含收藏商品列表
 */
const pageData = reactive({
  collectData: []
})

/**
 * 组件挂载后执行
 * 获取用户收藏的商品列表
 */
onMounted(()=>{
  getCollectThingList()
})

/**
 * 处理商品点击事件
 * 
 * @description 在新标签页中打开商品详情页
 * @param {Object} record - 商品数据对象
 */
const handleClickItem =(record) =>{
  let text = router.resolve({name: 'detail', query: {id: record.id}})
  window.open(text.href, '_blank')
}

/**
 * 处理移除收藏事件
 * 
 * @description 将商品从收藏列表中移除
 * @param {Object} record - 要移除的商品对象
 * @throws {Error} 当API请求失败时抛出错误
 */
const handleRemove =(record)=> {
  let username = userStore.user_name
  unCollectApi({id: record.id}).then(res => {
    message.success('移除成功')
    getCollectThingList()
  }).catch(err => {
    console.log(err)
  })
}

/**
 * 获取收藏商品列表
 * 
 * @description 从API获取当前用户的收藏商品列表
 * @throws {Error} 当API请求失败时抛出错误
 */
const getCollectThingList =()=> {
  let userId = userStore.user_id
  userCollectListApi({userId: userId}).then(res => {
    // 处理商品图片路径
    res.data.forEach(item => {
      item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover
    })
    console.log(res.data)
    pageData.collectData = res.data
  }).catch(err => {
    console.log(err.msg)
  })
}

</script>
<style scoped lang="less">
/**
 * 弹性布局容器样式
 * 提供多种浏览器兼容性的Flex布局
 */
.flex-view {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

/**
 * 内容列表容器样式
 * 设置弹性布局和基础样式
 */
.content-list {
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;

  /**
   * 列表标题样式
   * 设置字体、颜色和边距
   */
  .list-title {
    color: #152844;
    font-weight: 600;
    font-size: 18px;
    line-height: 24px;
    height: 24px;
    margin-bottom: 4px;
  }

  /**
   * 标签页导航样式
   * 设置底部边框和高度
   */
  .list-tabs-view {
    position: relative;
    border-bottom: 1px solid #cedce4;
    height: 12px;
    line-height: 42px;
  }
}

/**
 * 商品列表样式
 * 设置弹性换行和对齐方式
 */
.thing-list {
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
  -webkit-box-pack: start;
  -ms-flex-pack: start;
  justify-content: flex-start;

  /**
   * 单个商品项样式
   * 设置基础布局、尺寸和边距
   */
  .thing-item {
    position: relative;
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    margin-right: 20px;
    min-width: 255px;
    max-width: 255px;
    height: fit-content;
    border-radius: 4px;
    overflow: hidden;
    margin-top: 16px;
    cursor: pointer;

    /**
     * 移除按钮样式
     * 设置位置、尺寸和样式
     */
    .remove {
      position: absolute;
      right: 8px;
      top: 8px;
      width: 48px;
      height: 20px;
      text-align: center;
      line-height: 20px;
      color: #fff;
      background: #a1adc5;
      border-radius: 32px;
      cursor: pointer;
    }

    /**
     * 商品图片容器样式
     * 设置背景色、尺寸和内边距
     */
    .img-view {
      background: #eaf1f5;
      font-size: 0;
      text-align: center;
      height: 156px;
      padding: 8px 0;

      /**
       * 商品图片样式
       * 设置尺寸、边距和圆角
       */
      img {
        max-width: 100%;
        height: 100%;
        display: block;
        margin: 0 auto;
        border-radius: 4px;
        -webkit-box-sizing: border-box;
        box-sizing: border-box;
      }
    }

    /**
     * 商品信息容器样式
     * 设置背景色、尺寸和内边距
     */
    .info-view {
      background: #f6f9fb;
      text-align: center;
      height: 108px;
      overflow: hidden;
      padding: 0 16px;

      /**
       * 商品名称样式
       * 设置字体、颜色和多行文本溢出省略
       */
      h3 {
        color: #1c355a;
        font-weight: 500;
        font-size: 16px;
        line-height: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        margin: 12px 0 8px;
      }

      /**
       * 作者和译者信息样式
       * 设置字体、颜色和单行文本溢出省略
       */
      .authors, .translators {
        color: #6f6f6f;
        font-size: 12px;
        line-height: 14px;
        margin-top: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}
</style>
