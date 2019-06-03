package cn.edu.buaa.se.docs

import java.util.*

enum class ErrCode constructor(val code: Int) {
    SUCCESS(20000),
    TYPE_ILLEGAL(40001),
    DATA_NOT_EXISTS(40004),
    DATA_INTEGRITY_VIOLATION(40005),
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