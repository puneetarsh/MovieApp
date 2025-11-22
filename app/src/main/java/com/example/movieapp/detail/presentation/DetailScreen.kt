package com.example.movieapp.detail.presentation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import androidx.compose.ui.viewinterop.AndroidView

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size

import com.example.movieapp.movieList.data.remote.MovieApi
@Composable
fun StarRatingBar(
    rating: Float,   // 0.0 to 5.0
    modifier: Modifier = Modifier,
    starSize: Dp = 24.dp,
    spacing: Dp = 2.dp
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            val filled = rating >= i
            val halfFilled = !filled && rating > i - 1 && rating < i
            Icon(
                imageVector = Icons.Filled.Star, // import androidx.compose.material.icons.filled.Star
                contentDescription = null,
                tint = when {
                    filled -> Color(0xFFFFC107)
                    halfFilled -> Color(0xFFFFC107).copy(alpha = 0.5f)
                    else -> Color.LightGray
                },
                modifier = Modifier.size(starSize)
            )
            if (i < 5) Spacer(modifier = Modifier.width(spacing))
        }
    }
}



@Composable
fun DetailScreen(navController: NavHostController,movieId:Int){
    val detailsViewModel= hiltViewModel<DetailViewModel>()
    val detailState=detailsViewModel.detailState.collectAsState().value
    android.util.Log.d("DetailScreen", "Loaded movie: ${detailState.movie}")
    LaunchedEffect(key1 = movieId) {
        detailsViewModel.getTrailer(movieId)
    }

    if (detailState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return // Stop executing further to prevent crash
    }
    val backDropImageState= rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(MovieApi.IMAGE_BASE_URL+detailState.movie?.backdropPath).size(Size.ORIGINAL).build()
    ).state
    val posterImageState= rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(MovieApi.IMAGE_BASE_URL+detailState.movie?.posterPath).size(Size.ORIGINAL).build()
    ).state
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        if(backDropImageState is  AsyncImagePainter.State.Error){
            Box(modifier= Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center){
                Icon(modifier = Modifier.size(70.dp),
                    imageVector = Icons.Rounded.ImageNotSupported, contentDescription = detailState.movie?.title)

            }

        }
        Spacer(modifier = Modifier.height(70.dp))
        if(backDropImageState is  AsyncImagePainter.State.Success){

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                painter = backDropImageState.painter,
                contentDescription = detailState.movie?.title,
                contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)){
            Box(modifier = Modifier
                .width(160.dp)
                .height(240.dp)) {
                if (posterImageState is AsyncImagePainter.State.Error) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))


                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(70.dp),
                            imageVector = Icons.Rounded.ImageNotSupported,
                            contentDescription = detailState.movie?.title
                        )

                    }

                }
                if (posterImageState is AsyncImagePainter.State.Success) {

                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        painter = posterImageState.painter,
                        contentDescription = detailState.movie?.title,
                        contentScale = ContentScale.Crop
                    )
                }
            }
                detailState.movie?.let {movie ->
                    Column (modifier = Modifier.fillMaxWidth() ){
                        Text(modifier = Modifier.padding(16.dp), text = movie.title.toString(), fontSize = 19.sp, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier=Modifier

                            .padding(start = 16.dp)
                        ){
                            movie.voteAverage?.toFloat()?.let {
                                StarRatingBar(
                                    modifier = Modifier.height(18.dp),
                                    rating= (it /2)
                                )
                            }
                            Text(
                                modifier = Modifier.padding(start = 4.dp),
                                text = movie.voteAverage.toString().take(3),
                                color = Color.LightGray,
                                fontSize = 15.sp,
                                maxLines = 1,

                                )
                    }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(modifier = Modifier
                            .padding(16.dp),
                            text = "Language: " + movie.originalLanguage.toString())

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(modifier = Modifier
                            .padding(16.dp),
                            text = "Release Date: " + movie.releaseDate)

                        Spacer(modifier = Modifier.height(8.dp))


                        Text(modifier = Modifier
                            .padding(16.dp),
                            text =movie.voteCount.toString() + "Votes"
                        )

                }
             }
          }
        //Spacer(modifier = Modifier.height(8.dp))
        val trailerUrl = detailState.trailerUrl

        Button(onClick = {
            trailerUrl.let {
                navController.navigate("video/${Uri.encode(it)}")
            }
        }) {
            Text("Play Trailer")
        }
        Text(modifier = Modifier
            .padding(16.dp),
            text = "Overview",
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold)

       // Spacer(modifier = Modifier.height(10.dp))
        detailState.movie?.let {it->
            Text(
                modifier = Modifier
                    .padding(12.dp),
                text = it.overview.toString(),
                fontSize = 16.sp,
            )

        }
        Button(
            modifier = Modifier.padding(16.dp),
            onClick = {
                val videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                navController.navigate("video/$videoUrl")
            }
        ) {
            Text(text = "Play Trailer")
        }

        //Spacer(modifier = Modifier.height(10.dp))



       }
}