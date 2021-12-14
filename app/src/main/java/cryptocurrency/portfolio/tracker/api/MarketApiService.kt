package cryptocurrency.portfolio.tracker.api


import cryptocurrency.portfolio.tracker.model.MarketResponse
import cryptocurrency.portfolio.tracker.model.PriceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface MarketApiService {

    @GET("coins/markets")
    suspend fun getAllAssets(@Query("vs_currency") currency: String = "usd",
    @Query("page") pageNum: Int,
    @Query("per_page") perPage: Int = 250): Response<MarketResponse>

    @GET("simple/price")
    suspend fun getPrice(@Query("ids") ids: String,
    @Query("vs_currencies") currency: String = "usd"): PriceResponse

    companion object {
        const val BASE_URL = "https://api.coingecko.com/api/v3/"
    }

}