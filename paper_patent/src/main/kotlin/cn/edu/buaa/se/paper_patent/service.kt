package cn.edu.buaa.se.paper_patent

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.stereotype.Service
import java.util.*


@Service
class PaperService {

    @Autowired
    lateinit var paperMapper: PaperMapper

    /*fun findPaperByAuthor(author: Long): List<Paper> {
        return paperMapper.findPaperByAuthor(author)
    }

    fun findPaperByTitle(title: String): List<Paper> {
        return paperMapper.findPaperByTitle(title)
    }

    fun findPaperByAbstract(abstract: String): List<Paper> {
        return paperMapper.findPaperByAbstract(abstract)
    }*/

    fun findPaperById(id: Long): Paper = paperMapper.findPaperByID(id)

    fun viewingPersonalPapers(uid: Long,currIndex:Int): List<Paper> = paperMapper.viewingPersonalPapers(uid,currIndex)

    fun findPaperByAuthor(name: String, sort:String, page:Int): List<Paper>{
        var paper: List<Paper>
        var currIndex:Int = page * pageSize - pageSize
        if(sort == "click_times"){
            paper = paperMapper.findPaperByAuthor_click_times(name,currIndex)
        }else if(sort == "cite_times"){
            paper = paperMapper.findPaperByAuthor_cite_times(name,currIndex)
        }else{
            paper = paperMapper.findPaperByAuthor_publish_time(name,currIndex)

        }
        var count:Long = paper.count().toLong()
        for(item in paper){
            item.count = count
        }
        return paper
    }

    fun findPaperByTitle(title: String, sort:String, page:Int): List<Paper> {
        var paper: List<Paper>
        var currIndex:Int = page * pageSize - pageSize
        if(sort == "click_times"){
            paper = paperMapper.findPaperByTitle_click_times(title,currIndex)
        }else if(sort == "cite_times"){
            paper = paperMapper.findPaperByTitle_cite_times(title,currIndex)
        }else{
            paper = paperMapper.findPaperByTitle_cite_times(title,currIndex)
        }
        var count:Long = paper.count().toLong()
        for(item in paper){
            item.count = count
        }
        return paper
    }

    fun findPaperByAbstract(abstract: String, abstracts: String, sort:String, page:Int): List<Paper>{
        var paper: List<Paper>
        var currIndex:Int = page * pageSize - pageSize
        if(sort == "click_times"){
            paper = paperMapper.findPaperByAbstract_click_times(abstract, abstracts, currIndex)
        }else if(sort == "cite_times"){
            paper = paperMapper.findPaperByAbstract_cite_times(abstract, abstracts, currIndex)
        }else{
            paper = paperMapper.findPaperByAbstract_publish_time(abstract, abstracts, currIndex)
        }
        var count:Long = paper.count().toLong()
        for(item in paper){
            item.count = count
        }
        return paper
    }

    fun findpaperIDmax(): Long {
        return paperMapper.findpaperIDmax()
    }

    fun lookupPaper(id: Long): Int {
        var papers: Paper = paperMapper.findPaperByID(id)
        if (papers == null) { //并不是always 'false',而且经测试，还是会正常返回UNKNOWN_PAPER，下面同理
            return UNKNOWN_PAPER
        } else {
            paperMapper.updateclick_times(papers, id)
            return SUCCESS
        }
    }

    fun insertPaper(paper: Paper): Int {
        paperMapper.insertPaper(paper)
        return SUCCESS
    }

    fun updatePaper(uid:Long, id: Long, title: String, abstract: String): Int {
        var papers: Paper = paperMapper.findPaperByID(id)
        if (papers == null || papers.author != uid) {
            return UNKNOWN_PAPER
        } else {
            val p = Paper(id, title, uid, "", "", 0, 0, Date(), abstract)
            paperMapper.updatePaper(p, id)
            return SUCCESS
        }
    }

    fun deletePaperById(uid:Long, id: Long): Int {
        var papers: Paper = paperMapper.findPaperByID(id)

        if (papers == null || papers.author != uid) {
            return UNKNOWN_PAPER
        } else {
            paperMapper.deletePaperById(id)
            return SUCCESS
        }
    }
}


@Service
class PatentService {

    @Autowired
    lateinit var patentMapper: PatentMapper


    /*fun findPatentByTitle(title: String): List<Patent> {
        return patentMapper.findPatentByTitle(title)
    }*/
    fun findPatentById(id: Long): Patent = patentMapper.findPatentByID(id)

    fun viewingPersonalPatents(uid: Long,currIndex:Int): List<Patent> = patentMapper.viewingPersonalPatents(uid,currIndex)

    fun findPatentByTitle(title: String,currIndex: Int): List<Patent> = patentMapper.findPatentByTitle(title,currIndex)

    fun lookupPatent(id: Long): Int {
        var patents: Patent = patentMapper.findPatentByID(id)
        if (patents == null) {
            return UNKNOWN_PATENT
        } else {
            return SUCCESS
        }
    }

    fun findpatentIDmax(): Long {
        return patentMapper.findpatentIDmax()
    }

    fun insertPatent(patent: Patent): Int {
        patentMapper.insertPatent(patent)
        return SUCCESS
    }

    fun updatePatent(uid:Long, id: Long, title: String): Int {
        var patents: Patent = patentMapper.findPatentByID(id)

        if (patents == null || patents.applicant_id != uid) {
            return UNKNOWN_PATENT
        } else {
            val p = Patent(id, title, "", Date(), 0, uid)
            patentMapper.updatePatent(p, id)
            return SUCCESS
        }
    }

    fun deletePatentById(uid:Long, id: Long): Int {
        var patents: Patent = patentMapper.findPatentByID(id)

        if (patents == null || patents.applicant_id != uid) {
            return UNKNOWN_PATENT
        } else {
            patentMapper.deletePatentById(id)
            return SUCCESS
        }
    }

}


@Service
class Paper_collectionService {

    @Autowired
    lateinit var paper_collectionMapper: Paper_collectionMapper


    fun collectionPaper(user_id: Long, paper_id: Long, time: Date): Int {
        var collection: Paper_collection = paper_collectionMapper.findPaper_collection(user_id, paper_id)
        if (collection != null) {
            return SAME_PAPERCOLLECTION
        }
        else {
            val p = Paper_collection(user_id, paper_id, time)
            paper_collectionMapper.insertPaper_collection(p)
            return SUCCESS
        }
    }

    fun deletePaper_collection(user_id: Long, paper_id: Long): Int {
        var collection: Paper_collection = paper_collectionMapper.findPaper_collection(user_id, paper_id)
        if (collection == null) {
            return UNKNOWN_PAPERCOLLECTION
        } else {
            paper_collectionMapper.deletePaper_collection(user_id, paper_id)
            return SUCCESS
        }
    }
}


@Service
class Patent_collectionService {

    @Autowired
    lateinit var patent_collectionMapper: Patent_collectionMapper


    fun collectionPatent(user_id: Long, patent_id: Long, time: Date): Int {
        var collection: Patent_collection = patent_collectionMapper.findPatent_collection(user_id, patent_id)
        if (collection != null) {
            return SAME_PATENTCOLLECTION
        } else {
            val p = Patent_collection(user_id, patent_id, time)
            patent_collectionMapper.insertPatent_collection(p)
            return SUCCESS
        }
    }

    fun deletePatent_collection(user_id: Long, patent_id: Long): Int {
        var collection: Patent_collection = patent_collectionMapper.findPatent_collection(user_id, patent_id)
        if (collection == null) {
            return UNKNOWN_PATENTCOLLECTION
        } else {
            patent_collectionMapper.deletePatent_collection(user_id, patent_id)
            return SUCCESS
        }
    }

}