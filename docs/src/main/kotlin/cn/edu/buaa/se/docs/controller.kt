package cn.edu.buaa.se.docs

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.web.bind.annotation.*

@RestController
@RefreshScope
@RequestMapping("/search")
class SearchController {

    final val PAGE_COUNT = 12

    @Autowired
    lateinit var searchService: SearchService


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
    @GetMapping("")
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

    @Autowired
    lateinit var paperService: PaperService

    @ApiOperation(value = "获取论文详细信息", notes = "获取论文详细信息")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40004, message = "id不存在")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "论文id", dataType = "Long", required = true, paramType = "PathVariable")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    fun paper(@PathVariable id: Int): CResponseBody<Paper?> {
        val result = paperService.getPaperById(id.toLong())
                ?: return CResponseBody(errcode = ErrCode.DATA_NOT_EXISTS.code, msg = ErrCode.DATA_NOT_EXISTS.name, data = null)
        return CResponseBody(data = result)
    }
}


@RestController
@RefreshScope
@RequestMapping("/patent")
class PatentController {
    @Autowired
    lateinit var patentService: PatentService

    @ApiOperation(value = "获取专利详细信息", notes = "获取专利详细信息")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40004, message = "id不存在")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "专利id", dataType = "Long", required = true, paramType = "PathVariable")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    fun patent(@PathVariable id: Int): CResponseBody<Patent?> {
        val result = patentService.getPatentById(id.toLong())
                ?: return CResponseBody(errcode = ErrCode.DATA_NOT_EXISTS.code, msg = ErrCode.DATA_NOT_EXISTS.name, data = null)
        return CResponseBody(data = result)
    }
}

@RestController
@RefreshScope
@RequestMapping("/collection")
class CollectionController {

    @Autowired
    lateinit var collectionService: CollectionService

    @ApiOperation(value = "获取收藏内容", notes = "获取收藏的论文/专利")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    fun collections(): CResponseBody<Collections> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        return CResponseBody(data = Collections(papers = collectionService.getPaperCollection(uid), patents = collectionService.getPatentCollection(uid)))
    }

    @ApiOperation(value = "收藏论文/专利", notes = "收藏论文/专利")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40005, message = "数据库完整性/一致性约束异常,包括重复收藏或收藏内容不存在两种情况")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "type", value = "收藏类型,包括论文(paper)和专利(patent)", dataType = "String", required = true, paramType = "PathVariable"),
            ApiImplicitParam(name = "id", value = "论文/专利id", dataType = "Long", required = true, paramType = "PathVariable")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{type}/{id}")
    fun addCollection(@PathVariable type: String, @PathVariable id: Int): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val res = when (type) {
            "paper" -> collectionService.insertCollection(uid, id.toLong(), DocType.PAPER)
            "patent" -> collectionService.insertCollection(uid, id.toLong(), DocType.PATENT)
            else -> ErrCode.TYPE_ILLEGAL
        }
        return CResponseBody<Nothing?>(errcode = res.code, msg = res.name, data = null)
    }

    @ApiOperation(value = "取消收藏论文/专利", notes = "取消收藏论文/专利")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40004, message = "数据不存在")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "type", value = "收藏类型,包括论文(paper)和专利(patent)", dataType = "String", required = true, paramType = "PathVariable"),
            ApiImplicitParam(name = "id", value = "论文/专利id", dataType = "Long", required = true, paramType = "PathVariable")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{type}/{id}")
    fun deleteCollection(@PathVariable type: String, @PathVariable id: Int): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val res = when (type) {
            "paper" -> collectionService.deleteCollection(uid, id.toLong(), DocType.PAPER)
            "patent" -> collectionService.deleteCollection(uid, id.toLong(), DocType.PATENT)
            else -> ErrCode.TYPE_ILLEGAL
        }
        return CResponseBody<Nothing?>(errcode = res.code, msg = res.name, data = null)
    }
}

@RestController
@RequestMapping("/user")
@RefreshScope
class UserController {
    @Autowired
    lateinit var userService: UserService

    @ApiOperation(value = "获取用户详细信息", notes = "获取用户详细信息")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40004, message = "id不存在")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "用户id", dataType = "Long", required = true, paramType = "PathVariable")
    )
    @GetMapping("/{id}")
    fun getUserInfoById(@PathVariable id: Int): CResponseBody<User?> {
        val result = userService.getUserInfoId(id.toLong())
        return when (result) {
            null -> CResponseBody(errcode = ErrCode.DATA_NOT_EXISTS.code, msg = ErrCode.DATA_NOT_EXISTS.name, data = null)
            else -> CResponseBody(data = result)
        }
    }

    @ApiOperation(value = "获取已登录用户详细信息", notes = "获取已登录用户详细信息")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/")
    fun getUserInfo(): CResponseBody<User?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = userService.getUserInfoId(uid)
        return CResponseBody(data = result)
    }
}
