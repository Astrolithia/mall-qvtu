/**
 * 全局组件类型声明文件
 * 
 * 该文件由unplugin-vue-components插件自动生成，用于声明全局可用的Vue组件。
 * 主要功能包括：
 * - 声明Ant Design Vue组件的类型
 * - 声明Vue Router组件的类型
 * - 提供TypeScript智能提示支持
 * 
 * @module types/components
 * @author unplugin-vue-components
 * @version 1.0
 * @date 2024-03-27
 */

// generated by unplugin-vue-components
// We suggest you to commit this file into source control
// Read more: https://github.com/vuejs/core/pull/3399
import '@vue/runtime-core'

export {}

declare module '@vue/runtime-core' {
  export interface GlobalComponents {
    // 基础组件
    AButton: typeof import('ant-design-vue/es')['Button']
    AInput: typeof import('ant-design-vue/es')['Input']
    AInputNumber: typeof import('ant-design-vue/es')['InputNumber']
    AInputPassword: typeof import('ant-design-vue/es')['InputPassword']
    AInputSearch: typeof import('ant-design-vue/es')['InputSearch']
    ATextarea: typeof import('ant-design-vue/es')['Textarea']
    ASpace: typeof import('ant-design-vue/es')['Space']
    ATag: typeof import('ant-design-vue/es')['Tag']
    ADivider: typeof import('ant-design-vue/es')['Divider']
    ASwitch: typeof import('ant-design-vue/es')['Switch']

    // 布局组件
    ARow: typeof import('ant-design-vue/es')['Row']
    ACol: typeof import('ant-design-vue/es')['Col']
    ALayout: typeof import('ant-design-vue/es')['Layout']
    ALayoutContent: typeof import('ant-design-vue/es')['LayoutContent']
    ALayoutHeader: typeof import('ant-design-vue/es')['LayoutHeader']
    ALayoutSider: typeof import('ant-design-vue/es')['LayoutSider']
    ACard: typeof import('ant-design-vue/es')['Card']

    // 导航组件
    AMenu: typeof import('ant-design-vue/es')['Menu']
    AMenuItem: typeof import('ant-design-vue/es')['MenuItem']
    ASubMenu: typeof import('ant-design-vue/es')['SubMenu']
    ATabs: typeof import('ant-design-vue/es')['Tabs']
    ATabPane: typeof import('ant-design-vue/es')['TabPane']
    APagination: typeof import('ant-design-vue/es')['Pagination']
    ADropdown: typeof import('ant-design-vue/es')['Dropdown']

    // 数据展示组件
    ATable: typeof import('ant-design-vue/es')['Table']
    ATree: typeof import('ant-design-vue/es')['Tree']
    ATreeSelect: typeof import('ant-design-vue/es')['TreeSelect']
    ATreeSelectNode: typeof import('ant-design-vue/es')['TreeSelectNode']
    ADescriptions: typeof import('ant-design-vue/es')['Descriptions']
    ADescriptionsItem: typeof import('ant-design-vue/es')['DescriptionsItem']

    // 数据录入组件
    AForm: typeof import('ant-design-vue/es')['Form']
    AFormItem: typeof import('ant-design-vue/es')['FormItem']
    ASelect: typeof import('ant-design-vue/es')['Select']
    ASelectOption: typeof import('ant-design-vue/es')['SelectOption']
    AUpload: typeof import('ant-design-vue/es')['Upload']
    AUploadDragger: typeof import('ant-design-vue/es')['UploadDragger']

    // 反馈组件
    AModal: typeof import('ant-design-vue/es')['Modal']
    ADrawer: typeof import('ant-design-vue/es')['Drawer']
    APopconfirm: typeof import('ant-design-vue/es')['Popconfirm']
    ASpin: typeof import('ant-design-vue/es')['Spin']

    // 配置组件
    AConfigProvider: typeof import('ant-design-vue/es')['ConfigProvider']

    // Vue Router 组件
    RouterLink: typeof import('vue-router')['RouterLink']
    RouterView: typeof import('vue-router')['RouterView']
  }
}
