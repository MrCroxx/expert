package cn.edu.buaa.se.docs

import org.codehaus.jackson.annotate.JsonProperty
import java.util.*

enum class ErrCode constructor(val code: Int) {
    SUCCESS(20000),
    TYPE_ILLEGAL(40001),
    DATA_NOT_EXISTS(40004),
    DATA_INTEGRITY_VIOLATION(40005),
    LACK_OF_PARAMETERS(40002),
    UNKNOWN(50000);

    companion object {
        fun fromCode(code: Int): ErrCode = ErrCode.values().find { it.code == code } ?: UNKNOWN
    }
}

interface IResponseBody<T> {
    var errcode: Int
    var msg: String
    var date: Date
    var data: T
}

data class CResponseBody<T>(
        override var errcode: Int = ErrCode.SUCCESS.code,
        override var msg: String = ErrCode.SUCCESS.name,
        override var date: Date = Date(),
        override var data: T
) : IResponseBody<T>

enum class SearchSort constructor(val method: String) {
    DATE("date"), CLICK("click");

    companion object {
        fun fromString(method: String): SearchSort = SearchSort.values().find { it.method == method } ?: DATE
    }
}

data class SearchResult(
        var total: Int,
        val results: MutableList<out Doc>
)

data class Collections(
        var papers: MutableList<Paper>,
        var patents: MutableList<Patent>
)

data class RqUpdateEmail(
        val email: String,
        val NOTHING: Nothing?
)

data class RqUpdateExpertInfo(
        val name: String,
        val subject: String,
        val education: String,
        val introduction: String,
        val field: String,
        val organizationName: String
)

data class RqNewPaper(
        var title: String,
        var paperRec: String,
        var dataRec: String,
        var publishTime: Date,
        var abstract: String,
        var keywords: String
)

data class RqNewPatent(
        var title: String,
        var applicationNumber: String,
        var publicationNumber: String,
        var agency: String,
        var agent: String,
        var summary: String,
        var address: String,
        var applicationDate: Date,
        var publicationDate: Date
)