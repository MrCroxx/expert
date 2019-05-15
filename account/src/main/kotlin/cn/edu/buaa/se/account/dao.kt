package cn.edu.buaa.se.account

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.mapstruct.Mapper

@Mapper
interface UserMapper:BaseMapper<User>{}

@Mapper
interface ExpertMapper:BaseMapper<Expert>{}