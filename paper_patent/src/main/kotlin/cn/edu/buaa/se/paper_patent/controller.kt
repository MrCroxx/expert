package cn.edu.buaa.se.paper_patent

import com.rabbitmq.http.client.domain.UserInfo
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.web.bind.annotation.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest

//方法名称前面的Date.表示该方法扩展自Date类
//返回的日期时间格式形如2017-10-01 10:00:00
fun Date.getNowDateTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return sdf.format(this)
}


@RestController
@RefreshScope
@RequestMapping("/paper")
class PaperController {

    @Value("\${app.host}")
    lateinit var host: String

    fun getCdnUrl(resourceType: String, resourceId: String) = "http://$host/cdn/$resourceType/$resourceId$"

    @Autowired
    lateinit var paperService: PaperService

    @GetMapping("/author/{author}")
    @ApiOperation(value = "根据作者获取论文列表", notes = "根据作者获取论文列表,返回paper的id,title,author,cite_times,click_times,publish_time,abstract,name(作者名称)")
    @ApiImplicitParams(
            ApiImplicitParam(name = "author", value = "用户输入的作者名", required = true, dataType = "string", paramType = "query")
    )
    fun findPaperByAuthor(@PathVariable author: String): ResponseBody<List<Paper>> {
        val name = "%$author%"
        return ResponseBody(msg = "", data = paperService.findPaperByAuthor(name))
    }

    //根据题目获取论文信息
    @GetMapping("/title/{title}")
    @ApiOperation(value = "根据题目获取论文列表", notes = "根据题目获取论文列表,返回paper的id,title,author,cite_times,click_times,publish_time,abstract")
    @ApiImplicitParams(
            ApiImplicitParam(name = "title", value = "用户输入的题目名", required = true, dataType = "string", paramType = "query")
    )
    fun findPaperByTitle(@PathVariable title: String): ResponseBody<List<Paper>> {
        val name = "%$title%"
        return ResponseBody(msg = "", data = paperService.findPaperByTitle(name))
    }

    //根据摘要获取论文信息
    @ApiOperation(value = "根据摘要获取论文列表", notes = "根据摘要和题目信息获取论文列表,返回paper的id,title,author,cite_times,click_times,publish_time,abstract")
    @ApiImplicitParams(
            ApiImplicitParam(name = "abstract", value = "用户输入的查询信息", required = true, dataType = "string", paramType = "query")
    )
    @GetMapping("/abstract/{abstract}")
    fun findPaperByAbstract(@PathVariable abstract: String): ResponseBody<List<Paper>> {
        val title = "%$abstract%"
        return ResponseBody(msg = "", data = paperService.findPaperByAbstract(abstract, title))
    }


    @ApiOperation(value = "获取论文信息", notes = "根据论文id获取论文信息")
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "论文id", required = true, dataType = "string", paramType = "query")
    )
    @GetMapping("/{id}")
    fun paper(@PathVariable id: Int): ResponseBody<Paper> {
        var paper = paperService.findPaperById(id.toLong())
        return ResponseBody<Paper>(data = paper)
    }

    //插入新paper
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/insertPaper")
    @ApiOperation(value = "发布论文", notes = "专家发布论文，输入json文件")
    @ApiImplicitParams(
            ApiImplicitParam(name = "title", value = "论文标题", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "abstract", value = "论文摘要", required = true, dataType = "string", paramType = "query")
    )
    fun insertPaper(@RequestBody rqNewPaper: RqNewPaper): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val p = Paper(
                id = null,
                title = rqNewPaper.title,
                author = uid,
                abstract = rqNewPaper.abstract
        )
        val rdata: Int = paperService.insertPaper(p)
        return ResponseBody(rdata, msg = "", data = null)
    }

    //修改paper信息
    // TODO:验证论文作者
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/update")
    @ApiOperation(value = "修改论文", notes = "专家修改论文，输入json文件")
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "修改的论文id", required = true, dataType = "bigint", paramType = "query"),
            ApiImplicitParam(name = "title", value = "论文标题", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "abstract", value = "论文摘要", required = true, dataType = "string", paramType = "query")

    )
    fun updatePaper(@RequestBody rqUpdatePaper: RqUpdatePaper): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata: Int = paperService.updatePaper(rqUpdatePaper.id, rqUpdatePaper.title, rqUpdatePaper.abstract)
        return ResponseBody(rdata, msg = "", data = null)
    }

    //删除paper信息
    // TODO:验证论文作者
    @ApiOperation(value = "删除论文", notes = "专家删除论文，输入json文件")
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "删除的论文id", required = true, dataType = "bigint", paramType = "query")
    )
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @DeleteMapping("/{id}/delete")
    fun deletePaperById(@PathVariable id: Int): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata: Int = paperService.deletePaperById(id.toLong())
        return ResponseBody(rdata, msg = "", data = null)
    }

}


@RestController
@RefreshScope
@RequestMapping("/patent")
class PatentController {

    @Autowired
    lateinit var patentService: PatentService

    //根据题目获取论文信息
    @GetMapping("/findPatentByTitle")
    @ApiOperation(value = "根据题目获取专利信息", notes = "根据题目获取专利信息,返回patent的所有信息")
    @ApiImplicitParams(
            ApiImplicitParam(name = "title", value = "用户输入的专利题目名", required = true, dataType = "string", paramType = "query")
    )
            /*fun findPatentByTitle(title:String):List<Patent>{
                var name:String = "%" + title +"%"
                var patent:List<Patent> = patentService.findPatentByTitle(name)
                return patent
            }*/
    fun findPatentByTitle(title: String): ResponseBody<List<Patent>> {
        var name: String = "%" + title + "%"
        return ResponseBody(msg = "", data = patentService.findPatentByTitle(name))
    }

    //查看专利
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/lookupPatent")
    @ApiOperation(value = "查看专利", notes = "根据用户点击的专利，用专利id来获取专利信息下载地址（字符串）")
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "点击的专利id", required = true, dataType = "string", paramType = "query")
    )
    fun lookupPatent(id: String): ResponseBody<String> {
        var id: Long = id.toLong()
        val rdata: Int = patentService.lookupPatent(id)
        var url: String = "www.wjqproject.cn/cdn/patent/" + id
        return ResponseBody(rdata, msg = url, data = url)
    }


    //插入新patent
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/insertPatent")
    @ApiOperation(value = "发布专利", notes = "专家发布专利信息，输入json文件")
    @ApiImplicitParams(
            ApiImplicitParam(name = "title", value = "专利标题", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "application_date", value = "专利发布日期（格式为：yyyy-MM-dd HH:mm:ss）", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "inventor_id", value = "发明人id", required = true, dataType = "bigint", paramType = "query")
    )
    fun insertPatent(@RequestBody rqNewPatent: RqNewPatent): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        var today = Calendar.getInstance().timeInMillis
        var id: Long = patentService.findpatentIDmax() + 1
        /*var inventor_id:Long = inventor_id.toLong()
        var applicant_id:Long = applicant_id.toLong()*/
        val p = Patent(id, rqNewPatent.title, rqNewPatent.application_date, Date(today).getNowDateTime(), rqNewPatent.inventor_id, uid)
        val rdata: Int = patentService.insertPatent(p)
        return ResponseBody(rdata, msg = "", data = null)
    }

    //修改patent信息
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/updatePatent")
    @ApiOperation(value = "修改专利", notes = "专家修改专利信息，输入json文件")
    @ApiImplicitParams(
            ApiImplicitParam(name = "title", value = "专利标题", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "id", value = "发明人id", required = true, dataType = "bigint", paramType = "query")
    )
    fun updatePatent(@RequestBody rqUpdatePatent: RqUpdatePatent): ResponseBody<Nothing?> {
        //var id:Long = id.toLong()
        val rdata: Int = patentService.updatePatent(rqUpdatePatent.id, rqUpdatePatent.title)
        return ResponseBody(rdata, msg = "", data = null)
    }

    //删除patent信息
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @DeleteMapping("/deletePatentById")
    @ApiOperation(value = "删除专利", notes = "专家删除专利信息，输入json文件")
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "发明人id", required = true, dataType = "bigint", paramType = "query")
    )
    fun deletePatentById(@RequestBody rqDeletePatent: RqDeletePatent): ResponseBody<Nothing?> {
        //var id:Long = id.toLong()
        val rdata: Int = patentService.deletePatentById(rqDeletePatent.id)
        return ResponseBody(rdata, msg = "", data = null)
    }

}


@RestController
@RefreshScope
@RequestMapping("/paper_collection")
class Paper_collectionController {

    @Autowired
    lateinit var paper_collectionService: Paper_collectionService

    //收藏论文
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/collectionPaper")
    @ApiOperation(value = "收藏论文", notes = "用户收藏论文，输入json文件")
    @ApiImplicitParams(
            ApiImplicitParam(name = "paper_id", value = "论文id", required = true, dataType = "bigint", paramType = "query")
    )
    fun collectionPaper(@RequestBody rqNewPaperCollection: RqPaperCollection): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        //var paper_id:Long = paper_ids.toLong()
        var today = Calendar.getInstance().timeInMillis
        val rdata: Int = paper_collectionService.collectionPaper(uid, rqNewPaperCollection.paper_id, Date(today).getNowDateTime())
        return ResponseBody(rdata, msg = "", data = null)
    }

    //删除论文收藏信息
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @DeleteMapping("/deletePapercollection")
    @ApiOperation(value = "删除论文收藏", notes = "用户删除论文收藏信息，输入json文件")
    @ApiImplicitParams(
            ApiImplicitParam(name = "paper_id", value = "论文id", required = true, dataType = "bigint", paramType = "query")
    )
    fun deletePapercollection(@RequestBody rqDeletePaperCollection: RqPaperCollection): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        //var paper_id:Long = paper_ids.toLong()
        val rdata: Int = paper_collectionService.deletePaper_collection(uid, rqDeletePaperCollection.paper_id)
        return ResponseBody(rdata, msg = "", data = null)
    }


}


@RestController
@RefreshScope
@RequestMapping("/patent_collection")
class Patent_collectionController {

    @Autowired
    lateinit var patent_collectionService: Patent_collectionService

    //收藏专利
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @PostMapping("/collectionPatent")
    @ApiOperation(value = "收藏专利", notes = "用户收藏专利，输入json文件")
    @ApiImplicitParams(
            ApiImplicitParam(name = "patent_id", value = "专利id", required = true, dataType = "bigint", paramType = "query")
    )
    fun collectionPatent(@RequestBody rqNewPatentCollection: RqPatentCollection): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        //var patent_id:Long = patent_ids.toLong()
        var today = Calendar.getInstance().timeInMillis
        val rdata: Int = patent_collectionService.collectionPatent(uid, rqNewPatentCollection.patent_id, Date(today).getNowDateTime())
        return ResponseBody(rdata, msg = "", data = null)
    }

    //删除专利收藏信息
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @DeleteMapping("/deletePatent_collection")
    @ApiOperation(value = "删除专利收藏", notes = "用户删除专利收藏信息，输入json文件")
    @ApiImplicitParams(
            ApiImplicitParam(name = "patent_id", value = "专利id", required = true, dataType = "bigint", paramType = "query")
    )
    fun deletePatent_collection(@RequestBody rqDeletePatentCollection: RqPatentCollection): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        //var patent_id:Long = patent_ids.toLong()
        val rdata: Int = patent_collectionService.deletePatent_collection(uid, rqDeletePatentCollection.patent_id)
        return ResponseBody(rdata, msg = "", data = null)
    }

}