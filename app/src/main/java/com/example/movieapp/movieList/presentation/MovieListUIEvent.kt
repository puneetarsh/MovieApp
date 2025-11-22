package com.example.movieapp.movieList.presentation

import com.example.movieapp.movieList.util.Category


sealed interface MovieListUIEvent {
    data class Paginate(val category: Category): MovieListUIEvent
    object Navigate : MovieListUIEvent
}