/**
 * 系统日志 API 模块
 * 
 * 该模块提供系统日志相关的API请求方法，包括登录日志、操作日志和错误日志的查询功能。
 * 主要用于系统监控、用户行为分析和问题排查。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/log
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import {get, post} from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有日志相关API的URL路径，便于统一管理和维护
 */
enum URL {
    loginLogList = '/api/opLog/loginLogList',
    opLogList = '/api/opLog/list',
    errorLogList = '/api/errorLog/list',
}

/**
 * 获取登录日志列表
 * 
 * @description 获取系统用户的登录记录，包含登录时间、IP地址、登录状态等信息
 * @param {any} params - 查询参数对象，可包含时间范围、用户ID等筛选条件
 * @returns {Promise<any>} 返回登录日志列表数据的Promise
 */
const listLoginLogApi = async (params: any) =>
    get<any>({url: URL.loginLogList, params: params, data: {}, headers: {}});

/**
 * 获取操作日志列表
 * 
 * @description 获取系统用户的操作记录，包含操作类型、操作内容、操作时间等信息
 * @param {any} params - 查询参数对象，可包含时间范围、用户ID、操作类型等筛选条件
 * @returns {Promise<any>} 返回操作日志列表数据的Promise
 */
const listOpLogListApi = async (params: any) =>
    get<any>({url: URL.opLogList, params: params, data: {}, headers: {}});

/**
 * 获取错误日志列表
 * 
 * @description 获取系统运行过程中产生的错误记录，包含错误类型、错误信息、堆栈信息等
 * @param {any} params - 查询参数对象，可包含时间范围、错误级别等筛选条件
 * @returns {Promise<any>} 返回错误日志列表数据的Promise
 */
const listErrorLogListApi = async (params: any) =>
    get<any>({url: URL.errorLogList, params: params, data: {}, headers: {}});

export {listLoginLogApi, listOpLogListApi, listErrorLogListApi};
