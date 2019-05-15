package cn.edu.buaa.se.account

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import java.lang.reflect.Type


data class User(
        var id: Long=0,
        var username: String="",
        var password: String="",
        var email: String="",
        var credit: Int=0,
        var frozenCredit: Int=0,
        var role: Short=1

)

data class Expert(
       var id: Long=-1,
       var name: String="",
       var subject: String="",
       var education: String="",
       var introduction: String="",
       var famousValue: Double=0.0,
       var organizationId: Long=0
)