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
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40002, message = "缺少必要的參數")
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

    @ApiOperation(value = "获取热榜论文", notes = "获取热榜论文")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @GetMapping("/hot")
    fun hotPapers(): CResponseBody<MutableList<Paper>> = CResponseBody(data = paperService.getHotPapers())

    @ApiOperation(value = "获取推荐论文", notes = "根据用户id获取推荐论文")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/recommend")
    fun recommend(): CResponseBody<MutableList<Paper>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        return CResponseBody(data = paperService.getRecommendPapers(uid))
    }

    @ApiOperation(value = "添加论文", notes = "添加论文")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40002, message = "缺少必要的参数")
    )
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    @PutMapping("/")
    fun newPaper(@RequestBody rqNewPaper: RqNewPaper): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = paperService.insertPaper(uid = uid, title = rqNewPaper.title, paperRec = rqNewPaper.paperRec, dataRec = rqNewPaper.dataRec, publishTime = rqNewPaper.publishTime, abstract = rqNewPaper.abstract, keywords = rqNewPaper.keywords)
        return CResponseBody(errcode = result.code, msg = result.name, data = null)
    }

    @ApiOperation(value = "删除论文", notes = "删除添加论文")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40004, message = "论文不存在")
    )
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    @DeleteMapping("/{id}")
    fun deletePaper(@PathVariable id: Int): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = paperService.deletePaper(uid, id.toLong())
        return CResponseBody(errcode = result.code, msg = result.name, data = null)
    }

    @ApiOperation(value = "查询论文是否关注", notes = "查询论文是否关注")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}/collected")
    fun queryCollected(@PathVariable id: Int): CResponseBody<Boolean> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = paperService.queryCollected(uid, id.toLong())
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

    @ApiOperation(value = "添加专利", notes = "添加专利")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40002, message = "缺少必要的参数")
    )
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    @PutMapping("/")
    fun newPatent(@RequestBody rqNewPatent: RqNewPatent): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = patentService.insertPatent(uid = uid, title = rqNewPatent.title, applicationNumber = rqNewPatent.applicationNumber, publicationNumber = rqNewPatent.publicationNumber, agency = rqNewPatent.agency, agent = rqNewPatent.agent, summary = rqNewPatent.summary, address = rqNewPatent.address, applicationDate = rqNewPatent.applicationDate, publicationDate = rqNewPatent.publicationDate)
        return CResponseBody(errcode = result.code, msg = result.name, data = null)
    }

    @ApiOperation(value = "删除专利", notes = "删除添专利")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40004, message = "专利不存在")
    )
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    @DeleteMapping("/{id}")
    fun deletePatent(@PathVariable id: Int): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = patentService.deletePatent(uid, id.toLong())
        return CResponseBody(errcode = result.code, msg = result.name, data = null)
    }

    @ApiOperation(value = "查询专利是否关注", notes = "查询专利是否关注")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}/collected")
    fun queryCollected(@PathVariable id: Int): CResponseBody<Boolean> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = patentService.queryCollected(uid, id.toLong())
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
    @GetMapping("/")
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
@RefreshScope
@RequestMapping("/follow")
class FollowController {

    @Autowired
    lateinit var followService: FollowService

    @ApiOperation(value = "获取关注", notes = "获取关注的用户列表")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/")
    fun follows(): CResponseBody<MutableList<User>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        return CResponseBody(data = followService.getFollows(uid))
    }

    @ApiOperation(value = "关注用户", notes = "关注用户")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40005, message = "数据库完整性/一致性约束异常,包括重复关注或关注用户id不存在两种情况")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "被关注用户id", dataType = "Long", required = true, paramType = "PathVariable")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    fun addCollection(@PathVariable id: Int): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val res = followService.insertFollow(uid, id.toLong())
        return CResponseBody<Nothing?>(errcode = res.code, msg = res.name, data = null)
    }

    @ApiOperation(value = "取消关注用户", notes = "取消关注用户")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40004, message = "数据不存在")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "被关注用户id", dataType = "Long", required = true, paramType = "PathVariable")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{id}")
    fun deleteCollection(@PathVariable id: Int): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val res = followService.deleteFollow(uid, id.toLong())
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
        val result = userService.getUserInfoById(id.toLong())
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
        val result = userService.getUserInfoById(uid)
        return CResponseBody(data = result)
    }

    @ApiOperation(value = "更新用户邮箱", notes = "更新已登录用户邮箱")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40002, message = "json格式不正确(此RequestBody中不需要设置NOTHING的值)")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "email", value = "新email值", dataType = "String", required = true)
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/email/update")
    fun updateEmail(@RequestBody rqUpdateEmail: RqUpdateEmail): CResponseBody<String?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = userService.updateEmail(uid, rqUpdateEmail.email)
        return CResponseBody(errcode = result.code, msg = result.name, data = rqUpdateEmail.email)
    }

    @ApiOperation(value = "更新专家信息", notes = "更新专家详细信息")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40002, message = "json格式不正确(此RequestBody中不需要设置NOTHING的值)")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "name", value = "专家姓名", dataType = "String", required = true),
            ApiImplicitParam(name = "subject", value = "专家学科", dataType = "String", required = true),
            ApiImplicitParam(name = "education", value = "专家教育程度", dataType = "String", required = true),
            ApiImplicitParam(name = "introduction", value = "专家介绍", dataType = "String", required = true),
            ApiImplicitParam(name = "field", value = "专家领域", dataType = "String", required = true),
            ApiImplicitParam(name = "organizationName", value = "专家所在机构（如果不存在会创建新机构）", dataType = "String", required = true)
    )
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    @PostMapping("/expert/info/update")
    fun updateExpertInfo(@RequestBody rqUpdateExpertInfo: RqUpdateExpertInfo): CResponseBody<User?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = userService.updateExpertInfo(
                id = uid,
                name = rqUpdateExpertInfo.name,
                subject = rqUpdateExpertInfo.subject,
                education = rqUpdateExpertInfo.education,
                introduction = rqUpdateExpertInfo.introduction,
                field = rqUpdateExpertInfo.field,
                organizationName = rqUpdateExpertInfo.organizationName
        )
        return CResponseBody(errcode = result.code, msg = result.name, data = userService.getUserInfoById(uid))
    }


    @ApiOperation(value = "查询用户是否被关注", notes = "查询用户是否被关注")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}/followed")
    fun queryFollowed(@PathVariable id: Int): CResponseBody<Boolean> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = userService.selectFollowed(uid, id.toLong())
        return CResponseBody(data = result)
    }

    @ApiOperation(value = "查找未被认领的用户及相关信息", notes = "根据用户名查找未被认领的用户及相关信息")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40002, message = "json格式不正确(此RequestBody中不需要设置NOTHING的值)")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "name", value = "查询专家姓名", dataType = "String", required = true)
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/unclaimed")
    fun findUnclaimedUser(name: String): CResponseBody<User?> = CResponseBody(errcode = ErrCode.SUCCESS.code, msg = ErrCode.SUCCESS.name, data = userService.findUnchaimedUserByExpertName(name))

    @ApiOperation(value = "查询关联专家", notes = "根据用户id查询关联专家")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @ApiImplicitParams(
            ApiImplicitParam(name = "id", value = "需查询关联专家的专家id", dataType = "Long", required = true)
    )
    @GetMapping("/related/{id}")
    fun findRelatedUsers(@PathVariable id: Int): CResponseBody<MutableList<User>> = CResponseBody(data = userService.getRelatedUsers(id.toLong()))
}


@RestController
@RefreshScope
@RequestMapping("/application")
class ApplicationController {
    @Autowired
    lateinit var applicationService: ApplicationService

    @ApiOperation(value = "提交工单", notes = "提交工单")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success"),
            ApiResponse(code = 40002, message = "json格式不正确")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/")
    fun addApplication(@RequestBody expertApplication: ExpertApplication): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        applicationService.insertApplication(userId = uid, expertApplication = expertApplication)
        return CResponseBody(data = null)
    }

    @ApiOperation(value = "审核工单-通过", notes = "审核工单-通过")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}/pass")
    fun passApplication(@PathVariable id: Int): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        applicationService.examineApplication(uid, id.toLong(), true)
        return CResponseBody(data = null)
    }

    @ApiOperation(value = "审核工单-拒绝", notes = "审核工单-拒绝")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}/refuse")
    fun refuseApplication(@PathVariable id: Int): CResponseBody<Nothing?> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        applicationService.examineApplication(uid, id.toLong(), false)
        return CResponseBody(data = null)
    }

    @ApiOperation(value = "获取未处理的工单列表", notes = "获取未处理的工单列表")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/unhandled")
    fun unhandledApplications(): CResponseBody<MutableList<Application>> = CResponseBody(data = applicationService.selectUnhandled())

    @ApiOperation(value = "获取该管理员处理的用户列表", notes = "获取该管理员处理的用户列表")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/handled")
    fun handledApplication(): CResponseBody<MutableList<Application>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = applicationService.selectByAdminId(uid)
        return CResponseBody(data = result)
    }

    @ApiOperation(value = "获取该用户提交的工单列表", notes = "获取该用户提交的工单列表")
    @ApiResponses(
            ApiResponse(code = 20000, message = "success")
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/")
    fun applications(): CResponseBody<MutableList<Application>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val details = authentication.details as OAuth2AuthenticationDetails
        val decodedDetails = details.decodedDetails as MutableMap<String, *>
        val uid: Long = (decodedDetails["uid"] as Int).toLong()
        val result = applicationService.selectByUserId(uid)
        return CResponseBody(data = result)
    }

}
