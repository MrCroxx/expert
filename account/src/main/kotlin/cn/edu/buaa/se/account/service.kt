package cn.edu.buaa.se.account

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import org.springframework.dao.DataAccessException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.annotation.Resource
import javax.jws.soap.SOAPBinding
import javax.validation.constraints.Email

@Service
class UserService{
    @Resource
    lateinit var userMapper: UserMapper;

    fun ListUsers(): List<User>? {
        return userMapper.selectList(QueryWrapper<User>())
    }

    fun AddUser(username:String,password:String):String{
        var encoder= BCryptPasswordEncoder()
        var newUser=User(username = username,password = encoder.encode(password.trim()))
        userMapper.insert(newUser)
        return "success:insert user"
    }

    fun ChangePassword(username:String,password:String,newpassword:String):String{
        if(UserConfirms(username))
        {
            UpdatePassword(username,password,newpassword)
        }
        return "fail:unknown user"
    }

    fun UpdatePassword(username:String,password:String,newpassword: String):String{
        var encoder= BCryptPasswordEncoder()
        var user:User=userMapper.selectOne(QueryWrapper<User>().eq("username",username))
        if(encoder.matches(password,user.password))
        {
            if(encoder.matches(newpassword,user.password))
            {
                return "fail:same password"
            }
            userMapper.update(null,UpdateWrapper<User>(user).set("password",encoder.encode(newpassword).trim()))
            return "success:update password"
        }
        return "fail:wrong password"
    }

    fun UpdateMail(username:String,email: String):String{
        if(UserConfirms(username))
        {
            var user:User=userMapper.selectOne(QueryWrapper<User>().eq("username",username))
            userMapper.update(null,UpdateWrapper<User>(user).set("email",email))
            return "success:update email"
        }
        return "fail:unkonwn user"
    }

    fun UpdateCredit(username:String,credit: Int):String{
        if(UserConfirms(username))
        {
            var user:User=userMapper.selectOne(QueryWrapper<User>().eq("username",username))
            userMapper.update(null,UpdateWrapper<User>(user).set("credit",credit+user.credit))
            return "success:update credit"
        }
        return "fail:unkonwn user"
    }

    fun UserConfirms(username:String):Boolean{
        return (userMapper.selectList(QueryWrapper<User>().eq("username",username)).size==1)
    }

}

@Service
class ExpertService{
    @Resource
    lateinit var expertMapper:ExpertMapper;

    fun ListExperts():List<Expert>?{
        return expertMapper.selectList(QueryWrapper<Expert>())
    }

    /**
     *
     * key:{subject, education, introduction}
     */
    fun ChangeInfo(id:Long,key:String,value:String):String{
        val expertExists:Int=expertMapper.selectList(QueryWrapper<Expert>().eq("id",id)).size
        if(expertExists==1)
        {
            var expert:Expert=expertMapper.selectOne(QueryWrapper<Expert>().eq("id",id))
            expertMapper.update(null,UpdateWrapper<Expert>(expert).set(key,value))
            return "success:update "+key
        }
        return "fail:unkonwn expert"
    }
}