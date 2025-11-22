package com.example.movieapp.di

import android.app.Application
import androidx.room.Room
import com.example.movieapp.movieList.data.local.movie.MovieDatabase
import com.example.movieapp.movieList.data.remote.MovieApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val interceptor: HttpLoggingInterceptor= HttpLoggingInterceptor().apply {
        level= HttpLoggingInterceptor.Level.BODY
    }
    private val client: OkHttpClient= OkHttpClient.Builder()
        .addInterceptor(interceptor).build()
    @Singleton
    @Provides
    fun providesMoviesApi(): MovieApi {
        return Retrofit.Builder().addConverterFactory(
            GsonConverterFactory.create())
            .baseUrl(MovieApi.BASE_URL)
            .client( OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY // ✅ See full response in logcat
                })
                .build())
            .build()
            .create(MovieApi::class.java)

    }
    //now we provide a movie api
    @Singleton
    @Provides
    fun providesMovieDatabase(app: Application): MovieDatabase{
        return Room.databaseBuilder(
            app,
            MovieDatabase::class.java,
            "moviedb.db").fallbackToDestructiveMigration().build()
    }


}