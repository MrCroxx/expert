package cn.edu.buaa.se.docs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExpertService {
    @Autowired
    lateinit var userMapper: UserMapper

    fun getExpertById(id: Long): User {
        return userMapper.selectById(id)
    }
}

@Service
class PaperService {
    @Autowired
    lateinit var paperMapper: PaperMapper

    fun getPaperById(id: Long): Paper = paperMapper.selectById(id)

}


@Service
class PatentService {
    @Autowired
    lateinit var patentMapper: PatentMapper

    fun getPatentById(id: Long): Patent = patentMapper.selectById(id)

}