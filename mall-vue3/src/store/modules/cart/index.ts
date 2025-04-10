/**
 * 购物车状态管理 模块
 * 
 * 该模块使用Pinia状态管理库实现购物车功能，包括商品添加、删除、数量修改和清空等操作。
 * 主要用于电商系统中的购物车功能，支持持久化存储，确保页面刷新后购物车数据不丢失。
 * 提供商品数量、总价等计算属性，便于在界面上实时展示购物车状态。
 * 
 * @module store/modules/cart
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import { defineStore } from 'pinia';

/**
 * 购物车商品项接口
 * 
 * 定义购物车中单个商品项的数据结构
 */
export interface CartItem {
  id: string;      // 商品唯一标识
  title: string;   // 商品标题
  cover: string;   // 商品封面图片URL
  price: number;   // 商品单价
  count: number;   // 商品数量
}

/**
 * 购物车状态接口
 * 
 * 定义购物车的状态结构
 */
interface CartState {
  items: CartItem[]; // 购物车商品项数组
}

/**
 * 购物车状态管理
 * 
 * 使用Pinia defineStore创建的购物车状态管理仓库
 */
export const useCartStore = defineStore('cart', {
  /**
   * 状态定义
   * 
   * 初始化一个空的购物车商品数组
   */
  state: (): CartState => ({
    items: [],
  }),
  
  /**
   * 计算属性
   * 
   * 提供购物车相关的计算值，如商品总数、总价和是否有商品
   */
  getters: {
    /**
     * 购物车商品总数量
     * 
     * @returns {number} 所有商品数量的总和
     */
    cartCount: (state) => {
      return state.items.reduce((total, item) => total + item.count, 0);
    },
    
    /**
     * 购物车商品总价
     * 
     * @returns {string} 所有商品价格的总和，保留两位小数
     */
    cartTotal: (state) => {
      return state.items.reduce((total, item) => total + (item.price * item.count), 0).toFixed(2);
    },
    
    /**
     * 购物车是否有商品
     * 
     * @returns {boolean} 购物车是否包含商品
     */
    hasItems: (state) => state.items.length > 0,
  },
  
  /**
   * 操作方法
   * 
   * 提供修改购物车状态的方法，如添加、删除、更新和清空
   */
  actions: {
    /**
     * 添加商品到购物车
     * 
     * @description 如果商品已存在，则增加数量；否则添加新商品
     * @param {CartItem} item - 要添加的商品对象
     */
    addToCart(item: CartItem) {
      const existItem = this.items.find(i => i.id === item.id);
      if (existItem) {
        existItem.count += item.count || 1;
      } else {
        this.items.push({
          ...item,
          count: item.count || 1
        });
      }
    },
    
    /**
     * 从购物车移除商品
     * 
     * @description 根据商品ID移除购物车中的商品
     * @param {string} id - 要移除的商品ID
     */
    removeFromCart(id: string) {
      const index = this.items.findIndex(item => item.id === id);
      if (index !== -1) {
        this.items.splice(index, 1);
      }
    },
    
    /**
     * 更新购物车商品数量
     * 
     * @description 根据商品ID更新对应商品的数量
     * @param {string} id - 要更新的商品ID
     * @param {number} count - 新的商品数量
     */
    updateItemCount(id: string, count: number) {
      const item = this.items.find(i => i.id === id);
      if (item) {
        item.count = count;
      }
    },
    
    /**
     * 清空购物车
     * 
     * @description 移除购物车中的所有商品
     */
    clearCart() {
      this.items = [];
    }
  },
  
  /**
   * 持久化配置
   * 
   * 启用状态持久化，将购物车数据保存到localStorage
   */
  persist: true,
}); 