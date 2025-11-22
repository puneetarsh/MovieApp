package com.example.movieapp.movieList.data.remote.respond

data class MovieListDto(
    val page: Int,

    val results:List<MovieDto>,
    val totalpages:Int,
    val totalresults:Int


)
