package com.example.movieapp.core.presentation


import androidx.compose.foundation.Image


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import com.example.movieapp.core.presentation.drawable
//import com.example.movieapp.movieList.presentation.MovieListState
import com.example.movieapp.movieList.presentation.MovieListUIEvent
import com.example.movieapp.movieList.presentation.MoviesListViewModel
import com.example.movieapp.movieList.presentation.PopularMoviesScreen
import com.example.movieapp.movieList.presentation.UpcomingMoviesScreen
import com.example.movieapp.movieList.util.Screen
//import com.example.movieapp.R



@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun HomeScreen( navController: NavHostController) {
    val movieListViewModel= hiltViewModel<MoviesListViewModel>()
    val movieListState=movieListViewModel.movieListState.collectAsState().value
    val bottomNavController= rememberNavController()

    Scaffold(bottomBar =  {
        BottomNavigationBar(bottomNavController=bottomNavController,
        onEvent=movieListViewModel::onEvent)
    }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            NavHost(
                navController = bottomNavController,
                startDestination = Screen.PopularMovieList.rout
            ) {
                composable(Screen.PopularMovieList.rout) {
                    //PopularMoviesScreen()
                   // val movieListViewModel = hiltViewModel<MoviesListViewModel>()
                    //val state =movieListViewModel.movieListState.collectAsState()
                    PopularMoviesScreen(
                        navController=navController,
                        movieListState = movieListState,
                        onEvent = movieListViewModel::onEvent
                    )
                }
                composable(Screen.UpcomingMovieList.rout) {
                    UpcomingMoviesScreen(
                        navController=navController,
                        movieListState = movieListState,
                        onEvent = movieListViewModel::onEvent
                    )


                }
            }
        }
    }

}
@Composable
fun BottomNavigationBar(
    bottomNavController: NavHostController,
    onEvent: (MovieListUIEvent)->Unit
){
    val items=listOf(
        BottomItem(
            title = "Popular",
            icon = Icons.Rounded.Movie
    ),
        BottomItem(
            title = "Upcoming",
            icon = Icons.Rounded.Movie
        )
    )
    //if we rotate the screeen
    val selected= rememberSaveable{
        mutableIntStateOf(0)

    }
    NavigationBar {
        Row (modifier = Modifier.background(MaterialTheme.colorScheme.primary)){
            items.forEachIndexed{
                index,bottomitem->
                NavigationBarItem(
                    selected = selected.intValue==index,
                    onClick = {
                        selected.intValue=index
                        when(selected.intValue){
                            0->{
                                onEvent(MovieListUIEvent.Navigate)
                                bottomNavController.popBackStack()
                                bottomNavController.navigate(Screen.PopularMovieList.rout)

                            }
                            1->{
                                onEvent(MovieListUIEvent.Navigate)

                                bottomNavController.popBackStack()
                                bottomNavController.navigate(Screen.UpcomingMovieList.rout)

                            }
                        }


                    },
                    icon = {
                        Icon(imageVector = bottomitem.icon,
                            contentDescription = bottomitem.title,
                        tint =MaterialTheme.colorScheme.onBackground)

                    },
                    label = {
                        Text(text = bottomitem.title,
                            color = MaterialTheme.colorScheme.onBackground)
                    }

                )


            }
        }

    }
}
data class BottomItem(
    val title:String,
    val icon: ImageVector

)