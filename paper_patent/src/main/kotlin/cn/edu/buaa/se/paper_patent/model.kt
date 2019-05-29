package cn.edu.buaa.se.paper_patent

import java.util.*

const val pageSize = 10

const val SUCCESS = 20000
const val UNKNOWN_PAPER = 40001
const val UNKNOWN_PATENT = 40002
const val SAME_PAPERCOLLECTION = 40003
const val SAME_PATENTCOLLECTION = 40004
const val UNKNOWN_PAPERCOLLECTION = 40005
const val UNKNOWN_PATENTCOLLECTION = 40006
const val UNKNOWN_EXPERT = 40007
const val SORTTYPE_ERROR = 40008

val Status = mapOf<Int,String>(
        SUCCESS to "success" ,
        UNKNOWN_PAPER to "can't find the paper" ,
        UNKNOWN_PATENT to "can't find the patent" ,
        SAME_PAPERCOLLECTION to "the paper is already collected" ,
        SAME_PATENTCOLLECTION to "the patent is already collected" ,
        UNKNOWN_PAPERCOLLECTION to "can't find the paper collection" ,
        UNKNOWN_PATENTCOLLECTION to "can't find the patent collection"
        )



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