package cn.edu.buaa.se.docs

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface ExpertMapper : BaseMapper<Expert> {
    @Select("SELECT * FROM expert WHERE id=#{id}")
    @Results(
            Result(property = "name", column = "name"),
            Result(property = "subject", column = "subject"),
            Result(property = "education", column = "education"),
            Result(property = "introduction", column = "introduction"),
            Result(property = "famousValue", column = "famous_value"),
            Result(property = "organization", column = "organization_id", one = One(select = "cn.edu.buaa.se.docs.OrganizationMapper.selectById"))
    )
    fun selectById(id: Long): Expert
}

@Mapper
@Repository
interface UserMapper : BaseMapper<User> {
    @Select("SELECT * FROM user WHERE id=#{id}")
    @Results(
            Result(property = "id", column = "id"),
            Result(property = "username", column = "username"),
            Result(property = "email", column = "email"),
            Result(property = "credit", column = "credit"),
            Result(property = "frozenCredit", column = "frozen_credit"),
            Result(property = "role", column = "role"),
            Result(property = "expert", column = "id", one = One(select = "cn.edu.buaa.se.docs.ExpertMapper.selectById"))
    )
    fun selectById(id: Long): User

    @Select("SELECT * FROM user JOIN author_paper ON user.id=author_paper.author_id WHERE author_paper.paper_id=#{id}")
    @Results(
            Result(property = "id", column = "id"),
            Result(property = "username", column = "username"),
            Result(property = "email", column = "email"),
            Result(property = "credit", column = "credit"),
            Result(property = "frozenCredit", column = "frozen_credit"),
            Result(property = "role", column = "role"),
            Result(property = "expert", column = "id", one = One(select = "cn.edu.buaa.se.docs.ExpertMapper.selectById"))
    )
    fun selectAuthorsByPaperId(id: Long): MutableList<User>

    @Select("SELECT * FROM user JOIN applicant_patent ON user.id=applicant_patent.applicant_id WHERE applicant_patent.patent_id=#{id}")
    @Results(
            Result(property = "id", column = "id"),
            Result(property = "username", column = "username"),
            Result(property = "email", column = "email"),
            Result(property = "credit", column = "credit"),
            Result(property = "frozenCredit", column = "frozen_credit"),
            Result(property = "role", column = "role"),
            Result(property = "expert", column = "id", one = One(select = "cn.edu.buaa.se.docs.ExpertMapper.selectById"))
    )
    fun selectApplicantsByPatentId(id: Long): MutableList<User>

    @Select("SELECT * FROM user JOIN inventor_patent ON user.id=inventor_patent.inventor_id WHERE inventor_patent.patent_id=#{id}")
    @Results(
            Result(property = "id", column = "id"),
            Result(property = "username", column = "username"),
            Result(property = "email", column = "email"),
            Result(property = "credit", column = "credit"),
            Result(property = "frozenCredit", column = "frozen_credit"),
            Result(property = "role", column = "role"),
            Result(property = "expert", column = "id", one = One(select = "cn.edu.buaa.se.docs.ExpertMapper.selectById"))
    )
    fun selectInventorsByPatentId(id: Long): MutableList<User>
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
}

@Mapper
@Repository
interface PaperMapper : BaseMapper<Paper> {
    @Select("SELECT * FROM paper WHERE id=#{id}")
    @Results(
            Result(property = "id", column = "id"),
            Result(property = "title", column = "title"),
            Result(property = "paperRec", column = "paper_rec"),
            Result(property = "dataRec", column = "data_rec"),
            Result(property = "citeTimes", column = "cite_times"),
            Result(property = "clickTimes", column = "click_times"),
            Result(property = "publicTime", column = "public_time"),
            Result(property = "abstract", column = "abstract"),
            Result(property = "keywords", column = "keywords"),
            Result(property = "label", column = "label"),
            Result(property = "authors", column = "id", many = Many(select = "cn.edu.buaa.se.docs.UserMapper.selectAuthorsByPaperId"))
    )
    fun selectById(id: Long): Paper

    @Select("<script>" +
            "SELECT * FROM paper " +
            "WHERE MATCH(`title`,`abstract`) AGAINST(#{keyword}) " +
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
    @Results(
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
    )
    fun search(keyword: String, sort: SearchSort, year: Int?, offset: Int, limit: Int): MutableList<Paper>

    @Select("SELECT COUNT(*) FROM `paper`")
    fun selectCount(): Int
}


@Mapper
@Repository
interface PatentMapper : BaseMapper<Patent> {
    @Select("SELECT * FROM patent WHERE id=#{id}")
    @Results(
            Result(property = "id", column = "id"),
            Result(property = "title", column = "title"),
            Result(property = "applicationNumber", column = "application_number"),
            Result(property = "publicationNumber", column = "publication_number"),
            Result(property = "agency", column = "agency"),
            Result(property = "agent", column = "agent"),
            Result(property = "summary", column = "summary"),
            Result(property = "address", column = "address"),
            Result(property = "clickTime", column = "click_times"),
            Result(property = "applicationDate", column = "application_date"),
            Result(property = "publicationDate", column = "publication_date"),
            Result(property = "applicants", column = "id", many = Many(select = "cn.edu.buaa.se.docs.UserMapper.selectApplicantsByPatentId")),
            Result(property = "inventors", column = "id", many = Many(select = "cn.edu.buaa.se.docs.UserMapper.selectInventorsByPatentId"))
    )
    fun selectById(id: Long): Patent

    @Select("<script>" +
            "SELECT * FROM `patent` " +
            "WHERE MATCH(`title`,`summary`) AGAINST(#{keyword}) " +
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
    @Results(
            Result(property = "id", column = "id"),
            Result(property = "title", column = "title"),
            Result(property = "applicationNumber", column = "application_number"),
            Result(property = "publicationNumber", column = "publication_number"),
            Result(property = "agency", column = "agency"),
            Result(property = "agent", column = "agent"),
            Result(property = "summary", column = "summary"),
            Result(property = "address", column = "address"),
            Result(property = "clickTime", column = "click_times"),
            Result(property = "applicationDate", column = "application_date"),
            Result(property = "publicationDate", column = "publication_date"),
            Result(property = "applicants", column = "id", many = Many(select = "cn.edu.buaa.se.docs.UserMapper.selectApplicantsByPatentId")),
            Result(property = "inventors", column = "id", many = Many(select = "cn.edu.buaa.se.docs.UserMapper.selectInventorsByPatentId"))
    )
    fun search(keyword: String, sort: SearchSort, year: Int?, offset: Int, limit: Int): MutableList<Patent>

    @Select("SELECT COUNT(*) FROM `patent`")
    fun selectCount(): Int
}