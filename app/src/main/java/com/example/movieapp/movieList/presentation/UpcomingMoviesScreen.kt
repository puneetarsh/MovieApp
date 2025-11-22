package com.example.movieapp.movieList.presentation

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp

import androidx.navigation.NavHostController
import com.example.movieapp.movieList.presentation.components.MovieItem
import com.example.movieapp.movieList.util.Category

@Composable
fun UpcomingMoviesScreen(
    movieListState: MovieListState,
    navController: NavHostController,
    onEvent:(MovieListUIEvent)-> Unit

) {
    if(movieListState.isUpcomingLoading && movieListState.upComingMovieList.isEmpty()){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ){
            CircularProgressIndicator()
        }
    }
    else{
        //now we have the movies
        LazyVerticalGrid(columns = GridCells.Fixed(2), // two items in each row ,
                        modifier = Modifier.fillMaxSize(),
                         contentPadding = PaddingValues(vertical=8.dp, horizontal = 4.dp)
        ) {
           itemsIndexed(movieListState.upComingMovieList){index,movie->
               MovieItem(movie = movieListState.upComingMovieList[index],
                   navHostController =navController )
               Spacer(modifier = Modifier.height(16.dp))
               if(index>=movieListState.upComingMovieList.size-1 && !movieListState.isUpcomingLoading){
                   onEvent(MovieListUIEvent.Paginate(Category.Upcoming))

               }



           }
        }


    }
}