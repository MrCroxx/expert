package cn.edu.buaa.se.paper_patent

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.rabbitmq.http.client.domain.UserInfo
import org.apache.commons.lang.ObjectUtils
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService{

    @Autowired
    lateinit var userMapper: UserMapper

    /*fun listUser(): List<User> = userMapper.selectList(
            QueryWrapper<User>().select()
    )*/

    fun getUserById(id:Long): User{
        return userMapper.getUserById(id)
    }

}

@Service
class PaperService{

    @Autowired
    lateinit var paperMapper: PaperMapper

    fun findPaperByAuthor(author: Long): List<Paper> {
        return paperMapper.findPaperByAuthor(author)
    }

    fun findPaperByTitle(title: String): List<Paper> {
        return paperMapper.findPaperByTitle(title)
    }

    fun findPaperByAbstract(abstract: String): List<Paper> {
        return paperMapper.findPaperByAbstract(abstract)
    }

    fun findpaperIDmax():Long{
        return paperMapper.findpaperIDmax()
    }

    fun lookupPaper(id: Long):String{
        var papers:Paper = paperMapper.findPaperByID(id)
        if(papers == null){
            return "找不到此论文！"
        }
        else{
            paperMapper.updateclick_times(papers,id)
            return "返回论文地址为：！"
        }
    }

    fun insertPaper(paper: Paper):String?{
        paperMapper.insertPaper(paper)
        return "论文发表成功！"
    }

    fun updatePaper(id: Long,title: String,abstract: String):String?{
        var papers:Paper = paperMapper.findPaperByID(id)
        if(papers == null){
            return "找不到此论文！"
        }
        else{
            val p=Paper(id,title,1,"","",0,0,"",abstract)
            paperMapper.updatePaper(p,id)
            return "论文修改成功！"
        }
    }

    fun deletePaperById(id: Long):String?{
        var papers:Paper = paperMapper.findPaperByID(id)
        if(papers == null){
            return "找不到此论文！"
        }
        else{
            paperMapper.deletePaperById(id)
            return "论文删除成功！"
        }
    }
}


@Service
class PatentService{

    @Autowired
    lateinit var patentMapper: PatentMapper


    fun findPatentByTitle(title: String): List<Patent> {
        return patentMapper.findPatentByTitle(title)
    }

    fun lookupPatent():String{
        return "没有专利！"
    }

    fun findpatentIDmax():Long{
        return patentMapper.findpatentIDmax()
    }

    fun insertPatent(patent: Patent):String?{
        patentMapper.insertPatent(patent)
        return "专利发表成功！"
    }

    fun updatePatent(id: Long,title: String):String?{
        var patents:Patent = patentMapper.findPatentByID(id)
        if(patents == null){
            return "找不到此专利！"
        }
        else{
            val p=Patent(id,title,"","",0,0)
            patentMapper.updatePatent(p,id)
            return "专利修改成功！"
        }
    }

    fun deletePatentById(id: Long):String?{
        var patents:Patent = patentMapper.findPatentByID(id)
        if(patents == null){
            return "找不到此专利！"
        }
        else{
            patentMapper.deletePatentById(id)
            return "专利删除成功！"
        }
    }

}



@Service
class Paper_collectionService{

    @Autowired
    lateinit var paper_collectionMapper: Paper_collectionMapper


    fun collectionPaper(user_id:Long,paper_id:Long,time:String): String?{
        var collection:Paper_collection = paper_collectionMapper.findPaper_collection(user_id,paper_id)
        if(collection != null){
            return "已经收藏过此论文，请勿重复收藏！"
        }
        //return userMapper.getUserById(id)
        else{
            val p=Paper_collection(user_id,paper_id,time)
            paper_collectionMapper.insertPaper_collection(p)
            return "收藏成功！"
        }
    }

    fun deletePaper_collection(user_id: Long,paper_id: Long):String?{
        var collection:Paper_collection = paper_collectionMapper.findPaper_collection(user_id,paper_id)
        if(collection == null){
            return "找不到此收藏！"
        }
        else{
            paper_collectionMapper.deletePaper_collection(user_id,paper_id)
            return "论文收藏删除成功！"
        }
    }
}



@Service
class Patent_collectionService{

    @Autowired
    lateinit var patent_collectionMapper: Patent_collectionMapper


    fun collectionPatent(user_id:Long,patent_id:Long,time:String): String?{
        var collection:Patent_collection = patent_collectionMapper.findPatent_collection(user_id,patent_id)
        if(collection != null){
            return "已经收藏过此专利，请勿重复收藏！"
        }
        else{
            val p=Patent_collection(user_id,patent_id,time)
            patent_collectionMapper.insertPatent_collection(p)
            return "收藏成功！"
        }
    }

    fun deletePatent_collection(user_id: Long,patent_id: Long):String?{
        var collection:Patent_collection = patent_collectionMapper.findPatent_collection(user_id,patent_id)
        if(collection == null){
            return "找不到此收藏！"
        }
        else{
            patent_collectionMapper.deletePatent_collection(user_id,patent_id)
            return "专利收藏删除成功！"
        }
    }

}