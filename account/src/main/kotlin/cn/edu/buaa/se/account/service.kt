package cn.edu.buaa.se.account

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService {
    @Autowired
    lateinit var userMapper: UserMapper;

    fun listUsers(): List<User> {
        return userMapper.selectList(QueryWrapper<User>())
    }

    fun addUser(username: String, password: String, email: String): Int {
        var encoder = BCryptPasswordEncoder()
        var newUser = User(username = username, password = encoder.encode(password.trim()), email = email, role = 1)
        userMapper.insert(newUser)
        return SUCCESS
    }

    fun info(username: String): RpUser = userMapper.selectByUsername(username).toRUser()

    fun changePassword(username: String, password: String, newpassword: String): Int {
        if (userConfirms(username)) {
            return updatePassword(username, password, newpassword)
        }
        return UNKNOWN_USER
    }

    fun updatePassword(username: String, password: String, newpassword: String): Int {
        val encoder = BCryptPasswordEncoder()
        val user: User = userMapper.selectByUsername(username)
        if (encoder.matches(password, user.password)) {
            if (encoder.matches(newpassword, user.password)) {
                return SAME_PASSWORD
            }
            userMapper.updatePassword(username, encoder.encode(newpassword).trim())
            return SUCCESS
        }
        return WRONG_PASSWORD
    }

    fun updateMail(username: String, email: String): Int {
        if (userConfirms(username)) {
            userMapper.updateEmail(username, email)
            return SUCCESS
        }
        return UNKNOWN_USER
    }

    fun updateCredit(username: String, credit: Int): Int {
        if (userConfirms(username)) {
            userMapper.updateCredit(username, credit)
            return SUCCESS
        }
        return UNKNOWN_USER
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
    lateinit var userMapper: UserMapper

    fun listExperts(): List<Expert>? {
        return expertMapper.selectList(QueryWrapper<Expert>())
    }

    fun info(username: String): Expert = expertMapper.selectByUsername(username)

    fun updateInfo(username: String, key: String, value: String): Int {
        if (userMapper.selectCount(username) == 1) {
            val expert: Expert = expertMapper.selectById(userMapper.selectByUsername(username).id)
            expertMapper.update(null, UpdateWrapper<Expert>(expert).set(key, value))
            return SUCCESS
        }
        return UNKNOWN_EXPERT
    }

    fun updateSubject(username: String, subject: String): Int {
        if (userMapper.selectCount(username) == 1) {
            expertMapper.updateSubject(subject, userMapper.selectByUsername(username).id)
            return SUCCESS
        }
        return UNKNOWN_EXPERT
    }

    fun updateEducation(username: String, education: String): Int {
        if (userMapper.selectCount(username) == 1) {
            expertMapper.updateEducation(education, userMapper.selectByUsername(username).id)
            return SUCCESS
        }
        return UNKNOWN_EXPERT
    }

    fun updateIntroduction(username: String, introduction: String): Int {
        if (userMapper.selectCount(username) == 1) {
            expertMapper.updateIntroduction(introduction, userMapper.selectByUsername(username).id)
            return SUCCESS
        }
        return UNKNOWN_EXPERT
    }
}