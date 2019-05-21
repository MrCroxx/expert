package cn.edu.buaa.se.account

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.*
import org.mapstruct.Mapper
import org.springframework.stereotype.Repository

@Repository
@Mapper
interface UserMapper : BaseMapper<User> {

    @Select("select * from user where username=#{username}")
    fun selectByUsername(username: String): User

    @Select("select count(*) from user where username=#{username}")
    fun selectCount(username: String): Int

    @Update("update user set email=#{email} where username=#{username}")
    fun updateEmail(username: String, email: String): Int

    @Update("update user set password=#{password} where username=#{username}")
    fun updatePassword(username: String, password: String): Int

    @Update("update user set credit=credit+#{credit} where username=#{username}")
    fun updateCredit(username: String, credit: Int): Int
}

@Repository
@Mapper
interface ExpertMapper : BaseMapper<Expert> {

    @Select("select * from expert where id=#{id}")
    fun selectById(id: Long): Expert

    @Select("SELECT * FROM expert JOIN user ON expert.id=user.id WHERE user.username=#{username}")
    @Results(
            Result(property = "id", column = "id"),
            Result(property = "name", column = "name"),
            Result(property = "subject", column = "subject"),
            Result(property = "education", column = "education"),
            Result(property = "introduction", column = "introduction"),
            Result(property = "famousValue", column = "famous_value"),
            Result(property = "organization", column = "organization_id", one = One(select = "cn.edu.buaa.se.account.OrganizationMapper.selectById"))
    )
    fun selectByUsername(username: String): Expert

    @Update("update expert set subject=#{subject} where id=#{id}")
    fun updateSubject(subject: String, id: Long): Int

    @Update("update expert set education=#{education} where id=#{id}")
    fun updateEducation(education: String, id: Long): Int

    @Update("update expert set introduction=#{introduction} where id=#{id}")
    fun updateIntroduction(introduction: String, id: Long): Int
}

@Repository
@Mapper
interface OrganizationMapper : BaseMapper<Organization> {
    @Select("SELECT * FROM organization WHERE id=#{id}")
    fun selectById(id: Long): Organization
}