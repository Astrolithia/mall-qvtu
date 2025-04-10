/**
 * 认证工具模块
 * 
 * 该模块提供用户认证相关的工具函数和常量。
 * 主要用于管理用户token的存储、获取和清除操作。
 * 支持token的本地存储管理，包括设置、获取、验证和清除功能。
 * 
 * @module utils/auth
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

/**
 * Token存储键名
 * 
 * 用于在localStorage中存储token的键名
 * @constant {string}
 */
const TokenKey = 'fast-token';

/**
 * Token前缀
 * 
 * 用于在请求头中添加token时的前缀
 * @constant {string}
 */
const TokenPrefix = 'Bearer ';

/**
 * 检查用户是否已登录
 * 
 * 通过检查localStorage中是否存在token来判断用户登录状态
 * 
 * @function isLogin
 * @returns {boolean} 返回用户是否已登录
 * @example
 * if (isLogin()) {
 *   // 用户已登录，执行相关操作
 * }
 */
const isLogin = () => {
  return !!localStorage.getItem(TokenKey);
};

/**
 * 获取用户token
 * 
 * 从localStorage中获取存储的用户token
 * 
 * @function getToken
 * @returns {string|null} 返回用户token，如果不存在则返回null
 * @example
 * const token = getToken();
 * if (token) {
 *   // 使用token进行认证
 * }
 */
const getToken = () => {
  return localStorage.getItem(TokenKey);
};

/**
 * 设置用户token
 * 
 * 将用户token存储到localStorage中
 * 
 * @function setToken
 * @param {string} token - 要存储的用户token
 * @example
 * setToken('user-token-123');
 */
const setToken = (token: string) => {
  localStorage.setItem(TokenKey, token);
};

/**
 * 清除用户token
 * 
 * 从localStorage中移除用户token
 * 通常用于用户登出时调用
 * 
 * @function clearToken
 * @example
 * clearToken(); // 用户登出时调用
 */
const clearToken = () => {
  localStorage.removeItem(TokenKey);
};

export { TokenPrefix, isLogin, getToken, setToken, clearToken };
