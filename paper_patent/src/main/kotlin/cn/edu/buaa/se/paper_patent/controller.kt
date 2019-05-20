package cn.edu.buaa.se.paper_patent

import com.rabbitmq.http.client.domain.UserInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
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
class UserController{

    @Autowired
    lateinit var userService: UserService

    @RequestMapping("/getUserById")
    fun getUserById():User{
        var user:User = userService.getUserById(1)
        return user
    }

    /*@PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/userlist")
    fun userlist():List<User> = userService.listUser()*/

}

@RestController
class PaperController{

    @Autowired
    lateinit var paperService: PaperService

    //根据作者获取论文信息
    @RequestMapping("/findPaperByAuthor")
    fun findPaperByAuthor(author:String):List<Paper>{
        var authors:Long = author.toLong()
        var paper:List<Paper> = paperService.findPaperByAuthor(authors)
        return paper
    }

    //根据题目获取论文信息
    @RequestMapping("/findPaperByTitle")
    fun findPaperByTitle(title:String):List<Paper>{
        var name:String = "%" + title +"%"
        var paper:List<Paper> = paperService.findPaperByTitle(name)
        return paper
    }

    //根据摘要获取论文信息
    @RequestMapping("/findPaperByAbstract")
    fun findPaperByAbstract(abstract:String):List<Paper>{
        //var abstract:String = "a"
        var paper:List<Paper> = paperService.findPaperByAbstract(abstract)
        return paper
    }

    //查看论文
    @RequestMapping("/lookupPaper")
    fun lookupPaper(id: String):String{
        var id:Long = id.toLong()
        return paperService.lookupPaper(id).toString()
    }

    //插入新paper
    @RequestMapping("/insertPaper")
    fun insertPaper(title: String,author: String,abstract: String):String {
        var today = Calendar.getInstance().timeInMillis
        var id:Long = paperService.findpaperIDmax() + 1
        var author:Long = author.toLong()
        val p=Paper(id,title,author,"","",0,0,Date(today).getNowDateTime(),abstract)
        return paperService.insertPaper(p).toString()
    }

    //修改paper信息
    @RequestMapping("/updatePaper")
    fun updatePaper(id:String,title: String,abstract: String):String {
        var id:Long = id.toLong()
        return paperService.updatePaper(id,title,abstract).toString()
    }

    //删除paper信息
    @RequestMapping("/deletePaperById")
    fun deletePaperById(id:String):String {
        var id:Long = id.toLong()
        return paperService.deletePaperById(id).toString()
    }

}


@RestController
class PatentController{

    @Autowired
    lateinit var patentService: PatentService

    //根据题目获取论文信息
    @RequestMapping("/findPatentByTitle")
    fun findPatentByTitle(title:String):List<Patent>{
        var name:String = "%" + title +"%"
        var patent:List<Patent> = patentService.findPatentByTitle(name)
        return patent
    }

    //查看专利
    @RequestMapping("/lookupPatent")
    fun lookupPatent():String{
        return patentService.lookupPatent().toString()
    }

    //插入新patent
    @RequestMapping("/insertPatent")
    fun insertPatent(title: String,application_date:String,inventor_id: String,applicant_id: String):String {
        var today = Calendar.getInstance().timeInMillis
        var id:Long = patentService.findpatentIDmax() + 1
        var inventor_id:Long = inventor_id.toLong()
        var applicant_id:Long = applicant_id.toLong()
        val p=Patent(id,title,application_date,Date(today).getNowDateTime(),inventor_id,applicant_id)
        return patentService.insertPatent(p).toString()
    }

    //修改patent信息
    @RequestMapping("/updatePatent")
    fun updatePatent(id:String,title: String):String {
        var id:Long = id.toLong()
        return patentService.updatePatent(id,title).toString()
    }

    //删除patent信息
    @RequestMapping("/deletePatentById")
    fun deletePatentById(id:String):String {
        var id:Long = id.toLong()
        return patentService.deletePatentById(id).toString()
    }

}




@RestController
class Paper_collectionController{

    @Autowired
    lateinit var paper_collectionService: Paper_collectionService

    //收藏论文
    @RequestMapping("/collectionPaper")
    fun collectionPaper(user_ids:String,paper_ids:String):String?{
        var user_id:Long = user_ids.toLong()
        var paper_id:Long = paper_ids.toLong()
        var today = Calendar.getInstance().timeInMillis
        return paper_collectionService.collectionPaper(user_id,paper_id,Date(today).getNowDateTime()).toString()
    }

    //删除论文收藏信息
    @RequestMapping("/deletePaper_collection")
    fun deletePaper_collection(user_ids: String,paper_ids: String):String {
        var user_id:Long = user_ids.toLong()
        var paper_id:Long = paper_ids.toLong()
        return paper_collectionService.deletePaper_collection(user_id,paper_id).toString()
    }


}


@RestController
class Patent_collectionController{

    @Autowired
    lateinit var patent_collectionService: Patent_collectionService

    //收藏专利
    @RequestMapping("/collectionPatent")
    fun collectionPatent(user_ids:String,patent_ids:String):String?{
        var user_id:Long = user_ids.toLong()
        var patent_id:Long = patent_ids.toLong()
        var today = Calendar.getInstance().timeInMillis
        return patent_collectionService.collectionPatent(user_id,patent_id,Date(today).getNowDateTime()).toString()
    }

    //删除专利收藏信息
    @RequestMapping("/deletePatent_collection")
    fun deletePatent_collection(user_ids: String,patent_ids: String):String {
        var user_id:Long = user_ids.toLong()
        var patent_id:Long = patent_ids.toLong()
        return patent_collectionService.deletePatent_collection(user_id,patent_id).toString()
    }

}