package cn.edu.buaa.se.oauth2

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.TokenEnhancer
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import org.springframework.stereotype.Component
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableAuthorizationServer
class OAuth2Config : AuthorizationServerConfigurerAdapter() {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    lateinit var userDetailsService: CustomUserDetailsService


    @Value("\${auth-service.secret}")
    lateinit var secret: String

    @Value("\${auth-service.jks-path}")
    lateinit var jksPath: String

    @Value("\${auth-service.jks-secret}")
    lateinit var jksSecret: String

    @Value("\${auth-service.jks-alias}")
    lateinit var jksAlias: String

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory().withClient("zuul")
                .secret(passwordEncoder.encode(secret))
                .scopes("service")
                .authorizedGrantTypes("refresh_token", "password")
                .accessTokenValiditySeconds(3600)

    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(jwtTokenStore())
                .accessTokenConverter(jwtAccessTokenConverter())
                .tokenEnhancer(jwtTokenEnhancerChain())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
    }

    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
    }

    @Bean
    fun jwtTokenStore(): TokenStore = JwtTokenStore(jwtAccessTokenConverter())

    @Bean
    fun jwtAccessTokenConverter(): JwtAccessTokenConverter {
        val keyStoreKeyFactory = KeyStoreKeyFactory(ClassPathResource(jksPath), jksSecret.toCharArray())
        val converter = JwtAccessTokenConverter()
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair(jksAlias))
        return converter
    }

    @Bean
    fun jwtTokenEnhancer(): CustomTokenEnhancer = CustomTokenEnhancer()

    @Bean
    fun jwtTokenEnhancerChain(): TokenEnhancerChain {
        val chain = TokenEnhancerChain()
        chain.setTokenEnhancers(
                mutableListOf(jwtTokenEnhancer(), jwtAccessTokenConverter())
        )
        return chain
    }

}

@Component
class CustomTokenEnhancer : TokenEnhancer {
    override fun enhance(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): OAuth2AccessToken {
        // return accessToken
        val at: DefaultOAuth2AccessToken? = accessToken as? DefaultOAuth2AccessToken
        val user: User = (authentication.principal as? User)!!
        at?.additionalInformation = mutableMapOf(
                Pair("uid",user.id)
        ) as Map<String, Any>?
        return (at as? OAuth2AccessToken)!!

    }
}

@Component
class CustomUserDetailsService : UserDetailsService {

    @Autowired
    lateinit var userMapper: UserMapper

    override fun loadUserByUsername(username: String): UserDetails {
        return userMapper.selectOne(
                QueryWrapper<User?>().eq("username", username)
        ) ?: throw UsernameNotFoundException("username not found")
    }
}

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class CustomWebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Autowired
    lateinit var userDetailsService: CustomUserDetailsService

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    override fun configure(http: HttpSecurity) {
        http
                .csrf().and().httpBasic().disable()
                .authorizeRequests()
                .antMatchers("/v2/api-docs/**").permitAll()
                .anyRequest().authenticated()
    }

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        provider.isHideUserNotFoundExceptions = false
        return provider
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
            .apis(RequestHandlerSelectors.basePackage("org.springframework.security.oauth"))
            .paths(PathSelectors.any()).build()


    fun apiinfo(): ApiInfo = ApiInfoBuilder()
            .title("Expert Resource Sharing Platform ( OAuth2 APIs )")
            .version(version)
            .build()
}