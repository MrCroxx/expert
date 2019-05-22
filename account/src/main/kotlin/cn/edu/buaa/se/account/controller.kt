package cn.edu.buaa.se.account

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RefreshScope
@RequestMapping("/user")
class UsersController {
    @Autowired
    lateinit var userService: UserService

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/")
    fun user(): ResponseBody<RpUser> {
        val username: String = SecurityContextHolder.getContext().authentication.name
        return ResponseBody(msg = "", data = userService.info(username))
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/count")
    fun countUsers(username: String): ResponseBody<Int> {
        return ResponseBody(msg = "", data = userService.userCount(username))
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/password/change")
    fun updatePassword(password: String, newpassword: String): ResponseBody<Nothing?> {
        val rdata: Int
        val username: String = SecurityContextHolder.getContext().authentication.name
        if (userService.userConfirms(username)) {
            rdata = userService.updatePassword(username, password, newpassword)
        } else {
            rdata = UNKNOWN_USER
        }
        return ResponseBody(rdata, msg = "", data = null)
    }

    @PostMapping("/register")
    fun register(@RequestBody rqNewUser: RqNewUser): ResponseBody<Nothing?> {
        val rdata: Int
        if (!userService.userConfirms(rqNewUser.username)) {
            rdata = userService.addUser(rqNewUser.username, rqNewUser.password, rqNewUser.email)
        } else {
            rdata = USER_EXISTS
        }
        return ResponseBody(rdata, msg = "", data = null)
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/email/change")
    fun changeEmail(email: String): ResponseBody<Nothing?> {
        val username = SecurityContextHolder.getContext().authentication.name
        val rdata = userService.updateMail(username, email)
        return ResponseBody(rdata, msg = "", data = null)
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/credit/purchase")
    fun purchaseCredits(credit: Int): ResponseBody<Nothing?> {
        val username = SecurityContextHolder.getContext().authentication.name
        val rdata = userService.updateCredit(username, credit)
        return ResponseBody(rdata, msg = "", data = null)
    }

}

@RestController
@RefreshScope
@RequestMapping("/expert")
class ExpertController {


    @Autowired
    lateinit var expertService: ExpertService

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/")
    fun expert(): ResponseBody<Expert> {
        val username = SecurityContextHolder.getContext().authentication.name
        return ResponseBody<Expert>(msg = "", data = expertService.info(username))
    }

    @GetMapping("/{username}")
    fun expert(@PathVariable username: String): ResponseBody<Expert> = ResponseBody<Expert>(msg = "", data = expertService.info(username))

    // TODO:危险操作!!!!!!!!!!!
    /*
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/updateinfo")
    fun updateInfo(key: String, value: String): ResponseBody<Nothing?> {
        val username = SecurityContextHolder.getContext().authentication.name
        val rdata = expertService.updateInfo(username, key, value)
        return ResponseBody(rdata, msg = "", data = null)
    }
    */


    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/subject/update")
    fun updateSubject(subject: String): ResponseBody<Nothing?> {
        val username = SecurityContextHolder.getContext().authentication.name
        val rdata = expertService.updateSubject(username, subject)
        return ResponseBody(rdata, msg = "", data = null)
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/education/update")
    fun updateEducation(education: String): ResponseBody<Nothing?> {
        val username = SecurityContextHolder.getContext().authentication.name
        val rdata = expertService.updateEducation(username, education)
        return ResponseBody(rdata, msg = "", data = null)
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/introduction/update")
    fun updateIntroduction(introduction: String): ResponseBody<Nothing?> {
        val username = SecurityContextHolder.getContext().authentication.name
        val rdata = expertService.updateIntroduction(username, introduction)
        return ResponseBody(rdata, msg = "", data = null)
    }
}