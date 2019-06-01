package cn.edu.buaa.se.docs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


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