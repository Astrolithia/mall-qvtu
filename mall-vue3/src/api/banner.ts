/**
 * 轮播图Banner API模块
 * 
 * 该模块提供轮播图相关的API请求方法，包括轮播图列表获取、创建、更新和删除功能。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 轮播图通常用于网站首页和营销活动页面的图片展示。
 * 
 * @module api/banner
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import { get, post } from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有轮播图相关API的URL路径，便于统一管理和维护
 */
enum URL {
    list = '/api/banner/list',
    create = '/api/banner/create',
    update = '/api/banner/update',
    delete = '/api/banner/delete',
}

/**
 * 获取轮播图列表
 * 
 * @description 获取系统中的轮播图列表数据
 * @param {any} params - 查询参数对象，可包含状态、位置等筛选条件
 * @returns {Promise<any>} 返回轮播图列表数据的Promise
 */
const listApi = async (params: any) =>
    get<any>({ url: URL.list, params: params, data: {}, headers: {} });

/**
 * 创建新轮播图
 * 
 * @description 创建新的轮播图记录，支持图片上传
 * @param {any} data - 轮播图数据，包括标题、图片、链接、排序、状态等信息
 * @returns {Promise<any>} 返回创建结果的Promise
 */
const createApi = async (data: any) =>
    post<any>({
        url: URL.create,
        params: {},
        data: data,
        headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' }
    });

/**
 * 更新轮播图信息
 * 
 * @description 更新现有轮播图的信息，支持图片更新
 * @param {any} data - 更新的轮播图数据，必须包含轮播图ID
 * @returns {Promise<any>} 返回更新结果的Promise
 */
const updateApi = async (data: any) =>
    post<any>({
        url: URL.update,
        data: data,
        headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' }
    });

/**
 * 删除轮播图
 * 
 * @description 根据轮播图ID删除指定轮播图记录
 * @param {any} params - 包含轮播图ID的参数对象
 * @returns {Promise<any>} 返回删除结果的Promise
 */
const deleteApi = async (params: any) =>
    post<any>({ url: URL.delete, params: params, headers: {} });

export { listApi, createApi, updateApi, deleteApi }; 