/**
 * 用户积分 API 模块
 * 
 * 该模块提供用户积分相关的API请求方法，包括积分记录查询和总积分获取功能。
 * 主要用于电商系统中的用户积分管理，支持积分明细查看和积分统计。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/userScore
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import { get, post } from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有用户积分相关API的URL路径，便于统一管理和维护
 */
enum URL {
  list = '/api/userScore/list',
  total = '/api/userScore/total',
}

/**
 * 获取用户积分记录列表
 * 
 * @description 获取指定用户的积分变动记录，包含积分来源、变动数量、变动时间等信息
 * @param {any} params - 查询参数对象，必须包含userId参数标识用户
 * @returns {Promise<any>} 返回积分记录列表数据的Promise
 */
export const getScoreListApi = async (params: any) => 
  get<any>({ url: URL.list, params: params, data: {}, headers: {} });

/**
 * 获取用户总积分
 * 
 * @description 获取指定用户的当前总积分数量
 * @param {any} params - 查询参数对象，必须包含userId参数标识用户
 * @returns {Promise<any>} 返回用户总积分数据的Promise
 */
export const getTotalScoreApi = async (params: any) => 
  get<any>({ url: URL.total, params: params, data: {}, headers: {} }); 