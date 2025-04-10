/**
 * 系统概览 API 模块
 * 
 * 该模块提供系统概览相关的API请求方法，包括数据统计和系统信息获取功能。
 * 主要用于管理后台的数据看板和系统状态监控。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/overview
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import {get, post} from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有概览相关API的URL路径，便于统一管理和维护
 */
enum URL {
    list = '/api/overview/count',
    sysInfo= '/api/overview/sysInfo',
}

/**
 * 获取统计数据
 * 
 * @description 获取系统各项统计数据，包括用户数量、订单数量、商品数量等统计信息
 * @param {any} params - 查询参数对象，可包含时间范围等筛选条件
 * @returns {Promise<any>} 返回统计数据的Promise
 */
const listApi = async (params: any) =>
    get<any>({url: URL.list, params: params, data: {}, headers: {}});

/**
 * 获取系统信息
 * 
 * @description 获取当前系统的运行状态和配置信息
 * @param {any} params - 查询参数对象
 * @returns {Promise<any>} 返回系统信息数据的Promise，包含系统版本、运行时间等信息
 */
const sysInfoApi = async (params: any) =>
    get<any>({url: URL.sysInfo, params: params, data: {}, headers: {}});

export {listApi, sysInfoApi};
