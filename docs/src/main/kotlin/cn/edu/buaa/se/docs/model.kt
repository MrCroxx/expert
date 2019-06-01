package cn.edu.buaa.se.docs

import java.util.*

enum class ErrorCode constructor(val code: Int) {
    SUCCESS(20000),
    TYPE_ILLEGAL(40001),
    UNKNOWN(50000);

    companion object {
        fun fromCode(code: Int): ErrorCode = ErrorCode.values().find { it.code == code } ?: UNKNOWN
    }
}

interface IResponseBody<T> {
    var errcode: Int
    var msg: String
    var date: Date
    var data: T
}

data class CResponseBody<T>(
        override var errcode: Int = ErrorCode.SUCCESS.code,
        override var msg: String = ErrorCode.SUCCESS.name,
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