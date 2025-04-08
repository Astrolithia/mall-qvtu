<template>
  <div class="content-margin">
    <h1 class="search-name-box">{{ tData.keyword }}</h1>
    <div class="search-tab-nav clearfix">
      <div class="tab-text">
        <span>与</span>
        <span class="strong">{{ tData.keyword }}</span>
        <span>相关的内容</span>
      </div>
    </div>
    <div class="content-list">
      <div class="thing-list">

        <a-spin :spinning="tData.loading" style="min-height: 200px;">
          <div class="things flex-view">
            <div class="thing-item item-column-4" v-for="item in tData.pageData" :key="item.id" @click="handleDetail(item)">
              <div class="img-view">
                <img :src="item.cover" />
                <div class="discount-badge" v-if="item.originalPrice && item.originalPrice > item.price">
                  {{ calculateDiscount(item.price, item.originalPrice) }}折
                </div>
              </div>
              <div class="info-view">
                <h3 class="thing-name">{{ item.title }}</h3>
                <div class="product-meta">
                  <span class="category" v-if="item.classification_title">{{ item.classification_title }}</span>
                  <div class="tags" v-if="item.tagList && item.tagList.length">
                    <span class="tag" v-for="tag in item.tagList" :key="tag.id">{{ tag.title }}</span>
                  </div>
                </div>
                <div class="product-stats">
                  <span class="stars" v-if="item.score">
                    {{ '★'.repeat(Math.round(item.score || 0)) }}{{ '☆'.repeat(5 - Math.round(item.score || 0)) }}
                  </span>
                  <span v-if="item.score">{{ (item.score || 0).toFixed(1) }}分</span>
                  <span class="sales">{{ item.salesCount || 0 }}人已购</span>
                </div>
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
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { listApi as listThingList } from '/@/api/thing';
import { BASE_URL } from '/@/store/constants';
import { useUserStore } from '/@/store';

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

const tData = reactive({
  loading: false,
  keyword: '',
  thingData: [],
  pageData: [],

  page: 1,
  total: 0,
  pageSize: 20,
});

onMounted(() => {
  search();
});

// 监听query参数
watch(() => route.query, (newPath, oldPath) => {
  search();
}, {immediate: false});

// 计算折扣
const calculateDiscount = (price, originalPrice) => {
  if (!originalPrice || originalPrice <= 0) return 10;
  const discount = (price / originalPrice * 10).toFixed(1);
  return discount;
};

const search = () => {
  tData.keyword = route.query.keyword ? route.query.keyword.trim() : '';
  const categoryId = route.query.c; // 从URL中读取分类参数c
  const tag = route.query.tag; // 读取标签参数
  
  console.log('URL参数:', route.query); // 调试日志
  
  const queryParams = { keyword: tData.keyword };
  
  if (categoryId) {
    queryParams.c = categoryId; // 设置后端API的分类参数
    console.log('分类ID:', categoryId);
  }
  
  if (tag) {
    queryParams.tag = tag;
    console.log('标签ID:', tag);
  }
  
  getThingList(queryParams);
};

// 分页事件
const changePage = (page) => {
  tData.page = page;
  let start = (tData.page - 1) * tData.pageSize;
  tData.pageData = tData.thingData.slice(start, start + tData.pageSize);
  console.log('第' + tData.page + '页');
};

const handleDetail = (item) => {
  // 跳转新页面
  let text = router.resolve({name: 'detail', query: {id: item.id}});
  window.open(text.href, '_blank');
};

const getThingList = (data) => {
  tData.loading = true;
  console.log('API请求参数:', data);
  listThingList(data).then(res => {
    console.log('返回数据:', res.data.length, '条记录');
    res.data.forEach((item, index) => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover;
      }
    });
    tData.thingData = res.data;
    tData.total = tData.thingData.length;
    changePage(1);
    tData.loading = false;
  }).catch(err => {
    console.log('请求失败:', err);
    tData.loading = false;
  });
};
</script>
<style scoped lang="less">
.content-margin {
  margin: 156px 0 100px;
}

.page-view {
  width: 100%;
  text-align: center;
  margin-top: 48px;
}

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

.search-tab-nav {
  position: relative;
  padding: 24px 0 16px;
  text-align: center;

  .tab-text {
    float: left;
    color: #5f77a6;
    font-size: 14px;
  }

  .strong {
    color: #152844;
    font-weight: 600;
    margin: 0 4px;
  }
}

.things {
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
}

.flex-view {
  display: flex;
}

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
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }

  .img-view {
    //text-align: center;
    height: 200px;
    width: 255px;
    position: relative;
    overflow: hidden;

    img {
      height: 200px;
      width: 186px;
      margin: 0 auto;
      background-size: contain;
      transition: transform 0.3s;
    }
    
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

  .info-view {
    //background: #f6f9fb;
    overflow: hidden;
    padding: 0 16px;

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
    
    .product-meta {
      margin-top: 5px;
      font-size: 13px;
      
      .category {
        display: inline-block;
        background: #e6f7ff;
        color: #1890ff;
        padding: 2px 8px;
        border-radius: 4px;
        margin-right: 5px;
        font-size: 12px;
      }
      
      .tags {
        margin-top: 5px;
        display: flex;
        flex-wrap: wrap;
        gap: 5px;
        
        .tag {
          background: #f0f0f0;
          color: #666;
          padding: 1px 6px;
          border-radius: 4px;
          font-size: 11px;
        }
      }
    }
    
    .product-stats {
      display: flex;
      align-items: center;
      margin-top: 5px;
      font-size: 12px;
      color: #666;
      
      .stars {
        color: #fadb14;
        margin-right: 5px;
      }
      
      .sales {
        margin-left: auto;
      }
    }

    .price-container {
      display: flex;
      align-items: flex-end;
      margin-top: 5px;
      
      .a-price-symbol {
        color: #ff7b31;
        font-size: 14px;
        line-height: 14px;
      }
      
      .a-price {
        color: #ff7b31;
        font-size: 20px;
        line-height: 20px;
      }
      
      .original-price {
        margin-left: 8px;
        color: #999;
        font-size: 13px;
        text-decoration: line-through;
      }
    }
  }
}

.a-price-symbol {
  top: -0.5em;
  font-size: 12px;
}

.a-price {
  color: #0F1111;
  font-size: 21px;
}

</style>
