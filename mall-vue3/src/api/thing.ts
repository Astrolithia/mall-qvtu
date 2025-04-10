/**
 * 商品管理 API 模块
 * 
 * 该模块提供商品相关的API请求方法，包括商品的查询、创建、更新、删除、详情获取和浏览量更新等功能。
 * 主要用于电商系统中的商品全生命周期管理，支持后台商品管理和前台商品展示。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/thing
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

// 权限问题后期增加
import { get, post } from '/@/utils/http/axios';
import { UserState } from '/@/store/modules/user/types';
// import axios from 'axios';

/**
 * API路径枚举
 * 
 * 定义所有商品相关API的URL路径，便于统一管理和维护
 */
enum URL {
    list = '/api/thing/list',
    create = '/api/thing/create',
    update = '/api/thing/update',
    delete = '/api/thing/delete',
    detail = '/api/thing/detail',
    updateViews = '/api/thing/updateViews',
}

/**
 * 获取商品列表
 * 
 * @description 获取系统商品列表，包含商品标题、价格、分类等基本信息
 * @param {any} params - 查询参数对象，可包含分类、价格区间、关键词等筛选条件
 * @returns {Promise<any>} 返回商品列表数据的Promise
 */
const listApi = async (params: any) => get<any>({ url: URL.list, params: params, data: {}, headers: {} });

/**
 * 创建商品
 * 
 * @description 创建新的商品记录
 * @param {any} data - 商品对象，包含标题、描述、价格、库存、图片等必要信息
 * @returns {Promise<any>} 返回创建结果的Promise，通常包含创建状态和新商品ID
 */
const createApi = async (data: any) =>
    post<any>({ url: URL.create, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 更新商品
 * 
 * @description 更新已有商品的信息
 * @param {any} data - 商品对象，包含要更新的字段和商品ID
 * @returns {Promise<any>} 返回更新结果的Promise，通常包含更新状态
 */
const updateApi = async (data: any) =>
    post<any>({ url: URL.update,data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 删除商品
 * 
 * @description 删除指定的商品记录
 * @param {any} params - 包含商品ID的参数对象
 * @returns {Promise<any>} 返回删除结果的Promise，通常包含删除状态
 */
const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params, headers: {} });

/**
 * 获取商品详情
 * 
 * @description 获取指定商品的详细信息
 * @param {any} params - 包含商品ID的参数对象
 * @returns {Promise<any>} 返回商品详情数据的Promise
 */
const detailApi = async (params: any) => get<any>({ url: URL.detail, params: params, headers: {} });

/**
 * 更新商品浏览量
 * 
 * @description 增加指定商品的浏览次数，通常在用户查看商品详情时调用
 * @param {any} params - 包含商品ID的参数对象
 * @returns {Promise<any>} 返回更新结果的Promise，通常包含更新状态
 */
const updateViewsApi = async (params: any) => post<any>({ url: URL.updateViews, params: params, headers: {} });

export { listApi, createApi, updateApi, deleteApi, detailApi, updateViewsApi };

export { URL }
