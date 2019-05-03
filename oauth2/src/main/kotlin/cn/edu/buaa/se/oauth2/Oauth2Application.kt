package cn.edu.buaa.se.oauth2

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
@EnableEurekaClient
class Oauth2Application

fun main(args: Array<String>) {
    runApplication<Oauth2Application>(*args)
}

@RestController
class TestController {

    @Autowired
    lateinit var userMapper: UserMapper
    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @GetMapping("register")
    fun register(
            @RequestParam("username") username: String,
            @RequestParam("password") password: String,
            @RequestParam("email") email: String,
            @RequestParam("role") role: Int
    ): String {
        val user = User(
                id = null,
                _username = username,
                _password = passwordEncoder.encode(password),
                role = role,
                email = email
        )
        userMapper.insert(user)

        return "ok"
    }
/*
    @GetMapping("info")
    fun info(
            @RequestParam("username") username: String
    ): User? = userMapper.findByUsername(username)

   */
}