package cryptocurrency.portfolio.tracker.api

import com.google.gson.JsonObject
import cryptocurrency.portfolio.tracker.model.MarketResponse
import cryptocurrency.portfolio.tracker.model.PriceResponse
import cryptocurrency.portfolio.tracker.model.Something
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://api.coingecko.com/api/v3/"


private val logging = HttpLoggingInterceptor().also {
    it.level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder().addInterceptor(logging).build()

private val retrofit = Retrofit.Builder()
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface MarketApiService {

    @GET("coins/markets")
    suspend fun getAllAssets(@Query("vs_currency") currency: String,
    @Query("page") pageNum: Int,
    @Query("per_page") perPage: Int): Response<MarketResponse>

    @GET("simple/price")
    suspend fun getPrice(@Query("ids") ids: String,
    @Query("vs_currencies") currency: String): Response<PriceResponse>

}

object MarketApi {

    val retrofitService: MarketApiService by lazy {
        retrofit.create(MarketApiService::class.java)
    }

}