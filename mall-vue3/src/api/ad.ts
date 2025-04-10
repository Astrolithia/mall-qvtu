/**
 * 广告API模块
 * 
 * 该模块提供广告相关的API请求方法，包括广告列表获取、创建、更新和删除功能。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/ad
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import {get, post} from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有广告相关API的URL路径，便于统一管理和维护
 */
enum URL {
    userList = '/api/ad/list',
    create = '/api/ad/create',
    update = '/api/ad/update',
    delete = '/api/ad/delete',
}

/**
 * 获取广告列表
 * 
 * @description 根据查询参数获取广告列表数据
 * @param {any} params - 查询参数对象，可包含分页、筛选条件等
 * @returns {Promise<any>} 返回广告列表数据的Promise
 */
const listApi = async (params: any) =>
    get<any>({url: URL.userList, params: params, data: {}, headers: {}});

/**
 * 创建新广告
 * 
 * @description 创建新的广告记录，支持文件上传
 * @param {any} data - 广告数据，包括标题、图片、链接等信息
 * @returns {Promise<any>} 返回创建结果的Promise
 */
const createApi = async (data: any) =>
    post<any>({
        url: URL.create,
        params: {},
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });

/**
 * 更新广告信息
 * 
 * @description 更新现有广告记录的信息，支持文件上传
 * @param {any} data - 更新的广告数据，必须包含广告ID
 * @returns {Promise<any>} 返回更新结果的Promise
 */
const updateApi = async (data: any) =>
    post<any>({
        url: URL.update,
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });

/**
 * 删除广告
 * 
 * @description 根据广告ID删除指定广告记录
 * @param {any} params - 包含广告ID的参数对象
 * @returns {Promise<any>} 返回删除结果的Promise
 */
const deleteApi = async (params: any) =>
    post<any>({url: URL.delete, params: params, headers: {}});

export {listApi, createApi, updateApi, deleteApi};
