package cn.edu.buaa.se.applicationform

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.mapstruct.Mapper
import org.springframework.stereotype.Repository
import java.util.*

@Mapper
@Repository
interface ApplicationMapper:BaseMapper<Application>{

    @Select("select content from application where id=#{id}")
    fun getContent(id:Long):String

    @Update("update application set examineTime=#{etime}, status=#{status},adminId=#{aid} where id=#{id}")
    fun updateExamineResult(id: Long,status:Short,aid:Long,etime:Date)

    @Insert("insert into application(userId,content,status,applyTime) values(#{uid},#{content},#{status},#{atime})")
    fun insertApplication(uid: Long,content:String,status:Short,atime:Date)
}