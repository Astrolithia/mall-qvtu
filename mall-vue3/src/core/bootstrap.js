/**
 * 应用初始化 模块
 * 
 * 该模块负责在应用启动时初始化用户状态，将localStorage中保存的用户数据恢复到内存中的状态管理中。
 * 主要用于实现用户会话的持久化，确保页面刷新后用户的登录状态不丢失。
 * 包含对普通用户和管理员用户数据的同时处理。
 * 
 * @module core/bootstrap
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

// localStorage恢复到内存

import {useUserStore} from "/@/store";
import {USER_ID, USER_NAME, USER_TOKEN, USER_AVATAR, ADMIN_USER_ID, ADMIN_USER_NAME, ADMIN_USER_TOKEN} from "/@/store/constants";

/**
 * 应用初始化函数
 * 
 * @description 在应用启动时执行，从localStorage中恢复用户和管理员的登录状态到Pinia状态管理中
 * @returns {void} 无返回值
 */
export default function Initializer () {
  // 获取用户状态管理实例
  const userStore = useUserStore()
  
  // 使用$patch批量更新状态，从localStorage中恢复用户和管理员数据
  userStore.$patch((state)=>{
    // 恢复普通用户数据
    state.user_id = localStorage.getItem(USER_ID)
    state.user_name = localStorage.getItem(USER_NAME)
    state.user_token = localStorage.getItem(USER_TOKEN)
    state.user_avatar = localStorage.getItem(USER_AVATAR)

    // 恢复管理员用户数据
    state.admin_user_id = localStorage.getItem(ADMIN_USER_ID)
    state.admin_user_name = localStorage.getItem(ADMIN_USER_NAME)
    state.admin_user_token = localStorage.getItem(ADMIN_USER_TOKEN)
    
    console.log('恢复store完毕==>', state)
  })
}
