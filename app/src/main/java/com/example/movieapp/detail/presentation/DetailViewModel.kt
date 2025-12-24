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
                        /*val trailer = result.data?.results?.firstOrNull {
                            it.type == "Trailer"
                        }*/
                        //Log.d("DetailVM", "Trailer fetched: $trailer")
                        val trailers = result.data?.results?.filter {
                            Log.d("TrailerCheck", "Name=${it.name}, Official=${it.official}, Published=${it.publishedAt}, Key=${it.key}")
                            //Log.d("TrailerDebug", "Trailer: name=${it.name}, type=${it.type}, site=${it.site}, official=${it.official}, publishedAt=${it.publishedAt}, key=${it.key}")
                            it.type.equals("Trailer", ignoreCase = true) &&
                                    it.site.equals("YouTube", ignoreCase = true)
                        } ?: emptyList()
                      /*  val selectedTrailer = trailers.firstOrNull {
                            it.official== true} ||
                                    it.publishedAt.contains("2025", true)
                        }*/
                       // val mostRecentTrailer = trailers.maxByOrNull { it.publishedAt ?: "" }

                         //val trailerSite=trailer?.site//youtube
                      //  val officialTrailer = trailers.firstOrNull { it.official == true }
                      //  val sortedTrailers = trailers.sortedByDescending { it.publishedAt }
                        //val mostRecentTrailer = sortedTrailers.firstOrNull()
                      //  val selectedTrailer = expectedTrailer ?: officialTrailer ?: mostRecentTrailer ?: trailers.firstOrNull()
                        /*val youtubeUrl = when(trailerSite?.lowercase()) {
                            "youtube" -> "https://www.youtube.com/watch?v=${trailer.key}"

                            else -> trailer?.key
                        }*/

                        val selectedTrailer = trailers.firstOrNull {
                            it.official == true
                        } ?: trailers.firstOrNull()



                        val youtubeUrl = selectedTrailer?.key?.let { "https://www.youtube.com/watch?v=$it" }


                        detailsState.update {
                            it.copy(
                                trailerUrl = youtubeUrl ?:"" , // 🔥 NEW
                                trailerSite=selectedTrailer?.site
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

