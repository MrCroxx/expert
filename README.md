# Expert Resource Sharing Platform
## 模块简介
- eureka：Eureka服务发现服务器。
- config：分布式高可用配置中心，使用RabbitMQ，支持Spring Cloud Bus实时刷新配置。
- hi：测试模块，Eureka客户端、Config客户端。

TODO：
- oauth模块
- gateway模块

## 开发规范
### 分支规范
- master分支（master）：稳定版分支（勿动）。
- dev分支（dev）：开发版分支（勿动）。
- feature分支（feature/name）：添加新功能时，创建新的feature分支，新功能开发完成后，使用pull request请求合并到dev分支。
- fix分支（fix/name）：对issue中提到的bug修复时，创建新的fix分支，修复完成后，使用pull request请求合并到dev分支。
### 数据库规范
- 列名、表名单词间使用下划线"_"分割，而非使用驼峰命名法。
- 明确外键关系。
- id字段统一使用自增BIGINT(20)字段类型，以"id"命名。
### 安全规范
- 登录使用OAuth2.0规范，password模式。
- 不要在access_token中保存敏感信息。
- 不要在任何前端的本地存储中保存用户密码。
- 数据库密码加盐Hash处理，建议使用BCryptPasswordEncoder。
- 注意为需要鉴权的api加入注解。
- 不要在除登录、修改密码外的任何api中使用明文密码。
- 敏感操作使用验证码保护。