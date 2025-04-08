<template>
  <div class="page-view">
    <div class="header-section">
      <h2 class="page-title">数据概述</h2>
      <p class="page-description">查看系统运营数据和统计信息</p>
    </div>

    <!-- 数据卡片区域 -->
    <div class="data-cards">
      <a-row :gutter="24">
        <a-col :span="6">
          <a-card class="data-card">
            <template #title>
              <div class="card-title">
                <shopping-cart-outlined />
                <span>总订单数</span>
              </div>
            </template>
            <div class="card-content">
              <div class="number">{{ data.totalOrders }}</div>
              <div class="trend">
                <span class="label">较上月</span>
                <span class="value" :class="{ up: data.orderGrowth > 0, down: data.orderGrowth < 0 }">
                  {{ data.orderGrowth > 0 ? '+' : '' }}{{ data.orderGrowth }}%
                </span>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card class="data-card">
            <template #title>
              <div class="card-title">
                <dollar-circle-outlined />
                <span>总销售额</span>
              </div>
            </template>
            <div class="card-content">
              <div class="number">¥{{ data.totalSales }}</div>
              <div class="trend">
                <span class="label">较上月</span>
                <span class="value" :class="{ up: data.salesGrowth > 0, down: data.salesGrowth < 0 }">
                  {{ data.salesGrowth > 0 ? '+' : '' }}{{ data.salesGrowth }}%
                </span>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card class="data-card">
            <template #title>
              <div class="card-title">
                <user-outlined />
                <span>用户总数</span>
              </div>
            </template>
            <div class="card-content">
              <div class="number">{{ data.totalUsers }}</div>
              <div class="trend">
                <span class="label">较上月</span>
                <span class="value" :class="{ up: data.userGrowth > 0, down: data.userGrowth < 0 }">
                  {{ data.userGrowth > 0 ? '+' : '' }}{{ data.userGrowth }}%
                </span>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card class="data-card">
            <template #title>
              <div class="card-title">
                <shop-outlined />
                <span>商品总数</span>
              </div>
            </template>
            <div class="card-content">
              <div class="number">{{ data.totalProducts }}</div>
              <div class="trend">
                <span class="label">较上月</span>
                <span class="value" :class="{ up: data.productGrowth > 0, down: data.productGrowth < 0 }">
                  {{ data.productGrowth > 0 ? '+' : '' }}{{ data.productGrowth }}%
                </span>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <a-row :gutter="24">
        <a-col :span="24">
          <a-card class="chart-card">
            <template #title>
              <div class="chart-title">
                <span>订单状态分布</span>
                <div class="chart-actions">
                  <a-radio-group v-model="data.orderChartType" size="small">
                    <a-radio-button value="count">数量</a-radio-button>
                    <a-radio-button value="amount">金额</a-radio-button>
                  </a-radio-group>
                </div>
              </div>
            </template>
            <div class="chart-container">
              <div ref="orderChartRef" style="height: 300px"></div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <a-row :gutter="24" style="margin-top: 24px;">
        <a-col :span="24">
          <a-card class="chart-card">
            <template #title>
              <div class="chart-title">
                <span>最近一周访问量</span>
              </div>
            </template>
            <div class="chart-container">
              <div ref="visitChartRef" style="height: 300px"></div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue';
import { 
  ShoppingCartOutlined, 
  DollarCircleOutlined, 
  UserOutlined, 
  ShopOutlined
} from '@ant-design/icons-vue';
import * as echarts from 'echarts';
import { listApi } from '/@/api/order';
import { listApi as productListApi } from '/@/api/thing';
import { listApi as userListApi } from '/@/api/user';
import { listApi as overviewListApi } from '/@/api/overview';

// 页面数据
const data = reactive({
  loading: false,
  totalOrders: 0,
  totalSales: 0,
  totalUsers: 0,
  totalProducts: 0,
  orderGrowth: 0,
  salesGrowth: 0,
  userGrowth: 0,
  productGrowth: 0,
  orderChartType: 'count',
  orderStatusData: {
    count: {
      '1': 0, // 待付款
      '2': 0, // 待发货
      '3': 0, // 待收货
      '4': 0, // 待评价
      '5': 0, // 已完成
      '7': 0  // 已取消
    },
    amount: {
      '1': 0,
      '2': 0,
      '3': 0,
      '4': 0,
      '5': 0,
      '7': 0
    }
  },
  visitData: [] as { day: string; uv: number; pv: number }[],
});

const orderChartRef = ref();
const visitChartRef = ref();
let orderChart: echarts.ECharts | null = null;
let visitChart: echarts.ECharts | null = null;

// 获取状态文本
const getStatusText = (status: string) => {
  switch(status) {
    case '1': return '待付款';
    case '2': return '待发货';
    case '3': return '待收货';
    case '4': return '待评价';
    case '5': return '已完成';
    case '7': return '已取消';
    default: return '未知状态';
  }
};

// 获取状态颜色
const getStatusColor = (status: string) => {
  const statusColors = {
    '1': '#ff9f40',   // 待付款
    '2': '#5b8ff9',   // 待发货
    '3': '#5ad8a6',   // 待收货
    '4': '#5d7092',   // 待评价
    '5': '#f6bd16',   // 已完成
    '7': '#e8684a'    // 已取消
  };
  return statusColors[status] || '#d9d9d9';
};

// 初始化订单状态图表
const initOrderChart = () => {
  if (!orderChartRef.value) return;
  
  orderChart = echarts.init(orderChartRef.value);
  updateOrderChart();
};

// 更新订单状态图表
const updateOrderChart = () => {
  if (!orderChart) return;
  
  const type = data.orderChartType;
  const statusData = data.orderStatusData[type];
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}'
    },
    legend: {
      orient: 'horizontal',
      top: 'bottom',
      data: Object.keys(statusData).map(status => getStatusText(status))
    },
    series: [
      {
        name: type === 'amount' ? '订单金额' : '订单数量',
        type: 'pie',
        radius: '75%',
        center: ['50%', '45%'],
        itemStyle: {
          borderRadius: 0
        },
        label: {
          show: false
        },
        labelLine: {
          show: false
        },
        data: Object.entries(statusData).map(([status, value]) => ({
          name: getStatusText(status),
          value: value,
          itemStyle: {
            color: getStatusColor(status)
          }
        }))
      }
    ]
  };
  
  orderChart.setOption(option);
};

// 获取订单数据
const getOrderData = () => {
  // 先获取所有商品信息，创建商品ID到价格的映射
  productListApi({})
    .then(productRes => {
      const products = productRes.data;
      const productPriceMap = {};
      
      // 创建商品ID到价格的映射
      products.forEach(product => {
        if (product.id && product.price) {
          productPriceMap[product.id] = parseFloat(product.price);
        }
      });
      
      // 然后获取订单信息
      return listApi({}).then(orderRes => {
        const orders = orderRes.data;
        data.totalOrders = orders.length;
        
        // 重置状态数据，确保每次都从0开始统计
        Object.keys(data.orderStatusData.count).forEach(status => {
          data.orderStatusData.count[status] = 0;
          data.orderStatusData.amount[status] = 0;
        });
        
        // 统计各状态订单数量和金额
        let totalSales = 0;
        
        orders.forEach(order => {
          const status = order.status || '0';
          
          // 统计数量
          if (data.orderStatusData.count[status] !== undefined) {
            data.orderStatusData.count[status]++;
          }
          
          try {
            // 从商品映射中获取价格
            const productPrice = productPriceMap[order.thingId] || 0;
            
            // 获取订单数量
            let count = 1;
            if (order.count && order.count !== '') {
              count = parseInt(order.count);
            }
            
            const amount = productPrice * count;
            
            // 调试日志
            console.log('订单金额计算:', {
              orderId: order.id,
              productId: order.thingId,
              productPrice,
              count,
              amount
            });
            
            // 只累加非NaN的金额
            if (!isNaN(amount) && amount > 0) {
              totalSales += amount;
              
              // 状态金额统计
              if (data.orderStatusData.amount[status] !== undefined) {
                data.orderStatusData.amount[status] += amount;
              }
            }
          } catch (e) {
            console.error('处理订单金额出错:', order.id, e);
          }
        });
        
        // 设置总销售额，确保格式化为两位小数
        data.totalSales = Number(totalSales.toFixed(2));
        
        updateOrderChart();
      });
    })
    .catch((err) => {
      console.error('获取数据失败:', err);
    });
};

// 获取商品数据
const getProductData = () => {
  productListApi({})
    .then((res) => {
      data.totalProducts = res.data.length;
    })
    .catch((err) => {
      console.error('获取商品数据失败:', err);
    });
};

// 获取用户数据
const getUserData = () => {
  userListApi({})
    .then((res) => {
      data.totalUsers = res.data.length;
    })
    .catch((err) => {
      console.error('获取用户数据失败:', err);
    });
};

// 获取访问数据
const getVisitData = () => {
  overviewListApi({}).then(res => {
    data.visitData = res.data.visitList;
    updateVisitChart();
  }).catch(err => {
    console.error('获取访问量数据失败:', err);
  });
};

// 更新访问量图表
const updateVisitChart = () => {
  if (!visitChart) return;
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['IP', '访问量'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.visitData.map(item => item.day),
      axisLine: {
        lineStyle: {
          color: '#eee'
        }
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          color: '#eee',
          type: 'dashed'
        }
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      }
    },
    series: [
      {
        name: 'IP',
        type: 'line',
        smooth: true,
        showSymbol: false,
        itemStyle: {
          color: '#5b8ff9'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0,
              color: 'rgba(91, 143, 249, 0.2)'
            }, {
              offset: 1,
              color: 'rgba(91, 143, 249, 0.05)'
            }]
          }
        },
        data: data.visitData.map(item => item.uv)
      },
      {
        name: '访问量',
        type: 'line',
        smooth: true,
        showSymbol: false,
        itemStyle: {
          color: '#5ad8a6'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0,
              color: 'rgba(90, 216, 166, 0.2)'
            }, {
              offset: 1,
              color: 'rgba(90, 216, 166, 0.05)'
            }]
          }
        },
        data: data.visitData.map(item => item.pv)
      }
    ]
  };
  
  visitChart.setOption(option);
};

// 初始化访问量图表
const initVisitChart = () => {
  if (!visitChartRef.value) return;
  
  visitChart = echarts.init(visitChartRef.value);
  updateVisitChart();
};

// 修改 handleResize 函数
const handleResize = () => {
  visitChart?.resize();
  orderChart?.resize();
};

onMounted(() => {
  getVisitData();
  getOrderData();
  getProductData();
  getUserData();
  initVisitChart();
  initOrderChart();
  window.addEventListener('resize', handleResize);
});

// 监听订单图表类型变化
watch(() => data.orderChartType, () => {
  updateOrderChart();
});
</script>

<style scoped lang="less">
.page-view {
  min-height: 100%;
  background: #f0f2fa;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.header-section {
  margin-bottom: 8px;
  
  .page-title {
    margin: 0 0 8px 0;
    font-size: 24px;
    font-weight: 500;
    color: #333;
  }
  
  .page-description {
    margin: 0;
    color: #666;
    font-size: 14px;
  }
}

.data-cards {
  .data-card {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
    
    .card-title {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #666;
      
      .anticon {
        font-size: 18px;
        color: #5a7be0;
      }
    }
    
    .card-content {
      .number {
        font-size: 24px;
        font-weight: 500;
        color: #333;
        margin-bottom: 8px;
      }
      
      .trend {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .label {
          color: #999;
          font-size: 12px;
        }
        
        .value {
          font-size: 12px;
          font-weight: 500;
          
          &.up {
            color: #52c41a;
          }
          
          &.down {
            color: #f5222d;
          }
        }
      }
    }
  }
}

.charts-section {
  .chart-card {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
    
    .chart-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      span {
        color: #666;
      }
    }
    
    .chart-container {
      padding: 16px 0;
    }
  }
}
</style>

