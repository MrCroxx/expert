package cn.edu.buaa.se.hi

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RefreshScope
class HiController {
    @Value("\${hi.hi-word}")
    lateinit var hiWord: String

    @Value("\${server.port}")
    lateinit var port: String

    @PreAuthorize("hasRole('ROLE_USER')")
    //@PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/hi")
    fun hi(): String {
        val logger = LoggerFactory.getLogger(this.javaClass)

        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        logger.info(authentication.toString())
        logger.info(details.decodedDetails.toString())
        logger.info("Current uid : $uid")

        return "$hiWord, ${authentication.principal} ( current uid: $uid ), from port: ${port}."
    }

}