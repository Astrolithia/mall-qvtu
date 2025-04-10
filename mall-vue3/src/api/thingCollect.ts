/**
 * 商品收藏 API 模块
 * 
 * 该模块提供商品收藏相关的API请求方法，包括用户收藏商品、取消收藏和获取收藏列表等功能。
 * 主要用于电商系统中用户的收藏夹功能，支持用户保存感兴趣的商品。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/thingCollect
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import { get, post } from '/@/utils/http/axios';
import { UserState } from '/@/store/modules/user/types';

/**
 * API路径枚举
 * 
 * 定义所有商品收藏相关API的URL路径，便于统一管理和维护
 */
enum URL {
    userCollectList = '/api/thingCollect/getUserCollectList',
    collect = '/api/thingCollect/collect',
    unCollect = '/api/thingCollect/unCollect',
}

/**
 * 收藏商品
 * 
 * @description 将指定商品添加到用户的收藏列表
 * @param {any} data - 收藏对象，包含用户ID和商品ID
 * @returns {Promise<any>} 返回收藏结果的Promise，通常包含收藏状态
 */
const collectApi = async (data: any) => post<any>({ url: URL.collect, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 取消收藏
 * 
 * @description 从用户的收藏列表中移除指定商品
 * @param {any} params - 包含用户ID和商品ID的参数对象
 * @returns {Promise<any>} 返回取消收藏结果的Promise，通常包含操作状态
 */
const unCollectApi = async (params: any) => post<any>({ url: URL.unCollect, params: params, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 获取用户收藏列表
 * 
 * @description 获取指定用户的商品收藏列表
 * @param {any} params - 包含用户ID的参数对象，可选包含分页信息
 * @returns {Promise<any>} 返回收藏列表数据的Promise
 */
const userCollectListApi = async (params: any) => get<any>({ url: URL.userCollectList, params: params });

export { collectApi, unCollectApi, userCollectListApi };
