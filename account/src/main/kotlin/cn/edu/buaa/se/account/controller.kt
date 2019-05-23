package cn.edu.buaa.se.account

import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RefreshScope
@RequestMapping("/user")
class UsersController {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var followService: FollowService


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/")
    fun user(): ResponseBody<RpUser> {
        val username: String = SecurityContextHolder.getContext().authentication.name
        return ResponseBody(msg = "", data = userService.info(username))
    }

    @ApiOperation(value = "修改用户密码",notes = "修改用户密码")
    @ApiImplicitParams(
        ApiImplicitParam(name="password",value = "当前密码",dataType = "String"),
        ApiImplicitParam(name = "newpassword",value = "新密码",dataType = "String")
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/password/change")
    fun updatePassword(@RequestBody rqPassword: RqPassword): ResponseBody<Nothing?> {
        val rdata: Int
        val username: String = SecurityContextHolder.getContext().authentication.name
        if (userService.userConfirms(username)) {
            rdata = userService.updatePassword(username, rqPassword.password, rqPassword.newpassword)
        } else {
            rdata = UNKNOWN_USER
        }
        return ResponseBody(rdata, msg = "", data = null)
    }

    @ApiOperation(value = "注册",notes = "注册新账号")
    @ApiImplicitParams(
            ApiImplicitParam(name ="username",value = "用户名",dataType = "String"),
            ApiImplicitParam(name = "password",value = "密码",dataType = "String"),
            ApiImplicitParam(name = "email",value = "邮箱",dataType = "String")
    )
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

    @ApiOperation(value = "修改邮箱",notes = "修改邮箱")
    @ApiImplicitParam(name = "email",value = "邮箱",dataType = "String")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/email/change")
    fun changeEmail(@RequestBody rpUser: RpUser): ResponseBody<Nothing?> {
        val username = SecurityContextHolder.getContext().authentication.name
        val rdata = userService.updateMail(username, rpUser.email)
        return ResponseBody(rdata, msg = "", data = null)
    }

    @ApiOperation(value = "购买积分",notes = "增加购买的积分")
    @ApiImplicitParam(name = "credit",value = "增加的积分数",dataType = "Int")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/credit/purchase")
    fun purchaseCredits(@RequestBody rpUser: RpUser): ResponseBody<Nothing?> {
        val username = SecurityContextHolder.getContext().authentication.name
        val rdata = userService.updateCredit(username, rpUser.credit)
        return ResponseBody(rdata, msg = "", data = null)
    }

    @ApiOperation(value = "关注专家",notes = "关注一个专家")
    @ApiImplicitParam(name = "followed",value = "要关注的专家",dataType = "Long")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/follow")
    fun follow(@RequestBody rpExpert: RpExpert):ResponseBody<Nothing?>{

        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata=followService.followExpert(uid,rpExpert.id, Date())
        return ResponseBody(rdata,msg = "",data = null)
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

    @ApiOperation(value = "修改专业",notes = "修改专业")
    @ApiImplicitParam(name = "subject",value = "专业",dataType = "String")
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/subject/update")
    fun updateSubject(@RequestBody rpExpert: RpExpert): ResponseBody<Nothing?> {

        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata = expertService.updateSubject(uid, rpExpert.subject)
        return ResponseBody(rdata, msg = "", data = null)
    }

    @ApiOperation(value = "修改学历",notes = "修改学历")
    @ApiImplicitParam(name = "education",value = "学历",dataType = "String")
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/education/update")
    fun updateEducation(@RequestBody rpExpert: RpExpert): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata = expertService.updateEducation(uid, rpExpert.education)
        return ResponseBody(rdata, msg = "", data = null)
    }

    @ApiOperation(value = "修改个人介绍",notes = "修改个人介绍")
    @ApiImplicitParam(name = "introduction",value = "个人介绍",dataType = "String")
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/introduction/update")
    fun updateIntroduction(@RequestBody rpExpert: RpExpert): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata = expertService.updateIntroduction(uid, rpExpert.introduction)
        return ResponseBody(rdata, msg = "", data = null)
    }
}
