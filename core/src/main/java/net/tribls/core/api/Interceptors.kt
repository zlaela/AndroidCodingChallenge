package net.tribls.core.api

import android.content.res.Resources
import net.tribls.core.KEY
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class RequestInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Add the key to the end of the URL
        val url = chain.request().url()
            .newBuilder()
            .addQueryParameter("api-key", KEY)
            .build()
        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val unAuthorized = (response.code() == 401)
        if (unAuthorized) {
            throw UnauthorizedRequestException()
        }
        val notFound = (response.code() == 404)
        if (notFound) {
            throw Resources.NotFoundException()
        }
        val tooManyTries = response.code() == 429
        if(tooManyTries){
            throw TooManyRequestsException()
        }
        val serverIssue = (response.code() in 500..599)
        if(serverIssue){
            throw ServerResponseException()
        }
        return response
    }
}

class OfflineResponseCacheInterceptor(private val checker: NetworkChecker) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalResponse = chain.proceed(chain.request())
        if (checker.isOnline()) {
            val maxAge = 600 // server requirement
            return originalResponse.newBuilder()
                .header("Cache-Control", "max-age=$maxAge")
                .build()
        }

        val maxStale = TimeUnit.DAYS.toMinutes(28).toInt() // 4-weeks old
        return originalResponse.newBuilder()
            .header(
                "Cache-Control",
                "public, only-if-cached, max-stale=$maxStale"
            )
            .build()
    }
}

/** Exceptions **/
class UnauthorizedRequestException : IOException(){
    override fun getLocalizedMessage(): String = "Unauthorized Request."
}
class TooManyRequestsException : IOException(){
    override fun getLocalizedMessage(): String = "Too many requests. Try again later."
}
class ServerResponseException : IOException(){
    override fun getLocalizedMessage(): String = "Server Unavailable"
}
class NoCacheException : IOException(){
    override fun getLocalizedMessage(): String = "Cache Unavailable"
}
class NoNetworkException : IOException() {
    override fun getLocalizedMessage(): String = "Network Unavailable. Check your Wi-Fi or data connection."
}