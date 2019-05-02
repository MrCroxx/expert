package cn.edu.buaa.se.gateway

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso
import org.springframework.cloud.netflix.zuul.filters.RouteLocator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger.web.SwaggerResource
import springfox.documentation.swagger.web.SwaggerResourcesProvider
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
@Order(101)
class CustomSecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
    }
}

@Configuration
@EnableSwagger2
@Primary
class DocumentationConfig : SwaggerResourcesProvider {

    @Value("\${app.version}")
    lateinit var version: String

    @Autowired
    lateinit var routeLocator: RouteLocator

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

    override fun get(): MutableList<SwaggerResource> {
        val resources = arrayListOf<SwaggerResource>()
        routeLocator.routes.forEach {
            resources.add(swaggerResource(it.id, "${it.prefix}/v2/api-docs", "1.0.0-dev"))
        }
        return resources
    }

    fun swaggerResource(name: String, location: String, version: String): SwaggerResource {
        val swaggerResource = SwaggerResource()
        swaggerResource.name = name
        swaggerResource.location = location
        swaggerResource.swaggerVersion = version
        return swaggerResource

    }

}