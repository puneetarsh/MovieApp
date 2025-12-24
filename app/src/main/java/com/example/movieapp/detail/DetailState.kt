package com.example.movieapp.detail

import com.example.movieapp.movieList.domain.model.Movie
import com.example.movieapp.movieList.util.Resource

data class DetailState(

    val isLoading: Boolean=false,
    val movie: Movie ?=null,
    val trailerUrl:String="",
    val trailerSite:String?=""

)
