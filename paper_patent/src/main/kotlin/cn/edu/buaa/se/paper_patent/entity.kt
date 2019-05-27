package cn.edu.buaa.se.paper_patent

import java.util.*

val HOST = "www.wjqproject.cn"

enum class CDN_TYPE constructor(var value: String) {
    PAPER("paper"), PATENT("patent")
}


data class Paper(
        var id: Long? = -1,
        var title: String = "",
        var author: Long = -1,
        var paper_rec: String = "",
        var data_rec: String = "",
        var cite_times: Int = 0,
        var click_times: Int = 0,
        var publish_time: Date = Date(),
        var abstract: String = "",
        var name: String = "",
        var count: Long = -1
) {
    fun getUrl(): String = "http://${HOST}/${CDN_TYPE.PAPER.value}/${id}"
}

data class Patent(
        var id: Long? = -1,
        var title: String = "",
        var application_date: String? = "",
        var publication_date: Date = Date(),
        var inventor_id: Long = 0,
        var applicant_id: Long = 0
) {
    fun getUrl(): String = "http://${HOST}/${CDN_TYPE.PATENT.value}/${id}"
}

data class Paper_collection(
        var user_id: Long = -1,
        var paper_id: Long = -1,
        var time: Date = Date()
)

data class Patent_collection(
        var user_id: Long = -1,
        var patent_id: Long = -1,
        var time: Date = Date()
)