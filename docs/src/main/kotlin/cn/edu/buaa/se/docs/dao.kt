package cn.edu.buaa.se.docs

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.*
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.stereotype.Repository
import java.util.*

@Mapper
@Repository
interface ExpertMapper : BaseMapper<Expert> {
    @Select("SELECT * FROM expert WHERE id=#{id}")
    @Results(
            id = "ExpertMap",
            value = [
                Result(property = "name", column = "name"),
                Result(property = "subject", column = "subject"),
                Result(property = "education", column = "education"),
                Result(property = "introduction", column = "introduction"),
                Result(property = "field", column = "field"),
                Result(property = "famousValue", column = "famous_value"),
                Result(property = "organization", column = "organization_id", one = One(select = "cn.edu.buaa.se.docs.OrganizationMapper.selectById"))
            ]
    )
    @Throws(DataRetrievalFailureException::class)
    fun selectById(id: Long): Expert?

    @Update("UPDATE expert SET name=#{name}, subject=#{subject}, education=#{education}, introduction=#{introduction}, field=#{field}, organization_id=#{organizationId} WHERE id=#{id}")
    fun updateExpertInfo(id: Long, name: String, subject: String, education: String, introduction: String, field: String, organizationId: Long): Int


}

@Mapper
@Repository
interface UserMapper : BaseMapper<User> {
    @Select("SELECT * FROM user WHERE id=#{id}")
    @Results(
            id = "UserMap",
            value = [
                Result(property = "id", column = "id"),
                Result(property = "username", column = "username"),
                Result(property = "email", column = "email"),
                Result(property = "credit", column = "credit"),
                Result(property = "frozenCredit", column = "frozen_credit"),
                Result(property = "role", column = "role"),
                Result(property = "expert", column = "id", one = One(select = "cn.edu.buaa.se.docs.ExpertMapper.selectById"))
            ]
    )
    @Throws(DataRetrievalFailureException::class)
    fun selectById(id: Long): User?

    @Select("SELECT * FROM user JOIN author_paper ON user.id=author_paper.author_id WHERE author_paper.paper_id=#{id}")
    @ResultMap("UserMap")
    fun selectAuthorsByPaperId(id: Long): MutableList<User>

    @Select("SELECT * FROM user JOIN applicant_patent ON user.id=applicant_patent.applicant_id WHERE applicant_patent.patent_id=#{id}")
    @ResultMap("UserMap")
    fun selectApplicantsByPatentId(id: Long): MutableList<User>

    @Select("SELECT * FROM user JOIN inventor_patent ON user.id=inventor_patent.inventor_id WHERE inventor_patent.patent_id=#{id}")
    @ResultMap("UserMap")
    fun selectInventorsByPatentId(id: Long): MutableList<User>

    @Select("SELECT * FROM user JOIN follow ON user.id=follow.followed_id WHERE user.id=#{id} ORDERED BY time DESC")
    @ResultMap("UserMap")
    fun selectFollowedByFollwerId(id: Long): MutableList<User>

    @Insert("INSERT INTO follow(follower_id,followed_id,time) VALUES(#{follower_id},#{followed_id},#{time})")
    @Throws(DataIntegrityViolationException::class)
    fun insertFollowPair(follower_id: Long, followed_id: Long, time: Date)

    @Delete("DELETE FROM follow WHERE follower_id=#{follower_id} AND followed_id=#{followed_id}")
    fun deleteFollowPair(follower_id: Long, followed_id: Long): Int

    @Update("UPDATE user SET email=#{email} WHERE id=#{id}")
    fun updateEmail(id: Long, email: String): Int

    @Select("SELECT * FROM user JOIN expert ON user.id=expert.id WHERE user.email='' AND user.role=2 AND expert.name=#{name}")
    @ResultMap("UserMap")
    fun selectUnclaimedUserByExpertName(name: String): User?

}

@Mapper
@Repository
interface OrganizationMapper : BaseMapper<Organization> {
    @Select("SELECT * FROM organization WHERE id=#{id}")
    @Results(
            Result(property = "id", column = "id"),
            Result(property = "name", column = "name"),
            Result(property = "contact", column = "contact"),
            Result(property = "rank", column = "rank")
    )
    fun selectById(id: Long): Organization

    @Insert("INSERT IGNORE INTO organization(name) VALUES(#{name})")
    @SelectKey(statement = ["SELECT id FROM organization WHERE name=#{name}"], keyProperty = "id", keyColumn = "id", before = false, resultType = Long::class)
    fun insertIfNotExists(organization: Organization)
}

@Mapper
@Repository
interface PaperMapper : BaseMapper<Paper> {
    @Select("SELECT * FROM paper WHERE id=#{id}")
    @Results(
            id = "PaperMap",
            value = [
                Result(property = "id", column = "id"),
                Result(property = "title", column = "title"),
                Result(property = "paperRec", column = "paper_rec"),
                Result(property = "dataRec", column = "data_rec"),
                Result(property = "citeTimes", column = "cite_times"),
                Result(property = "clickTimes", column = "click_times"),
                Result(property = "publishTime", column = "publish_time"),
                Result(property = "abstract", column = "abstract"),
                Result(property = "keywords", column = "keywords"),
                Result(property = "label", column = "label"),
                Result(property = "authors", column = "id", many = Many(select = "cn.edu.buaa.se.docs.UserMapper.selectAuthorsByPaperId"))
            ]
    )
    @Throws(DataRetrievalFailureException::class)
    fun selectById(id: Long): Paper?


    @Select("<script>" +
            "SELECT * FROM paper " +
            "WHERE MATCH(`title`,`abstract`) AGAINST(#{keyword} IN NATURAL LANGUAGE MODE) " +
            "<if test='year != null'>" +
            "AND YEAR(`publish_time`)=#{year} " +
            "</if>" +
            "ORDER BY " +
            "<choose>" +
            "<when test='sort.method==\"date\"'>" +
            "`publish_time` DESC" +
            "</when>" +
            "<when test='sort.method==\"click\"'>" +
            "`click_times` DESC" +
            "</when>" +
            "</choose>" +
            "LIMIT #{offset},#{limit}" +
            "</script>")
    @ResultMap("PaperMap")
    fun search(keyword: String, sort: SearchSort, year: Int?, offset: Int, limit: Int): MutableList<Paper>

    @Select("SELECT * FROM paper JOIN author_paper ON paper.id=author_paper.paper_id WHERE author_paper.author_id=#{id}")
    @ResultMap("PaperMap")
    fun selectByAuthorId(id: Long): MutableList<Paper>


    @Select("SELECT * FROM paper JOIN collection_paper ON paper.id=collection_paper.paper_id WHERE collection_paper.user_id=#{id}")
    @ResultMap("PaperMap")
    fun selectPaperCollectionByUserId(id: Long): MutableList<Paper>

    @Insert("INSERT INTO collection_paper(user_id,paper_id,time) VALUES (#{user_id},#{paper_id},#{time})")
    @Throws(DataIntegrityViolationException::class)
    fun insertPaperCollection(user_id: Long, paper_id: Long, time: Date)

    @Delete("DELETE FROM collection_paper WHERE user_id=#{user_id} AND paper_id=#{paper_id}")
    fun deletePaperCollection(user_id: Long, paper_id: Long): Int

    @Update("UPDATE paper SET click_time=click_time+1 WHERE id=#{id}")
    fun updatePaperClickTime(id: Long)
}


@Mapper
@Repository
interface PatentMapper : BaseMapper<Patent> {
    @Select("SELECT * FROM patent WHERE id=#{id}")
    @Results(
            id = "PatentMap",
            value = [
                Result(property = "id", column = "id"),
                Result(property = "title", column = "title"),
                Result(property = "applicationNumber", column = "application_number"),
                Result(property = "publicationNumber", column = "publication_number"),
                Result(property = "agency", column = "agency"),
                Result(property = "agent", column = "agent"),
                Result(property = "summary", column = "summary"),
                Result(property = "address", column = "address"),
                Result(property = "clickTimes", column = "click_times"),
                Result(property = "applicationDate", column = "application_date"),
                Result(property = "publicationDate", column = "publication_date"),
                Result(property = "applicants", column = "id", many = Many(select = "cn.edu.buaa.se.docs.UserMapper.selectApplicantsByPatentId")),
                Result(property = "inventors", column = "id", many = Many(select = "cn.edu.buaa.se.docs.UserMapper.selectInventorsByPatentId"))
            ]
    )
    @Throws(DataRetrievalFailureException::class)
    fun selectById(id: Long): Patent?

    @Select("<script>" +
            "SELECT * FROM `patent` " +
            "WHERE MATCH(`title`,`summary`) AGAINST(#{keyword} IN NATURAL LANGUAGE MODE) " +
            "<if test='year!= null'>" +
            "AND YEAR(`public_time`)=#{year} " +
            "</if>" +
            "ORDER BY " +
            "<choose>" +
            "<when test='sort.method==\"date\"'>" +
            "`publication_date` DESC" +
            "</when>" +
            "<when test='sort.method==\"click\"'>" +
            "`click_times` DESC" +
            "</when>" +
            "</choose>" +
            "LIMIT #{offset},#{limit}" +
            "</script>")
    @ResultMap("PatentMap")
    fun search(keyword: String, sort: SearchSort, year: Int?, offset: Int, limit: Int): MutableList<Patent>

    @Select("SELECT * FROM patent JOIN applicant_patent ON patent.id=applicant_patent.patent_id WHERE applicant_patent.applicant_id=#{id}")
    @ResultMap("PatentMap")
    fun selectByApplicantId(id: Long): MutableList<Patent>


    @Select("SELECT * FROM patent JOIN inventor_patent ON patent.id=inventor_patent.patent_id WHERE inventor_patent.inventor_id=#{id}")
    @ResultMap("PatentMap")
    fun selectByInventorId(id: Long): MutableList<Patent>


    @Select("SELECT * FROM patent JOIN collection_patent ON patent.id=collection_patent.patent_id WHERE collection_patent.user_id=#{id}")
    @ResultMap("PatentMap")
    fun selectPatentCollectionByUserId(id: Long): MutableList<Patent>

    @Insert("INSERT INTO collection_patent(user_id,patent_id,time) VALUES (#{user_id},#{patent_id},#{time})")
    @Throws(DataIntegrityViolationException::class)
    fun insertPatentCollection(user_id: Long, patent_id: Long, time: Date)

    @Delete("DELETE FROM collection_patent WHERE user_id=#{user_id} AND patent_id=#{patent_id}")
    fun deletePatentCollection(user_id: Long, patent_id: Long): Int

    @Update("UPDATE patent SET click_time=click_time+1 WHERE id=#{id}")
    fun updatePatentClickTime(id: Long)
}