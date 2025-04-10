/**
 * 收货地址API模块
 * 
 * 该模块提供用户收货地址相关的API请求方法，包括地址列表获取、创建、更新和删除功能。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/address
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

/* eslint-disable prettier/prettier */
import {get, post} from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有收货地址相关API的URL路径，便于统一管理和维护
 */
enum URL {
    userList = '/api/address/list',
    create = '/api/address/create',
    update = '/api/address/update',
    delete = '/api/address/delete',
}

/**
 * 获取收货地址列表
 * 
 * @description 根据用户ID获取收货地址列表数据
 * @param {any} params - 查询参数对象，通常包含用户ID
 * @returns {Promise<any>} 返回地址列表数据的Promise
 */
const listApi = async (params: any) =>
    get<any>({url: URL.userList, params: params, data: {}, headers: {}});

/**
 * 创建新收货地址
 * 
 * @description 为用户创建新的收货地址记录
 * @param {any} data - 地址数据，包括收件人、联系电话、详细地址、是否默认等信息
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
 * 更新收货地址信息
 * 
 * @description 更新现有收货地址的信息
 * @param {any} data - 更新的地址数据，必须包含地址ID
 * @returns {Promise<any>} 返回更新结果的Promise
 */
const updateApi = async (data: any) =>
    post<any>({
        url: URL.update,
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });

/**
 * 删除收货地址
 * 
 * @description 根据地址ID删除指定收货地址记录
 * @param {any} params - 包含地址ID的参数对象
 * @returns {Promise<any>} 返回删除结果的Promise
 */
const deleteApi = async (params: any) =>
    post<any>({url: URL.delete, params: params, headers: {}});

export {listApi, createApi, updateApi, deleteApi};
