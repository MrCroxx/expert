package cn.edu.buaa.se.docs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
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
    lateinit var expertMapper: ExpertMapper
    @Autowired
    lateinit var paperMapper: PaperMapper
    @Autowired
    lateinit var patentMapper: PatentMapper
    @Autowired
    lateinit var organizationService: OrganizationService


    fun getUserInfoById(id: Long): User? {
        val user = userMapper.selectById(id) ?: return null
        if (user.role != ROLE.ROLE_EXPERT.value.toShort() || user.expert == null) return user
        val expert: Expert = user.expert!!
        expert.papers = paperMapper.selectByAuthorId(id)
        expert.patents_inventor = patentMapper.selectByInventorId(id)
        expert.patents_applicant = patentMapper.selectByApplicantId(id)
        user.expert = expert
        return user
    }

    fun findUnchaimedUserByExpertName(name: String): User? {
        val user = userMapper.selectUnclaimedUserByExpertName(name) ?: return null
        val expert = user.expert!!
        expert.papers = paperMapper.selectByAuthorId(user.id)
        expert.patents_inventor = patentMapper.selectByInventorId(user.id)
        expert.patents_applicant = patentMapper.selectByApplicantId(user.id)
        user.expert = expert
        return user
    }

    fun updateEmail(id: Long, email: String): ErrCode {
        val res: Int = userMapper.updateEmail(id, email)
        return when (res) {
            0 -> ErrCode.DATA_NOT_EXISTS
            else -> ErrCode.SUCCESS
        }
    }

    fun updateExpertInfo(id: Long, name: String, subject: String, education: String, introduction: String, field: String, organizationName: String): ErrCode {
        val organizationId: Long = organizationService.insertIfNotExists(organizationName)
        val affectRows: Int = expertMapper.updateExpertInfo(id, name, subject, education, introduction, field, organizationId)
        return when (affectRows) {
            0 -> ErrCode.DATA_NOT_EXISTS
            else -> ErrCode.SUCCESS
        }
    }
}

@Service
class PaperService {
    @Autowired
    lateinit var paperMapper: PaperMapper

    fun getPaperById(id: Long): Paper? {
        paperMapper.updatePaperClickTime(id)
        return paperMapper.selectById(id)
    }

}


@Service
class PatentService {
    @Autowired
    lateinit var patentMapper: PatentMapper

    fun getPatentById(id: Long): Patent? {
        patentMapper.updatePatentClickTime(id)
        return patentMapper.selectById(id)
    }

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

@Service
class FollowService {
    @Autowired
    lateinit var userMapper: UserMapper

    fun getFollows(id: Long) = userMapper.selectFollowedByFollwerId(id)

    fun insertFollow(follower_id: Long, followed_id: Long): ErrCode {
        try {
            userMapper.insertFollowPair(follower_id, followed_id, Date())
        } catch (e: DataIntegrityViolationException) {
            return ErrCode.DATA_INTEGRITY_VIOLATION
        }
        return ErrCode.SUCCESS
    }

    fun deleteFollow(follower_id: Long, followed_id: Long): ErrCode {
        val affectRows: Int = userMapper.deleteFollowPair(follower_id, followed_id)
        return when (affectRows) {
            0 -> ErrCode.DATA_NOT_EXISTS
            else -> ErrCode.SUCCESS
        }
    }
}

@Service
class OrganizationService {
    @Autowired
    lateinit var organizationMapper: OrganizationMapper

    fun insertIfNotExists(name: String): Long {
        val newOrganization = Organization(name = name)
        organizationMapper.insertIfNotExists(newOrganization)
        return newOrganization.id
    }

}