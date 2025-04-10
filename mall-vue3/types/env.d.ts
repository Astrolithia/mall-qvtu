/**
 * 环境类型声明文件
 * 
 * 该文件为TypeScript提供项目中使用的环境变量和模块的类型声明。
 * 主要功能包括：
 * - 引用Vite客户端类型
 * - 声明Vue单文件组件的类型
 * - 声明虚拟模块的类型
 * - 提供TypeScript编译器识别特殊模块的能力
 * 
 * @module types/env
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

/**
 * 引用Vite客户端类型声明
 * 
 * 包含Vite特有的环境变量和API类型，如import.meta.env
 */
/// <reference types="vite/client" />

/**
 * Vue单文件组件(.vue)类型声明
 * 
 * 使TypeScript能够识别并正确处理.vue文件的导入
 * 将.vue文件视为返回Vue组件的模块
 */
declare module '*.vue' {
  import { DefineComponent } from 'vue';
  const component: DefineComponent<{}, {}, any>;
  export default component;
}

/**
 * 虚拟模块类型声明
 * 
 * 处理由Vite插件生成的虚拟模块导入
 * 例如vite-plugin-pages等插件生成的虚拟模块
 */
declare module 'virtual:*' {
  const result: any;
  export default result;
}
