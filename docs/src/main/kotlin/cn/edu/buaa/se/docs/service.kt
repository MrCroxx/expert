package cn.edu.buaa.se.docs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.stereotype.Service
import java.util.*


@Service
class SearchService {
    @Autowired
    lateinit var patentMapper: PatentMapper
    @Autowired
    lateinit var paperMapper: PaperMapper

    fun search(
            keyword: String,
            type: DocType,
            sort: SearchSort,
            year: Int?,
            offset: Int,
            limit: Int
    ): SearchResult {
        val results = when (type) {
            DocType.PAPER -> paperMapper.search(
                    keyword = keyword,
                    sort = sort,
                    year = year,
                    offset = offset,
                    limit = limit
            )
            DocType.PATENT -> patentMapper.search(
                    keyword = keyword,
                    sort = sort,
                    year = year,
                    offset = offset,
                    limit = limit
            )
        }
        return SearchResult(total = results.size, results = results)
    }
}

@Service
class UserService {
    @Autowired
    lateinit var userMapper: UserMapper
    @Autowired
    lateinit var paperMapper: PaperMapper
    @Autowired
    lateinit var patentMapper: PatentMapper

    fun getUserInfoId(id: Long): User? {
        val user = userMapper.selectById(id) ?: return null
        if (user.role != ROLE.ROLE_EXPERT.value.toShort() || user.expert == null) return user
        val expert: Expert = user.expert!!
        expert.papers = paperMapper.selectByAuthorId(id)
        expert.patents_inventor = patentMapper.selectByInventorId(id)
        expert.patents_applicant = patentMapper.selectByApplicantId(id)
        user.expert = expert
        return user
    }
}

@Service
class PaperService {
    @Autowired
    lateinit var paperMapper: PaperMapper

    fun getPaperById(id: Long): Paper? = paperMapper.selectById(id)

}


@Service
class PatentService {
    @Autowired
    lateinit var patentMapper: PatentMapper

    fun getPatentById(id: Long): Patent? = patentMapper.selectById(id)

}

@Service
class CollectionService {
    @Autowired
    lateinit var paperMapper: PaperMapper
    @Autowired
    lateinit var patentMapper: PatentMapper

    fun getPaperCollection(id: Long) = paperMapper.selectPaperCollectionByUserId(id)

    fun getPatentCollection(id: Long) = patentMapper.selectPatentCollectionByUserId(id)

    fun insertCollection(uid: Long, cid: Long, type: DocType): ErrCode {
        try {
            when (type) {
                DocType.PAPER -> paperMapper.insertPaperCollection(uid, cid, Date())
                DocType.PATENT -> patentMapper.insertPatentCollection(uid, cid, Date())
            }
        } catch (exception: DataIntegrityViolationException) {
            return ErrCode.DATA_INTEGRITY_VIOLATION
        }
        return ErrCode.SUCCESS
    }

    fun deleteCollection(uid: Long, cid: Long, type: DocType): ErrCode {
        val affectRows: Int = when (type) {
            DocType.PAPER -> paperMapper.deletePaperCollection(uid, cid)
            DocType.PATENT -> patentMapper.deletePatentCollection(uid, cid)
        }
        return when (affectRows) {
            0 -> ErrCode.DATA_NOT_EXISTS
            else -> ErrCode.SUCCESS
        }
    }
}