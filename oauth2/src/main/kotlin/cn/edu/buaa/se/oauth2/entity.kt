package cn.edu.buaa.se.oauth2

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable

@TableName("user")
data class User(
        @TableId("id", type = IdType.AUTO)
        var id: Long? = -1,
        @TableField("username")
        var _username: String = "",
        @TableField("password")
        var _password: String = "",
        @TableField("email")
        var email: String = "",
        @TableField("credit")
        var credit: Int = 0,
        @TableField("frozen_credit")
        var frozenCredit: Int = 0,
        @TableField("role")
        var role: Int = 0

) : UserDetails, Serializable {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        when(role)
        {
            3->return mutableListOf(Authority(role),Authority(role-1),Authority(role-2))
            2->return mutableListOf(Authority(role),Authority(role-1))
            else->return mutableListOf(Authority(role))
        }
    }

    override fun isEnabled(): Boolean {
        return ROLE.fromInt(role) != ROLE.ROLE_KNOWN
    }

    override fun getUsername(): String {
        return _username
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return _password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    fun setUsername(username: String) {
        _username = username
    }

    fun setPassword(password: String) {
        _password = password
    }
}

enum class ROLE constructor(var value: Int) {
    ROLE_KNOWN(0),
    ROLE_USER(1),
    ROLE_EXPERT(2),
    ROLE_ADMIN(3);


    companion object {
        fun fromInt(roleId: Int): ROLE = ROLE.values().find { it.value == roleId } ?: ROLE.ROLE_KNOWN
    }
}

data class Authority(
        val roleId: Int
) : GrantedAuthority {
    override fun getAuthority(): String {
        return ROLE.fromInt(roleId).name
    }
}