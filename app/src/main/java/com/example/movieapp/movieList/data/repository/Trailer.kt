package com.example.movieapp.movieList.data.repository

data class Trailer(val key: String,
                   val name: String,
                   val site: String,
                   val type: String
)
data class TrailerResponse(
    val id: Int,
    val results: List<Trailer>
)
