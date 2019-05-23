package cn.edu.buaa.se.paper_patent

import java.util.*

val HOST = "www.wjqproject.cn"

enum class CDN_TYPE constructor(var value: String) {
    PAPER("paper"), PATENT("patent")
}

data class User(
        var id: Long = 0,
        var username: String = "",
        var password: String = "",
        var email: String = "",
        var credit: Int = 0,
        var frozen_credit: Int = 0,
        var role: Int = 0
) {
    fun toRUser(): RpUser = RpUser(
            id = id,
            username = username,
            email = email,
            credit = credit,
            frozen_credit = frozen_credit
    )
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
        var name: String = ""
) {
    fun getUrl(): String = "http://${HOST}/${CDN_TYPE.PAPER.value}/${id}"
}

data class Patent(
        var id: Long = -1,
        var title: String = "",
        var application_date: String = "",
        var publication_date: String = "",
        var inventor_id: Long = 0,
        var applicant_id: Long = 0
) {
    fun getUrl(): String = "http://${HOST}/${CDN_TYPE.PATENT.value}/${id}"
}

data class Paper_collection(
        var user_id: Long = -1,
        var paper_id: Long = -1,
        var time: String = ""
)

data class Patent_collection(
        var user_id: Long = -1,
        var patent_id: Long = -1,
        var time: String = ""
)