package cn.edu.buaa.se.hi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class HiApplication

fun main(args: Array<String>) {
    runApplication<HiApplication>(*args)
}
