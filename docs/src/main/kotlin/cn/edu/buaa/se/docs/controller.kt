package cn.edu.buaa.se.docs

import io.swagger.annotations.*
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


    /*
    @ApiOperation(value = "修改专业", notes = "修改专业")
    @ApiImplicitParam(name = "subject", value = "专业", dataType = "String")
     */
    @ApiOperation(value = "查询论文/专利", notes = "查询论文/专利")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "keyword", value = "搜索关键词", dataType = "String", required = true),
            ApiImplicitParam(name = "type", value = "搜索类型,论文为'paper',专利为'patent',默认搜索论文", dataType = "String", required = false),
            ApiImplicitParam(name = "sort", value = "排序方式,时间为'date',点击量为'click',默认按时间排序", dataType = "String", required = false),
            ApiImplicitParam(name = "year", value = "限定搜索结果年份,默认全部", dataType = "String", required = false),
            ApiImplicitParam(name = "limit", value = "限定搜索结果最大条目数,默认为12*10条", dataType = "String", required = false)
    )
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
        val nLimit = limit ?: PAGE_COUNT * 10
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