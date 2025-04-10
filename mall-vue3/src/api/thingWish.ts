/**
 * 商品心愿单 API 模块
 * 
 * 该模块提供商品心愿单相关的API请求方法，包括添加心愿商品、取消心愿和获取心愿列表等功能。
 * 主要用于电商系统中用户的愿望清单功能，支持用户记录想要购买的商品。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/thingWish
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
 * 定义所有商品心愿单相关API的URL路径，便于统一管理和维护
 */
enum URL {
    userWishList = '/api/thingWish/getUserWishList',
    wish = '/api/thingWish/wish',
    unWish = '/api/thingWish/unWish',
}

/**
 * 添加心愿商品
 * 
 * @description 将指定商品添加到用户的心愿单列表
 * @param {any} data - 心愿对象，包含用户ID和商品ID
 * @returns {Promise<any>} 返回添加结果的Promise，通常包含操作状态
 */
const wishApi = async (data: any) => post<any>({ url: URL.wish, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 取消心愿商品
 * 
 * @description 从用户的心愿单列表中移除指定商品
 * @param {any} params - 包含用户ID和商品ID的参数对象
 * @returns {Promise<any>} 返回取消心愿结果的Promise，通常包含操作状态
 */
const unWishApi = async (params: any) => post<any>({ url: URL.unWish, params: params, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 获取用户心愿单列表
 * 
 * @description 获取指定用户的商品心愿单列表
 * @param {any} params - 包含用户ID的参数对象，可选包含分页信息
 * @returns {Promise<any>} 返回心愿单列表数据的Promise
 */
const userWishListApi = async (params: any) => get<any>({ url: URL.userWishList, params: params });

export { wishApi, unWishApi, userWishListApi };
