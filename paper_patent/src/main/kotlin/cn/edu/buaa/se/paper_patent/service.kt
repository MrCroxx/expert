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

    fun findPaperByAuthor(name: String): List<Paper> = paperMapper.findPaperByAuthor(name)

    fun findPaperByTitle(title: String): List<Paper> = paperMapper.findPaperByAuthor(title)

    fun findPaperByAbstract(abstract: String, abstracts: String): List<Paper> = paperMapper.findPaperByAbstract(abstract, abstracts)

    fun findpaperIDmax(): Long {
        return paperMapper.findpaperIDmax()
    }

    fun lookupPaper(id: Long): Int {
        var papers: Paper = paperMapper.findPaperByID(id)
        if (papers == null) {
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

    fun updatePaper(id: Long, title: String, abstract: String): Int {
        var papers: Paper = paperMapper.findPaperByID(id)
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        if (papers == null || papers.author != uid) {
            return UNKNOWN_PAPER
        } else {
            val p = Paper(id, title, uid, "", "", 0, 0, Date(), abstract)
            paperMapper.updatePaper(p, id)
            return SUCCESS
        }
    }

    fun deletePaperById(id: Long): Int {
        var papers: Paper = paperMapper.findPaperByID(id)
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
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
    fun findPatentByTitle(title: String): List<Patent> = patentMapper.findPatentByTitle(title)


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

    fun updatePatent(id: Long, title: String): Int {
        var patents: Patent = patentMapper.findPatentByID(id)
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        if (patents == null || patents.applicant_id != uid) {
            return UNKNOWN_PATENT
        } else {
            val p = Patent(id, title, "", "", 0, uid)
            patentMapper.updatePatent(p, id)
            return SUCCESS
        }
    }

    fun deletePatentById(id: Long): Int {
        var patents: Patent = patentMapper.findPatentByID(id)
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
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


    fun collectionPaper(user_id: Long, paper_id: Long, time: String): Int {
        var collection: Paper_collection = paper_collectionMapper.findPaper_collection(user_id, paper_id)
        if (collection != null) {
            return SAME_PAPERCOLLECTION
        }
        //return userMapper.getUserById(id)
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


    fun collectionPatent(user_id: Long, patent_id: Long, time: String): Int {
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