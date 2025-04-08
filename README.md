# 基于SpringBoot+Vue的电商平台系统

## 项目概述

本项目是一个基于SpringBoot和Vue开发的全功能电商平台系统，采用前后端分离架构。系统支持用户从浏览商品、加入购物车到下单支付的完整购物流程，同时提供完善的后台管理功能。

## 系统架构

系统采用经典的前后端分离架构：
- **前端**：基于Vue 3框架构建的SPA应用
- **后端**：基于SpringBoot的RESTful API服务
- **数据库**：MySQL关系型数据库

![系统架构图](系统架构示意图将在论文中展示)

## 功能模块

### 1. 前台用户系统
- **用户认证**：注册、登录、找回密码
- **商品浏览**：首页展示、商品列表、商品详情、商品搜索
- **分类导航**：多级商品分类
- **购物车**：添加商品、修改数量、删除商品、结算
- **订单管理**：订单创建、订单支付、订单详情、订单列表
- **用户中心**：个人信息管理、收货地址管理、订单历史、收藏商品管理
- **评论系统**：商品评论与评分

### 2. 后台管理系统
- **控制面板**：系统概览、统计数据
- **商品管理**：添加商品、编辑商品、上下架商品、删除商品
- **订单管理**：订单列表、订单详情、订单状态管理
- **用户管理**：用户列表、用户详情、账号状态管理
- **分类管理**：分类添加、编辑、删除、层级管理
- **标签管理**：标签CRUD
- **广告管理**：轮播图管理、广告位管理
- **通知管理**：系统通知设置
- **数据统计**：销售统计、用户行为分析
- **系统日志**：操作日志、错误日志、登录日志

## 技术栈

### 前端技术
- **核心框架**：Vue 3
- **开发语言**：TypeScript
- **UI框架**：Ant Design Vue 3.x
- **状态管理**：Pinia
- **路由管理**：Vue Router 4.x
- **HTTP客户端**：Axios
- **构建工具**：Vite
- **编辑器**：WangEditor (富文本编辑)
- **数据可视化**：ECharts

### 后端技术
- **核心框架**：Spring Boot 2.5.5
- **ORM框架**：MyBatis-Plus 3.5.2
- **数据库**：MySQL 8.0
- **项目管理**：Maven
- **JSON处理**：Gson
- **工具库**：Lombok、Apache Commons

### 开发与部署环境
- **开发环境**：macOS
- **JDK版本**：Java 8
- **Node版本**：适配Vue 3的Node版本
- **部署方式**：传统服务器部署

## 数据库设计

系统核心数据表包括：
- 用户表 (用户信息)
- 商品表 (商品详情)
- 分类表 (多级分类)
- 标签表 (商品标签)
- 订单表 (订单信息)
- 订单项表 (订单商品明细)
- 购物车表 (购物车项)
- 地址表 (用户收货地址)
- 评论表 (商品评价)
- 收藏表 (用户收藏)
- 轮播图表 (首页轮播)
- 广告表 (广告信息)
- 通知表 (系统通知)
- 日志表 (系统各类日志)

## 系统特点

1. **前后端分离架构**：采用当前流行的前后端分离架构，提高开发效率和系统可维护性
2. **RESTful API设计**：后端API遵循RESTful设计规范，提供标准化的接口
3. **响应式布局**：前端UI适配不同设备屏幕尺寸
4. **多级分类系统**：支持三级分类结构，满足复杂商品分类需求
5. **完整的用户体验**：从商品浏览到下单支付的完整购物流程
6. **丰富的后台管理**：全面的后台管理功能，方便系统维护
7. **系统监控日志**：完善的日志记录功能，便于问题排查和系统优化

## 系统安全性设计

1. **用户认证**：基于Token的用户认证机制
2. **数据校验**：前后端双重数据验证
3. **SQL防注入**：使用MyBatis-Plus参数绑定，防止SQL注入
4. **敏感数据加密**：用户密码加密存储
5. **操作日志记录**：系统操作日志完整记录，便于审计

## 性能优化措施

1. **前端优化**：
   - 组件按需加载
   - 静态资源优化
   - 路由懒加载

2. **后端优化**：
   - 数据库查询优化
   - 接口响应数据精简
   - 适当使用缓存

3. **数据库优化**：
   - 索引设计
   - 分页查询
   - 批量操作

## 系统测试

系统测试采用手动功能测试，主要测试用例包括：
- 用户注册登录测试
- 商品浏览与搜索测试
- 购物车功能测试
- 订单流程测试
- 后台管理功能测试
- 系统安全性测试

## 未来展望

1. 引入Docker容器化部署
2. 集成支付系统对接
3. 添加数据分析功能
4. 优化移动端体验
5. 增强系统安全性 

## 论文大纲

### 第1章 引言
- 1.1 研究背景
- 1.2 研究的目的与意义
- 1.3 国内外研究现状
- 1.4 研究内容
- 1.5 论文组织结构

### 第2章 相关技术
- 2.1 xxx技术概述
- 2.2 xxx技术
- 2.3 xxx技术
- ...

### 第3章 系统分析（8-10页）
- 3.1 可行性分析（半页-1页）
  - 3.1.1 技术可行性
  - 3.1.2 经济可行性
  - 3.1.3 社会可行性
- 3.2 需求分析
  - 3.2.1 系统场景描述
  - 3.2.2 功能分析
  - 3.2.3 角色分析
- 3.3 系统用例建模
  - 3.3.1 系统用例
  - 3.3.2 系统用例规约
- 3.4 系统主要类模型

### 第4章 系统设计
- 4.1 系统总体结构
- 4.2 主要功能模块
- 4.3 系统活动图
- 4.4 系统时序图
- 4.5 数据库设计
  - 4.5.1 数据库关系图
  - 4.5.2 数据库的物理设计

### 第5章 系统实现
- 5.1 系统总体功能说明
- 5.2 功能模块实现

### 第6章 系统测试
- 6.1 测试方法
- 6.2 功能测试
- 6.3 测试用例设计
- 6.4 小结

### 第7章 总结与展望
- 7.1 总结
- 7.2 展望

## 论文进度
- [x] 项目总体描述（README.md）
- [x] 第1章 引言
- [x] 第2章 相关技术
- [ ] 第3章 系统分析
- [ ] 第4章 系统设计
- [ ] 第5章 系统实现
- [ ] 第6章 系统测试
- [ ] 第7章 总结与展望 