package net.tribls.core.api

import android.os.Environment
import io.reactivex.Observable
import net.tribls.core.REVIEWS_URL
import net.tribls.core.api_models.ReviewsData
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

interface NetworkChecker {
    fun isOnline(): Boolean
}

class Api(checker: NetworkChecker, file: File) {
    // Logging
    private val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)

    // Client
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(RequestInterceptor())
        .addInterceptor(ResponseInterceptor())
        .addInterceptor(OfflineResponseCacheInterceptor(checker))
        .addInterceptor(loggingInterceptor)
        // Set the cache location and size (10 MB)
        .cache(getCache(file))
        .build()

    private val rxReviewsApi: RxReviewsRequest
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(REVIEWS_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        rxReviewsApi = retrofit.create(RxReviewsRequest::class.java)
    }

    /**
     * This returns an Observable to which an Observer can subscribe.
     */
    fun rxGetReviews(type: String, offset: Int, order: String) : Observable<ReviewsData> {
        return rxReviewsApi.rxGetReviewsByType(type, offset, order)
    }

    private fun getCache(file: File): Cache {
        return Cache(file, CACHE_SIZE)
    }

    companion object {
        const val CACHE_SIZE = (10 * 1024 * 1024).toLong() // 10 MB
    }
}