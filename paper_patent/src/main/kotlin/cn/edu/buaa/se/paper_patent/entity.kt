package cn.edu.buaa.se.paper_patent

import java.util.*

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
        var keywords: String = "",
        var label: String = ""
)

data class Patent(
        var id: Long? = -1,
        var title: String = "",
        var application_number: String = "",
        var publication_number: String = "",
        var agency: String = "",
        var agent: String = "",
        var summary: String = "",
        var address: String = "",
        var application_date: Date = Date(),
        var publication_date: Date = Date(),
        var applicant: MutableList<Expert> = mutableListOf(),
        var inventor: MutableList<Expert> = mutableListOf()
)

data class PaperCollection(
        var user_id: Long = -1,
        var paper_id: Long = -1,
        var time: Date = Date()
)

data class PatentCollection(
        var user_id: Long = -1,
        var patent_id: Long = -1,
        var time: Date = Date()
)


data class User(
        var id: Long = 0,
        var username: String = "",
        var email: String = "",
        var credit: Int = 0,
        var frozenCredit: Int = 0,
        var role: Short = 0

)

data class Expert(
        var id: Long = -1,
        var name: String = "",
        var subject: String = "",
        var education: String = "",
        var introduction: String = "",
        var famousValue: Double = 0.0,
        var organization: Organization? = null,
        var papers: MutableList<Paper> = mutableListOf(),
        var patent: MutableList<Patent> = mutableListOf()
)


data class Organization(
        var id: Long = -1,
        var name: String = "",
        var contact: String = "",
        var rank: Int = 0
)