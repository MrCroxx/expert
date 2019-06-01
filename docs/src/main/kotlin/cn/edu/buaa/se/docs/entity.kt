package cn.edu.buaa.se.docs

import java.util.*

data class Paper(
        var id: Long? = -1,
        var title: String = "",
        var paperRec: String = "",
        var dataRec: String = "",
        var citeTimes: Int = 0,
        var clickTimes: Int = 0,
        var publishTime: Date = Date(),
        var abstract: String = "",
        var keywords: String = "",
        var label: String = "",
        var authors: MutableList<User> = mutableListOf()
)

data class Patent(
        var id: Long? = -1,
        var title: String = "",
        var applicationNumber: String = "",
        var publicationNumber: String = "",
        var agency: String = "",
        var agent: String = "",
        var summary: String = "",
        var address: String = "",
        var applicationDate: Date = Date(),
        var publicationDate: Date = Date(),
        var applicants: MutableList<User> = mutableListOf(),
        var inventors: MutableList<User> = mutableListOf()
)

data class User(
        var id: Long = -1,
        var username: String = "",
        var email: String = "",
        var credit: Int = 0,
        var frozenCredit: Int = 0,
        var role: Short = 0,
        var expert: Expert? = null
)

data class Expert(
        var name: String = "",
        var subject: String = "",
        var education: String = "",
        var introduction: String = "",
        var famousValue: Double = 0.0,
        var organization: Organization? = null,
        var papers: MutableList<Paper> = mutableListOf(),
        var patents: MutableList<Paper> = mutableListOf()
)


data class Organization(
        var id: Long = -1,
        var name: String = "",
        var contact: String = "",
        var rank: Int = 0,
        var experts: MutableList<User> = mutableListOf()
)