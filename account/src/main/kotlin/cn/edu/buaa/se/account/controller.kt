package cn.edu.buaa.se.account

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RefreshScope
@RequestMapping("/user")
class UsersController{
    @Autowired
    lateinit var userService: UserService

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/count")
    fun countUsers(username:String):ResponseBody<Int>{
        return ResponseBody(msg="",data = userService.userCount(username))
    }

    @GetMapping("/list")
    fun listAll():ResponseBody<List<User>>{
        return ResponseBody(msg="",data=userService.listUsers())
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @RequestMapping("/changepassword")
    fun updatePassword(password: String,newpassword: String):ResponseBody<Nothing?>{
        val rdata:Int
        val username:String = SecurityContextHolder.getContext().authentication.name
        if(userService.userConfirms(username)) {
            rdata=userService.updatePassword(username, password, newpassword)
        }
        else{
            rdata= UNKNOWN_USER
        }
        return ResponseBody(rdata,msg="",data=null)
    }

    @RequestMapping("/register")
    fun register(username: String,password:String):ResponseBody<Nothing?>{
        val rdata:Int
        if(!userService.userConfirms(username)) {
            rdata=userService.addUser(username, password)
        }
        else {
            rdata = USER_EXISTS
        }
        return ResponseBody(rdata,msg="",data=null)
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @RequestMapping("/changemail")
    fun changeEmail(email:String):ResponseBody<Nothing?>{
        val username=SecurityContextHolder.getContext().authentication.name
        val rdata=userService.updateMail(username,email)
        return ResponseBody(rdata,msg="",data=null)
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @RequestMapping("/perchasecredit")
    fun perchaseCredits(credit:Int):ResponseBody<Nothing?>{
        val username=SecurityContextHolder.getContext().authentication.name
        val rdata=userService.updateCredit(username,credit)
        return ResponseBody(rdata,msg="",data=null)
    }

}

@RestController
@RefreshScope
@RequestMapping("/expert")
class ExpertController{
    @Autowired
    lateinit var expertService:ExpertService

    @GetMapping("/list")
    fun listAll():ResponseBody<List<Expert>?>{
        val list= expertService.listExperts()
        return ResponseBody(msg="",data=list)
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/updateinfo")
    fun updateInfo(key:String,value:String):ResponseBody<Nothing?>{
        val username=SecurityContextHolder.getContext().authentication.name
        val rdata= expertService.updateInfo(username,key,value)
        return ResponseBody(rdata,msg="",data=null)
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/updatesubject")
    fun updateSubject(subject: String):ResponseBody<Nothing?>{
        val username=SecurityContextHolder.getContext().authentication.name
        val rdata= expertService.updateSubject(username,subject)
        return ResponseBody(rdata,msg="",data=null)
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/updateecudation")
    fun updateEducation(education: String):ResponseBody<Nothing?>{
        val username=SecurityContextHolder.getContext().authentication.name
        val rdata= expertService.updateEducation(username,education)
        return ResponseBody(rdata,msg="",data=null)
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/updateiintroduction")
    fun updateIntroduction(introduction: String):ResponseBody<Nothing?>{
        val username=SecurityContextHolder.getContext().authentication.name
        val rdata= expertService.updateIntroduction(username,introduction)
        return ResponseBody(rdata,msg="",data=null)
    }
}