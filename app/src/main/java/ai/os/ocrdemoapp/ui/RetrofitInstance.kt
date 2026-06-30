package ai.os.ocrdemoapp.ui


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val TIME_OUT = 30L

    private val converterFactory: GsonConverterFactory by lazy {
        GsonConverterFactory.create(gson)
    }

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder().apply {
            addInterceptor(requestInterceptor)
            addNetworkInterceptor(loggingInterceptor)
            connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            readTimeout(TIME_OUT, TimeUnit.SECONDS)
            writeTimeout(TIME_OUT, TimeUnit.SECONDS)
        }.build()
    }

    private val requestInterceptor by lazy {
        Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder().apply {
                addHeader("Connection", "close")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    private val gson: Gson = GsonBuilder().apply {
        setStrictness(Strictness.LENIENT)
        setPrettyPrinting()
    }.create()

    fun <T> createService(
        baseUrl: String = "https://vision.googleapis.com/",
        serviceClass: Class<T>,
    ): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(serviceClass)
    }

}

