package com.example.movieapp.movieList.data.remote


import com.example.movieapp.movieList.data.remote.respond.MovieListDto
import com.example.movieapp.movieList.domain.VideoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("movie/{category}")
    suspend fun getMoviesList(
        @Path("category") category: String,
        @Query("page") page :Int,
        @Query("api_key") apiKey: String = API_KEY
    ): MovieListDto
    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): VideoResponse



    companion object{
        //val response = movieApi.getMoviesList(category, page)
        const val BASE_URL="https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL="https://image.tmdb.org/t/p/w500"
        const val API_KEY="86ffb2e9d5037e90190de05e27902b78"
    }
}