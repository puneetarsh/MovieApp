package com.example.movieapp.movieList.presentation


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.movieList.domain.repository.MovieListRepository
import com.example.movieapp.movieList.util.Category
import com.example.movieapp.movieList.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val movieListRepository: MovieListRepository
): ViewModel() {
    private var _movieListState= MutableStateFlow(MovieListState())
    val movieListState= _movieListState.asStateFlow()
    init { Log.d("TEST", "MoviesListViewModel initialized")
       getPopularMovieList(false, Category.Popular)
        getUpcomingMovieList(false, Category.Upcoming)
    }
    fun onEvent(event: MovieListUIEvent){
        Log.d("MoviesListVM", "onEvent: $event")
        when(event){
            MovieListUIEvent.Navigate -> {
                _movieListState.update {
                    it.copy(
                        isCurrentPopularScreen = !it.isCurrentPopularScreen
                    )
                }

            }
            is MovieListUIEvent.Paginate -> {
                if(event.category == Category.Popular){
                    getPopularMovieList(true, Category.Popular)
                }else if(event.category == Category.Upcoming){
                    getUpcomingMovieList(true, Category.Upcoming)

                }


            }
        }

    }
    private fun getPopularMovieList(forceFetchFromRemote: Boolean, category: Category) {
        Log.d("MoviesListVM", "getPopularMovieList called — forceFetchFromRemote: $forceFetchFromRemote, page: ${_movieListState.value.popularMovieListPage}")
        viewModelScope.launch {
           // _movieListState.update {
              //  it.copy(isLoading = true)
           // }
            movieListRepository.getMovieList(
                forceFetchFromRemote,
                category.value,
                _movieListState.value.popularMovieListPage

            ).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        Log.e("MoviesListVM", "Error: ${result.message ?: "Unknown error"}")
                        _movieListState.update {
                            it.copy(isPopularLoading =  false)
                        }
                    }
                    is Resource.Success -> {
                        Log.d("MoviesListVM", "Success: ${result.data?.size ?: 0} movies")
                        result.data?.let { popularlist ->
                            popularlist.forEach { movie ->
                                Log.d("MovieListVM", "Popular Movie: ${movie.title}, posterPath: ${movie.posterPath}")
                            }
                            _movieListState.update { currentState ->
                                Log.d("MoviesListVM", "Updating state: adding ${popularlist.size} movies. Current size: ${currentState.popularMovieList.size}")
                                currentState.copy(
                                    popularMovieList = currentState.popularMovieList
                                            + popularlist.shuffled(),
                                    popularMovieListPage = currentState.popularMovieListPage + 1,
                                    isPopularLoading =  false
                                )



                            }
                        }
                    }
                        is Resource.Loading -> {
                            Log.d("MoviesListVM", "Loading: ${result.isLoading}")
                            _movieListState.update {
                                it.copy(
                                    isPopularLoading =  result.isLoading,

                                    )
                            }
                        }
                    }
                }
            }
        }

        private fun getUpcomingMovieList(forceFetchFromRemote: Boolean,category: Category) {
            Log.d("MoviesListVM", "getUpcomingMovieList called — forceFetchFromRemote: $forceFetchFromRemote, page: ${_movieListState.value.upcomingMovieListPage}")
            viewModelScope.launch {
                /*_movieListState.update {
                    it.copy(isLoading = true)
                }*/
                movieListRepository.getMovieList(
                    forceFetchFromRemote,
                  category.value,
                    _movieListState.value.upcomingMovieListPage

                ).collectLatest { result ->
                    when (result) {
                        is Resource.Error -> {
                            _movieListState.update {
                                it.copy(
                                   isUpcomingLoading = false

                                )
                            }
                        }

                        is Resource.Success -> {
                            result.data?.let { upcominglist ->
                                upcominglist.forEach { movie ->
                                    Log.d("MoviesListVM", "Upcoming Movie: ${movie.title}, posterPath: ${movie.posterPath}")
                                }
                                _movieListState.update {currentState->
                                    currentState.copy(
                                        upComingMovieList =currentState.upComingMovieList
                                                + upcominglist.shuffled(),
                                        upcomingMovieListPage = currentState.upcomingMovieListPage + 1,
                                        isUpcomingLoading = false
                                    )

                                }
                            }
                        }
                        is Resource.Loading -> {
                            _movieListState.update {
                                it.copy(
                                    isUpcomingLoading =  result.isLoading
                                    )
                            }
                        }
                    }
                }
            }
        }
    }




