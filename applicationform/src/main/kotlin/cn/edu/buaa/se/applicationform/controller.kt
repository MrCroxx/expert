package cn.edu.buaa.se.applicationform

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.gson.GsonProperties
import org.springframework.boot.json.GsonJsonParser
import org.springframework.boot.json.JsonParser
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.function.json.GsonMapper
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.spring.web.json.Json
import springfox.documentation.spring.web.json.JsonSerializer

@RestController
@RefreshScope
@RequestMapping("/apply")
class ApplyController{
    @Autowired
    lateinit var applyService: ApplicationApplyService

    @ApiOperation(value = "提交申请工单",notes = "三种工单的提交")
    @ApiImplicitParams(
            ApiImplicitParam(name="type",value = "申请类型",dataType = "Int"),
            ApiImplicitParam(name = "amount",value = "提现金额",dataType = "Double"),
            ApiImplicitParam(name = "name",value = "申请身份姓名",dataType = "String"),
            ApiImplicitParam(name = "organization",value = "机构",dataType = "String")
    )
    @ApiResponse(code=20000,message = "success")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/newform")
    fun newApplication(@RequestBody content: ApplicationContent):ResponseBody<Nothing?>{
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata=applyService.applyApplication(uid,content)
        return ResponseBody(rdata,data=null)
    }
}

@RestController
@RefreshScope
@RequestMapping("/examine")
class ExamineController{
    @Autowired
    lateinit var examineService: ApplicationExamineService

    @ApiOperation(value = "查看申请单",notes = "查看申请工单的详细信息")
    @ApiImplicitParam(name="id",value = "申请单编号",dataType = "Long")
    @ApiResponse(code=20000,message = "success")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/examine")
    fun examineApplication(@RequestBody rqResult: RqResult):ResponseBody<ApplicationContent>
            = ResponseBody(ErrorCode.SUCCESS.code,data=examineService.getInfo(rqResult.id))

    @ApiOperation(value = "提交申请工单",notes = "三种工单的提交")
    @ApiImplicitParams(
            ApiImplicitParam(name="id",value = "申请单编号",dataType = "Long"),
            ApiImplicitParam(name = "status",value = "审批结果",dataType = "Short")
    )
    @ApiResponse(code=20000,message = "success")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/approve")
    fun approveApplication(@RequestBody rqResult: RqResult):ResponseBody<Nothing?>{
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val aid: Long = (decodedDetails["aid"] as Int).toLong()

        val rdata=examineService.approveApplication(rqResult.id,rqResult.status,aid)
        return ResponseBody(rdata,data = null)
    }
}