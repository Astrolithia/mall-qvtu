<!--
/**
 * 数据概述组件
 * 
 * 该组件实现了管理后台的数据统计与可视化功能，包括：
 * 1. 核心业务数据展示（订单、销售额、用户、商品数量）
 * 2. 数据增长趋势分析（与上月比较）
 * 3. 订单状态分布分析（数量/金额维度）
 * 4. 访问流量统计与趋势图表
 * 
 * 组件依赖：
 * - Vue Composition API
 * - Ant Design Vue 组件库
 * - ECharts 图表库
 * - 多个API服务（订单、商品、用户、概览）
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <div class="page-view">
    <!--页面标题区域：展示页面标题和描述-->
    <div class="header-section">
      <h2 class="page-title">数据概述</h2>
      <p class="page-description">查看系统运营数据和统计信息</p>
    </div>

    <!-- 数据卡片区域：展示四个核心业务指标卡片 -->
    <div class="data-cards">
      <a-row :gutter="24">
        <!--订单数据卡片：展示总订单数及环比增长率-->
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
        <!--销售额数据卡片：展示总销售额及环比增长率-->
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
        <!--用户数据卡片：展示总用户数及环比增长率-->
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
        <!--商品数据卡片：展示总商品数及环比增长率-->
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

    <!-- 图表区域：展示订单状态分布和访问量统计图表 -->
    <div class="charts-section">
      <!--订单状态分布图表：展示不同订单状态的分布情况-->
      <a-row :gutter="24">
        <a-col :span="24">
          <a-card class="chart-card">
            <template #title>
              <div class="chart-title">
                <span>订单状态分布</span>
                <!--图表切换按钮：在数量和金额维度间切换-->
                <div class="chart-actions">
                  <a-radio-group v-model="data.orderChartType" size="small">
                    <a-radio-button value="count">数量</a-radio-button>
                    <a-radio-button value="amount">金额</a-radio-button>
                  </a-radio-group>
                </div>
              </div>
            </template>
            <div class="chart-container">
              <!--订单状态分布图表容器：渲染饼图-->
              <div ref="orderChartRef" style="height: 300px"></div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <!--访问量统计图表：展示最近一周访问量趋势-->
      <a-row :gutter="24" style="margin-top: 24px;">
        <a-col :span="24">
          <a-card class="chart-card">
            <template #title>
              <div class="chart-title">
                <span>最近一周访问量</span>
              </div>
            </template>
            <div class="chart-container">
              <!--访问量统计图表容器：渲染曲线图-->
              <div ref="visitChartRef" style="height: 300px"></div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 导入必要的依赖和组件
 * - Vue Composition API：用于响应式状态管理和生命周期钩子
 * - Ant Design Vue 图标组件：用于UI展示
 * - ECharts：用于数据可视化
 * - API服务：用于数据获取
 */
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

/**
 * 页面数据状态
 * 使用reactive创建响应式数据对象，包含所有页面展示所需的数据
 * - 基础统计数据：订单数、销售额、用户数、商品数及其环比增长率
 * - 订单状态分布数据：按数量和金额两个维度统计各状态订单
 * - 访问量数据：存储最近一周的访问数据
 */
const data = reactive({
  loading: false,                  // 加载状态标识
  totalOrders: 0,                  // 总订单数
  totalSales: 0,                   // 总销售额
  totalUsers: 0,                   // 总用户数
  totalProducts: 0,                // 总商品数
  orderGrowth: 0,                  // 订单环比增长率
  salesGrowth: 0,                  // 销售额环比增长率
  userGrowth: 0,                   // 用户环比增长率
  productGrowth: 0,                // 商品环比增长率
  orderChartType: 'count',         // 订单图表类型：count-数量,amount-金额
  orderStatusData: {               // 订单状态数据
    count: {                       // 按数量统计
      '1': 0, // 待付款
      '2': 0, // 待发货
      '3': 0, // 待收货
      '4': 0, // 待评价
      '5': 0, // 已完成
      '7': 0  // 已取消
    },
    amount: {                      // 按金额统计
      '1': 0,
      '2': 0,
      '3': 0,
      '4': 0,
      '5': 0,
      '7': 0
    }
  },
  visitData: [] as { day: string; uv: number; pv: number }[], // 访问量数据数组
});

/**
 * 图表引用和实例
 * - 使用ref创建DOM引用，用于图表初始化
 * - 声明图表实例变量，用于后续操作图表
 */
const orderChartRef = ref();             // 订单图表DOM引用
const visitChartRef = ref();             // 访问量图表DOM引用
let orderChart: echarts.ECharts | null = null;  // 订单图表实例
let visitChart: echarts.ECharts | null = null;   // 访问量图表实例

/**
 * 获取订单状态对应的文本描述
 * 
 * @param {string} status - 订单状态码
 * @returns {string} 状态对应的中文描述
 */
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

/**
 * 获取订单状态对应的显示颜色
 * 为不同状态设置不同颜色，提高可视化辨识度
 * 
 * @param {string} status - 订单状态码
 * @returns {string} 状态对应的十六进制颜色值
 */
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

/**
 * 初始化订单状态分布图表
 * 在DOM引用可用时创建ECharts实例并初始化图表
 */
const initOrderChart = () => {
  // 检查DOM引用是否存在
  if (!orderChartRef.value) return;
  
  // 创建ECharts实例
  orderChart = echarts.init(orderChartRef.value);
  // 更新图表数据和配置
  updateOrderChart();
};

/**
 * 更新订单状态分布图表
 * 根据当前选择的图表类型和数据更新图表配置和展示
 */
const updateOrderChart = () => {
  // 检查图表实例是否存在
  if (!orderChart) return;
  
  // 获取当前图表类型和对应数据
  const type = data.orderChartType;
  const statusData = data.orderStatusData[type];
  
  // 配置图表选项
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
  
  // 应用配置到图表
  orderChart.setOption(option);
};

/**
 * 获取订单数据
 * 依次获取商品和订单数据，计算销售额和状态分布
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
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
        
        // 更新订单图表
        updateOrderChart();
      });
    })
    .catch((err) => {
      console.error('获取数据失败:', err);
    });
};

/**
 * 获取商品数据
 * 获取并设置商品总数
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
const getProductData = () => {
  productListApi({})
    .then((res) => {
      data.totalProducts = res.data.length;
    })
    .catch((err) => {
      console.error('获取商品数据失败:', err);
    });
};

/**
 * 获取用户数据
 * 获取并设置用户总数
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
const getUserData = () => {
  userListApi({})
    .then((res) => {
      data.totalUsers = res.data.length;
    })
    .catch((err) => {
      console.error('获取用户数据失败:', err);
    });
};

/**
 * 获取访问数据
 * 获取最近一周的访问数据并更新图表
 * 
 * @throws {Error} 当获取数据失败时抛出错误
 */
const getVisitData = () => {
  overviewListApi({}).then(res => {
    // 保存访问数据
    data.visitData = res.data.visitList;
    // 更新访问量图表
    updateVisitChart();
  }).catch(err => {
    console.error('获取访问量数据失败:', err);
  });
};

/**
 * 更新访问量图表
 * 根据访问量数据更新图表配置和展示
 */
const updateVisitChart = () => {
  // 检查图表实例是否存在
  if (!visitChart) return;
  
  // 配置图表选项
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
  
  // 应用配置到图表
  visitChart.setOption(option);
};

/**
 * 初始化访问量图表
 * 在DOM引用可用时创建ECharts实例并初始化图表
 */
const initVisitChart = () => {
  // 检查DOM引用是否存在
  if (!visitChartRef.value) return;
  
  // 创建ECharts实例
  visitChart = echarts.init(visitChartRef.value);
  // 更新图表
  updateVisitChart();
};

/**
 * 处理窗口大小变化
 * 在窗口大小变化时重新调整图表尺寸
 */
const handleResize = () => {
  // 调整两个图表的尺寸，使用可选链确保实例存在时才调用
  visitChart?.resize();
  orderChart?.resize();
};

/**
 * 组件挂载时的初始化操作
 * 获取数据、初始化图表并添加窗口大小变化监听
 */
onMounted(() => {
  // 获取各类数据
  getVisitData();
  getOrderData();
  getProductData();
  getUserData();
  // 初始化图表
  initVisitChart();
  initOrderChart();
  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize);
});

/**
 * 监听订单图表类型变化
 * 当图表类型变化时，更新订单状态分布图表
 */
watch(() => data.orderChartType, () => {
  updateOrderChart();
});
</script>

<style scoped lang="less">
/**
 * 页面整体样式
 * 设置页面布局、背景颜色和内边距
 */
.page-view {
  min-height: 100%;
  background: #f0f2fa;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/**
 * 页面标题区域样式
 * 设置标题和描述文字的样式与间距
 */
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

/**
 * 数据卡片区域样式
 * 设置数据卡片的圆角、阴影和内部元素样式
 */
.data-cards {
  .data-card {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
    
    /**
     * 卡片标题样式
     * 设置标题区域的布局和图标样式
     */
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
    
    /**
     * 卡片内容样式
     * 设置数据显示区域的字体和颜色
     */
    .card-content {
      .number {
        font-size: 24px;
        font-weight: 500;
        color: #333;
        margin-bottom: 8px;
      }
      
      /**
       * 趋势指标样式
       * 设置环比数据的布局和颜色变化
       */
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
          
          /**
           * 上升趋势样式
           * 正增长显示为绿色
           */
          &.up {
            color: #52c41a;
          }
          
          /**
           * 下降趋势样式
           * 负增长显示为红色
           */
          &.down {
            color: #f5222d;
          }
        }
      }
    }
  }
}

/**
 * 图表区域样式
 * 设置图表卡片的圆角、阴影和内部布局
 */
.charts-section {
  .chart-card {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
    
    /**
     * 图表标题样式
     * 设置标题区域的布局和对齐方式
     */
    .chart-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      span {
        color: #666;
      }
    }
    
    /**
     * 图表容器样式
     * 设置图表区域的内边距
     */
    .chart-container {
      padding: 16px 0;
    }
  }
}
</style>

