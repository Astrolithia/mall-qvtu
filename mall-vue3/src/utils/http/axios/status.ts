/**
 * HTTP 状态码处理模块
 * 
 * 该模块提供HTTP状态码的映射和错误消息处理功能。
 * 主要用于统一处理HTTP请求中的各种错误状态，提供友好的错误提示信息。
 * 支持常见的HTTP状态码，包括客户端错误(4xx)和服务器错误(5xx)。
 * 
 * @module utils/http/axios/status
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

/**
 * 根据HTTP状态码返回对应的错误消息
 * 
 * 该函数将HTTP状态码映射为友好的中文错误提示信息。
 * 支持常见的HTTP错误状态码，包括：
 * - 4xx: 客户端错误
 * - 5xx: 服务器错误
 * 
 * @param {number|string} status - HTTP状态码
 * @returns {string} 格式化后的错误消息，包含状态码和提示信息
 * 
 * @example
 * showMessage(404) // 返回: "请求出错(404)，请检查网络或联系管理员！"
 */
export const showMessage = (status: number | string): string => {
  let message = '';
  switch (status) {
    // 客户端错误 (4xx)
    case 400:
      message = '请求错误(400)';
      break;
    case 401:
      message = '未授权，请重新登录(401)';
      break;
    case 403:
      message = '拒绝访问(403)';
      break;
    case 404:
      message = '请求出错(404)';
      break;
    case 408:
      message = '请求超时(408)';
      break;
    
    // 服务器错误 (5xx)
    case 500:
      message = '服务器错误(500)';
      break;
    case 501:
      message = '服务未实现(501)';
      break;
    case 502:
      message = '网络错误(502)';
      break;
    case 503:
      message = '服务不可用(503)';
      break;
    case 504:
      message = '网络超时(504)';
      break;
    case 505:
      message = 'HTTP版本不受支持(505)';
      break;
    
    // 其他未知错误
    default:
      message = `连接出错(${status})!`;
  }
  return `${message}，请检查网络或联系管理员！`;
};
