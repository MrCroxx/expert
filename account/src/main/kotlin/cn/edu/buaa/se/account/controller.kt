package cn.edu.buaa.se.account

import com.oracle.deploy.update.UpdateInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.dao.DataAccessException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Email

@RestController
@RefreshScope
@RequestMapping("/user")
class UsersController{
    @Autowired
    lateinit var userService: UserService
    lateinit var generator: RestResultGenerator

    @GetMapping("/all")
    fun ListAll():List<User>?{
        generator.GenResult(userService.ListUsers(),"success")
        return userService.ListUsers()
    }

    @RequestMapping("/confirm")
    fun UserExists(username:String):String?{
        return userService.UserConfirms(username).toString()
    }

    @RequestMapping("/updatepassword")
    fun UpdatePassword(username:String,password: String,newpassword: String):String{
        if(userService.UserConfirms(username)) {
            return userService.UpdatePassword(username, password, newpassword)
        }
        else{
            return "fail:unknown user"
        }
    }

    @RequestMapping("/register")
    fun Register(username: String,password:String):String?{
        if(!userService.UserConfirms(username)) {
            return userService.AddUser(username, password)
        }
        return "fail:user exists"
    }

    @RequestMapping("/changemail")
    fun ChangeEmail(username: String,email:String):String?{
        return userService.UpdateMail(username,email)
    }

    @RequestMapping("/perchasecredit")
    fun PerchaseCredits(username:String,credit:Int):String{
        return userService.UpdateCredit(username,credit)
    }

}

@RestController
@RefreshScope
@RequestMapping("/expert")
class ExpertController{
    @Autowired
    lateinit var expertService:ExpertService

    @GetMapping("/all")
    fun ListAll():List<Expert>?{
        return expertService.ListExperts()
    }

    @GetMapping("/updateinfo")
    fun UpdateInfo(id:Long,key:String,value:String):String{
        return expertService.ChangeInfo(id,key,value)
    }
}