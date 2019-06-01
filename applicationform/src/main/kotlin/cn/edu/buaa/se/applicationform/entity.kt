package cn.edu.buaa.se.applicationform

import java.util.*

data class Application(
        var id:Long=0,
        var userId:Long=0,
        var adminId:Long?=null,
        var content:String="",
        var applyTime:Date= Date(),
        var examineTime:Date?= null,
        var status:Short=0
)
