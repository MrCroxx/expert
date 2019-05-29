package cn.edu.buaa.se.account

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.xml.ws.Response

@RestController
@RefreshScope
@RequestMapping("/user")
class UsersController {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var followService: FollowService


    @ApiOperation(value = "获取用户信息",notes = "获取当前登录用户的信息")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/")
    fun user(): ResponseBody<RpUser> {
        val username: String = SecurityContextHolder.getContext().authentication.name
        return ResponseBody(data = userService.info(username))
    }

    @ApiOperation(value = "修改用户密码",notes = "修改用户密码")
    @ApiImplicitParams(
        ApiImplicitParam(name="password",value = "当前密码",dataType = "String"),
        ApiImplicitParam(name = "newpassword",value = "新密码",dataType = "String")
    )
    @ApiResponses(
            ApiResponse(code=20000,message = "success"),
            ApiResponse(code = 40102,message = "不存在的用户"),
            ApiResponse(code=40103,message = "密码错误"),
            ApiResponse(code=40104,message = "新旧密码不能相同")
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/password/change")
    fun updatePassword(@RequestBody rqPassword: RqPassword): ResponseBody<Nothing?> {
        val rdata: Int
        val username: String = SecurityContextHolder.getContext().authentication.name
        if (userService.userConfirms(username)) {
            rdata = userService.updatePassword(username, rqPassword.password, rqPassword.newpassword)
        } else {
            rdata = ErrorCode.UNKNOWN_USER.code
        }
        return ResponseBody(rdata, data = null)
    }

    @ApiOperation(value = "注册",notes = "注册新账号")
    @ApiImplicitParams(
            ApiImplicitParam(name ="username",value = "用户名",dataType = "String"),
            ApiImplicitParam(name = "password",value = "密码",dataType = "String"),
            ApiImplicitParam(name = "email",value = "邮箱",dataType = "String")
    )
    @ApiResponses(
            ApiResponse(code=20000,message = "success"),
            ApiResponse(code = 40101,message = "用户名已存在")
    )
    @PostMapping("/register")
    fun register(@RequestBody rqNewUser: RqNewUser): ResponseBody<Nothing?> {
        val rdata: Int
        if (!userService.userConfirms(rqNewUser.username)) {
            rdata = userService.addUser(rqNewUser.username, rqNewUser.password, rqNewUser.email)
        } else {
            rdata = ErrorCode.USER_EXISTS.code
        }
        return ResponseBody(rdata, data = null)
    }

    @ApiOperation(value = "修改邮箱",notes = "修改邮箱")
    @ApiImplicitParam(name = "email",value = "邮箱",dataType = "String")
    @ApiResponses(
            ApiResponse(code=20000,message = "success"),
            ApiResponse(code = 40102,message = "不存在的用户")
            )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/email/change")
    fun changeEmail(@RequestBody rpUser: RpUser): ResponseBody<Nothing?> {
        val username = SecurityContextHolder.getContext().authentication.name
        val rdata = userService.updateMail(username, rpUser.email)
        return ResponseBody(rdata,  data = null)
    }

    @ApiOperation(value = "购买积分",notes = "增加购买的积分")
    @ApiImplicitParam(name = "credit",value = "增加的积分数",dataType = "Int")
    @ApiResponses(
            ApiResponse(code=20000,message = "success"),
            ApiResponse(code = 40102,message = "不存在的用户")
    )
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/credit/purchase")
    fun purchaseCredits(@RequestBody rpUser: RpUser): ResponseBody<Nothing?> {
        val username = SecurityContextHolder.getContext().authentication.name
        val rdata = userService.updateCredit(username, rpUser.credit)
        return ResponseBody(rdata, data = null)
    }

    @ApiOperation(value = "关注专家",notes = "关注一个专家")
    @ApiImplicitParam(name = "followed",value = "要关注的专家",dataType = "Long")
    @ApiResponse(code=20000,message = "success")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/follow")
    fun follow(@RequestBody rpExpert: RpExpert):ResponseBody<Nothing?>{

        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata=followService.followExpert(uid,rpExpert.id, Date())
        return ResponseBody(rdata,data = null)
    }

}

@RestController
@RefreshScope
@RequestMapping("/expert")
class ExpertController {

    @Autowired
    lateinit var expertService: ExpertService

    @ApiOperation(value="获取专家信息",notes = "获取当前登录专家的信息")
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/")
    fun expert(): ResponseBody<Expert> {
        val username = SecurityContextHolder.getContext().authentication.name
        return ResponseBody<Expert>(data = expertService.info(username))
    }

//    @GetMapping("/{username}")
//    fun expert(@PathVariable username: String): ResponseBody<Expert> = ResponseBody<Expert>(msg = "", data = expertService.info(username))

    @ApiOperation(value = "获取专家信息",notes = "通过用户id获取专家的信息")
    @ApiResponses(
            ApiResponse(code=20000,message = "success"),
            ApiResponse(code = 40105,message = "不存在的专家")
    )
    @GetMapping("/{uid}")
    fun expert(@PathVariable uid: Long): ResponseBody<Expert>{
        var rdata:Int=ErrorCode.SUCCESS.code
        if(!expertService.expertExists(uid))
            rdata=ErrorCode.UNKNOWN_EXPERT.code
        return ResponseBody<Expert>(rdata,data = expertService.infoByUid(uid))
    }

    @ApiOperation(value = "修改专业",notes = "修改专业")
    @ApiImplicitParam(name = "subject",value = "专业",dataType = "String")
    @ApiResponse(code=20000,message = "success")
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/subject/update")
    fun updateSubject(@RequestBody rpExpert: RpExpert): ResponseBody<Nothing?> {

        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata = expertService.updateSubject(uid, rpExpert.subject)
        return ResponseBody(rdata, data = null)
    }

    @ApiOperation(value = "修改学历",notes = "修改学历")
    @ApiImplicitParam(name = "education",value = "学历",dataType = "String")
    @ApiResponse(code=20000,message = "success")
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/education/update")
    fun updateEducation(@RequestBody rpExpert: RpExpert): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata = expertService.updateEducation(uid, rpExpert.education)
        return ResponseBody(rdata, data = null)
    }

    @ApiOperation(value = "修改个人介绍",notes = "修改个人介绍")
    @ApiImplicitParam(name = "introduction",value = "个人介绍",dataType = "String")
    @ApiResponse(code=20000,message = "success")
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/introduction/update")
    fun updateIntroduction(@RequestBody rpExpert: RpExpert): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata = expertService.updateIntroduction(uid, rpExpert.introduction)
        return ResponseBody(rdata, data = null)
    }
}
