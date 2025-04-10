/**
 * 路由管理 模块
 * 
 * 该模块负责应用的路由配置、导航守卫和路由权限控制。
 * 主要实现前台用户和后台管理员的路由访问控制，包括登录状态检查和重定向。
 * 处理路由之间的跳转，确保用户只能访问其权限范围内的页面。
 * 
 * @module router/index
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import { createRouter, createWebHashHistory } from 'vue-router';
import root from './root';

import { ADMIN_USER_TOKEN, USER_TOKEN } from '/@/store/constants'

/**
 * 路由权限白名单
 * 
 * 定义无需登录即可访问的路由名称列表
 */
const allowList = ['adminLogin', 'login', 'register', 'portal', 'search', 'detail', 'cart', '403', '404']

/**
 * 前台登录路由路径
 * 
 * 普通用户的登录页面路径，用于未登录用户重定向
 */
const loginRoutePath = '/index/login'

/**
 * 后台登录路由路径
 * 
 * 管理员的登录页面路径，用于未登录管理员重定向
 */
const adminLoginRoutePath = '/adminLogin'


/**
 * 创建路由实例
 * 
 * 使用哈希历史模式创建Vue Router实例，并引入根路由配置
 */
const router = createRouter({
  history: createWebHashHistory(),
  routes: root,
});

/**
 * 全局前置守卫
 * 
 * @description 在路由跳转前执行，负责验证用户权限和执行重定向
 * @param {RouteLocationNormalized} to - 目标路由
 * @param {RouteLocationNormalized} from - 来源路由
 * @param {NavigationGuardNext} next - 控制路由跳转的函数
 */
router.beforeEach(async (to, from, next) => {
  console.log(to, from)

  /** 后台路由访问控制 **/
  if (to.path.startsWith('/admin')) {
    // 检查管理员是否已登录（判断token是否存在）
    if (localStorage.getItem(ADMIN_USER_TOKEN)) {
      // 已登录状态下，如果访问登录页则重定向到首页
      if (to.path === adminLoginRoutePath) {
        next({ path: '/' })
      } else {
        // 正常访问请求的页面
        next()
      }
    } else {
      // 未登录状态下的处理
      if (allowList.includes(to.name as string)) {
        // 在免登录名单，直接进入
        next()
      } else {
        // 不在免登录名单，重定向到管理员登录页，并携带原目标路径
        next({ path: adminLoginRoutePath, query: { redirect: to.fullPath } })
      }
    }
  }

  /** 前台路由访问控制 **/
  if (to.path.startsWith('/index')) {
    // 检查用户是否已登录（判断token是否存在）
    if (localStorage.getItem(USER_TOKEN)) {
      // 已登录状态下，如果访问登录页则重定向到首页
      if (to.path === loginRoutePath) {
        next({ path: '/' })
      } else {
        // 正常访问请求的页面
        next()
      }
    } else {
      // 未登录状态下的处理
      if (allowList.includes(to.name as string)) {
        // 在免登录名单，直接进入
        next()
      } else {
        // 不在免登录名单，重定向到用户登录页，并携带原目标路径
        next({ path: loginRoutePath, query: { redirect: to.fullPath } })
      }
    }
  }

});

/**
 * 全局后置钩子
 * 
 * @description 在路由跳转完成后执行，用于处理页面滚动位置等
 * @param {RouteLocationNormalized} _to - 当前访问的路由
 */
router.afterEach((_to) => {
  // 回到顶部，确保新页面始终从顶部开始显示
  document.getElementById("html")?.scrollTo(0, 0)
});

export default router;
