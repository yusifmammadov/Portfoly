package cryptocurrency.portfolio.tracker.di

import android.app.Application
import androidx.room.Room
import cryptocurrency.portfolio.tracker.api.MarketApiService
import cryptocurrency.portfolio.tracker.db.PortfolioDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


private val logging = HttpLoggingInterceptor().also {
    it.level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder().addInterceptor(logging).build()

@Module
@InstallIn(SingletonComponent::class)
class AppModule {



    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(MarketApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideMarketApi(retrofit: Retrofit): MarketApiService =
        retrofit.create(MarketApiService::class.java)


    @Provides
    @Singleton
    fun provideDatabase(app: Application): PortfolioDatabase =
        Room.databaseBuilder(app, PortfolioDatabase::class.java, "portfolio_db")
            .createFromAsset("database/portfoliodb.db")
            .build()
}








