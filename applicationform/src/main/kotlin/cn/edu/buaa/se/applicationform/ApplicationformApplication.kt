package cn.edu.buaa.se.applicationform

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@SpringBootApplication
@EnableEurekaClient
@MapperScan("cn/edu/buaa/se/applicationform")
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ApplicationformApplication


fun main(args: Array<String>) {
    runApplication<ApplicationformApplication>(*args)
}
