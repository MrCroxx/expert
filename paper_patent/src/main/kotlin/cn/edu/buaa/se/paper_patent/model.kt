package cn.edu.buaa.se.paper_patent

import java.util.*

const val SUCCESS = 0
const val USER_EXISTS = 11
const val UNKNOWN_USER = 12
const val WRONG_PASSWORD = 13
const val SAME_PASSWORD = 14
const val UNKNOWN_PAPER = 15
const val UNKNOWN_PATENT = 16
const val SAME_PAPERCOLLECTION = 17
const val SAME_PATENTCOLLECTION = 18
const val UNKNOWN_PAPERCOLLECTION = 19
const val UNKNOWN_PATENTCOLLECTION = 20
const val UNKNOWN_EXPERT = 21

interface IResponseBody<T> {
    var errcode: Int
    var msg: String
    var date: Date
    var data: T
}

data class ResponseBody<T>(
        override var errcode: Int = 0,
        override var msg: String = "",
        override var date: Date = Date(System.currentTimeMillis()),
        override var data: T
) : IResponseBody<T>

data class RpUser(
        var id: Long = 0,
        var username: String = "",
        var email: String = "",
        var credit: Int = 0,
        var frozen_credit: Int = 0
)

data class RpPaper(
        var id:Long = -1,
        var title:String = "",
        var author:Long = -1,
        var cite_times:Int = 0,
        var click_times:Int = 0,
        var publish_time: String = "",
        var abstract: String = "",
        var name: String = ""
)
data class RqNewPaper(
        var title:String = "",
        var abstract: String = ""
)
data class RqUpdatePaper(
        var title:String = "",
        var id:Long = -1,
        var abstract: String = ""
)
data class RqDeletePaper(
        var id:Long = -1
)

data class RpPatent(
        var id:Long = -1,
        var title:String = "",
        var application_date:String = "",
        var publication_date:String = "",
        var inventor_id:Long = 0,
        var applicant_id:Long = 0
)
data class RqNewPatent(
        var title:String = "",
        var application_date:String = "",
        var inventor_id:Long = 0
)
data class RqUpdatePatent(
        var title:String = "",
        var id:Long = -1
)
data class RqDeletePatent(
        var id:Long = -1
)

data class RqPaperCollection(
        var paper_id:Long = -1
)
data class RqPatentCollection(
        var patent_id:Long = -1
)