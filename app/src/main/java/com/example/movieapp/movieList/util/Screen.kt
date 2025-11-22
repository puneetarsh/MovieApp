package com.example.movieapp.movieList.util

sealed class Screen(val rout: String) {
    object Home: Screen("main")
    object PopularMovieList:Screen("popularMovie")

    object UpcomingMovieList:Screen("upcomingMovie")
    object Details:Screen("details/{movieId}"){
        fun createRoute(movieId:Int)= "details/$movieId"

    }

}