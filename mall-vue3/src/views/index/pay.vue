<template>
  <div>
    <Header />
    <div class="pay-content">
      <div class="title">订单提交并支付成功</div>
      <!-- <div class="text time-margin">
        <span>请在 </span>
        <span class="time">{{ ddlTime }}</span>
        <span> 之前付款，超时订单将自动取消</span>
      </div> -->
      <div class="text">支付金额</div>
      <div class="price">
        <span class="num">{{ amount }}</span>
        <span>元</span>
      </div>
      <div class="pay-choose-view" style="">
        <!-- <div class="pay-choose-box flex-view">
          <div class="choose-box choose-box-active">
            <img :src="WxPayIcon" />
            <span>微信支付</span>
          </div>
          <div class="choose-box">
            <img :src="AliPayIcon" />
            <span>支付宝</span>
          </div>
        </div> -->
        <div class="tips">请选择任意一种支付方式</div>
        <button class="pay-btn pay-btn-active" @click="showConfirmModal">确认支付</button>
      </div>
      <div class="pay-qr-view" style="display: none">
        <div class="loading-tip" style="">正在生成安全支付二维码</div>
        <div class="qr-box" style="display: none">
          <div id="qrCode" class="qr"> </div>
          <div class="tips">请打开微信扫一扫进行支付</div>
          <button class="pay-finish-btn">支付完成</button>
          <button class="back-pay-btn">选择其他支付方式</button>
        </div>
      </div>
    </div>

    <!-- 支付确认弹窗 -->
    <a-modal
      v-model:visible="confirmModalVisible"
      title="确认支付"
      @ok="handlePay"
      :confirmLoading="payLoading"
    >
      <div class="confirm-content">
        <p>订单金额: <span class="price">¥{{ amount }}</span></p>
        <p>订单编号: {{ route.query.orderNum }}</p>
        <p class="tips">确认支付后将无法取消订单</p>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
  import Header from '/@/views/index/components/header.vue';
  import { message } from 'ant-design-vue';
  import WxPayIcon from '/@/assets/images/wx-pay-icon.svg';
  import AliPayIcon from '/@/assets/images/ali-pay-icon.svg';
  import { payOrderApi } from '/@/api/order';
  import { useRouter, useRoute } from 'vue-router';

  const router = useRouter();
  const route = useRoute();

  let ddlTime = ref();
  let amount = ref();
  let confirmModalVisible = ref(false);
  let payLoading = ref(false);

  onMounted(() => {
    amount.value = route.query.amount;
    ddlTime.value = formatDate(new Date().getTime(), 'YY-MM-DD hh:mm:ss');
  });

  const showConfirmModal = () => {
    confirmModalVisible.value = true;
  };

  const handlePay = () => {
    payLoading.value = true;
    const formData = new FormData();
    formData.append('orderNumber', route.query.orderNum);
    
    payOrderApi(formData)
      .then((res) => {
        message.success('订单支付成功');
        // 支付成功后跳转到订单页面
        router.push({ name: 'orderView' });
      })
      .catch((err) => {
        message.error(err.msg || '支付失败');
      })
      .finally(() => {
        payLoading.value = false;
        confirmModalVisible.value = false;
      });
  };

  const formatDate = (time, format = 'YY-MM-DD hh:mm:ss') => {
    const date = new Date(time);

    const year = date.getFullYear(),
      month = date.getMonth() + 1,
      day = date.getDate() + 1,
      hour = date.getHours(),
      min = date.getMinutes(),
      sec = date.getSeconds();
    const preArr = Array.apply(null, Array(10)).map(function (elem, index) {
      return '0' + index;
    });

    const newTime = format
      .replace(/YY/g, year)
      .replace(/MM/g, preArr[month] || month)
      .replace(/DD/g, preArr[day] || day)
      .replace(/hh/g, preArr[hour] || hour)
      .replace(/mm/g, preArr[min] || min)
      .replace(/ss/g, preArr[sec] || sec);

    return newTime;
  };
</script>

<style scoped lang="less">
  .flex-view {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
  }

  .pay-content {
    position: relative;
    margin: 120px auto 0;
    width: 500px;
    background: #fff;
    overflow: hidden;

    .title {
      color: #152844;
      font-weight: 500;
      font-size: 24px;
      line-height: 22px;
      height: 22px;
      text-align: center;
      margin-bottom: 11px;
    }

    .time-margin {
      margin: 11px 0 24px;
    }

    .text {
      height: 22px;
      line-height: 22px;
      font-size: 14px;
      text-align: center;
      color: #152844;
    }

    .time {
      color: #f62a2a;
    }

    .text {
      height: 22px;
      line-height: 22px;
      font-size: 14px;
      text-align: center;
      color: #152844;
    }

    .price {
      color: #ff8a00;
      font-weight: 500;
      font-size: 16px;
      height: 36px;
      line-height: 36px;
      text-align: center;

      .num {
        font-size: 28px;
      }
    }

    .pay-choose-view {
      margin-top: 24px;

      .choose-box {
        width: 140px;
        height: 126px;
        border: 1px solid #cedce4;
        border-radius: 4px;
        text-align: center;
        cursor: pointer;
      }

      .pay-choose-box {
        -webkit-box-pack: justify;
        -ms-flex-pack: justify;
        justify-content: space-between;
        max-width: 300px;
        margin: 0 auto;

        img {
          height: 40px;
          margin: 24px auto 16px;
          display: block;
        }
      }

      .tips {
        color: #6f6f6f;
        font-size: 14px;
        line-height: 22px;
        height: 22px;
        text-align: center;
        margin: 16px 0 24px;
      }

      .choose-box-active {
        border: 1px solid #4684e2;
      }

      .tips {
        color: #6f6f6f;
        font-size: 14px;
        line-height: 22px;
        height: 22px;
        text-align: center;
        margin: 16px 0 24px;
      }

      .pay-btn {
        cursor: pointer;
        background: #c3c9d5;
        border-radius: 32px;
        width: 104px;
        height: 32px;
        line-height: 32px;
        border: none;
        outline: none;
        font-size: 14px;
        color: #fff;
        text-align: center;
        display: block;
        margin: 0 auto;
      }

      .pay-btn-active {
        background: #4684e2;
      }
    }
  }

  .confirm-content {
    padding: 20px;
    text-align: center;
    
    p {
      margin-bottom: 10px;
      font-size: 16px;
      
      .price {
        color: #ff8a00;
        font-size: 20px;
        font-weight: bold;
      }
    }
    
    .tips {
      color: #999;
      font-size: 14px;
    }
  }
</style>
