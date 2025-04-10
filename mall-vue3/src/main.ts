/**
 * 应用入口模块
 * 
 * 该模块是Vue应用的入口文件，负责应用的初始化和配置。
 * 主要功能包括：
 * - 创建Vue应用实例
 * - 注册全局插件和组件
 * - 配置路由和状态管理
 * - 引入全局样式
 * - 启动应用
 * 
 * @module main
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

/**
 * 导入Vue核心功能
 */
import { createApp } from 'vue';

/**
 * 导入应用根组件
 */
import App from './App.vue';

/**
 * 导入路由配置
 */
import router from './router';

/**
 * 导入状态管理配置
 */
import piniaStore from './store';

/**
 * 导入应用启动配置
 */
import bootstrap from './core/bootstrap';

/**
 * 导入全局样式
 */
import '/@/styles/reset.less';
import '/@/styles/index.less';

/**
 * 导入Ant Design Vue组件库
 */
import Antd from 'ant-design-vue';

/**
 * 创建Vue应用实例
 */
const app = createApp(App);

/**
 * 注册全局插件和配置
 * 
 * 按顺序注册以下功能：
 * 1. Ant Design Vue组件库
 * 2. 路由系统
 * 3. 状态管理
 * 4. 应用启动配置
 */
app.use(Antd);
app.use(router);
app.use(piniaStore);
app.use(bootstrap);

/**
 * 挂载应用到DOM
 * 
 * 将Vue应用挂载到id为'app'的DOM元素上
 */
app.mount('#app');
