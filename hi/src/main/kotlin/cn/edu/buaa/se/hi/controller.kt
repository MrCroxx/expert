package cn.edu.buaa.se.hi

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RefreshScope
class HiController {
    @Value("\${hi.hi-word}")
    lateinit var hiWord: String

    @Value("\${server.port}")
    lateinit var port: String

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/hi")
    fun hi(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val logger = LoggerFactory.getLogger(this.javaClass)
        logger.info(authentication.toString())
        return "$hiWord, ${authentication.principal}, from port: $port"
    }

}