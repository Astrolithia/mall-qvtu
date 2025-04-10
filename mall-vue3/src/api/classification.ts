/**
 * 商品分类 API 模块
 * 
 * 该模块提供商品分类相关的API请求方法，包括分类列表获取、树形结构获取、子分类查询、创建、更新和删除功能。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 商品分类是电商平台的基础数据结构，用于对商品进行归类和组织。
 * 
 * @module api/classification
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import { get, post } from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有商品分类相关API的URL路径，便于统一管理和维护
 */
enum URL {
  userList = '/api/classification/list',
  tree = '/api/classification/tree',
  children = '/api/classification/children',
  create = '/api/classification/create',
  update = '/api/classification/update',
  delete = '/api/classification/delete',
}

/**
 * 获取分类列表
 * 
 * @description 获取所有商品分类列表，不包含层级关系
 * @param {any} params - 查询参数对象，可包含筛选条件
 * @returns {Promise<any>} 返回分类列表数据的Promise
 */
const listApi = async (params: any) => get<any>({ url: URL.userList, params: params, data: {}, headers: {} });

/**
 * 获取分类树结构
 * 
 * @description 获取分类的树形结构数据，包含完整的父子层级关系
 * @param {any} params - 查询参数对象，可包含根节点ID等条件
 * @returns {Promise<any>} 返回分类树形结构数据的Promise
 */
const treeApi = async (params: any) => get<any>({ url: URL.tree, params: params, data: {}, headers: {} });

/**
 * 获取子分类列表
 * 
 * @description 根据父分类ID获取其直接子分类列表
 * @param {any} params - 查询参数对象，通常包含父分类ID
 * @returns {Promise<any>} 返回子分类列表数据的Promise
 */
const childrenApi = async (params: any) => get<any>({ url: URL.children, params: params, data: {}, headers: {} });

/**
 * 创建新分类
 * 
 * @description 创建新的商品分类记录
 * @param {any} data - 分类数据，包括名称、父分类ID、排序、图标等信息
 * @returns {Promise<any>} 返回创建结果的Promise
 */
const createApi = async (data: any) =>
  post<any>({ url: URL.create, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 更新分类信息
 * 
 * @description 更新现有分类的信息
 * @param {any} params - 包含分类ID的参数对象
 * @param {any} data - 更新的分类数据，包括名称、排序等信息
 * @returns {Promise<any>} 返回更新结果的Promise
 */
const updateApi = async (params: any, data: any) =>
  post<any>({ url: URL.update, params: params, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 删除分类
 * 
 * @description 根据分类ID删除指定分类记录
 * @param {any} params - 包含分类ID的参数对象
 * @returns {Promise<any>} 返回删除结果的Promise
 */
const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params, headers: {} });

export { listApi, treeApi, childrenApi, createApi, updateApi, deleteApi };
