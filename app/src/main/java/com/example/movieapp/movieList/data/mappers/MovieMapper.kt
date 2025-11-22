package com.example.movieapp.movieList.data.mappers

import android.util.Log
import com.example.movieapp.movieList.data.local.movie.MovieEntity
import com.example.movieapp.movieList.data.remote.MovieApi
import com.example.movieapp.movieList.data.remote.respond.MovieDto
import com.example.movieapp.movieList.domain.model.Movie
import kotlin.text.split

fun MovieDto.toMovieEntity(category: String): MovieEntity {
   // throw Exception("toMovieEntitiy not expected to run")
    Log.d("MovieMapper", "PosterPath raw: $posterPath")

    return MovieEntity(
        adult = adult ?: false,
        backdropPath = backdropPath ?: "",
        originalLanguage = originalLanguage ?: "",
        originalTitle = originalTitle ?: "",
        overview = overview ?: "",
        popularity = popularity ?: 0.0,
        posterPath = if (!posterPath.isNullOrEmpty()) {
            MovieApi.IMAGE_BASE_URL + posterPath   // 👈 FULL URL
        } else "",
        releaseDate = releaseDate ?: "",
        title = title ?: "",
        video = video ?: false,
        voteAverage = voteAverage ?: 0.0,
        category = category,
        genreIds = try {
            genreIds?.joinToString(",") ?: "-1,-2"
        } catch (e: Exception) {
            "-1,-2"
        }
    )
}


fun MovieEntity.toMovie(
category:String
): Movie {
    Log.d("MyNewTag", "MovieEntity posterPath: $posterPath")
    return Movie(
        adult = adult ?: false,
        backdropPath = backdropPath ?: "",

        originalLanguage =originalLanguage ,
        originalTitle =originalTitle ,
        overview =overview,
        popularity =popularity ,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        video =video ,
        voteAverage = voteAverage,
        voteCount =voteCount ,
        id =id,
        category =category ,
        genreIds = try {
            genreIds?.split(",")?.map{
                it.toInt()
            }?:listOf(-1,-2)
        }catch (e:Exception){
           listOf(-1,-2)
            }
    )
        }


