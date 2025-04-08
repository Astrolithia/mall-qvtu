<template>
  <div class="portal">
    <Header />
    <div class="hero-banner" ref="heroBanner">
      <div class="parallax-layer background" ref="parallaxBg"></div>
      
      <div class="banner-carousel-container">
        <div v-if="bannerData && bannerData.length > 0" class="banner-carousel">
          <a-carousel autoplay class="carousel" :dots="true" :effect="'fade'" :autoplaySpeed="3000">
            <div v-for="(item, index) in bannerData" :key="index" class="carousel-item">
              <img :src="getBannerImageUrl(item.image)" alt="Banner" />
              <div class="carousel-caption">
                <h3>{{ item.title || '精选商品' }}</h3>
                <p>{{ item.description || '发现更多精彩内容' }}</p>
              </div>
            </div>
          </a-carousel>
        </div>
        <div v-else class="banner-carousel">
          <div class="carousel-placeholder">
            <a-spin tip="加载中..." />
          </div>
        </div>
      </div>
      
      <div class="banner-overlay">
        <div class="banner-content">
          <div class="search-container">
            <div class="banner-search">
              <input placeholder="输入内容搜索，多个关键词..." ref="bannerKeywordRef" @keyup.enter="search" />
              <button class="search-btn" @click="search">
                <i class="search-icon"></i>
                搜索
              </button>
            </div>
            <div class="hot-tags">
              <span class="hot-tag-title">热门搜索：</span>
              <a href="#" class="hot-tag">图书</a>
              <a href="#" class="hot-tag">电子</a>
              <a href="#" class="hot-tag">计算机</a>
              <a href="#" class="hot-tag">手机</a>
              <a href="#" class="hot-tag">文学小说</a>
            </div>
          </div>
        </div>
      </div>
      
      <div class="scroll-indicator">
        <div class="mouse">
          <div class="wheel"></div>
        </div>
        <div class="arrow">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
    </div>
    
    <div class="content-wrapper">
      <!-- 内容区域 -->
      <Content />
      
      <!-- 广告展示区域 - 横幅式 -->
      <div class="banner-ad-section" v-if="adData && adData.length > 0">
        <div class="banner-ad-container">
          <div class="banner-ad-wrapper">
            <a-carousel autoplay arrows class="ad-carousel" :dots="false" :autoplaySpeed="5000">
              <div v-for="(item, index) in adData" :key="index" class="banner-ad-item">
                <a :href="item.link || '#'" target="_blank">
                  <div class="ad-badge">广告</div>
                  <img :src="getAdImageUrl(item.image)" :alt="'促销活动' + (index + 1)" />
                </a>
              </div>
            </a-carousel>
          </div>
        </div>
      </div>
    </div>
    
    <Footer />
  </div>
</template>

<script setup>
import Header from '/@/views/index/components/header.vue';
import Footer from '/@/views/index/components/footer.vue';
import Content from '/@/views/index/components/content.vue';
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { listApi as listAdApi } from '/@/api/ad';
import { listApi as listBannerApi } from '/@/api/banner';
import { BASE_URL } from '/@/store/constants';

const router = useRouter();
const route = useRoute();
const bannerKeywordRef = ref(null);
const bannerData = ref([]);
const adData = ref([]);
const heroBanner = ref(null);
const parallaxBg = ref(null);

const getBannerImageUrl = (image) => {
  return `${BASE_URL}/api/staticfiles/banner/${image}`;
};

const getAdImageUrl = (image) => {
  return `${BASE_URL}/api/staticfiles/image/${image}`;
};

// 获取轮播图数据
const getBannerData = () => {
  listBannerApi({}).then(res => {
    if (res.code === 200 && res.data && res.data.length > 0) {
      bannerData.value = res.data;
      console.log('轮播图数据:', bannerData.value);
    }
  }).catch(err => {
    console.error('获取轮播图数据失败:', err);
  });
};

// 获取广告数据
const getAdData = () => {
  listAdApi({}).then(res => {
    if (res.code === 200 && res.data && res.data.length > 0) {
      adData.value = res.data.slice(0, 3); // 最多展示3个广告
      console.log('广告数据:', adData.value);
    }
  }).catch(err => {
    console.error('获取广告数据失败:', err);
  });
};

// 视差滚动效果
const handleScroll = () => {
  if (!heroBanner.value || !parallaxBg.value) return;
  
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  const bannerHeight = heroBanner.value.offsetHeight;
  
  if (scrollTop <= bannerHeight) {
    const scrollPercentage = scrollTop / bannerHeight;
    parallaxBg.value.style.transform = `translateY(${scrollTop * 0.5}px)`;
    heroBanner.value.style.opacity = 1 - scrollPercentage * 1.5;
  }
};

onMounted(() => {
  getBannerData();
  getAdData();
  window.addEventListener('scroll', handleScroll);
  
  // 添加背景动画
  if (parallaxBg.value) {
    const moveBg = () => {
      parallaxBg.value.style.backgroundPosition = `${Math.random() * 100}% ${Math.random() * 100}%`;
    };
    
    setInterval(moveBg, 10000);
  }
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

const search = () => {
  const keyword = bannerKeywordRef.value.value;
  if (!keyword) return;
  
  if (route.name === 'search') {
    router.push({name: 'search', query: {keyword: keyword}});
  } else {
    let text = router.resolve({name: 'search', query: {keyword: keyword}});
    window.open(text.href, '_blank');
  }
};
</script>

<style scoped lang="less">
.portal {
  background-color: #f5f7fa;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}

.hero-banner {
  height: 80vh;
  max-height: 650px;
  min-height: 450px;
  margin-top: 70px;
  position: relative;
  overflow: hidden;
  
  .parallax-layer {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 120%;
    z-index: 0;
    
    &.background {
      background: radial-gradient(circle at 20% 20%, rgba(74, 138, 244, 0.2), transparent 40%),
                 radial-gradient(circle at 80% 60%, rgba(122, 90, 248, 0.2), transparent 40%);
      background-size: 200% 200%;
      transition: background-position 10s ease;
    }
  }
  
  .banner-carousel-container {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 1;
    
    .banner-carousel {
      width: 100%;
      height: 100%;
      
      .carousel {
        width: 100%;
        height: 100%;
      }
      
      .carousel-placeholder {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.05);
      }
    }
    
    :deep(.ant-carousel) {
      height: 100%;
    }
    
    :deep(.slick-slider),
    :deep(.slick-list),
    :deep(.slick-track) {
      height: 100%;
    }
    
    :deep(.slick-slide) {
      height: 100%;
      
      & > div {
        height: 100%;
      }
    }
    
    :deep(.slick-dots) {
      bottom: 20px;
      
      li {
        button {
          background: rgba(255, 255, 255, 0.7);
          height: 8px;
          border-radius: 4px;
          transition: all 0.3s;
        }
        
        &.slick-active button {
          background: white;
          width: 16px;
        }
      }
    }
    
    .carousel-item {
      height: 100%;
      width: 100%;
      position: relative;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        object-position: center;
        transition: transform 8s ease;
        transform: scale(1.05);
      }
      
      .carousel-caption {
        position: absolute;
        bottom: 60px;
        right: 0;
        width: 40%;
        padding: 20px;
        background: linear-gradient(to right, transparent, rgba(0, 0, 0, 0.5));
        color: white;
        text-align: right;
        opacity: 0;
        transform: translateX(20px);
        transition: all 0.8s ease 0.2s;
        
        h3 {
          font-size: 24px;
          font-weight: 600;
          margin-bottom: 8px;
        }
        
        p {
          font-size: 16px;
          opacity: 0.9;
        }
      }
      
      &.slick-active {
        img {
          transform: scale(1);
        }
        
        .carousel-caption {
          opacity: 1;
          transform: translateX(0);
        }
      }
    }
  }
  
  .banner-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 2;
    background: linear-gradient(90deg, rgba(0, 0, 0, 0.5) 0%, rgba(0, 0, 0, 0.3) 50%, transparent 100%);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .banner-content {
    width: 70%;
    max-width: 900px;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 0 20px;
    color: white;
    text-align: center;
    transform: translateY(-30px);
    opacity: 0;
    animation: fadeInUp 1s forwards 0.5s;
    
    .search-container {
      max-width: 650px;
      width: 100%;
      
      .banner-search {
        display: flex;
        height: 54px;
        width: 100%;
        position: relative;
        background: rgba(255, 255, 255, 0.9);
        border-radius: 27px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        overflow: hidden;
        transition: all 0.3s;
        
        &:hover, &:focus-within {
          background: white;
          box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
          transform: translateY(-2px);
        }
        
        input {
          flex: 1;
          height: 100%;
          border: none;
          padding: 0 24px;
          font-size: 16px;
          outline: none;
          color: #333;
          background: transparent;
          
          &::placeholder {
            color: #999;
          }
        }
        
        .search-btn {
          width: 140px;
          height: 100%;
          border: none;
          border-radius: 0 27px 27px 0;
          background: linear-gradient(135deg, #4684e2, #7a5af8);
          color: white;
          font-size: 16px;
          font-weight: 500;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.3s;
          
          &:hover {
            background: linear-gradient(135deg, #3670c5, #6345e0);
          }
          
          .search-icon {
            width: 20px;
            height: 20px;
            margin-right: 8px;
            background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/></svg>');
            background-repeat: no-repeat;
            background-position: center;
            background-size: contain;
          }
        }
      }
      
      .hot-tags {
        margin-top: 16px;
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 10px;
        
        .hot-tag-title {
          color: rgba(255, 255, 255, 0.8);
          font-size: 14px;
        }
        
        .hot-tag {
          color: white;
          background: rgba(255, 255, 255, 0.2);
          padding: 5px 12px;
          border-radius: 15px;
          font-size: 13px;
          text-decoration: none;
          transition: all 0.3s;
          backdrop-filter: blur(5px);
          
          &:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: translateY(-2px);
          }
        }
      }
    }
  }
  
  .scroll-indicator {
    position: absolute;
    bottom: 30px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 10;
    display: flex;
    flex-direction: column;
    align-items: center;
    color: white;
    
    .mouse {
      width: 30px;
      height: 50px;
      border: 2px solid white;
      border-radius: 20px;
      position: relative;
      
      .wheel {
        position: absolute;
        top: 10px;
        left: 50%;
        width: 4px;
        height: 8px;
        margin-left: -2px;
        background: white;
        border-radius: 2px;
        animation: scroll 2s infinite;
      }
    }
    
    .arrow {
      margin-top: 15px;
      
      span {
        display: block;
        width: 10px;
        height: 10px;
        border-bottom: 2px solid white;
        border-right: 2px solid white;
        transform: rotate(45deg);
        margin: -5px auto;
        animation: arrow 2s infinite;
        
        &:nth-child(2) {
          animation-delay: -0.2s;
        }
        
        &:nth-child(3) {
          animation-delay: -0.4s;
        }
      }
    }
  }
}

.content-wrapper {
  position: relative;
  z-index: 10;
  margin-top: -50px;
  padding-top: 50px;
  background: #f5f7fa;
  border-radius: 30px 30px 0 0;
  box-shadow: 0 -10px 30px rgba(0, 0, 0, 0.05);
}

/* 广告区样式 */
.banner-ad-section {
  padding: 0 0 30px;
  
  .banner-ad-container {
    display: flex;
    justify-content: center;
    
    .banner-ad-wrapper {
      width: 100%;
      max-width: 1200px;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      margin: 0 20px;
      
      .ad-carousel {
        width: 100%;
        height: 100%;
      }
      
      .banner-ad-item {
        width: 100%;
        height: 100%;
        position: relative;
        
        a {
          display: block;
          width: 100%;
          height: 100%;
          position: relative;
        }
        
        .ad-badge {
          position: absolute;
          top: 0;
          left: 0;
          background: #999;
          color: white;
          padding: 2px 5px;
          font-size: 12px;
          font-weight: normal;
          z-index: 10;
          border-radius: 0;
        }
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.5s ease;
        }
      }
      
      :deep(.slick-arrow) {
        z-index: 2;
        width: 40px;
        height: 40px;
        background: rgba(255, 255, 255, 0.5);
        border-radius: 50%;
        display: flex !important;
        align-items: center;
        justify-content: center;
        
        &:hover {
          background: rgba(255, 255, 255, 0.8);
        }
        
        &.slick-prev {
          left: 20px;
        }
        
        &.slick-next {
          right: 20px;
        }
        
        &::before {
          color: #333;
        }
      }
    }
  }
}

@keyframes scroll {
  0% {
    opacity: 1;
    transform: translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateY(15px);
  }
}

@keyframes arrow {
  0% {
    opacity: 0;
    transform: rotate(45deg) translate(-5px, -5px);
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
    transform: rotate(45deg) translate(5px, 5px);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
