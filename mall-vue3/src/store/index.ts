/**
 * 状态管理入口 模块
 * 
 * 该模块配置和初始化Pinia状态管理库，并导出各功能模块的状态钩子。
 * 主要用于统一管理应用的全局状态，集成持久化插件。
 * 作为应用状态管理的核心入口，连接各个功能模块的状态。
 * 
 * @module store/index
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import { createPinia } from 'pinia';
import { useAppStore } from './modules/app';
import { useUserStore } from './modules/user';
import { useCartStore } from './modules/cart';

/**
 * 导入Pinia持久化插件
 * 
 * 该插件使状态可以保存到localStorage，页面刷新后自动恢复
 */
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';

/**
 * 创建Pinia实例
 * 
 * 初始化Pinia并应用持久化插件
 */
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

/**
 * 导出状态钩子
 * 
 * 便于在组件中直接导入使用，简化导入路径
 */
export { useAppStore, useUserStore, useCartStore };

/**
 * 导出Pinia实例
 * 
 * 默认导出，用于在应用入口注册Pinia
 */
export default pinia;
