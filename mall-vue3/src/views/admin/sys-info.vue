<!--
/**
 * 系统设置组件
 * 
 * 该组件实现了系统信息展示和监控功能，包括：
 * 1. 系统基本信息展示（系统名称、版本、操作系统等）
 * 2. 系统资源状态监控（CPU负载、内存使用率等）
 * 3. 数据刷新功能
 * 4. 资源状态可视化显示（进度条、颜色标识等）
 * 
 * 组件依赖：
 * - Vue Composition API
 * - Ant Design Vue 组件库
 * - 系统信息API服务
 * - 图标组件
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
-->

<template>
  <div class="page-view">
    <!-- 页面标题区域：展示页面标题和描述 -->
    <div class="header-section">
      <h2 class="page-title">系统设置</h2>
      <p class="page-description">查看系统基本信息和运行状态</p>
    </div>
    
    <!-- 系统信息卡片：包含基本信息和资源状态 -->
    <a-card class="info-card">
      <!-- 卡片右上角操作区：刷新按钮 -->
      <template #extra>
        <a-button type="primary" @click="refreshSysInfo">
          <template #icon><reload-outlined /></template>
          刷新数据
        </a-button>
      </template>
      
      <!-- 加载中状态显示 -->
      <a-spin :spinning="loading">
        <!-- 系统基本信息描述列表 -->
        <a-descriptions 
          title="系统基本信息" 
          bordered 
          :column="{ lg: 3, md: 2, sm: 1 }"
          size="middle"
        >
          <a-descriptions-item label="系统名称">
            <span class="info-value">{{ data.sysName }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="版本信息">
            <span class="info-value">{{ data.versionName }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="操作系统">
            <span class="info-value">{{ data.osName }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="系统平台">
            <span class="info-value">{{ data.pf }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="CPU核数">
            <span class="info-value">{{ data.cpuCount }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="处理器">
            <span class="info-value">{{ data.processor }}</span>
          </a-descriptions-item>
        </a-descriptions>
        
        <!-- 系统资源状态描述列表 -->
        <a-descriptions 
          title="系统资源状态" 
          bordered 
          :column="{ lg: 3, md: 2, sm: 1 }"
          size="middle"
          class="status-descriptions"
        >
          <a-descriptions-item label="CPU负载">
            <!-- CPU负载进度条，根据负载值显示不同颜色和状态 -->
            <a-progress 
              :percent="parseFloat(data.cpuLoad)" 
              :status="getCpuLoadStatus(data.cpuLoad)" 
              :stroke-color="getCpuLoadColor(data.cpuLoad)"
            />
          </a-descriptions-item>
          <a-descriptions-item label="系统内存">
            <span class="info-value">{{ data.memory }}G</span>
          </a-descriptions-item>
          <a-descriptions-item label="内存使用">
            <span class="info-value">{{ data.usedMemory }}G</span>
          </a-descriptions-item>
          <a-descriptions-item label="内存利用率">
            <!-- 内存利用率进度条，根据利用率显示不同颜色和状态 -->
            <a-progress 
              :percent="parseFloat(data.percentMemory)" 
              :status="getMemoryStatus(data.percentMemory)"
              :stroke-color="getMemoryColor(data.percentMemory)"
            />
          </a-descriptions-item>
          <a-descriptions-item label="系统语言">
            <span class="info-value">{{ data.sysLan }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="Java版本">
            <span class="info-value">{{ data.jvmVersion }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="系统时区">
            <span class="info-value">{{ data.sysZone }}</span>
          </a-descriptions-item>
        </a-descriptions>
      </a-spin>
    </a-card>
  </div>
</template>

<script setup>
/**
 * 导入必要的依赖和组件
 * - Vue Composition API：用于响应式状态管理和生命周期钩子
 * - 系统信息API：用于获取系统数据
 * - 图标组件：用于UI展示
 */
import { ref, reactive, onMounted } from 'vue';
import { sysInfoApi } from '/@/api/overview';
import { ReloadOutlined } from '@ant-design/icons-vue';

/**
 * 页面加载状态
 * @type {Ref<boolean>}
 */
const loading = ref(false);

/**
 * 系统信息数据对象
 * 存储从API获取的所有系统信息和资源状态数据
 * @type {Object}
 */
const data = reactive({});

/**
 * 获取系统信息
 * 从API获取系统基本信息和资源状态数据
 * 
 * @function getSysInfo
 * @throws {Error} 当API请求失败时抛出错误
 */
const getSysInfo = () => {
  loading.value = true;
  sysInfoApi().then(res => {
    loading.value = false;
    Object.assign(data, res.data);
  }).catch(err => {
    loading.value = false;
    console.error('获取系统信息失败', err);
  });
};

/**
 * 刷新系统信息
 * 用户触发的手动刷新操作，重新获取最新系统数据
 * 
 * @function refreshSysInfo
 */
const refreshSysInfo = () => {
  getSysInfo();
};

/**
 * 获取CPU负载状态
 * 根据CPU负载值确定进度条的状态
 * 
 * @function getCpuLoadStatus
 * @param {string} load - CPU负载百分比
 * @returns {string} 返回进度条状态：exception(异常)、warning(警告)或normal(正常)
 */
const getCpuLoadStatus = (load) => {
  const loadValue = parseFloat(load);
  if (loadValue >= 90) return 'exception';
  if (loadValue >= 70) return 'warning';
  return 'normal';
};

/**
 * 获取CPU负载颜色
 * 根据CPU负载值确定进度条的颜色
 * 
 * @function getCpuLoadColor
 * @param {string} load - CPU负载百分比
 * @returns {string} 返回颜色代码：红色(异常)、黄色(警告)或蓝色(正常)
 */
const getCpuLoadColor = (load) => {
  const loadValue = parseFloat(load);
  if (loadValue >= 90) return '#ff4d4f';
  if (loadValue >= 70) return '#faad14';
  return '#5a7be0';
};

/**
 * 获取内存状态
 * 根据内存利用率确定进度条的状态
 * 
 * @function getMemoryStatus
 * @param {string} percent - 内存利用率百分比
 * @returns {string} 返回进度条状态：exception(异常)、warning(警告)或normal(正常)
 */
const getMemoryStatus = (percent) => {
  const percentValue = parseFloat(percent);
  if (percentValue >= 90) return 'exception';
  if (percentValue >= 70) return 'warning';
  return 'normal';
};

/**
 * 获取内存颜色
 * 根据内存利用率确定进度条的颜色
 * 
 * @function getMemoryColor
 * @param {string} percent - 内存利用率百分比
 * @returns {string} 返回颜色代码：红色(异常)、黄色(警告)或蓝色(正常)
 */
const getMemoryColor = (percent) => {
  const percentValue = parseFloat(percent);
  if (percentValue >= 90) return '#ff4d4f';
  if (percentValue >= 70) return '#faad14';
  return '#5a7be0';
};

/**
 * 组件挂载时的初始化操作
 * 在组件创建后立即获取系统信息数据
 */
onMounted(() => {
  getSysInfo();
});
</script>

<style lang="less" scoped>
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
  gap: 20px;
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
 * 信息卡片样式
 * 设置卡片的圆角、阴影和内部元素样式
 */
.info-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
  
  /**
   * 卡片头部样式
   */
  :deep(.ant-card-head) {
    border-bottom-color: #eaeffd;
  }
  
  /**
   * 卡片右上角操作区样式
   */
  :deep(.ant-card-extra) {
    .ant-btn-primary {
      background-color: #5a7be0;
      border-color: #5a7be0;
      
      &:hover {
        background-color: #6e89e8;
        border-color: #6e89e8;
      }
    }
  }
  
  /**
   * 描述列表标题样式
   */
  :deep(.ant-descriptions-title) {
    font-size: 16px;
    font-weight: 500;
    color: #333;
  }
  
  /**
   * 描述列表标签样式
   */
  :deep(.ant-descriptions-item-label) {
    background-color: #f5f7ff;
    color: #333;
    font-weight: 500;
  }
  
  /**
   * 描述列表边框样式
   */
  :deep(.ant-descriptions-view) {
    border-color: #eaeffd;
  }
  
  /**
   * 描述列表内容样式
   */
  :deep(.ant-descriptions-item-content) {
    color: #666;
  }
}

/**
 * 资源状态描述列表样式
 * 设置与上方描述列表的间距
 */
.status-descriptions {
  margin-top: 24px;
}

/**
 * 信息数值样式
 * 使用更适合显示数据的字体
 */
.info-value {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
