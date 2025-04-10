/**
 * HTTP 请求类型定义模块
 * 
 * 该模块定义了HTTP请求相关的TypeScript接口和类型。
 * 主要用于规范请求配置、响应数据结构和用户认证相关的数据类型。
 * 包含请求选项、响应数据、用户登录等接口定义。
 * 
 * @module utils/http/axios/type
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

/**
 * 请求配置选项接口
 * 
 * 定义HTTP请求的配置选项，用于控制请求的处理行为。
 * 
 * @interface RequestOptions
 * @property {boolean} [isTransformResponse] - 是否处理响应结果，默认为true
 */
export interface RequestOptions {
  // Whether to process the request result
  isTransformResponse?: boolean;
}

/**
 * 统一响应数据结构接口
 * 
 * 定义后端返回的统一响应数据格式。
 * 所有HTTP请求的响应都会遵循此接口定义的结构。
 * 
 * @interface IResponse
 * @template T - 响应数据的实际类型
 * @property {number|string} code - 响应状态码，0或200表示成功
 * @property {T} result - 响应数据主体
 * @property {string} message - 响应消息，通常用于错误提示
 * @property {string|number} status - HTTP状态码
 */
export interface IResponse<T = any> {
  code: number | string;
  result: T;
  message: string;
  status: string | number;
}

/**
 * 用户登录请求接口
 * 
 * 定义用户登录时需要的请求参数结构。
 * 用于规范登录接口的请求数据格式。
 * 
 * @interface ILogin
 * @property {string} username - 用户账号
 * @property {string} password - 用户密码
 */
export interface ILogin {
  /** 用户账号 */
  username: string;
  /** 用户密码 */
  password: string;
}
