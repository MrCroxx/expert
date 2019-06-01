package cn.edu.buaa.se.applicationform

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.GsonJsonParser
import org.springframework.stereotype.Service
import java.util.*

@Service
class ApplicationApplyService{
    @Autowired
    lateinit var applicationMapper: ApplicationMapper

    fun applyApplication(uid: Long,content: ApplicationContent):Int{
        val contentString= buildContent(content)
        val application=Application(
                userId = uid,
                content = contentString,
                status = Status.UNDER_REVIEW.status,
                applyTime = Date()
        )
        applicationMapper.insert(application)
        return ErrorCode.SUCCESS.code
    }

    private fun buildContent(content:ApplicationContent):String {
        var rdata='{'+addProperty("type",content.type)
        rdata+=','+addProperty("amount",content.amount)
        rdata+=','+addProperty("name",content.name)
        rdata+=','+addProperty("organization",content.organization)
        return rdata+'}'
    }

    private fun addProperty(name:String, value:Any?):String{
        var rdata= "\"" +name+"\":"
        when(value){
            null->rdata+= "null"
            is String->rdata+= addKey(value)
            is Boolean,is Number->rdata+=value.toString()
            else->rdata+= value.toString()
        }
        return rdata
    }

    private fun addKey(name:String):String='\"' +name+'\"'

}

@Service
class ApplicationExamineService{
    @Autowired
    lateinit var applicationMapper: ApplicationMapper

    fun getInfo(id:Long):ApplicationContent{
        val content=GsonJsonParser().parseMap(applicationMapper.getContent(id))
        return ApplicationContent(
                type=(content["type"] as Double).toInt(),
                amount = content["amount"] as Double,
                name=content["name"] as String,
                organization = (content["organization"] as Double).toLong()
                )
    }

    fun approveApplication(id: Long,status:Short,aid:Long):Int{
        applicationMapper.updateExamineResult(id,status,aid, Date())
        return ErrorCode.SUCCESS.code
    }
}
