package com.example.movieapp.movieList.presentation

import com.example.movieapp.movieList.domain.model.Movie

data class MovieListState (
     val isPopularLoading: Boolean=false,
     val isUpcomingLoading: Boolean=false,
     val error: String="",
      val popularMovieListPage:Int=1,
      val upcomingMovieListPage:Int=1,
    val isCurrentPopularScreen: Boolean=true,
     val popularMovieList:List<Movie> =emptyList(),
    val upComingMovieList:List<Movie> =emptyList()
)
