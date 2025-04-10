/**
 * 用户状态管理 模块
 * 
 * 该模块使用Pinia状态管理库实现用户认证功能，包括普通用户和管理员的登录、登出操作。
 * 主要用于管理用户身份信息和访问令牌，支持前后台用户系统分离。
 * 状态变更时自动同步到localStorage，确保页面刷新后用户的登录状态不丢失。
 * 
 * @module store/modules/user
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import { defineStore } from 'pinia';
import {loginApi as adminLogin, userLoginApi} from '/@/api/user';
import { setToken, clearToken } from '/@/utils/auth';
import { UserState, LoginForm } from './types';
import {USER_ID, USER_NAME, USER_TOKEN, ADMIN_USER_ID,ADMIN_USER_NAME,ADMIN_USER_TOKEN} from "/@/store/constants";

/**
 * 用户状态管理
 * 
 * 使用Pinia defineStore创建的用户状态管理仓库，管理普通用户和管理员用户的认证状态
 */
export const useUserStore = defineStore('user', {
  /**
   * 状态定义
   * 
   * 初始化用户和管理员的状态，优先从localStorage读取现有数据
   */
  state: (): UserState => ({
    // 普通用户状态
    user_id: localStorage.getItem(USER_ID) || undefined,
    user_name: localStorage.getItem(USER_NAME) || undefined,
    user_token: localStorage.getItem(USER_TOKEN) || undefined,
    user_avatar: localStorage.getItem('user_avatar') || undefined,
    user_nickname: localStorage.getItem('user_nickname') || undefined,

    // 管理员用户状态
    admin_user_id: localStorage.getItem(ADMIN_USER_ID) || undefined,
    admin_user_name: localStorage.getItem(ADMIN_USER_NAME) || undefined,
    admin_user_token: localStorage.getItem(ADMIN_USER_TOKEN) || undefined,
  }),
  
  /**
   * 计算属性
   */
  getters: {},
  
  /**
   * 操作方法
   * 
   * 提供修改用户状态的方法，如登录和登出
   */
  actions: {
    /**
     * 普通用户登录
     * 
     * @description 调用用户登录API，成功后更新状态并保存到localStorage
     * @param {LoginForm} loginForm - 登录表单数据，包含用户名和密码
     * @returns {Promise<any>} 返回登录操作的结果
     */
    async login(loginForm: LoginForm) {
      try {
        const result = await userLoginApi(loginForm);
        if (result.code === 200) {
          // 使用$patch更新多个状态属性
          this.$patch((state)=>{
            state.user_token = result.data.token;
            state.user_id = result.data.id;
            state.user_name = result.data.username;
            state.user_avatar = result.data.avatar;
            state.user_nickname = result.data.nickname;
            console.log('state==>', state)
          })

          // 将用户信息保存到localStorage
          localStorage.setItem('user_token', result.data.token);
          localStorage.setItem('user_id', result.data.id);
          localStorage.setItem('user_name', result.data.username);
          localStorage.setItem('user_avatar', result.data.avatar);
          localStorage.setItem('user_nickname', result.data.nickname || '');
          return result;
        }
        return result;
      } catch (error) {
        return error;
      }
    },
    
    /**
     * 普通用户登出
     * 
     * @description 清除用户状态和localStorage中的用户数据
     * @returns {Promise<void>}
     */
    async logout() {
      // await userLogout();
      this.$patch((state)=>{
        // 移除localStorage中的用户数据
        localStorage.removeItem(USER_ID)
        localStorage.removeItem(USER_NAME)
        localStorage.removeItem(USER_TOKEN)
        localStorage.removeItem('user_nickname')
        localStorage.removeItem('user_avatar')

        // 清空状态
        state.user_id = undefined
        state.user_name = undefined
        state.user_token = undefined
        state.user_avatar = undefined
        state.user_nickname = undefined
      })
    },

    /**
     * 管理员登录
     * 
     * @description 调用管理员登录API，成功后更新状态并保存到localStorage
     * @param {LoginForm} loginForm - 登录表单数据，包含用户名和密码
     * @returns {Promise<any>} 返回登录操作的结果
     */
    async adminLogin(loginForm: LoginForm) {
      const result = await adminLogin(loginForm);
      console.log('result==>', result)

      if(result.code === 200) {
        // 使用$patch更新多个状态属性
        this.$patch((state)=>{
          state.admin_user_id = result.data.id
          state.admin_user_name = result.data.username
          state.admin_user_token = result.data.token
          console.log('state==>', state)
        })

        // 将管理员信息保存到localStorage
        localStorage.setItem(ADMIN_USER_TOKEN, result.data.token)
        localStorage.setItem(ADMIN_USER_NAME, result.data.username)
        localStorage.setItem(ADMIN_USER_ID, result.data.id)
      }

      return result;
    },
    
    /**
     * 管理员登出
     * 
     * @description 清除管理员状态和localStorage中的管理员数据
     * @returns {Promise<void>}
     */
    async adminLogout() {
      // await userLogout();
      this.$patch((state)=>{
        // 移除localStorage中的管理员数据
        localStorage.removeItem(ADMIN_USER_ID)
        localStorage.removeItem(ADMIN_USER_NAME)
        localStorage.removeItem(ADMIN_USER_TOKEN)

        // 清空状态
        state.admin_user_id = undefined
        state.admin_user_name = undefined
        state.admin_user_token = undefined
      })
    },
  },
});
