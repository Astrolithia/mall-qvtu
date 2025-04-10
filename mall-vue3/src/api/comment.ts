/**
 * 评论 API 模块
 * 
 * 该模块提供商品评论相关的API请求方法，包括评论列表获取、创建、删除、点赞等功能。
 * 支持按商品和用户两种维度查询评论数据。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/comment
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import {get, post} from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有评论相关API的URL路径，便于统一管理和维护
 */
enum URL {
    list = '/api/comment/list',
    create = '/api/comment/create',
    delete = '/api/comment/delete',
    listThingComments = '/api/comment/listThingComments',
    listUserComments = '/api/comment/listUserComments',
    like = '/api/comment/like'
}

/**
 * 获取评论列表
 * 
 * @description 获取系统中的所有评论列表，通常用于管理后台
 * @param {any} params - 查询参数对象，可包含分页和筛选条件
 * @returns {Promise<any>} 返回评论列表数据的Promise
 */
const listApi = async (params: any) => get<any>({url: URL.list, params: params, data: {}, headers: {}});

/**
 * 创建新评论
 * 
 * @description 创建新的商品评论记录
 * @param {any} data - 评论数据，包括商品ID、用户ID、评论内容、评分等信息
 * @returns {Promise<any>} 返回创建结果的Promise
 */
const createApi = async (data: any) => post<any>({
    url: URL.create,
    params: {},
    data: data,
    headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
});

/**
 * 删除评论
 * 
 * @description 根据评论ID删除指定评论记录
 * @param {any} params - 包含评论ID的参数对象
 * @returns {Promise<any>} 返回删除结果的Promise
 */
const deleteApi = async (params: any) => post<any>({url: URL.delete, params: params, headers: {}});

/**
 * 获取商品评论列表
 * 
 * @description 根据商品ID获取该商品的评论列表
 * @param {any} params - 查询参数对象，必须包含商品ID(thingId)，可选包含排序方式等
 * @returns {Promise<any>} 返回指定商品的评论列表数据的Promise
 */
const listThingCommentsApi = async (params: any) => get<any>({url: URL.listThingComments, params: params, data: {}, headers: {}});

/**
 * 获取用户评论列表
 * 
 * @description 根据用户ID获取该用户发表的评论列表
 * @param {any} params - 查询参数对象，必须包含用户ID(userId)
 * @returns {Promise<any>} 返回指定用户的评论列表数据的Promise
 */
const listUserCommentsApi = async (params: any) => get<any>({url: URL.listUserComments, params: params, data: {}, headers: {}});

/**
 * 评论点赞
 * 
 * @description 对指定评论进行点赞或取消点赞操作
 * @param {any} params - 参数对象，包含评论ID(commentId)和操作类型
 * @returns {Promise<any>} 返回点赞操作结果的Promise
 */
const likeApi = async (params: any) => post<any>({url: URL.like, params: params, headers: {}});

export {listApi, createApi, deleteApi, listThingCommentsApi, listUserCommentsApi, likeApi};
