<!--
/**
 * 搜索结果内容组件
 * 
 * 该组件实现了电商平台的商品搜索结果展示功能，包括：
 * 1. 搜索关键词展示
 * 2. 商品列表展示
 * 3. 商品折扣计算与展示
 * 4. 商品分类与标签展示
 * 5. 商品评分展示
 * 6. 分页功能
 * 
 * 组件依赖：
 * - Vue 3.x
 * - Vue Router
 * - Vuex/Pinia
 * - Ant Design Vue
 * - Less 预处理器
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <!-- 搜索结果主容器 -->
  <div class="content-margin">
    <!-- 搜索关键词标题 -->
    <h1 class="search-name-box">{{ tData.keyword }}</h1>
    
    <!-- 搜索结果导航栏 -->
    <div class="search-tab-nav clearfix">
      <div class="tab-text">
        <span>与</span>
        <span class="strong">{{ tData.keyword }}</span>
        <span>相关的内容</span>
      </div>
    </div>
    
    <!-- 搜索结果内容区 -->
    <div class="content-list">
      <div class="thing-list">
        <!-- 加载中状态展示 -->
        <a-spin :spinning="tData.loading" style="min-height: 200px;">
          <!-- 商品列表网格 -->
          <div class="things flex-view">
            <!-- 单个商品项 -->
            <div class="thing-item item-column-4" v-for="item in tData.pageData" :key="item.id" @click="handleDetail(item)">
              <!-- 商品图片展示区 -->
              <div class="img-view">
                <img :src="item.cover" />
                <!-- 商品折扣标签 -->
                <div class="discount-badge" v-if="item.originalPrice && item.originalPrice > item.price">
                  {{ calculateDiscount(item.price, item.originalPrice) }}折
                </div>
              </div>
              
              <!-- 商品信息展示区 -->
              <div class="info-view">
                <!-- 商品名称 -->
                <h3 class="thing-name">{{ item.title }}</h3>
                
                <!-- 商品分类和标签信息 -->
                <div class="product-meta">
                  <span class="category" v-if="item.classification_title">{{ item.classification_title }}</span>
                  <div class="tags" v-if="item.tagList && item.tagList.length">
                    <span class="tag" v-for="tag in item.tagList" :key="tag.id">{{ tag.title }}</span>
                  </div>
                </div>
                
                <!-- 商品评分和销量 -->
                <div class="product-stats">
                  <span class="stars" v-if="item.score">
                    {{ '★'.repeat(Math.round(item.score || 0)) }}{{ '☆'.repeat(5 - Math.round(item.score || 0)) }}
                  </span>
                  <span v-if="item.score">{{ (item.score || 0).toFixed(1) }}分</span>
                  <span class="sales">{{ item.salesCount || 0 }}人已购</span>
                </div>
                
                <!-- 商品价格信息 -->
                <div class="price-container">
                  <span class="a-price-symbol">¥</span>
                  <span class="a-price">{{ item.price }}</span>
                  <span class="original-price" v-if="item.originalPrice && item.originalPrice > item.price">
                    ¥{{ item.originalPrice }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </a-spin>
        
        <!-- 分页控件 -->
        <div class="page-view">
          <a-pagination 
            v-model:current="tData.page" 
            size="small" 
            @change="changePage" 
            :hideOnSinglePage="true"
            :defaultPageSize="tData.pageSize" 
            :total="tData.total"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 导入Vue相关依赖
 */
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { listApi as listThingList } from '/@/api/thing';
import { BASE_URL } from '/@/store/constants';
import { useUserStore } from '/@/store';

/**
 * 初始化Vue相关实例
 */
const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

/**
 * 响应式数据定义
 * 包含搜索关键词、页面数据、加载状态、分页信息等
 */
const tData = reactive({
  loading: false,
  keyword: '',
  thingData: [],
  pageData: [],

  page: 1,
  total: 0,
  pageSize: 20,
});

/**
 * 组件挂载后执行
 * 初始化搜索功能
 */
onMounted(() => {
  search();
});

/**
 * 监听URL查询参数变化
 * 当路由查询参数改变时重新执行搜索
 */
watch(() => route.query, (newPath, oldPath) => {
  search();
}, {immediate: false});

/**
 * 计算商品折扣
 * 
 * @description 根据实际价格和原价计算折扣率
 * @param {number} price - 实际价格
 * @param {number} originalPrice - 原价
 * @returns {number} 折扣率，以"几折"的形式展示
 */
const calculateDiscount = (price, originalPrice) => {
  if (!originalPrice || originalPrice <= 0) return 10;
  const discount = (price / originalPrice * 10).toFixed(1);
  return discount;
};

/**
 * 执行搜索操作
 * 
 * @description 从路由参数获取搜索条件，并调用API获取结果
 */
const search = () => {
  // 优先使用关键词参数
  tData.keyword = route.query.keyword ? route.query.keyword.trim() : '';
  const categoryId = route.query.c; // 从URL中读取分类参数c
  const tag = route.query.tag; // 读取标签参数
  
  // 如果没有关键词但有分类ID，需要获取分类名称 - 仅用于显示
  if (!tData.keyword && categoryId) {
    if (route.query.category_name) {
      tData.keyword = route.query.category_name;
    } else {
      getCategoryName(categoryId).then(name => {
        tData.keyword = name || '所选分类';
      });
    }
  }
  
  // 初始化API请求参数 - 这里是关键修改
  const queryParams = {};
  
  // 仅当有搜索关键词且没有分类ID和标签ID时，才传keyword参数
  if (route.query.keyword && !categoryId && !tag) {
    queryParams.keyword = route.query.keyword.trim();
  }
  
  // 添加分类参数（如果存在）
  if (categoryId) {
    queryParams.c = categoryId; // 设置后端API的分类参数
    console.log('分类ID:', categoryId);
  }
  
  // 添加标签参数（如果存在）
  if (tag) {
    queryParams.tag = tag;
    console.log('标签ID:', tag);
  }
  
  console.log('最终API请求参数:', queryParams); // 调试日志
  getThingList(queryParams);
};

/**
 * 处理分页变更
 * 
 * @description 更新当前页码并重新计算显示的数据
 * @param {number} page - 目标页码
 */
const changePage = (page) => {
  tData.page = page;
  let start = (tData.page - 1) * tData.pageSize;
  tData.pageData = tData.thingData.slice(start, start + tData.pageSize);
  console.log('第' + tData.page + '页');
};

/**
 * 处理商品点击事件
 * 
 * @description 跳转到商品详情页
 * @param {Object} item - 商品对象
 */
const handleDetail = (item) => {
  // 跳转新页面
  let text = router.resolve({name: 'detail', query: {id: item.id}});
  window.open(text.href, '_blank');
};

/**
 * 获取商品列表
 * 
 * @description 调用API获取商品数据并处理结果
 * @param {Object} data - API请求参数
 * @throws {Error} 当API请求失败时抛出错误
 */
const getThingList = (data) => {
  tData.loading = true;
  console.log('API请求参数:', data);
  listThingList(data).then(res => {
    console.log('返回数据:', res.data.length, '条记录');
    
    // 处理图片路径
    res.data.forEach((item, index) => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
      }
    });
    
    // 更新数据和分页信息
    tData.thingData = res.data;
    tData.total = tData.thingData.length;
    changePage(1);
    tData.loading = false;
  }).catch(err => {
    console.log('请求失败:', err);
    tData.loading = false;
  });
};

// 添加获取分类名称的方法
const getCategoryName = async (categoryId) => {
  // 调用获取分类详情的API
  try {
    // 示例: const res = await getCategoryApi(categoryId);
    // return res.data.name;
    return '美妆护肤'; // 临时返回固定值作为示例
  } catch (error) {
    console.error('获取分类名称失败:', error);
    return '分类';
  }
};
</script>
<style scoped lang="less">
/**
 * 内容区域外边距样式
 * 确保内容区域不被顶部导航栏遮挡
 */
.content-margin {
  margin: 156px 0 100px;
}

/**
 * 分页控件容器样式
 */
.page-view {
  width: 100%;
  text-align: center;
  margin-top: 48px;
}

/**
 * 搜索关键词标题样式
 * 固定在顶部的搜索关键词展示区
 */
.search-name-box {
  background: #f5f9fb;
  height: 100px;
  line-height: 100px;
  font-size: 20px;
  color: #152844;
  text-align: center;
  position: fixed;
  top: 56px;
  left: 0;
  z-index: 1;
  width: calc(100% - 8px);
}

/**
 * 搜索结果导航栏样式
 */
.search-tab-nav {
  position: relative;
  padding: 24px 0 16px;
  text-align: center;

  /**
   * 导航栏文本样式
   */
  .tab-text {
    float: left;
    color: #5f77a6;
    font-size: 14px;
  }

  /**
   * 强调文本样式
   * 用于高亮显示搜索关键词
   */
  .strong {
    color: #152844;
    font-weight: 600;
    margin: 0 4px;
  }
}

/**
 * 商品列表容器样式
 * 设置为弹性布局，实现响应式网格
 */
.things {
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
}

/**
 * 弹性布局视图样式
 */
.flex-view {
  display: flex;
}

/**
 * 单个商品项样式
 * 设置尺寸、边距、动画效果等
 */
.thing-item {
  min-width: 255px;
  max-width: 255px;
  position: relative;
  flex: 1;
  margin-right: 20px;
  height: fit-content;
  overflow: hidden;
  margin-top: 26px;
  margin-bottom: 36px;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  border-radius: 8px;
  
  /**
   * 商品项悬浮效果
   * 向上轻微移动并添加阴影
   */
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }

  /**
   * 商品图片容器样式
   */
  .img-view {
    //text-align: center;
    height: 200px;
    width: 255px;
    position: relative;
    overflow: hidden;

    /**
     * 商品图片样式
     */
    img {
      height: 200px;
      width: 186px;
      margin: 0 auto;
      background-size: contain;
      transition: transform 0.3s;
    }
    
    /**
     * 折扣标签样式
     */
    .discount-badge {
      position: absolute;
      top: 10px;
      right: 10px;
      background-color: #ff4d4f;
      color: white;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: bold;
    }
  }

  /**
   * 商品信息容器样式
   */
  .info-view {
    //background: #f6f9fb;
    overflow: hidden;
    padding: 0 16px;

    /**
     * 商品名称样式
     * 使用省略号处理过长的名称
     */
    .thing-name {
      line-height: 32px;
      margin-top: 12px;
      color: #0F1111 !important;
      font-size: 15px !important;
      font-weight: 400 !important;
      font-style: normal !important;
      text-transform: none !important;
      text-decoration: none !important;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    /**
     * 商品元数据样式
     * 包含分类和标签信息
     */
    .product-meta {
      margin-top: 5px;
      font-size: 13px;
      
      /**
       * 分类标签样式
       */
      .category {
        display: inline-block;
        background: #e6f7ff;
        color: #1890ff;
        padding: 2px 8px;
        border-radius: 4px;
        margin-right: 5px;
        font-size: 12px;
      }
      
      /**
       * 标签容器样式
       */
      .tags {
        margin-top: 5px;
        display: flex;
        flex-wrap: wrap;
        gap: 5px;
        
        /**
         * 单个标签样式
         */
        .tag {
          background: #f0f0f0;
          color: #666;
          padding: 1px 6px;
          border-radius: 4px;
          font-size: 11px;
        }
      }
    }
    
    /**
     * 商品统计信息样式
     * 包含评分和销量
     */
    .product-stats {
      display: flex;
      align-items: center;
      margin-top: 5px;
      font-size: 12px;
      color: #666;
      
      /**
       * 星级评分样式
       */
      .stars {
        color: #fadb14;
        margin-right: 5px;
      }
      
      /**
       * 销量信息样式
       */
      .sales {
        margin-left: auto;
      }
    }

    /**
     * 价格容器样式
     */
    .price-container {
      display: flex;
      align-items: flex-end;
      margin-top: 5px;
      
      /**
       * 价格符号样式
       */
      .a-price-symbol {
        color: #ff7b31;
        font-size: 14px;
        line-height: 14px;
      }
      
      /**
       * 价格数值样式
       */
      .a-price {
        color: #ff7b31;
        font-size: 20px;
        line-height: 20px;
      }
      
      /**
       * 原价样式
       * 使用删除线表示
       */
      .original-price {
        margin-left: 8px;
        color: #999;
        font-size: 13px;
        text-decoration: line-through;
      }
    }
  }
}

/**
 * 价格符号样式
 * 设置位置和大小
 */
.a-price-symbol {
  top: -0.5em;
  font-size: 12px;
}

/**
 * 价格值样式
 * 设置颜色和大小
 */
.a-price {
  color: #0F1111;
  font-size: 21px;
}

</style>
