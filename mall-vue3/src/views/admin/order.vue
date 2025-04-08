<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="header-section">
        <h2 class="page-title">订单中心</h2>
        <p class="page-description">管理所有订单状态、物流与售后</p>
      </div>
      
      <div class="table-operations">
        <a-space>
          <a-button @click="handleBatchDelete">
            <template #icon><delete-outlined /></template>
            批量删除
          </a-button>
          <a-select 
            v-model:value="data.statusFilter" 
            style="width: 140px" 
            placeholder="订单状态"
            @change="handleStatusFilterChange"
            class="status-filter"
          >
            <a-select-option value="">全部订单</a-select-option>
            <a-select-option value="1">待付款</a-select-option>
            <a-select-option value="2">待发货</a-select-option>
            <a-select-option value="3">待收货</a-select-option>
            <a-select-option value="4">待评价</a-select-option>
            <a-select-option value="7">已取消</a-select-option>
          </a-select>
        </a-space>
      </div>
      
      <a-card class="table-card">
        <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.tagList"
          :scroll="{ x: 'max-content' }"
          :row-selection="rowSelection"
          :pagination="{
            size: 'default',
            current: data.page,
            pageSize: data.pageSize,
            onChange: (current) => (data.page = current),
            showSizeChanger: false,
            showTotal: (total) => `共${total}条数据`,
          }"
        >
          <template #bodyCell="{ text, record, column }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(text)">
                {{ getStatusText(text) }}
              </a-tag>
            </template>
            <template v-if="column.key === 'title'">
              <a-tooltip :title="record.title">
                <span class="ellipsis-text">{{ record.title ? (record.title.length > 20 ? record.title.substring(0, 20) + '...' : record.title) : '--' }}</span>
              </a-tooltip>
            </template>
            <template v-if="column.key === 'operation'">
              <a-space>
                <a-button 
                  v-if="record.status === '2'" 
                  type="primary"
                  size="small"
                  @click="showShippingModal(record)"
                >
                  <template #icon><car-outlined /></template>
                  发货
                </a-button>
                <a-button 
                  v-if="record.status === '1'" 
                  type="link"
                  size="small"
                  @click="confirmCancel(record)"
                >
                  <template #icon><stop-outlined /></template>
                  取消
                </a-button>
                <a-button 
                  v-if="record.status === '2'" 
                  type="link"
                  size="small"
                  @click="confirmCancel(record)"
                >
                  <template #icon><stop-outlined /></template>
                  取消
                </a-button>
                <a-popconfirm 
                  title="确定删除此订单?" 
                  ok-text="是" 
                  cancel-text="否" 
                  @confirm="confirmDelete(record)"
                >
                  <a-button type="link" danger size="small">
                    <template #icon><delete-outlined /></template>
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>
    
    <!-- 发货模态框 -->
    <a-modal
      :visible="shippingModalVisible"
      title="订单发货管理"
      :maskClosable="false"
      :confirmLoading="shippingLoading"
      @ok="handleShipment"
      @cancel="() => shippingModalVisible = false"
      okText="确认发货"
      cancelText="取消"
      width="520px"
    >
      <a-form
        :model="shippingForm"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="订单编号" class="form-item-with-icon">
          <template #label>
            <span><file-outlined /> 订单编号</span>
          </template>
          <span class="order-detail-text">{{ shippingForm.orderNumber }}</span>
        </a-form-item>
        <a-form-item label="商品信息" class="form-item-with-icon">
          <template #label>
            <span><shopping-outlined /> 商品信息</span>
          </template>
          <span class="order-detail-text">{{ shippingForm.title }}</span>
        </a-form-item>
        <a-form-item label="收货人" class="form-item-with-icon">
          <template #label>
            <span><user-outlined /> 收货人</span>
          </template>
          <span class="order-detail-text">{{ shippingForm.receiverName }}</span>
        </a-form-item>
        <a-form-item label="联系电话" class="form-item-with-icon">
          <template #label>
            <span><phone-outlined /> 联系电话</span>
          </template>
          <span class="order-detail-text">{{ shippingForm.receiverPhone }}</span>
        </a-form-item>
        <a-form-item label="收货地址" class="form-item-with-icon">
          <template #label>
            <span><environment-outlined /> 收货地址</span>
          </template>
          <span class="order-detail-text">{{ shippingForm.receiverAddress }}</span>
        </a-form-item>
        <a-divider>物流信息</a-divider>
        <a-form-item label="快递公司" name="shippingCompany" class="form-item-with-icon">
          <template #label>
            <span><car-outlined /> 快递公司</span>
          </template>
          <a-select 
            v-model:value="shippingForm.shippingCompany"
          >
            <a-select-option value="顺丰速运">顺丰速运</a-select-option>
            <a-select-option value="中通快递">中通快递</a-select-option>
            <a-select-option value="圆通速递">圆通速递</a-select-option>
            <a-select-option value="韵达快递">韵达快递</a-select-option>
            <a-select-option value="申通快递">申通快递</a-select-option>
            <a-select-option value="邮政EMS">邮政EMS</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="快递单号" name="trackingNumber" class="form-item-with-icon">
          <template #label>
            <span><barcode-outlined /> 快递单号</span>
          </template>
          <a-input 
            v-model:value="shippingForm.trackingNumber" 
            placeholder="请输入快递单号" 
          />
        </a-form-item>
        <a-form-item label="备注" name="shippingRemark" class="form-item-with-icon">
          <template #label>
            <span><message-outlined /> 备注</span>
          </template>
          <a-textarea 
            v-model:value="shippingForm.shippingRemark"
            placeholder="发货备注信息（可选）" 
            :rows="2" 
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { FormInstance, message } from 'ant-design-vue';
  import { createApi, listApi, updateApi, deleteApi, cancelApi, shipOrderApi } from '/@/api/order';
  import { getFormatTime } from '/@/utils';
  import { reactive, ref, onMounted, nextTick } from 'vue';
  import { 
    DeleteOutlined, 
    CarOutlined, 
    StopOutlined, 
    FileOutlined,
    UserOutlined,
    PhoneOutlined,
    ShoppingOutlined,
    EnvironmentOutlined,
    BarcodeOutlined,
    MessageOutlined
  } from '@ant-design/icons-vue';

  const columns = reactive([
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      align: 'center',
      width: 80
    },
    {
      title: '用户',
      dataIndex: 'username',
      key: 'username',
      align: 'center',
    },
    {
      title: '商品',
      dataIndex: 'title',
      key: 'title',
      align: 'center',
      width: 250
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      align: 'center',
      width: 100
    },
    {
      title: '收货人',
      dataIndex: 'receiverName',
      key: 'receiverName',
      align: 'center',
    },
    {
      title: '联系电话',
      dataIndex: 'receiverPhone',
      key: 'receiverPhone',
      align: 'center',
      customRender: ({ text }) => {
        if (!text) return '--';
        if (text.length !== 11) return text;
        return text.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
      },
    },
    {
      title: '订单时间',
      dataIndex: 'orderTime',
      key: 'orderTime',
      align: 'center',
      customRender: ({ text }) => getFormatTime(text, true),
    },
    {
      title: '操作',
      key: 'operation',
      align: 'center',
      fixed: 'right',
      width: 180,
    },
  ]);

  // 页面数据
  const data = reactive({
    tagList: [],
    loading: false,
    keyword: '',
    statusFilter: '',
    selectedRowKeys: [] as any[],
    pageSize: 10,
    page: 1,
  });

  // 发货模态框
  const shippingModalVisible = ref(false);
  const shippingLoading = ref(false);
  const shippingForm = reactive({
    id: '',
    orderNumber: '',
    title: '',
    receiverName: '',
    receiverPhone: '',
    receiverAddress: '',
    shippingCompany: '顺丰速运',
    trackingNumber: '',
    shippingRemark: ''
  });

  onMounted(() => {
    getDataList();
  });

  const getDataList = () => {
    data.loading = true;
    listApi({
      keyword: data.keyword,
      status: data.statusFilter
    })
      .then((res) => {
        data.loading = false;
        console.log('订单列表数据:', res);
        
        if (res.data && Array.isArray(res.data)) {
          res.data.forEach((item, index) => {
            item.index = index + 1;
          });
          data.tagList = res.data;
        } else {
          data.tagList = [];
          console.error('数据格式异常: ' + JSON.stringify(res));
        }
      })
      .catch((err) => {
        data.loading = false;
        console.error('获取订单列表失败:', err);
      });
  };

  // 获取状态文本
  const getStatusText = (status) => {
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
  const getStatusColor = (status) => {
    const statusColors = {
      '1': '#fa8c16',   // 待付款
      '2': '#5a7be0',   // 待发货
      '3': '#722ed1',   // 待收货
      '4': '#13c2c2',   // 待评价
      '5': '#52c41a',   // 已完成
      '7': '#f5222d'    // 已取消
    };
    return statusColors[status] || 'default';
  };

  // 状态筛选变化
  const handleStatusFilterChange = () => {
    data.page = 1;
    getDataList();
  };

  const rowSelection = ref({
    onChange: (selectedRowKeys: (string | number)[], selectedRows: DataItem[]) => {
      console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
      data.selectedRowKeys = selectedRowKeys;
    },
  });

  // 显示发货模态框
  const showShippingModal = (record) => {
    console.log('showShippingModal called with record:', record);
    
    // 确保record不为空
    if (!record || !record.id) {
      message.error('订单信息不完整');
      return;
    }
    
    shippingForm.id = record.id;
    shippingForm.orderNumber = record.orderNumber || '未知';
    shippingForm.title = record.title || '未知';
    shippingForm.receiverName = record.receiverName || '未知';
    shippingForm.receiverPhone = record.receiverPhone || '未知';
    shippingForm.receiverAddress = record.receiverAddress || '未知';
    shippingForm.shippingCompany = '顺丰速运';
    shippingForm.trackingNumber = '';
    shippingForm.shippingRemark = '';
    
    console.log('设置表单数据:', shippingForm);
    // 确保这里能够正确触发UI更新
    nextTick(() => {
      shippingModalVisible.value = true;
      console.log('模态框显示状态:', shippingModalVisible.value);
    });
  };

  // 处理发货
  const handleShipment = () => {
    console.log('点击确认发货按钮，当前表单数据:', JSON.stringify(shippingForm));

    if (!shippingForm.trackingNumber || shippingForm.trackingNumber.trim() === '') {
      message.warning('请输入快递单号');
      return;
    }
    
    shippingLoading.value = true;
    
    const formData = new FormData();
    formData.append('id', shippingForm.id);
    formData.append('status', '3'); // 更新为待收货状态
    formData.append('shippingCompany', shippingForm.shippingCompany);
    formData.append('trackingNumber', shippingForm.trackingNumber);
    formData.append('shippingRemark', shippingForm.shippingRemark || '');
    
    shipOrderApi(formData)
      .then(res => {
        console.log('发货成功，响应:', res);
        message.success('订单已发货，状态已更新为待收货');
        shippingModalVisible.value = false;
        getDataList();
      })
      .catch(err => {
        console.error('发货失败:', err);
        message.error(err.msg || '发货失败，请确认快递单号是否正确');
      })
      .finally(() => {
        shippingLoading.value = false;
      });
  };

  const confirmCancel = (record: any) => {
    cancelApi({ id: record.id })
      .then((res) => {
        getDataList();
        message.success('订单已取消');
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
  };

  const confirmDelete = (record: any) => {
    console.log('delete', record);
    deleteApi({ ids: record.id })
      .then((res) => {
        message.success('删除成功');
        getDataList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
  };

  const handleBatchDelete = () => {
    console.log(data.selectedRowKeys);
    if (data.selectedRowKeys.length <= 0) {
      console.log('hello');
      message.warn('请勾选要删除的订单');
      return;
    }
    deleteApi({ ids: data.selectedRowKeys.join(',') })
      .then((res) => {
        message.success('批量删除成功');
        data.selectedRowKeys = [];
        getDataList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
  };
</script>

<style scoped lang="less">
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

  .table-operations {
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;
    
    .status-filter {
      :deep(.ant-select-selector) {
        border-color: #d9d9d9;
        
        &:hover {
          border-color: #5a7be0;
        }
      }
      
      &:deep(.ant-select-focused:not(.ant-select-disabled).ant-select:not(.ant-select-customize-input) .ant-select-selector) {
        border-color: #5a7be0;
        box-shadow: 0 0 0 2px rgba(90, 123, 224, 0.2);
      }
    }
  }
  
  .table-card {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
    
    :deep(.ant-table-thead > tr > th) {
      background-color: #f5f7ff;
      color: #333;
      font-weight: 500;
    }
    
    :deep(.ant-pagination-item-active) {
      border-color: #5a7be0;
      
      a {
        color: #5a7be0;
      }
    }
    
    :deep(.ant-table-row:hover) {
      td {
        background-color: #f5f7ff !important;
      }
    }
    
    :deep(.ant-btn-primary) {
      background-color: #5a7be0;
      border-color: #5a7be0;
      
      &:hover {
        background-color: #6e89e8;
        border-color: #6e89e8;
      }
    }
  }

  .order-detail-text {
    color: #333;
    font-weight: 500;
  }
  
  .ellipsis-text {
    display: inline-block;
    max-width: 100%;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  :deep(.ant-form-item-label > label) {
    color: #666;
  }

  :deep(.ant-divider-inner-text) {
    color: #5a7be0;
    font-size: 14px;
    font-weight: 500;
  }
  
  .form-item-with-icon {
    :deep(.ant-form-item-label > label) {
      display: flex;
      align-items: center;
      
      span {
        display: flex;
        align-items: center;
        gap: 6px;
      }
    }
  }
</style>
