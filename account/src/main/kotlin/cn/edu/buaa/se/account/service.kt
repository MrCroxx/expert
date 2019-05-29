package cn.edu.buaa.se.account

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService {
    @Autowired
    lateinit var userMapper: UserMapper;

    fun listUsers(): List<User> {
        return userMapper.selectList(QueryWrapper<User>())
    }

    fun addUser(username: String, password: String, email: String): Int {
        val encoder = BCryptPasswordEncoder()
        val newUser = User(username = username, password = encoder.encode(password.trim()), email = email, role = 1)
        userMapper.insert(newUser)
        return ErrorCode.SUCCESS.code
    }

    fun info(username: String): RpUser = userMapper.selectByUsername(username).toRUser()

    fun changePassword(username: String, password: String, newpassword: String): Int {
        if (userConfirms(username)) {
            return updatePassword(username, password, newpassword)
        }
        return ErrorCode.UNKNOWN_USER.code
    }

    fun updatePassword(username: String, password: String, newpassword: String): Int {
        val encoder = BCryptPasswordEncoder()
        val user: User = userMapper.selectByUsername(username)
        if (encoder.matches(password, user.password)) {
            if (encoder.matches(newpassword, user.password)) {
                return ErrorCode.SAME_PASSWORD.code
            }
            userMapper.updatePassword(username, encoder.encode(newpassword).trim())
            return ErrorCode.SUCCESS.code
        }
        return ErrorCode.WRONG_PASSWORD.code
    }

    fun updateMail(username: String, email: String): Int {
        if (userConfirms(username)) {
            userMapper.updateEmail(username, email)
            return ErrorCode.SUCCESS.code
        }
        return ErrorCode.UNKNOWN_USER.code
    }

    fun updateCredit(username: String, credit: Int): Int {
        if (userConfirms(username)) {
            userMapper.updateCredit(username, credit)
            return ErrorCode.SUCCESS.code
        }
        return ErrorCode.UNKNOWN_USER.code
    }

    fun userConfirms(username: String): Boolean {
        return (userMapper.selectCount(username) == 1)
    }

    fun userCount(username: String): Int {
        return userMapper.selectCount(username)
    }

}

@Service
class ExpertService {
    @Autowired
    lateinit var expertMapper: ExpertMapper;

    fun listExperts(): List<Expert>? {
        return expertMapper.selectList(QueryWrapper<Expert>())
    }

    fun info(username: String): Expert = expertMapper.selectByUsername(username)

    fun expertExists(uid:Long):Boolean=expertMapper.count(uid)!=0

    fun infoByUid(uid:Long):Expert=expertMapper.selectById(uid)

    fun updateSubject(id:Long, subject: String): Int {
        expertMapper.updateSubject(subject, id)
        return ErrorCode.SUCCESS.code
    }

    fun updateEducation(id:Long, education: String): Int {
        expertMapper.updateEducation(education, id)
        return ErrorCode.SUCCESS.code
    }

    fun updateIntroduction(id:Long, introduction: String): Int {
        expertMapper.updateIntroduction(introduction, id)
        return ErrorCode.SUCCESS.code
    }
}

@Service
class FollowService{
    @Autowired
    lateinit var followMapper: FollowMapper

    fun followExpert(id:Long,followed:Long,time:Date):Int{
        val newFollow=Follow(id,followed,time)
        followMapper.insert(newFollow)
        return ErrorCode.SUCCESS.code
    }
}