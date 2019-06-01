package cn.edu.buaa.se.paper_patent

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository


@Mapper
@Repository
interface PaperMapper:BaseMapper<Paper>{
    //根据id获取论文信息
    @Select("SELECT paper.id as pid,title,author,cite_times,click_times,publish_time,abstract FROM paper " +
            "WHERE paper.id = #{id}")
    @Results(
            Result(property = "id",column = "pid"),
            Result(property = "title",column = "title"),
            Result(property = "author",column = "author"),
            Result(property = "paper_rec",column = "paper_rec"),
            Result(property = "data_rec",column = "data_rec"),
            Result(property = "cite_times",column = "cite_times"),
            Result(property = "click_times",column = "click_times"),
            Result(property = "publish_time",column = "publish_time"),
            Result(property = "abstract",column = "abstract")
    )
    fun findPaperByID(id: Long): Paper

    //专家查看个人论文列表
    @Select("select paper.id,title,author,cite_times,click_times,publish_time,abstract,name from paper,expert " +
            "where paper.author = expert.id and author = #{uid} ORDER BY publish_time DESC , id ASC limit #{currIndex} ," + pageSize )
    @Results(
            Result(property = "id",column = "id"),
            Result(property = "title",column = "title"),
            Result(property = "author",column = "author"),
            Result(property = "cite_times",column = "cite_times"),
            Result(property = "click_times",column = "click_times"),
            Result(property = "publish_time",column = "publish_time"),
            Result(property = "abstract",column = "abstract"),
            Result(property = "name",column = "name")
    )
    fun viewingPersonalPapers(uid: Long,currIndex: Int): List<Paper>

    //根据作者获取论文信息(按点击量排序)
    @Select("select paper.id,title,author,cite_times,click_times,publish_time,abstract,name from paper,expert " +
            "where name LIKE #{name} and paper.author = expert.id ORDER BY click_times DESC , id ASC limit #{currIndex} ," + pageSize )
    @Results(
            Result(property = "id",column = "id"),
            Result(property = "title",column = "title"),
            Result(property = "author",column = "author"),
            Result(property = "cite_times",column = "cite_times"),
            Result(property = "click_times",column = "click_times"),
            Result(property = "publish_time",column = "publish_time"),
            Result(property = "abstract",column = "abstract"),
            Result(property = "name",column = "name")
    )
    fun findPaperByAuthor_click_times(name: String,currIndex: Int): List<Paper>

    //根据作者获取论文信息(按被引量排序)
    @Select("select paper.id,title,author,cite_times,click_times,publish_time,abstract,name from paper,expert " +
            "where name LIKE #{name} and paper.author = expert.id ORDER BY cite_times DESC , id ASC limit #{currIndex} ," + pageSize )
    @Results(
            Result(property = "id",column = "id"),
            Result(property = "title",column = "title"),
            Result(property = "author",column = "author"),
            Result(property = "cite_times",column = "cite_times"),
            Result(property = "click_times",column = "click_times"),
            Result(property = "publish_time",column = "publish_time"),
            Result(property = "abstract",column = "abstract"),
            Result(property = "name",column = "name")
    )
    fun findPaperByAuthor_cite_times(name: String,currIndex: Int): List<Paper>

    //根据作者获取论文信息(按时间排序)
    @Select("select paper.id,title,author,cite_times,click_times,publish_time,abstract,name from paper,expert " +
            "where name LIKE #{name} and paper.author = expert.id ORDER BY publish_time DESC , id ASC limit #{currIndex} ," + pageSize )
    @Results(
            Result(property = "id",column = "id"),
            Result(property = "title",column = "title"),
            Result(property = "author",column = "author"),
            Result(property = "cite_times",column = "cite_times"),
            Result(property = "click_times",column = "click_times"),
            Result(property = "publish_time",column = "publish_time"),
            Result(property = "abstract",column = "abstract"),
            Result(property = "name",column = "name")
    )
    fun findPaperByAuthor_publish_time(name: String,currIndex: Int): List<Paper>

    //根据题目获取论文信息（按点击量）
    @Select("SELECT id,title,author,cite_times,click_times,publish_time,abstract FROM paper " +
            "WHERE title LIKE #{title} ORDER BY click_times DESC , id ASC limit #{currIndex} ," + pageSize )
    @Results(
            Result(property = "id",column = "id"),
            Result(property = "title",column = "title"),
            Result(property = "author",column = "author"),
            Result(property = "cite_times",column = "cite_times"),
            Result(property = "click_times",column = "click_times"),
            Result(property = "publish_time",column = "publish_time"),
            Result(property = "abstract",column = "abstract")
    )
    fun findPaperByTitle_click_times(title: String,currIndex: Int): List<Paper>

    //根据题目获取论文信息（按被引量）
    @Select("SELECT id,title,author,cite_times,click_times,publish_time,abstract FROM paper " +
            "WHERE title LIKE #{title} ORDER BY cite_times DESC , id ASC limit #{currIndex} ," + pageSize )
    @Results(
            Result(property = "id",column = "id"),
            Result(property = "title",column = "title"),
            Result(property = "author",column = "author"),
            Result(property = "cite_times",column = "cite_times"),
            Result(property = "click_times",column = "click_times"),
            Result(property = "publish_time",column = "publish_time"),
            Result(property = "abstract",column = "abstract")
    )
    fun findPaperByTitle_cite_times(title: String,currIndex: Int): List<Paper>

    //根据题目获取论文信息（按时间）
    @Select("SELECT id,title,author,cite_times,click_times,publish_time,abstract FROM paper " +
            "WHERE title LIKE #{title} ORDER BY publish_time DESC , id ASC limit #{currIndex} ," + pageSize )
    @Results(
            Result(property = "id",column = "id"),
            Result(property = "title",column = "title"),
            Result(property = "author",column = "author"),
            Result(property = "cite_times",column = "cite_times"),
            Result(property = "click_times",column = "click_times"),
            Result(property = "publish_time",column = "publish_time"),
            Result(property = "abstract",column = "abstract")
    )
    fun findPaperByTitle_publish_time(title: String,currIndex: Int): List<Paper>

    //根据摘要获取论文信息（按点击量）
    @Select("(SELECT id,title,author,cite_times,click_times,publish_time,abstract FROM paper " +
            "WHERE MATCH (title,abstract) AGAINST (#{abstract} in natural language mode)) UNION " +
            "(SELECT id,title,author,cite_times,click_times,publish_time,abstract FROM paper WHERE abstract LIKE #{title} or title LIKE #{title})" +
            "ORDER BY click_times DESC , id ASC limit #{currIndex} ," + pageSize )
    fun findPaperByAbstract_click_times(abstract: String,title: String,currIndex: Int): List<Paper>

    //根据摘要获取论文信息（按被引量）
    @Select("(SELECT id,title,author,cite_times,click_times,publish_time,abstract FROM paper " +
            "WHERE MATCH (title,abstract) AGAINST (#{abstract} in natural language mode)) UNION " +
            "(SELECT id,title,author,cite_times,click_times,publish_time,abstract FROM paper WHERE abstract LIKE #{title} or title LIKE #{title})" +
            "ORDER BY cite_times DESC , id ASC limit #{currIndex} ," + pageSize )
    fun findPaperByAbstract_cite_times(abstract: String,title: String,currIndex: Int): List<Paper>

    //根据摘要获取论文信息（按时间）
    @Select("(SELECT id,title,author,cite_times,click_times,publish_time,abstract FROM paper " +
            "WHERE MATCH (title,abstract) AGAINST (#{abstract} in natural language mode)) UNION " +
            "(SELECT id,title,author,cite_times,click_times,publish_time,abstract FROM paper WHERE abstract LIKE #{title} or title LIKE #{title})" +
            "ORDER BY publish_time DESC , id ASC limit #{currIndex} ," + pageSize )
    fun findPaperByAbstract_publish_time(abstract: String,title: String,currIndex: Int): List<Paper>

    //论文点击次数+1
    @Update("UPDATE paper SET click_times = #{p.click_times} + 1 WHERE id = #{p.id}")
    fun updateclick_times(@Param(value="p") p: Paper,@Param(value="id") id: Long)

    //查询最大值
    @Select("SELECT MAX(id) FROM paper")
    fun findpaperIDmax(): Long

    //插入新paper
    @Insert("INSERT INTO paper(id, title, author, paper_rec, data_rec ,cite_times ,click_times ,publish_time, abstract) " +
            "VALUES (#{p.id}, #{p.title}, #{p.author}, #{p.paper_rec}, #{p.data_rec}, #{p.cite_times}, " +
            "#{p.click_times}, #{p.publish_time}, #{p.abstract})")
    fun insertPaper(@Param(value="p") p: Paper)

    //修改paper信息
    @Update("UPDATE paper SET title = #{p.title}, abstract = #{p.abstract} WHERE id = #{p.id}")
    fun updatePaper(@Param(value="p") p: Paper,@Param(value="id") id: Long)

    //删除paper信息
    @Delete("DELETE from paper WHERE id = #{id}")
    fun deletePaperById(@Param(value="id") id: Long)
}


@Mapper
@Repository
interface PatentMapper:BaseMapper<Patent>{
    //根据id获取专利信息
    @Select("SELECT * FROM patent WHERE id = #{id}")
    @Results(
            Result(property = "id",column = "id"),
            Result(property = "title",column = "title"),
            Result(property = "application_date",column = "application_date"),
            Result(property = "publication_date",column = "publication_date"),
            Result(property = "inventor_id",column = "inventor_id"),
            Result(property = "applicant_id",column = "applicant_id")
    )
    fun findPatentByID(id: Long): Patent

    //专家查看个人专利列表
    @Select("select patent.id as pid,title,application_date,publication_date,inventor_id,applicant_id from patent,expert " +
            "where patent.applicant_id = expert.id and applicant_id = #{uid} " +
            "ORDER BY publication_date DESC , patent.id ASC limit #{currIndex} ," + pageSize )
    @Results(
            Result(property = "id",column = "pid"),
            Result(property = "title",column = "title"),
            Result(property = "application_date",column = "application_date"),
            Result(property = "publication_date",column = "publication_date"),
            Result(property = "inventor_id",column = "inventor_id"),
            Result(property = "applicant_id",column = "applicant_id")
    )
    fun viewingPersonalPatents(uid: Long,currIndex: Int): List<Patent>

    //根据题目获取专利信息
    @Select("SELECT * FROM patent WHERE title LIKE #{title} limit #{currIndex} ," + pageSize)
    @Results(
            Result(property = "id",column = "id"),
            Result(property = "title",column = "title"),
            Result(property = "application_date",column = "application_date"),
            Result(property = "publication_date",column = "publication_date"),
            Result(property = "inventor_id",column = "inventor_id"),
            Result(property = "applicant_id",column = "applicant_id")
    )
    fun findPatentByTitle(title: String,currIndex: Int): List<Patent>

    //查询最大值
    @Select("SELECT MAX(id) FROM patent")
    fun findpatentIDmax(): Long

    //插入新patent
    @Insert("INSERT INTO patent(id, title, application_date, publication_date, inventor_id ,applicant_id) " +
            "VALUES (#{p.id}, #{p.title}, #{p.application_date}, #{p.publication_date}, #{p.inventor_id}, #{p.applicant_id})")
    fun insertPatent(@Param(value="p") p: Patent)

    //修改patent信息
    @Update("UPDATE patent SET title = #{p.title} WHERE id = #{p.id}")
    fun updatePatent(@Param(value="p") p: Patent,@Param(value="id") id: Long)

    //删除patent信息
    @Delete("DELETE from patent WHERE id = #{id}")
    fun deletePatentById(@Param(value="id") id: Long)
}


@Mapper
@Repository
interface Paper_collectionMapper:BaseMapper<PaperCollection>{

    //查找
    @Select("SELECT * FROM collection_paper WHERE user_id = #{user_id} and paper_id = #{paper_id}")
    @Results(
            Result(property = "user_id",column = "user_id"),
            Result(property = "paper_id",column = "paper_id"),
            Result(property = "time",column = "time")
    )
    fun findPaper_collection(user_id: Long,paper_id: Long): PaperCollection


    //插入
    @Insert("INSERT INTO collection_paper(user_id, paper_id, time) VALUES (#{p.user_id}, #{p.paper_id}, #{p.time})")
    fun insertPaper_collection(@Param(value="p") p: PaperCollection)

    //删除收藏信息
    @Delete("DELETE from collection_paper WHERE user_id = #{user_id} and paper_id = #{paper_id}")
    fun deletePaper_collection(user_id: Long,paper_id: Long)
}


@Mapper
@Repository
interface Patent_collectionMapper:BaseMapper<PatentCollection>{

    //查找
    @Select("SELECT * FROM collection_patent WHERE user_id = #{user_id} and patent_id = #{patent_id}")
    @Results(
            Result(property = "user_id",column = "user_id"),
            Result(property = "patent_id",column = "patent_id"),
            Result(property = "time",column = "time")
    )
    fun findPatent_collection(user_id: Long,patent_id: Long): PatentCollection


    //插入
    @Insert("INSERT INTO collection_patent(user_id, patent_id, time) VALUES (#{p.user_id}, #{p.patent_id}, #{p.time})")
    fun insertPatent_collection(@Param(value="p") p: PatentCollection)

    //删除收藏信息
    @Delete("DELETE from collection_patent WHERE user_id = #{user_id} and patent_id = #{patent_id}")
    fun deletePatent_collection(user_id: Long,patent_id: Long)
}