package util.phonograph.lastfm.rest

import android.content.Context
import lib.phonograph.serialization.KtSerializationRetrofitConverterFactory
import okhttp3.internal.userAgent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import player.phonograph.BuildConfig
import retrofit2.Retrofit
import util.phonograph.lastfm.rest.service.LastFMService
import java.io.File
import java.util.*

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
class LastFMRestClient {

    val apiService: LastFMService

    constructor(f: okhttp3.Call.Factory) {
        val restAdapter = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .callFactory(f)
            .addConverterFactory(KtSerializationRetrofitConverterFactory("application/json".toMediaType()))
            .build()
        apiService = restAdapter.create(LastFMService::class.java)
    }
    constructor(context: Context) : this(createDefaultOkHttpClientBuilder(context).build())

    companion object {

        private const val BASE_URL = "https://ws.audioscrobbler.com/2.0/"
        private const val USER_AGENT = "$userAgent PhonographPlus/${BuildConfig.VERSION_NAME}"

        fun createDefaultCache(context: Context): Cache? {
            val cacheDir = File(context.cacheDir.absolutePath, "/okhttp-lastfm/")
            return if (cacheDir.mkdirs() || cacheDir.isDirectory) {
                Cache(cacheDir, 1024 * 1024 * 10)
            } else null
        }

        fun createCacheControlInterceptor(): Interceptor {
            return Interceptor { chain: Interceptor.Chain ->
                val modifiedRequest = chain.request().newBuilder()
                    .addHeader("Cache-Control", String.format(Locale.getDefault(), "max-age=%d, max-stale=%d", 31536000, 31536000))
                    .build()
                chain.proceed(modifiedRequest)
            }
        }

        fun createUserAgentInterceptor(): Interceptor {
            return Interceptor { chain: Interceptor.Chain ->
                val modifiedRequest = chain.request()
                    .newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", USER_AGENT)
                    .build()
                chain.proceed(modifiedRequest)
            }
        }

        fun createDefaultOkHttpClientBuilder(context: Context): OkHttpClient.Builder {
            return OkHttpClient.Builder()
                .cache(createDefaultCache(context))
                .addInterceptor(createCacheControlInterceptor())
                .addInterceptor(createUserAgentInterceptor())
        }
    }
}
