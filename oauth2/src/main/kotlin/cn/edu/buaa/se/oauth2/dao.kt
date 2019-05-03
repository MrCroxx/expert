package cn.edu.buaa.se.oauth2

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface UserMapper : BaseMapper<User>
/*
{
    fun findByUsername(username: String): User? = this.selectOne(
            QueryWrapper<User?>().eq("username", username)
    )
}
        */