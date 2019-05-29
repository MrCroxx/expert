package cn.edu.buaa.se.hi

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors


@Configuration
@EnableResourceServer
class OAuth2ResourceServerConfig : ResourceServerConfigurerAdapter() {


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
        return converter
    }

    fun getPublicKey(): String {
        val resource = ClassPathResource("public.txt")
        val br = BufferedReader(InputStreamReader(resource.inputStream))
        return br.lines().collect(Collectors.joining("\n"))
    }

}

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