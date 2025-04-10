/**
 * 标签管理 API 模块
 * 
 * 该模块提供标签相关的API请求方法，包括标签的查询、创建、更新和删除功能。
 * 主要用于系统内容分类、商品归类等标签体系的管理。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/tag
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import {get, post} from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有标签相关API的URL路径，便于统一管理和维护
 */
enum URL {
    userList = '/api/tag/list',
    create = '/api/tag/create',
    update = '/api/tag/update',
    delete = '/api/tag/delete',
}

/**
 * 获取标签列表
 * 
 * @description 获取系统标签列表，包含标签名称、使用次数、创建时间等信息
 * @param {any} params - 查询参数对象，可包含标签名称、创建时间等筛选条件
 * @returns {Promise<any>} 返回标签列表数据的Promise
 */
const listApi = async (params: any) =>
    get<any>({url: URL.userList, params: params, data: {}, headers: {}});

/**
 * 创建标签
 * 
 * @description 创建新的系统标签
 * @param {any} data - 标签对象，包含标签名称、描述等必要信息
 * @returns {Promise<any>} 返回创建结果的Promise，通常包含创建状态和新标签ID
 */
const createApi = async (data: any) =>
    post<any>({
        url: URL.create,
        params: {},
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });

/**
 * 更新标签
 * 
 * @description 更新已有的标签信息
 * @param {any} params - 包含标签ID的参数对象
 * @param {any} data - 标签对象，包含要更新的字段
 * @returns {Promise<any>} 返回更新结果的Promise，通常包含更新状态
 */
const updateApi = async (params: any, data: any) =>
    post<any>({
        url: URL.update,
        params: params,
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });

/**
 * 删除标签
 * 
 * @description 删除指定的标签
 * @param {any} params - 包含标签ID的参数对象
 * @returns {Promise<any>} 返回删除结果的Promise，通常包含删除状态
 */
const deleteApi = async (params: any) =>
    post<any>({url: URL.delete, params: params, headers: {}});

export {listApi, createApi, updateApi, deleteApi};
