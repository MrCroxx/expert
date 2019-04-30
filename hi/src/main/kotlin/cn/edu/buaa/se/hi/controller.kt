package cn.edu.buaa.se.hi

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HiController {
    @Value("\${hi.hi-word}")
    lateinit var hi_word: String

    @Value("\${server.port}")
    lateinit var port: String

    fun hi(
            @RequestParam("name", defaultValue = "Guest") name: String
    ): String = "$hi_word, $name, from $port"

}