package com.example.movieapp.core.presentation


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movieapp.detail.presentation.DetailScreen
import com.example.movieapp.detail.presentation.VideoPlayer
import com.example.movieapp.detail.presentation.VideoScreen
//import com.example.movieapp.detail.presentation.VideoPlayerScreen
import com.example.movieapp.movieList.presentation.MoviesListViewModel
import com.example.movieapp.movieList.util.Screen
import com.example.movieapp.ui.theme.MovieAppTheme
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT


        setContent {
            MovieAppTheme {
                Surface(modifier
                = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background){
                    //creating an instance
                    val movieListViews=hiltViewModel<MoviesListViewModel>()
                    val navController= rememberNavController()
                    NavHost(navController,startDestination = Screen.Home.rout){
                        composable(Screen.Home.rout){


                            HomeScreen(
                                navController=navController)

                        }
                        composable("video/{url}") { backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url") ?: ""
                            VideoScreen(
                                videoUrl = url,
                                onBack = { navController.popBackStack() }
                            )
                        }


                        composable("details/{movieId}",
                            arguments =listOf(
                                navArgument("movieId"){
                                    type= NavType.IntType
                                }
                            ) ){backStackEntry->
                            val movieId=backStackEntry.arguments?.getInt("movieId")?:-1
                            DetailScreen(navController=navController, movieId =movieId)

                        }
                    }
                }

            }
        }

    }
}

