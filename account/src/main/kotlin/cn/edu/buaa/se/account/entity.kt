package cn.edu.buaa.se.account

import java.util.*

data class User(
        var id: Long = 0,
        var username: String = "",
        var password: String = "",
        var email: String = "",
        var credit: Int = 0,
        var frozenCredit: Int = 0,
        var role: Short = 0

) {
    fun toRUser(): RpUser = RpUser(
            id = id,
            username = username,
            email = email,
            credit = credit,
            frozenCredit = frozenCredit
    )
}

data class Expert(
        var id: Long = -1,
        var name: String = "",
        var subject: String = "",
        var education: String = "",
        var introduction: String = "",
        var famousValue: Double = 0.0,
        var organization: Organization? = null
)

data class Organization(
        var id: Long = -1,
        var name: String = "",
        var contact: String = "",
        var rank: Int = 0
)

data class Follow(
        var followerId:Long=0,
        var followedId:Long=0,
        var time:Date=Date()
)