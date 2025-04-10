/**
 * 用户状态类型定义 模块
 * 
 * 该模块定义用户状态管理中使用的各种类型和接口。
 * 包含用户角色类型、用户状态接口和登录表单接口等类型定义。
 * 为状态管理提供类型安全和代码提示支持。
 * 
 * @module store/modules/user/types
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

/**
 * 角色类型
 * 
 * 定义系统中可能的用户角色类型
 * '' - 未设置角色
 * '*' - 所有权限
 * 'admin' - 管理员角色
 * 'user' - 普通用户角色
 */
export type RoleType = '' | '*' | 'admin' | 'user';

/**
 * 用户状态接口
 * 
 * 定义Pinia用户状态仓库的状态结构，包含普通用户和管理员的状态属性
 */
export interface UserState {
  // 普通用户属性
  user_id: any;         // 用户ID
  user_name: any;       // 用户名
  user_token: any;      // 访问令牌
  user_avatar: any;     // 用户头像
  user_nickname: any;   // 用户昵称

  // 管理员属性
  admin_user_id: any;    // 管理员ID
  admin_user_name: any;  // 管理员用户名
  admin_user_token: any; // 管理员访问令牌
}

/**
 * 登录表单接口
 * 
 * 定义登录请求所需的表单数据结构
 */
export interface LoginForm {
  username: string;  // 登录用户名
  password: string;  // 登录密码
}
