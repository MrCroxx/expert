package cn.edu.buaa.se.account

import org.springframework.format.annotation.DateTimeFormat
import java.util.*
import javax.swing.KeyStroke

enum class ErrorCode(val code:Int,val msg: String){
    SUCCESS(20000,"success"),
    USER_EXISTS(40101,"用户名已存在"),
    UNKNOWN_USER(40102,"没有该用户"),
    WRONG_PASSWORD(40103,"密码错误"),
    SAME_PASSWORD(40104,"新旧密码不能相同"),
    UNKNOWN_EXPERT(40105,"未知的专家");

    companion object {
        fun getMsgByCode(code: Int): String = ErrorCode.values().find { it.code == code }?.msg ?:"unknown error"
    }
}

interface IResponseBody<T> {
    var errcode: Int
    var msg: String
    var date: Date
    var data: T
}

data class ResponseBody<T>(
        override var errcode: Int = 20000,
        override var msg: String = ErrorCode.getMsgByCode(errcode),
        override var date: Date = Date(),
        override var data: T
) : IResponseBody<T>

data class RpUser(
        var id: Long = 0,
        var username: String = "",
        var email: String = "",
        var credit: Int = 0,
        var frozenCredit: Int = 0
)

data class RqNewUser(
        val username: String,
        val password: String,
        val email: String
)

data class RqPassword(
        val password: String,
        val newpassword:String
)

data class RpExpert(
        var id:Long=0,
        var subject: String="",
        var education:String="",
        var introduction:String=""
)
