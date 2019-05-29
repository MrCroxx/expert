package cn.edu.buaa.se.paper_patent

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableEurekaClient
@SpringBootApplication
@MapperScan("cn.edu.buaa.se.paper_patent")
class PaperPatentApplication

fun main(args: Array<String>) {
    runApplication<PaperPatentApplication>(*args)
}
