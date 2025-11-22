package com.example.movieapp.detail.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.detail.DetailState
import com.example.movieapp.movieList.domain.repository.MovieListRepository
//import com.example.movieapp.movieList.presentation.MovieListState
import com.example.movieapp.movieList.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val movieListRepository: MovieListRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val movieId = savedStateHandle.get<Int>("movieId")
    //Log.d("DetailVM", "movieId from savedStateHandle: $movieId")
    private var detailsState = MutableStateFlow(DetailState())
    val detailState = detailsState.asStateFlow()

    init {
        //movielistrepositoryImpl me get movie hai
        Log.d("DetailVM", "Initialized with movieId=$movieId")
        val id = movieId ?: -1
        if (id == -1) {
            Log.e("DetailVM", "Invalid movieId: $movieId")
        } else {
            getMovie(id)
            getTrailer(id)
        }
    }

    private fun getMovie(id: Int) {
        viewModelScope.launch {
            Log.d("DetailVM", "getMovie called with id=$id")
            detailsState.update {
                it.copy(isLoading = true)
            }
            movieListRepository.getMovie(id).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        Log.e("DetailVM", "Error loading movie for ID $id")

                        detailsState.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Loading ->{
                        Log.d("DetailVM", "Loading: ${result.isLoading}")
                        detailsState.update {
                            it.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }

                    is Resource.Success -> {
                        Log.d("DetailVM", "Movie fetched with ID $id: ${result.data}")
                        result.data?.let { movie ->
                            detailsState.update {
                                it.copy(
                                    isLoading = false,
                                    movie = movie

                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun getTrailer(id: Int) {
        viewModelScope.launch {
            Log.d("DetailVM", "getTrailer called with id=$id")
            movieListRepository.getMovieTrailer(id).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        val trailer = result.data?.results?.firstOrNull {
                            it.type == "Trailer"
                        }
                        Log.d("DetailVM", "Trailer fetched: $trailer")

                        val youtubeUrl = if (trailer != null) {
                            "https://www.youtube.com/watch?v=${trailer.key}"
                        }
                        else null


                        detailsState.update {
                            it.copy(
                                trailerUrl = youtubeUrl ?:""  // 🔥 NEW
                            )
                        }
                    }
                    is Resource.Error -> {
                        Log.e("DetailVM", "Trailer error: ${result.message}")
                        detailsState.update { it.copy(trailerUrl = "") }
                    }
                    is Resource.Loading -> {
                        Log.d("DetailVM", "Trailer loading: ${result.isLoading}")
                    }

                }
            }
        }
    }
}

