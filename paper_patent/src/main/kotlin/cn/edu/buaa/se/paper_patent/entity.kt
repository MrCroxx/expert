package cn.edu.buaa.se.paper_patent

data class User(
        var id:Long = 0,
        var username:String = "",
        var password:String="",
        var email:String="",
        var credit:Int=0,
        var frozen_credit:Int=0,
        var role:Int=0
){
    fun toRUser(): RpUser = RpUser(
            id = id,
            username = username,
            email = email,
            credit = credit,
            frozen_credit = frozen_credit
    )
}

data class Paper(
        var id:Long = -1,
        var title:String = "",
        var author:Long = -1,
        var paper_rec:String = "",
        var data_rec:String = "",
        var cite_times:Int = 0,
        var click_times:Int = 0,
        var publish_time: String = "",
        var abstract: String = "",
        var name:String = ""

){
    fun toRPaper(): RpPaper = RpPaper(
            id = id,
            title = title,
            author = author,
            cite_times = cite_times,
            click_times = click_times,
            publish_time = publish_time,
            abstract = abstract,
            name = name
    )
}

data class Patent(
        var id:Long = -1,
        var title:String = "",
        var application_date:String = "",
        var publication_date:String = "",
        var inventor_id:Long = 0,
        var applicant_id:Long = 0

){
    fun toRPatent(): RpPatent = RpPatent(
            id = id,
            title = title,
            application_date = application_date,
            publication_date = publication_date,
            inventor_id = inventor_id,
            applicant_id = applicant_id
    )
}

data class Paper_collection(
        var user_id:Long = -1,
        var paper_id:Long = -1,
        var time: String = ""
)

data class Patent_collection(
        var user_id:Long = -1,
        var patent_id:Long = -1,
        var time: String = ""
)