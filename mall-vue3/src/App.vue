/**
 * 应用根组件
 * 
 * 该组件是Vue应用的根组件，负责全局配置和布局。
 * 主要功能包括：
 * - 配置Ant Design Vue的中文语言包
 * - 提供全局路由视图容器
 * - 设置全局样式和字体
 * 
 * @component App
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

<template>
  <!-- Ant Design Vue配置提供器，设置中文语言环境 -->
  <a-config-provider :locale="zhCN">
    <!-- 路由视图容器，用于显示当前路由对应的组件 -->
    <router-view />
  </a-config-provider>
</template>

<script setup lang="ts">
/**
 * 导入Ant Design Vue的中文语言包
 * 用于设置组件的语言环境为中文
 */
import zhCN from 'ant-design-vue/es/locale/zh_CN';
</script>

<style>
/**
 * 全局样式设置
 * 
 * 设置应用的基础样式，包括：
 * - 字体族设置
 * - 应用高度设置
 */
#app {
  /* 设置全局字体 */
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  /* 设置应用高度为100%视口高度 */
  height: 100vh;
}
</style>
