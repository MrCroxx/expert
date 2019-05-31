package cn.edu.buaa.se.applicationform

import java.util.*

enum class ErrorCode(val code:Int,val msg:String){
    SUCCESS(20000,"success"),
    UNKNOWN_TYPE(40200,"未知申请类型");

    companion object {
        fun getMsgByCode(code: Int):String=ErrorCode.values().find{it.code==code}?.msg?:"unknown error"
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

enum class Status(val status:Short){
    UNDER_REVIEW(0),
    PASS(1),
    REJECT(2)
}

enum class ApplicationType(val code: Int){
    IDENTIFY(0),
    UPDATE_INFO(1),
    WITHDRAW(2)
}

data class ApplicationContent(
        var type:Int=0,
        var amount:Double=0.0,
        var name:String="",
        var organization:Long=0
)

data class RqResult(
        var id:Long=0,
        var status: Short=0

)