/**
 * 地址编辑组件
 * 
 * 该组件用于用户地址的创建和编辑功能，支持：
 * - 新增收货地址
 * - 修改现有地址
 * - 设置默认地址
 * - 表单验证
 * 
 * 组件依赖：
 * - Ant Design Vue 表单组件
 * - 地址管理API
 * - Vuex状态管理
 * 
 * @component EditAddress
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

<template>
  <!-- 地址编辑表单：使用Ant Design表单组件 -->
  <a-form-model
    ref="myform"
    :model="form"
    :rules="rules">
    <!-- 姓名输入区域 -->
    <a-row :gutter="24">
      <a-col span="24">
        <a-form-model-item label="姓名" prop="name">
          <a-input placeholder="请输入" v-model="form.name"></a-input>
        </a-form-model-item>
      </a-col>
    </a-row>
    
    <!-- 电话号码输入区域 -->
    <a-row :gutter="24">
      <a-col span="24">
        <a-form-model-item label="电话号码" prop="mobile">
          <a-input placeholder="请输入" v-model="form.mobile"></a-input>
        </a-form-model-item>
      </a-col>
    </a-row>
    
    <!-- 送货地址输入区域 -->
    <a-row :gutter="24">
      <a-col span="24">
        <a-form-model-item label="送货地址" prop="desc">
          <a-input placeholder="请输入" v-model="form.desc"></a-input>
        </a-form-model-item>
      </a-col>
    </a-row>
    
    <!-- 默认地址开关 -->
    <a-row :gutter="24">
      <a-col span="24">
        <a-form-model-item label="默认地址">
          <a-switch v-model="form.default"></a-switch>
        </a-form-model-item>
      </a-col>
    </a-row>
  </a-form-model>
</template>

<script>
/**
 * 导入地址管理相关API
 * - createApi: 创建新地址
 * - updateApi: 更新现有地址
 */
import {createApi, updateApi} from '/@/api/index/address'

export default {
  /**
   * 组件名称
   */
  name: 'EditAddress',
  
  /**
   * 组件属性定义
   * 
   * @property {Boolean} modifyFlag - 是否为修改模式，true表示修改现有地址，false表示新增地址
   * @property {Object} address - 要编辑的地址数据对象，在修改模式下使用
   */
  props: {
    modifyFlag: {
      type: Boolean,
      default: () => false
    },
    address: {
      type: Object,
      default: () => {}
    }
  },
  
  /**
   * 组件数据
   * 
   * @returns {Object} 包含表单数据
   */
  data () {
    return {
      /**
       * 表单数据对象
       * 
       * @property {String} name - 收件人姓名
       * @property {String} mobile - 联系电话
       * @property {String} desc - 详细地址描述
       * @property {Boolean} default - 是否设为默认地址
       */
      form: {
        name: undefined,
        mobile: undefined,
        desc: undefined,
        default: undefined
      }
    }
  },
  
  /**
   * 组件创建时的生命周期钩子
   * 如果是修改模式，则用传入的地址数据初始化表单
   */
  created () {
    if (this.modifyFlag) {
      this.form = this.address
    }
  },
  
  /**
   * 组件方法
   */
  methods: {
    /**
     * 确认按钮处理函数
     * 
     * 验证表单并提交数据，根据modifyFlag决定是创建新地址还是更新现有地址。
     * 此方法返回Promise以支持父组件的异步操作（如关闭模态框）。
     * 
     * @returns {Promise<boolean>} 操作成功返回resolve(true)，失败返回reject
     */
    onOk () {
      return new Promise((resolve, reject) => {
        console.log(this.form)
        
        // 构建表单数据
        const formData = new FormData()
        formData.append('user', this.$store.state.user.userId)
        formData.append('default', this.form.default ? this.form.default : false)
        
        // 添加非空字段
        if (this.form.name) {
          formData.append('name', this.form.name)
        }
        if (this.form.mobile) {
          formData.append('mobile', this.form.mobile)
        }
        if (this.form.desc) {
          formData.append('desc', this.form.desc)
        }
        
        // 验证表单并提交
        this.$refs.myform.validate(valid => {
          if (valid) {
            if (this.modifyFlag) {
              // 修改现有地址
              updateApi({
                id: this.address.id
              }, formData).then(res => {
                console.log(res)
                resolve(true)
              }).catch(err => {
                this.$message.error(err.msg || '更新失败')
                reject(new Error('更新失败'))
              })
            } else {
              // 创建新地址
              createApi(formData).then(res => {
                console.log(res)
                resolve(true)
              }).catch(err => {
                this.$message.error(err.msg || '新建失败')
                reject(new Error('新建失败'))
              })
            }
          }
        })
      })
    }
  }
}
</script>

<style lang="less" scoped>
/**
 * 组件样式
 * 
 * 当前使用Ant Design默认样式，无需额外样式定义
 */
</style>
