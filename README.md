# Expert Resource Sharing Platform
## 一、模块简介
- eureka：Eureka服务发现服务器。
- config：分布式高可用配置中心，使用RabbitMQ，支持Spring Cloud Bus实时刷新配置。
- oauth模块：OAuth2.0认证服务器。
- gateway模块：基于Zuul的API网关，同时为OAuth认证客户端。
- hi：测试模块，Eureka客户端、Config客户端。

## 二、开发规范
### 2.1 分支规范
- master分支（master）：稳定版分支（勿动）。
- dev分支（dev）：开发版分支（勿动）。
- feature分支（feature/name）：添加新功能时，创建新的feature分支，新功能开发完成后，使用pull request请求合并到dev分支。
- fix分支（fix/name）：对issue中提到的bug修复时，创建新的fix分支，修复完成后，使用pull request请求合并到dev分支。
### 2.2 数据库规范
- 列名、表名单词间使用下划线"_"分割，而非使用驼峰命名法。
- 明确外键关系。
- id字段统一使用自增BIGINT(20)字段类型，以"id"命名。
### 2.3 安全规范
- 登录使用OAuth2.0规范，password模式。
- 不要在access_token中保存敏感信息。
- 不要在任何前端的本地存储中保存用户密码。
- 数据库密码加盐Hash处理，建议使用BCryptPasswordEncoder。
- 注意为需要鉴权的api加入注解。
- 不要在除登录、修改密码外的任何api中使用明文密码。
- 敏感操作使用验证码保护。

## 三、微服务开发流程
### 3.1 模块创建
1. 在根项目*expert*上右击，选择*New* > *Module*，创建新模块。
2. 模块*groupId*均为*cn.edu.buaa.se*，*artifactId*需要简洁地表达模块主要功能。
3. 模块创建后修改*pom*文件，将*parent*节点修改为根项目，*packing*属性修改为*jar*，删除默认的*properties*、*dependencies*、*dependencyManagement*与*build*节点。
4. 将除根项目*pom*包含的依赖外的*maven*依赖写入*dependencies*下，包括但不限于本节后所包含的依赖。
5. 在项目包路径下新建一下Kotlin源文件：
    1. config.kt：包含项目配置Bean。
    2. entity.kt：包含数据库实体类。
    3. dao.kt：包含DAO接口类（如MyBatis中的Mapper或JPA中的Repository等）。
    4. service.kt：包含Service的Bean。
    5. controller.kt：包含controller的Bean。
    6. model.kt：包含Request与Response的模型（注意：不要在Response的Bean中包含内部信息或敏感信息如用户的权限信息或密码等！！！）。
6. 将项目*resources*目录下的*application.properties*文件格式修改为*application.yml*，使用*yaml*格式配置；建立*bootstrap.yml*用于配置配置中心地址等优先级高的属性。
```xml
        <!-- Spring Web 基础依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- Eureka Client 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!-- 配置中心依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <!-- Spring Bus 依赖 -->
        <!-- 用于刷新配置时消息传递中间件 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>
        <!-- Spring 管理依赖 -->
        <!-- 用于刷新配置 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!-- OAuth2.0 基础依赖 -->
        <!-- 包含 Spring Security -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-oauth2</artifactId>
        </dependency>
        <!-- MyBatis Plus 依赖 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.1.1</version>
        </dependency>
        <!-- MySql 连接器依赖 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- Open Feign 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        
```
### 3.2 项目配置
#### 3.2.1 Eureka Client
在*bootstrap.yml*中添加*spring.application.name*配置、*server.port*配置与*eureka*客户端相关配置：
```yaml
spring:
  application:
    name: service-***（微服务按照service-***格式命名，其后缀要简洁地体现该微服务的功能。）

server:
  port: *****（端口号不要与其他微服务冲突，便于本地调试，不要占用常用服务端口。范围在12400-12499间。）

eureka:
  client:
    service-url:
      defaultZone: http://localhost:12300/eureka/
```
在项目启动类（位于***Application.kt中）上增加*Eureka Client*的注解：
```kotlin
@EnableEurekaClient
```
#### 3.2.2 Config Client
在*bootstrap.yml*中添加配置中心客户端需要的相关配置：
```yaml
spring:
  cloud:
    config:
      label: dev
      profile: dev
      discovery:
        enabled: true
        service-id: service-config
```
在*application.yml*中添加自动刷新配置需要的相关配置：
```yaml
spring:
  rabbitmq:
    host: 188.131.253.61
    port: 5672
    username: se
    password: se123456

  cloud:
    bus:
      enabled: true
      trace:
        enabled: true
management:
  endpoints:
    web:
      exposure:
        include: bus-refresh
```
为需要动态刷新的配置所在的Bean添加自动刷新注解：
```kotlin
@RefreshScope
```
#### 3.2.3 OAuth2 Resource Server
将*oauth2*模块的*resources*目录下的公钥文件*public.txt*拷贝到本项目的*resources*目录下。

为项目添加*OAuth2 Resource Server*需要的配置Bean。其中，*configure(http: HttpSecurity)函数中用来配置校验或放行的具体路径，请根据项目要求自行配置*（该配置中放行了Swagger2相关路径，Swagger2其他配置在下一节中介绍）：
```kotlin
@Configuration
@EnableResourceServer
class OAuth2ResourceServerConfig : ResourceServerConfigurerAdapter() {

    @Autowired
    lateinit var customAccessTokenConverter: CustomAccessTokenConverter

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers(
                        "/webjars/**",
                        "/resources/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/v2/api-docs")
                .permitAll()
    }

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.tokenServices(tokenServices())
    }

    @Bean
    fun tokenServices(): DefaultTokenServices {
        val services = DefaultTokenServices()
        services.setTokenStore(tokenStore())
        return services
    }

    @Bean
    fun tokenStore(): TokenStore = JwtTokenStore(accessTokenConverter())

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.setVerifierKey(getPublicKey())
        converter.accessTokenConverter = customAccessTokenConverter
        return converter
    }

    fun getPublicKey(): String {
        val resource = ClassPathResource("public.txt")
        val br = BufferedReader(InputStreamReader(resource.inputStream))
        return br.lines().collect(Collectors.joining("\n"))
    }

}

@Component
class CustomAccessTokenConverter : DefaultAccessTokenConverter() {
    override fun extractAuthentication(claims: MutableMap<String, *>): OAuth2Authentication {
        val authentication = super.extractAuthentication(claims)
        authentication.details = claims
        return authentication
    }
}
```
为项目的启动类添加全局方法安全注解：
```kotlin
@EnableGlobalMethodSecurity(prePostEnabled = true)
```
为需要身份鉴权的API方法上添加身份鉴权注解。身份权限格式为*ROLE_XXX*，具体值请参考*oauth2*模块下的*entity.kt*文件中的枚举类*ROLE*：
```kotlin
@PreAuthorize("hasAuthority('ROLE_XXX')")
```
为*Feign*配置全局的*Authorization*头信息，防止*Feign*调用时*Authorization*头丢失。创建配置类：
```kotlin
@Configuration
class FeignOauth2RequestInterceptor : RequestInterceptor {

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_TOKEN_TYPE = "Bearer"
    }

    override fun apply(requestTemplate: RequestTemplate) {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication
        if (authentication != null && authentication.details is OAuth2AuthenticationDetails) {
            val details: OAuth2AuthenticationDetails = authentication.details as OAuth2AuthenticationDetails
            requestTemplate.header(AUTHORIZATION_HEADER, "$BEARER_TOKEN_TYPE ${details.tokenValue}")
        }
    }
}
```
#### 3.2.4 Swagger2
在*application.yml*中插入*app.version*属性，获取*pom*文件中的*version*值：
```yaml
app:
  version: @project.version@
```
在配置中添加Swagger2配置需要的Bean：
```kotlin
@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Value("\${app.version}")
    lateinit var version: String

    @Bean
    fun createRestApi(): Docket = Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiinfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("cn.edu.buaa.se"))
            .paths(PathSelectors.any()).build()


    fun apiinfo(): ApiInfo = ApiInfoBuilder()
            .title("Expert Resource Sharing Platform ( OAuth2 APIs )")
            .version(version)
            .build()

}
```
为*model.kt*中的*Request*与*Response*的模板、*Controller*中的方法添加Swagger2描述注解，为其添加注释信息。
#### 3.2.5 数据库配置
在*application.yml*中添加数据库连接配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://188.131.253.61:3306/se
    username: se
    password: se123456!@#
    driver-class-name: com.mysql.jdbc.Driver
```
### 3.3 其他提示
#### 3.3.1 在认证上下文环境中获取用户id与权限信息
用户鉴权相关信息包含在如下对象中：
```kotlin
val authentication = SecurityContextHolder.getContext().authentication
```
通过*authentication*对象获取额外Jwt信息，如uid：
```kotlin
val authentication = SecurityContextHolder.getContext().authentication
val details = authentication.details as OAuth2AuthenticationDetails
val decodedDetails = details.decodedDetails as MutableMap<String, *>
val uid: Long = (decodedDetails["uid"] as Int).toLong()
```
**!!如果获取uid代码不成功，请根据文档3.2.3节更新资源服务器的OAuth2 Resource Server Config**
#### 3.3.2 微服务间调用
使用*Feign*调用。

