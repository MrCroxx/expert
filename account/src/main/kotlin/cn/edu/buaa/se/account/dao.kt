package cn.edu.buaa.se.account

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.mapstruct.Mapper
import org.springframework.stereotype.Repository

@Repository
@Mapper
interface UserMapper:BaseMapper<User>{

    @Select("select * from user where username=#{username}")
    fun selectByUsername(username:String):User

    @Select("select count(*) from user where username=#{username}")
    fun selectCount(username: String):Int

    @Update("update user set email=#{email} where username=#{username}")
    fun updateEmail(username: String,email: String):Int

    @Update("update user set password=#{password} where username=#{username}")
    fun updatePassword(username:String,password:String):Int

    @Update("update user set credit=credit+#{credit} where username=#{username}")
    fun updateCredit(username:String,credit:Int):Int
}

@Repository
@Mapper
interface ExpertMapper:BaseMapper<Expert>{

    @Select("select * from expert where id=#{id}")
    fun selectById(id:Long):Expert

    @Update("update expert set subject=#{subject} where id=#{id}")
    fun updateSubject(subject:String,id:Long):Int

    @Update("update expert set education=#{education} where id=#{id}")
    fun updateEducation(education:String,id:Long):Int

    @Update("update expert set introduction=#{introduction} where id=#{id}")
    fun updateIntroduction(introduction:String,id:Long):Int
}