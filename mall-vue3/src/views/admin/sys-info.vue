<template>
  <div class="page-view">
    <div class="header-section">
      <h2 class="page-title">系统设置</h2>
      <p class="page-description">查看系统基本信息和运行状态</p>
    </div>
    
    <a-card class="info-card">
      <template #extra>
        <a-button type="primary" @click="refreshSysInfo">
          <template #icon><reload-outlined /></template>
          刷新数据
        </a-button>
      </template>
      
      <a-spin :spinning="loading">
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
        
        <a-descriptions 
          title="系统资源状态" 
          bordered 
          :column="{ lg: 3, md: 2, sm: 1 }"
          size="middle"
          class="status-descriptions"
        >
          <a-descriptions-item label="CPU负载">
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
import { ref, reactive, onMounted } from 'vue';
import { sysInfoApi } from '/@/api/overview';
import { ReloadOutlined } from '@ant-design/icons-vue';

const loading = ref(false);
const data = reactive({});

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

const refreshSysInfo = () => {
  getSysInfo();
};

const getCpuLoadStatus = (load) => {
  const loadValue = parseFloat(load);
  if (loadValue >= 90) return 'exception';
  if (loadValue >= 70) return 'warning';
  return 'normal';
};

const getCpuLoadColor = (load) => {
  const loadValue = parseFloat(load);
  if (loadValue >= 90) return '#ff4d4f';
  if (loadValue >= 70) return '#faad14';
  return '#5a7be0';
};

const getMemoryStatus = (percent) => {
  const percentValue = parseFloat(percent);
  if (percentValue >= 90) return 'exception';
  if (percentValue >= 70) return 'warning';
  return 'normal';
};

const getMemoryColor = (percent) => {
  const percentValue = parseFloat(percent);
  if (percentValue >= 90) return '#ff4d4f';
  if (percentValue >= 70) return '#faad14';
  return '#5a7be0';
};

onMounted(() => {
  getSysInfo();
});
</script>

<style lang="less" scoped>
.page-view {
  min-height: 100%;
  background: #f0f2fa;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
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

.info-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
  
  :deep(.ant-card-head) {
    border-bottom-color: #eaeffd;
  }
  
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
  
  :deep(.ant-descriptions-title) {
    font-size: 16px;
    font-weight: 500;
    color: #333;
  }
  
  :deep(.ant-descriptions-item-label) {
    background-color: #f5f7ff;
    color: #333;
    font-weight: 500;
  }
  
  :deep(.ant-descriptions-view) {
    border-color: #eaeffd;
  }
  
  :deep(.ant-descriptions-item-content) {
    color: #666;
  }
}

.status-descriptions {
  margin-top: 24px;
}

.info-value {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
