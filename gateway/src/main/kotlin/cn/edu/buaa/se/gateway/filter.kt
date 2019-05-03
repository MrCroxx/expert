package cn.edu.buaa.se.gateway

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants
import org.springframework.stereotype.Component

@Component
class BasicFilter : ZuulFilter() {

    @Value("\${basic.client-id}")
    lateinit var clientId: String

    @Value("\${basic.client-secret}")
    lateinit var clientSecret: String

    override fun run(): Any {
        val requestContext = RequestContext.getCurrentContext()
        val request = requestContext.request
        if (request.getHeader("Authorization") == null)
            requestContext.addZuulRequestHeader("Authorization", "Basic ${getBase64Credentials(clientId, clientSecret)}")
        return Any()
    }

    fun getBase64Credentials(id: String, secret: String): String {
        return String(Base64.encodeBase64("$id:$secret".toByteArray()))
    }

    override fun shouldFilter(): Boolean = true

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun filterOrder(): Int = 11
}