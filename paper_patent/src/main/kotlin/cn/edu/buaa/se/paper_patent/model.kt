package cn.edu.buaa.se.paper_patent

data class ResponseResult<T>(
        var success:Boolean=false,
        var message:String="",
        var data:T,
        var errorcode:String=""
)

class RestResultGenerator{
    fun <T>GenResult(data:T,message:String):ResponseResult<T>{
        lateinit var result:ResponseResult<T>
        result.data=data
        result.success=true
        result.message=message
        return result
    }
}