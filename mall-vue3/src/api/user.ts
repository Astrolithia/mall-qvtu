/**
 * 用户管理 API 模块
 * 
 * 该模块提供用户相关的API请求方法，包括用户登录、注册、信息管理等功能。
 * 主要用于电商系统中的用户认证和用户信息管理，支持管理员和普通用户的操作。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/user
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

// 权限问题后期增加
import { get, post } from '/@/utils/http/axios';
import { UserState } from '/@/store/modules/user/types';
// import axios from 'axios';

/**
 * API路径枚举
 * 
 * 定义所有用户相关API的URL路径，便于统一管理和维护
 */
enum URL {
    login = '/api/user/login',
    userList = '/api/user/list',
    detail = '/api/user/detail',
    create = '/api/user/create',
    update = '/api/user/update',
    delete = '/api/user/delete',
    userLogin = '/api/user/userLogin',
    userRegister = '/api/user/userRegister',
    updateUserPwd = '/api/user/updatePwd',
    updateUserInfo = '/api/user/updateUserInfo'
}

/**
 * 登录响应接口
 * 
 * 定义用户登录成功后返回的数据结构
 */
interface LoginRes {
    token: string;
}

/**
 * 登录请求接口
 * 
 * 定义用户登录时需要的参数结构
 */
export interface LoginData {
    username: string;
    password: string;
}

/**
 * 管理员登录
 * 
 * @description 管理员登录系统，获取访问令牌
 * @param {LoginData} data - 登录信息对象，包含用户名和密码
 * @returns {Promise<LoginRes>} 返回登录结果的Promise，包含访问令牌
 */
const loginApi = async (data: LoginData) => post<any>({ url: URL.login, data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 获取用户列表
 * 
 * @description 获取系统用户列表，通常用于管理后台
 * @param {any} params - 查询参数对象，可包含分页、搜索条件等
 * @returns {Promise<any>} 返回用户列表数据的Promise
 */
const listApi = async (params: any) => get<any>({ url: URL.userList, params: params, data: {}, headers: {} });

/**
 * 获取用户详情
 * 
 * @description 获取指定用户的详细信息
 * @param {any} params - 包含用户ID的参数对象
 * @returns {Promise<any>} 返回用户详情数据的Promise
 */
const detailApi = async (params: any) => get<any>({ url: URL.detail, params: params, data: {}, headers: {} });

/**
 * 创建用户
 * 
 * @description 创建新的用户账号
 * @param {any} data - 用户对象，包含用户名、密码、邮箱等必要信息
 * @returns {Promise<any>} 返回创建结果的Promise，通常包含创建状态和新用户ID
 */
const createApi = async (data: any) => post<any>({ url: URL.create, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 更新用户信息
 * 
 * @description 更新已有用户的信息
 * @param {any} data - 用户对象，包含要更新的字段和用户ID
 * @returns {Promise<any>} 返回更新结果的Promise，通常包含更新状态
 */
const updateApi = async (data: any) => post<any>({ url: URL.update, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 删除用户
 * 
 * @description 删除指定的用户账号
 * @param {any} params - 包含用户ID的参数对象
 * @returns {Promise<any>} 返回删除结果的Promise，通常包含删除状态
 */
const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params, headers: {} });

/**
 * 用户登录
 * 
 * @description 普通用户登录系统，获取访问令牌
 * @param {LoginData} data - 登录信息对象，包含用户名和密码
 * @returns {Promise<LoginRes>} 返回登录结果的Promise，包含访问令牌
 */
const userLoginApi = async (data: LoginData) => post<any>({ url: URL.userLogin, data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 用户注册
 * 
 * @description 新用户注册账号
 * @param {any} data - 注册信息对象，包含用户名、密码、邮箱等必要信息
 * @returns {Promise<any>} 返回注册结果的Promise，通常包含注册状态
 */
const userRegisterApi = async (data: any) => post<any>({ url: URL.userRegister, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

/**
 * 更新用户密码
 * 
 * @description 修改用户的登录密码
 * @param {any} params - 包含用户ID和新密码的参数对象
 * @returns {Promise<any>} 返回更新结果的Promise，通常包含更新状态
 */
const updateUserPwdApi = async (params: any) => post<any>({ url: URL.updateUserPwd, params: params });

/**
 * 更新用户信息
 * 
 * @description 用户更新自己的个人信息
 * @param {any} data - 用户信息对象，包含要更新的字段
 * @returns {Promise<any>} 返回更新结果的Promise，通常包含更新状态
 */
const updateUserInfoApi = async (data: any) => post<any>({ url: URL.updateUserInfo, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

export { loginApi, listApi, detailApi, createApi, updateApi, deleteApi, userLoginApi, userRegisterApi, updateUserPwdApi, updateUserInfoApi};
