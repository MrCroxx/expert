/**
 * created by lx
 * 2019/5
 */
package cn.edu.buaa.se.account

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@SpringBootApplication
@EnableEurekaClient
@MapperScan("cn/edu/buaa/se/account")
@EnableGlobalMethodSecurity(prePostEnabled = true)
class AccountApplication

fun main(args: Array<String>) {
    runApplication<AccountApplication>(*args)
}
