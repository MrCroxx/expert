package cn.edu.buaa.se.docs

import io.swagger.annotations.ApiModel
import java.util.*

enum class DocType constructor(val type: String) {
    PAPER("paper"),
    PATENT("patent");

    companion object {
        fun fromString(tyoe: String): DocType = DocType.values().find { it.type == tyoe } ?: PAPER
    }
}

interface Doc {
    val type: String
}


@ApiModel
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
) : Doc {
    override val type: String
        get() = DocType.PAPER.name
}

@ApiModel
data class Patent(
        var id: Long? = -1,
        var title: String = "",
        var applicationNumber: String = "",
        var publicationNumber: String = "",
        var agency: String = "",
        var agent: String = "",
        var summary: String = "",
        var address: String = "",
        var clickTimes: Int = 0,
        var applicationDate: Date = Date(),
        var publicationDate: Date = Date(),
        var applicants: MutableList<User> = mutableListOf(),
        var inventors: MutableList<User> = mutableListOf()
) : Doc {
    override val type: String
        get() = DocType.PATENT.name
}

enum class ROLE constructor(var value: Int) {
    ROLE_KNOWN(0),
    ROLE_USER(1),
    ROLE_EXPERT(2),
    ROLE_ADMIN(3);

    companion object {
        fun fromInt(roleId: Int): ROLE = ROLE.values().find { it.value == roleId } ?: ROLE.ROLE_KNOWN
    }
}

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
        var patents_applicant: MutableList<Patent> = mutableListOf(),
        var patents_inventor: MutableList<Patent> = mutableListOf()
)


data class Organization(
        var id: Long = -1,
        var name: String = "",
        var contact: String = "",
        var rank: Int = 0,
        var experts: MutableList<User> = mutableListOf()
)