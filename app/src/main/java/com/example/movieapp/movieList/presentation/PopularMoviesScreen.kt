package com.example.movieapp.movieList.presentation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.movieapp.movieList.util.Resource

import androidx.compose.ui.unit.dp

import androidx.navigation.NavHostController
import com.example.movieapp.movieList.presentation.components.MovieItem
import com.example.movieapp.movieList.util.Category

@Composable
fun PopularMoviesScreen(
    movieListState: MovieListState,
    navController: NavHostController,
    onEvent:(MovieListUIEvent)-> Unit

) {
    if(movieListState.popularMovieList.isEmpty()) {
        when {
            movieListState.isPopularLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            movieListState.popularMovieList.isEmpty() && movieListState.error.isNotEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error")
                }
            }

            movieListState.popularMovieList.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No movies found")
                }
            }
        }
    }

    else{
        //now we have the movies
        LazyVerticalGrid(columns = GridCells.Fixed(2), // two items in each row ,
                        modifier = Modifier.fillMaxSize(),
                         contentPadding = PaddingValues(vertical=8.dp, horizontal = 4.dp)
        ) {
           itemsIndexed(movieListState.popularMovieList){index,movie->
             //  Log.d("MovieImage", "Poster URL: ${movie.posterPath}")
               MovieItem(movie = movieListState.popularMovieList[index],
                   navHostController =navController )
               Spacer(modifier = Modifier.height(16.dp))
               if(index>=movieListState.popularMovieList.size-1 && !movieListState.isPopularLoading){
                   onEvent(MovieListUIEvent.Paginate(Category.Popular))

               }



           }
        }


    }
}