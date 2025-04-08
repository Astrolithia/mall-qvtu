import { defineStore } from 'pinia';
import {loginApi as adminLogin, userLoginApi} from '/@/api/user';
import { setToken, clearToken } from '/@/utils/auth';
import { UserState } from './types';
import {USER_ID, USER_NAME, USER_TOKEN, ADMIN_USER_ID,ADMIN_USER_NAME,ADMIN_USER_TOKEN} from "/@/store/constants";

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    user_id: localStorage.getItem(USER_ID) || undefined,
    user_name: localStorage.getItem(USER_NAME) || undefined,
    user_token: localStorage.getItem(USER_TOKEN) || undefined,
    user_avatar: localStorage.getItem('user_avatar') || undefined,
    user_nickname: localStorage.getItem('user_nickname') || undefined,

    admin_user_id: localStorage.getItem(ADMIN_USER_ID) || undefined,
    admin_user_name: localStorage.getItem(ADMIN_USER_NAME) || undefined,
    admin_user_token: localStorage.getItem(ADMIN_USER_TOKEN) || undefined,
  }),
  getters: {},
  actions: {
    // 用户登录
    async login(loginForm: LoginForm) {
      try {
        const result = await userLoginApi(loginForm);
        if (result.code === 200) {
          this.$patch((state)=>{
            state.user_token = result.data.token;
            state.user_id = result.data.id;
            state.user_name = result.data.username;
            state.user_avatar = result.data.avatar;
            state.user_nickname = result.data.nickname;
            console.log('state==>', state)
          })

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
    // 用户登出
    async logout() {
      // await userLogout();
      this.$patch((state)=>{
        localStorage.removeItem(USER_ID)
        localStorage.removeItem(USER_NAME)
        localStorage.removeItem(USER_TOKEN)
        localStorage.removeItem('user_nickname')
        localStorage.removeItem('user_avatar')

        state.user_id = undefined
        state.user_name = undefined
        state.user_token = undefined
        state.user_avatar = undefined
        state.user_nickname = undefined
      })
    },

    // 管理员登录
    async adminLogin(loginForm) {
      const result = await adminLogin(loginForm);
      console.log('result==>', result)

      if(result.code === 200) {
        this.$patch((state)=>{
          state.admin_user_id = result.data.id
          state.admin_user_name = result.data.username
          state.admin_user_token = result.data.token
          console.log('state==>', state)
        })

        localStorage.setItem(ADMIN_USER_TOKEN, result.data.token)
        localStorage.setItem(ADMIN_USER_NAME, result.data.username)
        localStorage.setItem(ADMIN_USER_ID, result.data.id)
      }

      return result;
    },
    // 管理员登出
    async adminLogout() {
      // await userLogout();
      this.$patch((state)=>{
        localStorage.removeItem(ADMIN_USER_ID)
        localStorage.removeItem(ADMIN_USER_NAME)
        localStorage.removeItem(ADMIN_USER_TOKEN)

        state.admin_user_id = undefined
        state.admin_user_name = undefined
        state.admin_user_token = undefined
      })
    },
  },
});
