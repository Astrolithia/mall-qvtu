/**
 * Axios HTTP 请求模块
 * 
 * 该模块封装了基于Axios的HTTP请求功能，提供统一的请求配置、拦截器和请求方法。
 * 主要用于处理前后端数据交互，包括请求拦截、响应拦截、错误处理等功能。
 * 支持普通用户和管理员两种身份认证，自动处理token的添加和错误状态码。
 * 
 * @module utils/http/axios
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError, InternalAxiosRequestConfig } from 'axios';
import { showMessage } from './status';
import { IResponse } from './type';
import { getToken } from '/@/utils/auth';
import { TokenPrefix } from '/@/utils/auth';
import {ADMIN_USER_TOKEN, USER_TOKEN, BASE_URL} from '/@/store/constants'

/**
 * 创建Axios实例
 * 
 * 配置基础URL和超时时间，用于所有HTTP请求
 */
const service: AxiosInstance = axios.create({
  // baseURL: import.meta.env.BASE_URL + '',
  baseURL: BASE_URL + '',
  timeout: 15000,
});

/**
 * 请求拦截器
 * 
 * 在发送请求前添加认证token
 * 支持普通用户和管理员两种身份认证
 */
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加管理员token
    config.headers.ADMINTOKEN = localStorage.getItem(ADMIN_USER_TOKEN)
    // 添加普通用户token
    config.headers.TOKEN = localStorage.getItem(USER_TOKEN)

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  },
);

/**
 * 响应拦截器
 * 
 * 处理响应数据，统一处理成功和错误状态
 * 根据状态码进行相应的错误处理
 */
service.interceptors.response.use(
  (response: AxiosResponse) => {
    if(response.status == 200) {
      if(response.data.code == 0 || response.data.code == 200) {
        return response
      }else {
        return Promise.reject(response.data)
      }
    } else {
      return Promise.reject(response.data)
    }
  },
  // 请求失败处理
  (error: any) => {
    console.log(error.response.status)
    if(error.response.status == 404) {
      // todo: 处理404错误
    } else if(error.response.status == 403) {
      // todo: 处理403错误
    }
    return Promise.reject(error)
  },
);

/**
 * 通用请求方法
 * 
 * 封装axios请求，统一处理响应和错误
 * @template T 响应数据类型
 * @param config 请求配置
 * @returns Promise<T> 响应数据
 */
const request = <T = any>(config: AxiosRequestConfig): Promise<T> => {
  const conf = config;
  return new Promise((resolve, reject) => {
    service.request<any, AxiosResponse<IResponse>>(conf).then((res: AxiosResponse<IResponse>) => {
      const data = res.data
      resolve(data as T);
    }).catch(err => {
      reject(err)
    });
  });
};

/**
 * GET请求方法
 * 
 * @template T 响应数据类型
 * @param config 请求配置
 * @returns Promise<T> 响应数据
 */
export function get<T = any>(config: AxiosRequestConfig): Promise<T> {
  return request({ ...config, method: 'GET' });
}

/**
 * POST请求方法
 * 
 * @template T 响应数据类型
 * @param config 请求配置
 * @returns Promise<T> 响应数据
 */
export function post<T = any>(config: AxiosRequestConfig): Promise<T> {
  return request({ ...config, method: 'POST' });
}

export default request;

export type { AxiosInstance, AxiosResponse };
