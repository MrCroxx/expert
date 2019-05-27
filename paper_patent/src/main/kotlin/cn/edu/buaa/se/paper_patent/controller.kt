package cn.edu.buaa.se.paper_patent

import com.rabbitmq.http.client.domain.UserInfo
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import net.sf.jsqlparser.expression.DateTimeLiteralExpression
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

    //专家查看个人论文列表
    // TODO:验证论文作者
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/personal/{page}")
    @ApiOperation(value = "专家查看个人论文列表", notes = "专家查看个人论文列表," +
            "返回paper的id,title,author,cite_times,click_times,publish_time,abstract,name(作者名称),正确返回时errorcode = 20000")
    @ApiImplicitParams(
            ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "int", paramType = "query")
    )
    fun viewingPersonalPapers(@PathVariable page: Int): ResponseBody<List<Paper>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        var currIndex:Int = page * pageSize - pageSize
        return ResponseBody(SUCCESS, msg = Status.get(SUCCESS).toString(), data = paperService.viewingPersonalPapers(uid,currIndex))
    }

    @GetMapping("/author/{author}/{page}")
    @ApiOperation(value = "按照作者名称来搜索论文", notes = "按照作者名称来搜索论文," +
            "返回paper的id,title,author,cite_times,click_times,publish_time,abstract,name(作者名称),正确返回时errorcode = 20000")
    @ApiImplicitParams(
            ApiImplicitParam(name = "author", value = "用户输入的作者名", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "sort", value = "排序的方法(cite_times,click_times,publish_time)", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "int", paramType = "query")
    )
    fun findPaperByAuthor(sort:String,@PathVariable author: String,@PathVariable page: Int): ResponseBody<List<Paper>> {
        val name = "%$author%"
        return ResponseBody(SUCCESS, msg = Status.get(SUCCESS).toString(), data = paperService.findPaperByAuthor(name,sort,page))
    }

    //根据题目获取论文信息
    @GetMapping("/title/{title}/{page}")
    @ApiOperation(value = "按照题目来搜索论文", notes = "按照题目来搜索论文," +
            "返回paper的id,title,author,cite_times,click_times,publish_time,abstract,正确返回时errorcode = 20000")
    @ApiImplicitParams(
            ApiImplicitParam(name = "title", value = "用户输入的题目名", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "sort", value = "排序的方法(cite_times,click_times,publish_time)", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "int", paramType = "query")
    )
    fun findPaperByTitle(sort:String,@PathVariable title: String,@PathVariable page: Int): ResponseBody<List<Paper>> {
        val name = "%$title%"
        return ResponseBody(SUCCESS, msg = Status.get(SUCCESS).toString(), data = paperService.findPaperByTitle(name,sort,page))
    }

    //根据摘要获取论文信息
    @ApiOperation(value = "按照摘要来搜索论文", notes = "按照摘要来搜索论文," +
            "返回paper的id,title,author,cite_times,click_times,publish_time,abstract,正确返回时errorcode = 20000")
    @ApiImplicitParams(
            ApiImplicitParam(name = "abstract", value = "用户输入的查询信息", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "sort", value = "排序的方法(cite_times,click_times,publish_time)", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "int", paramType = "query")
    )
    @GetMapping("/abstract/{abstract}/{page}")
    fun findPaperByAbstract(sort:String,@PathVariable abstract: String,@PathVariable page: Int): ResponseBody<List<Paper>> {
        val title = "%$abstract%"
        return ResponseBody(SUCCESS, msg = Status.get(SUCCESS).toString(), data = paperService.findPaperByAbstract(abstract, title, sort, page))
    }


    @ApiOperation(value = "获取论文信息", notes = "根据论文id获取论文信息,正确返回时errorcode = 20000，找不到论文返回40001")
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "论文id", required = true, dataType = "string", paramType = "query")
    )
    @GetMapping("/{id}")
    fun paper(@PathVariable id: Int): ResponseBody<Paper> {
        var paper = paperService.findPaperById(id.toLong())
        val rdata: Int = paperService.lookupPaper(id.toLong())
        return ResponseBody<Paper>(rdata,msg = Status.get(rdata).toString(),data = paper)
    }

    //插入新paper
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/insert")
    @ApiOperation(value = "发布论文", notes = "专家发布论文，输入json文件，正确返回时errorcode = 20000")
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
        return ResponseBody(rdata, msg = Status.get(rdata).toString(), data = null)
    }

    //修改paper信息
    // TODO:验证论文作者
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/update")
    @ApiOperation(value = "修改论文", notes = "专家修改论文，输入json文件，正确返回时errorcode = 20000，找不到论文返回40001")
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

        val rdata: Int = paperService.updatePaper(uid, rqUpdatePaper.id, rqUpdatePaper.title, rqUpdatePaper.abstract)
        return ResponseBody(rdata, msg = Status.get(rdata).toString(), data = null)
    }

    //删除paper信息
    // TODO:验证论文作者
    @ApiOperation(value = "删除论文", notes = "专家删除论文，输入json文件，正确返回时errorcode = 20000，找不到论文返回40001")
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "删除的论文id", required = true, dataType = "bigint", paramType = "query")
    )
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @DeleteMapping("/delete/{id}")
    fun deletePaperById(@PathVariable id: Int): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata: Int = paperService.deletePaperById(uid,id.toLong())
        return ResponseBody(rdata, msg = Status.get(rdata).toString(), data = null)
    }

}


@RestController
@RefreshScope
@RequestMapping("/patent")
class PatentController {
    @Value("\${app.host}")
    lateinit var host: String

    fun getCdnUrl(resourceType: String, resourceId: String) = "http://$host/cdn/$resourceType/$resourceId$"

    @Autowired
    lateinit var patentService: PatentService

    //专家查看个人专利列表
    // TODO:验证专利作者
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/personal/{page}")
    @ApiOperation(value = "专家查看个人专利列表", notes = "专家查看个人专利列表," +
            "返回paper的id,title,author,cite_times,click_times,publish_time,abstract,name(作者名称)，正确返回时errorcode = 20000")
    @ApiImplicitParams(
            ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "int", paramType = "query")
    )
    fun viewingPersonalPatents(@PathVariable page: Int): ResponseBody<List<Patent>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        var currIndex:Int = page * pageSize - pageSize
        return ResponseBody(SUCCESS, msg = Status.get(SUCCESS).toString(), data = patentService.viewingPersonalPatents(uid,currIndex))
    }

    //根据题目获取论文信息
    @GetMapping("/title/{title}/{page}")
    @ApiOperation(value = "根据题目获取专利信息", notes = "根据题目获取专利信息,返回patent的所有信息，正确返回时errorcode = 20000")
    @ApiImplicitParams(
            ApiImplicitParam(name = "title", value = "用户输入的专利题目名", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "int", paramType = "query")
    )
            /*fun findPatentByTitle(title:String):List<Patent>{
                var name:String = "%" + title +"%"
                var patent:List<Patent> = patentService.findPatentByTitle(name)
                return patent
            }*/
    fun findPatentByTitle(@PathVariable title: String,@PathVariable page: Int): ResponseBody<List<Patent>> {
        var name = "%$title%"
        var currIndex:Int = page * pageSize - pageSize
        return ResponseBody(SUCCESS, msg = Status.get(SUCCESS).toString(), data = patentService.findPatentByTitle(name,currIndex))
    }


    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/{id}")
    @ApiOperation(value = "查看专利", notes = "根据用户点击的专利，用专利id来获取专利信息下载地址（字符串），" +
            "正确返回时errorcode = 20000，找不到专利时返回40002")
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "点击的专利id", required = true, dataType = "string", paramType = "query")
    )
    fun lookupPatent(@PathVariable id: Int): ResponseBody<Patent> {
        var patent = patentService.findPatentById(id.toLong())
        val rdata: Int = patentService.lookupPatent(id.toLong())
        return ResponseBody<Patent>(rdata,msg = Status.get(rdata).toString(),data = patent)
    }


    //插入新patent
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/insertPatent")
    @ApiOperation(value = "发布专利", notes = "专家发布专利信息，输入json文件，正确返回时errorcode = 20000")
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

        val p = Patent(
                id = null,
                title = rqNewPatent.title,
                application_date = rqNewPatent.application_date,
                applicant_id = uid,
                inventor_id = rqNewPatent.inventor_id
        )
        /*var today = Calendar.getInstance().timeInMillis
        var id: Long = patentService.findpatentIDmax() + 1*/

        //val p = Patent(id, rqNewPatent.title, rqNewPatent.application_date, Date(today).getNowDateTime(), rqNewPatent.inventor_id, uid)
        val rdata: Int = patentService.insertPatent(p)
        return ResponseBody(rdata, msg = Status.get(rdata).toString(), data = null)
    }

    //修改patent信息
    // TODO:验证专利作者
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/updatePatent")
    @ApiOperation(value = "修改专利", notes = "专家修改专利信息，输入json文件，正确返回时errorcode = 20000，找不到专利时返回40002")
    @ApiImplicitParams(
            ApiImplicitParam(name = "title", value = "专利标题", required = true, dataType = "string", paramType = "query"),
            ApiImplicitParam(name = "id", value = "发明人id", required = true, dataType = "bigint", paramType = "query")
    )
    fun updatePatent(@RequestBody rqUpdatePatent: RqUpdatePatent): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        //var id:Long = id.toLong()
        val rdata: Int = patentService.updatePatent(uid, rqUpdatePatent.id, rqUpdatePatent.title)
        return ResponseBody(rdata, msg = Status.get(rdata).toString(), data = null)
    }


    //删除patent信息
    // TODO:验证专利作者
    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除专利", notes = "专家删除专利信息，输入json文件，正确返回时errorcode = 20000，找不到专利时返回40002")
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "发明人id", required = true, dataType = "bigint", paramType = "query")
    )
    fun deletePatentById(@PathVariable id: Int): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata: Int = patentService.deletePatentById(uid, id.toLong())
        return ResponseBody(rdata, msg = Status.get(rdata).toString(), data = null)
    }

}


@RestController
@RefreshScope
@RequestMapping("/papercollection")
class Paper_collectionController {

    @Autowired
    lateinit var paper_collectionService: Paper_collectionService

    //收藏论文
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/collect/{paper_id}")
    @ApiOperation(value = "收藏论文", notes = "用户收藏论文，输入json文件，正确返回时errorcode = 20000，已收藏此论文返回40003")
    @ApiImplicitParams(
            ApiImplicitParam(name = "paper_id", value = "论文id", required = true, dataType = "bigint", paramType = "query")
    )
    fun collectionPaper(@PathVariable paper_id: Int): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        //var today = Calendar.getInstance().timeInMillis
        //val rdata: Int = paper_collectionService.collectionPaper(uid, rqNewPaperCollection.paper_id, Date(today).getNowDateTime())
        val rdata: Int = paper_collectionService.collectionPaper(uid, paper_id.toLong(), Date())
        return ResponseBody(rdata, msg = Status.get(rdata).toString(), data = null)
    }

    //删除论文收藏信息
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @DeleteMapping("/delete/{paper_id}")
    @ApiOperation(value = "删除论文收藏", notes = "用户删除论文收藏信息，输入json文件，正确返回时errorcode = 20000，找不到此论文收藏为40005")
    @ApiImplicitParams(
            ApiImplicitParam(name = "paper_id", value = "论文id", required = true, dataType = "bigint", paramType = "query")
    )
    fun deletePapercollection(@PathVariable paper_id: Int): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata: Int = paper_collectionService.deletePaper_collection(uid, paper_id.toLong())
        return ResponseBody(rdata, msg = Status.get(rdata).toString(), data = null)
    }


}


@RestController
@RefreshScope
@RequestMapping("/patentcollection")
class Patent_collectionController {

    @Autowired
    lateinit var patent_collectionService: Patent_collectionService

    //收藏专利
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @PostMapping("/collect/{patent_id}")
    @ApiOperation(value = "收藏专利", notes = "用户收藏专利，输入json文件，正确返回时errorcode = 20000，已收藏此专利返回40004")
    @ApiImplicitParams(
            ApiImplicitParam(name = "patent_id", value = "专利id", required = true, dataType = "bigint", paramType = "query")
    )
    fun collectionPatent(@PathVariable patent_id: Int): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata: Int = patent_collectionService.collectionPatent(uid, patent_id.toLong(), Date())
        return ResponseBody(rdata, msg = Status.get(rdata).toString(), data = null)
    }

    //删除专利收藏信息
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @DeleteMapping("/delete/{patent_id}")
    @ApiOperation(value = "删除专利收藏", notes = "用户删除专利收藏信息，输入json文件，正确返回时errorcode = 20000，找不到此专利收藏返回40006")
    @ApiImplicitParams(
            ApiImplicitParam(name = "patent_id", value = "专利id", required = true, dataType = "bigint", paramType = "query")
    )
    fun deletePatent_collection(@PathVariable patent_id: Int): ResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()

        val rdata: Int = patent_collectionService.deletePatent_collection(uid, patent_id.toLong())
        return ResponseBody(rdata, msg = Status.get(rdata).toString(), data = null)
    }

}