/**
 * 系统通知 API 模块
 * 
 * 该模块提供系统通知相关的API请求方法，包括通知的查询、创建、更新和删除功能。
 * 主要用于系统公告、用户消息等通知内容的管理。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/notice
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import {get, post} from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有通知相关API的URL路径，便于统一管理和维护
 */
enum URL {
    list = '/api/notice/list',
    create = '/api/notice/create',
    update = '/api/notice/update',
    delete = '/api/notice/delete',
}

/**
 * 获取通知列表
 * 
 * @description 获取系统通知列表，包含通知标题、内容、发布时间等信息
 * @param {any} params - 查询参数对象，可包含时间范围、通知类型等筛选条件
 * @returns {Promise<any>} 返回通知列表数据的Promise
 */
const listApi = async (params: any) =>
    get<any>({url: URL.list, params: params, data: {}, headers: {}});

/**
 * 创建系统通知
 * 
 * @description 创建新的系统通知，需要通知内容和相关参数
 * @param {any} data - 通知对象，包含标题、内容、类型等必要信息
 * @returns {Promise<any>} 返回创建结果的Promise，通常包含创建状态和新通知ID
 */
const createApi = async (data: any) =>
    post<any>({
        url: URL.create,
        params: {},
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });

/**
 * 更新系统通知
 * 
 * @description 更新已有的系统通知内容或状态
 * @param {any} data - 通知对象，包含要更新的字段和通知ID
 * @returns {Promise<any>} 返回更新结果的Promise，通常包含更新状态
 */
const updateApi = async (data: any) =>
    post<any>({
        url: URL.update,
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });

/**
 * 删除系统通知
 * 
 * @description 删除指定的系统通知
 * @param {any} params - 包含通知ID的参数对象
 * @returns {Promise<any>} 返回删除结果的Promise，通常包含删除状态
 */
const deleteApi = async (params: any) =>
    post<any>({url: URL.delete, params: params, headers: {}});

export {listApi, createApi, updateApi, deleteApi};
