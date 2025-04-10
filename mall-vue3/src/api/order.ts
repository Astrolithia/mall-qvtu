/**
 * 订单管理 API 模块
 * 
 * 该模块提供订单相关的API请求方法，包括订单的查询、创建、支付、更新、删除、取消和物流管理等功能。
 * 主要用于电商系统中的订单全生命周期管理，支持商家端和用户端的订单操作。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/order
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import { get, post } from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有订单相关API的URL路径，便于统一管理和维护
 */
enum URL {
  list = '/api/order/list',
  create = '/api/order/create',
  payOrder = '/api/order/payOrder',
  update = '/api/order/update',
  delete = '/api/order/delete',
  cancel = '/api/order/cancelOrder',
  cancelUserOrder = '/api/order/cancelUserOrder',
  userOrderList = '/api/order/userOrderList',
  confirmReceipt = '/api/order/confirmReceipt',
  checkShipping = '/api/order/checkShipping',
  ship = '/api/order/ship',
}

/**
 * 获取订单列表（管理端）
 * 
 * @description 获取系统所有订单的列表，通常用于管理后台
 * @param {any} params - 查询参数对象，可包含时间范围、订单状态等筛选条件
 * @returns {Promise<any>} 返回订单列表数据的Promise
 */
const listApi = async (params: any) => get<any>({ url: URL.list, params: params, data: {}, headers: {} });

/**
 * 获取用户订单列表
 * 
 * @description 获取指定用户的订单列表，用于用户端展示
 * @param {any} params - 查询参数对象，包含用户ID和可选的筛选条件
 * @returns {Promise<any>} 返回用户订单列表数据的Promise
 */
const userOrderListApi = async (params: any) => get<any>({ url: URL.userOrderList, params: params, data: {}, headers: {} });

/**
 * 创建订单
 * 
 * @description 创建新的订单记录
 * @param {any} data - 订单对象，包含商品信息、收货地址、支付方式等必要信息
 * @returns {Promise<any>} 返回创建结果的Promise，通常包含创建状态和新订单ID
 */
const createApi = async (data: any) =>
  post<any>({
    url: URL.create,
    params: {},
    data: data,
    headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' },
  });

/**
 * 支付订单
 * 
 * @description 处理订单支付请求
 * @param {any} data - 支付对象，包含订单ID、支付方式、支付金额等信息
 * @returns {Promise<any>} 返回支付结果的Promise，通常包含支付状态和支付流水号
 */
const payOrderApi = async (data: any) =>
  post<any>({
    url: URL.payOrder,
    params: {},
    data: data,
    headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' },
  });

/**
 * 更新订单
 * 
 * @description 更新已有订单的信息
 * @param {any} params - 包含订单ID的参数对象
 * @param {any} data - 订单对象，包含要更新的字段
 * @returns {Promise<any>} 返回更新结果的Promise，通常包含更新状态
 */
const updateApi = async (params: any, data: any) =>
  post<any>({
    url: URL.update,
    params: params,
    data: data,
    headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' },
  });

/**
 * 删除订单
 * 
 * @description 删除指定的订单记录，通常为软删除
 * @param {any} params - 包含订单ID的参数对象
 * @returns {Promise<any>} 返回删除结果的Promise，通常包含删除状态
 */
const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params, headers: {} });

/**
 * 取消订单（管理端）
 * 
 * @description 管理员取消指定订单
 * @param {any} params - 包含订单ID的参数对象
 * @returns {Promise<any>} 返回取消结果的Promise，通常包含取消状态
 */
const cancelApi = async (params: any) => post<any>({ url: URL.cancel, params: params, headers: {} });

/**
 * 取消订单（用户端）
 * 
 * @description 用户取消自己的订单
 * @param {any} params - 包含订单ID和用户ID的参数对象
 * @returns {Promise<any>} 返回取消结果的Promise，通常包含取消状态
 */
const cancelUserOrderApi = async (params: any) => post<any>({ url: URL.cancelUserOrder, params: params, headers: {} });

/**
 * 确认收货
 * 
 * @description 用户确认已收到商品
 * @param {any} params - 包含订单ID的参数对象
 * @returns {Promise<any>} 返回确认结果的Promise，通常包含确认状态
 */
const confirmReceiptApi = async (params: any) => post<any>({ url: URL.confirmReceipt, params: params, headers: {} });

/**
 * 查询物流信息
 * 
 * @description 获取指定订单的物流跟踪信息
 * @param {any} params - 包含订单ID或物流单号的参数对象
 * @returns {Promise<any>} 返回物流信息的Promise，包含物流状态和轨迹
 */
const checkShippingApi = async (params: any) => get<any>({ url: URL.checkShipping, params: params, headers: {} });

/**
 * 订单发货
 * 
 * @description 商家为订单填写物流信息并标记为已发货
 * @param {any} data - 发货对象，包含订单ID、物流公司、物流单号等信息
 * @returns {Promise<any>} 返回发货结果的Promise，通常包含发货状态
 */
const shipOrderApi = async (data: any) => 
  post<any>({
    url: URL.ship,
    params: {},
    data: data,
    headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' },
  });

export { 
  listApi, 
  userOrderListApi, 
  createApi, 
  payOrderApi, 
  updateApi, 
  deleteApi, 
  cancelApi, 
  cancelUserOrderApi,
  confirmReceiptApi,
  checkShippingApi,
  shipOrderApi
};
