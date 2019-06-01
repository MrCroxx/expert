package cn.edu.buaa.se.docs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RefreshScope
@RequestMapping("/search")
class SearchController {

    final val PAGE_COUNT = 12

    @Autowired
    lateinit var searchService: SearchService


    @GetMapping("/")
    fun search(
            keyword: String,
            type: String?,
            sort: String?,
            year: Int?,
            limit: Int?
    ): CResponseBody<SearchResult?> {
        val eType = DocType.fromString(type ?: "")
        val eSort = SearchSort.fromString(sort ?: "")
        val nLimit = limit ?: 120
        val result = searchService.search(
                keyword = keyword,
                type = eType,
                sort = eSort,
                year = year,
                offset = 0,
                limit = nLimit
        )
        return CResponseBody(data = result)
    }

}


@RestController
@RefreshScope
@RequestMapping("/paper")
class PaperController {

}


@RestController
@RefreshScope
@RequestMapping("/patent")
class PatentController {

}

@RestController
@RefreshScope
@RequestMapping("/test")
class TestController {

    @Autowired
    lateinit var expertService: ExpertService

    @Autowired
    lateinit var paperService: PaperService

    @Autowired
    lateinit var patentService: PatentService

    @GetMapping("/user/{id}")
    fun getUser(@PathVariable id: Int): User {
        return expertService.getExpertById(id.toLong())
    }

    @GetMapping("/paper/{id}")
    fun getPaper(@PathVariable id: Int): Paper {
        return paperService.getPaperById(id.toLong())
    }

    @GetMapping("/patent/{id}")
    fun getPatent(@PathVariable id: Int): Patent {
        return patentService.getPatentById(id.toLong())
    }

    @Autowired
    lateinit var userMapper: UserMapper

    @GetMapping("/patent/{id}/inventors")
    fun getPatentInventors(@PathVariable id: Int) = userMapper.selectInventorsByPatentId(id.toLong())
}