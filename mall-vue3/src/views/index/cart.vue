<template>
  <div>
    <Header />
    <section class="cart-page flex-view">
      <div v-if="!cartStore.hasItems" class="empty-cart">
        <div class="empty-icon">
          <img src="../../assets/images/empty-cart.svg" alt="购物车为空" />
        </div>
        <p class="empty-text">您的购物车是空的</p>
        <button class="go-shopping-btn" @click="$router.push({ name: 'portal' })">
          去购物
        </button>
      </div>
      
      <template v-else>
        <div class="left-flex">
          <div class="title flex-view">
            <h3>购物车</h3>
            <a class="clear-cart" @click="handleClearCart">清空购物车</a>
          </div>
          <div class="cart-list-view">
            <div class="list-th flex-view">
              <span class="line-1">商品名称</span>
              <span class="line-2">价格</span>
              <span class="line-5">数量</span>
              <span class="line-6">操作</span>
            </div>
            <div class="list">
              <div 
                v-for="item in cartStore.items" 
                :key="item.id"
                class="items flex-view"
              >
                <div class="book flex-view">
                  <img :src="item.cover" />
                  <h2 @click="handleViewDetail(item.id)">{{ item.title }}</h2>
                </div>
                <div class="pay">¥{{ item.price }}</div>
                <a-input-number 
                  v-model:value="item.count" 
                  :min="1" 
                  :max="10" 
                  @change="(value) => handleCountChange(item.id, value)" 
                />
                <img 
                  src="../../assets/images/delete-icon.svg" 
                  class="delete" 
                  @click="handleDelete(item.id)" 
                />
              </div>
            </div>
          </div>
        </div>
        
        <div class="right-flex">
          <div class="title flex-view">
            <h3>商品金额</h3>
          </div>
          <div class="price-view">
            <div class="price-item flex-view">
              <div class="item-name">商品总价</div>
              <div class="price-txt">¥{{ cartStore.cartTotal }}</div>
            </div>
            <div class="price-item flex-view">
              <div class="item-name">商品优惠</div>
              <div class="price-txt">¥0</div>
            </div>
            <div class="price-item flex-view">
              <div class="item-name">商品折扣</div>
              <div class="price-txt">¥0</div>
            </div>
            <div class="total-price-view flex-view">
              <span>合计</span>
              <div class="price">
                <span class="font-big">¥{{ cartStore.cartTotal }}</span>
              </div>
            </div>
            <div class="btns-view">
              <button class="btn buy" @click="$router.push({ name: 'portal' })">继续购物</button>
              <button class="btn pay jiesuan" @click="handleCheckout">结算</button>
            </div>
          </div>
        </div>
      </template>
    </section>
  </div>
</template>

<script setup lang="ts">
import { message } from 'ant-design-vue';
import Header from './components/header.vue';
import { useCartStore } from '/@/store';
import { useRouter } from 'vue-router';

const router = useRouter();
const cartStore = useCartStore();

// 修改商品数量
const handleCountChange = (id: string, count: number) => {
  cartStore.updateItemCount(id, count);
};

// 删除商品
const handleDelete = (id: string) => {
  cartStore.removeFromCart(id);
  message.success('商品已从购物车移除');
};

// 清空购物车
const handleClearCart = () => {
  if (cartStore.items.length === 0) return;
  
  cartStore.clearCart();
  message.success('购物车已清空');
};

// 查看商品详情
const handleViewDetail = (id: string) => {
  router.push({ 
    name: 'detail', 
    query: { id } 
  });
};

// 结算
const handleCheckout = () => {
  if (cartStore.items.length === 0) {
    message.warning('购物车为空');
    return;
  }
  
  // 如果只有一个商品，直接跳转到确认页面
  if (cartStore.items.length === 1) {
    const item = cartStore.items[0];
    router.push({
      name: 'confirm',
      query: {
        id: item.id,
        title: item.title,
        cover: item.cover,
        price: item.price.toString(),
        count: item.count.toString()
      }
    });
    return;
  }
  
  // 如果多个商品，目前系统不支持一次购买多个商品
  // 这里应该跳转到多商品结算页面，但当前系统不支持
  // 提示用户当前只能单个商品购买
  message.warning('当前系统仅支持单个商品结算，请分别购买');
};
</script>

<style scoped lang="less">
.flex-view {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

.cart-page {
  width: 1024px;
  min-height: 50vh;
  margin: 100px auto;
}

.left-flex {
  -webkit-box-flex: 17;
  -ms-flex: 17;
  flex: 17;
  padding-right: 20px;
}

.right-flex {
  -webkit-box-flex: 7;
  -ms-flex: 7;
  flex: 7;
  width: 280px;
}

.title {
  -webkit-box-pack: justify;
  -ms-flex-pack: justify;
  justify-content: space-between;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;

  h3 {
    color: #152844;
    font-weight: 600;
    font-size: 18px;
    height: 26px;
    line-height: 26px;
    margin: 0;
  }
  
  .clear-cart {
    color: #4684e2;
    cursor: pointer;
    font-size: 14px;
    
    &:hover {
      text-decoration: underline;
    }
  }
}

.cart-list-view {
  margin: 4px 0 40px;

  .list-th {
    height: 42px;
    line-height: 42px;
    border-bottom: 1px solid #cedce4;
    color: #152844;
    font-size: 14px;

    .line-1 {
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      margin-right: 20px;
    }

    .line-2,
    .pc-style .cart-list-view .list-th .line-3,
    .pc-style .cart-list-view .list-th .line-4 {
      width: 65px;
      margin-right: 20px;
    }

    .line-5 {
      width: 80px;
      margin-right: 40px;
    }

    .line-6 {
      width: 28px;
    }
  }
}

.items {
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  margin-top: 20px;
  padding-bottom: 20px;
  border-bottom: 1px dashed #eaeaea;

  .book {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    margin-right: 20px;

    img {
      width: 48px;
      margin-right: 16px;
      border-radius: 4px;
    }

    h2 {
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      font-size: 14px;
      line-height: 22px;
      color: #152844;
      cursor: pointer;
      
      &:hover {
        color: #4684e2;
      }
    }
  }
  
  .delete {
    cursor: pointer;
    &:hover {
      opacity: 0.8;
    }
  }
}

.pay {
  width: 65px;
  margin-right: 20px;
  font-weight: 500;
  color: #ff8a00;
}

.price-view {
  background: #f9fafc;
  padding: 24px;
  border-radius: 4px;
  
  .price-item {
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    margin-bottom: 8px;
    font-size: 14px;

    .item-name {
      color: #152844;
    }

    .price-txt {
      font-weight: 500;
      color: #ff8a00;
    }
  }

  .total-price-view {
    margin-top: 12px;
    border-top: 1px solid #cedce4;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    -webkit-box-align: start;
    -ms-flex-align: start;
    align-items: flex-start;
    padding-top: 10px;
    color: #152844;
    font-weight: 500;

    .price {
      color: #ff8a00;
      font-size: 16px;
      height: 36px;
      line-height: 36px;
    }
  }

  .btns-view {
    margin-top: 24px;
    text-align: right;

    .buy {
      background: #fff;
      color: #4684e2;
    }

    .jiesuan {
      cursor: pointer;
      background: #4684e2;
      color: #fff;
    }

    .btn {
      cursor: pointer;
      width: 96px;
      height: 36px;
      line-height: 33px;
      margin-left: 16px;
      text-align: center;
      border-radius: 32px;
      border: 1px solid #4684e2;
      font-size: 16px;
      outline: none;
    }
  }
}

.empty-cart {
  width: 100%;
  text-align: center;
  padding: 50px 0;
  
  .empty-icon {
    margin-bottom: 20px;
    
    img {
      width: 120px;
      height: 120px;
    }
  }
  
  .empty-text {
    font-size: 16px;
    color: #8c8c8c;
    margin-bottom: 24px;
  }
  
  .go-shopping-btn {
    padding: 8px 24px;
    background: #4684e2;
    color: white;
    border: none;
    border-radius: 20px;
    font-size: 16px;
    cursor: pointer;
    
    &:hover {
      opacity: 0.9;
    }
  }
}
</style> 