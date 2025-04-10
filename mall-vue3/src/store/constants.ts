/**
 * 常量定义 模块
 * 
 * 该模块定义应用中使用的全局常量，包括API基础URL和本地存储键名。
 * 主要用于统一管理应用中的各种常量，避免硬编码和重复定义。
 * 集中管理用户和管理员相关的存储键名，便于状态持久化处理。
 * 
 * @module store/constants
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

/**
 * API基础URL
 * 
 * 定义系统API的基础URL，用于构建完整的API请求地址
 * 包含开发环境（本地）和生产环境的URL配置
 */
const BASE_URL = 'http://127.0.0.1:9100'
// const BASE_URL = 'http://1.117.171.66:9100'

/**
 * 普通用户相关常量
 * 
 * 定义与普通用户相关的localStorage键名，用于存储用户登录状态
 */
const USER_ID = 'user_id'           // 用户ID键名
const USER_NAME = 'user_name'       // 用户名键名
const USER_TOKEN = 'user_token'     // 用户令牌键名
const USER_AVATAR = 'user_avatar'   // 用户头像键名

/**
 * 管理员用户相关常量
 * 
 * 定义与管理员用户相关的localStorage键名，用于存储管理员登录状态
 */
const ADMIN_USER_ID = 'admin_user_id'         // 管理员ID键名
const ADMIN_USER_NAME = 'admin_user_name'     // 管理员用户名键名
const ADMIN_USER_TOKEN = 'admin_user_token'   // 管理员令牌键名


/**
 * 导出所有常量
 * 
 * 使这些常量可以在应用的其他模块中使用
 */
export {BASE_URL, USER_TOKEN, USER_NAME, USER_ID, USER_AVATAR, ADMIN_USER_ID,ADMIN_USER_NAME,ADMIN_USER_TOKEN }
