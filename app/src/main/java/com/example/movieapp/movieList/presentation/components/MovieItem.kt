package com.example.movieapp.movieList.presentation.components


import android.widget.RatingBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

import coil.size.Size
import com.example.movieapp.movieList.data.remote.MovieApi
import com.example.movieapp.movieList.domain.model.Movie
import com.example.movieapp.movieList.util.Screen
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView

import androidx.palette.graphics.Palette

fun getAverageColor(imageBitmap: Bitmap): Color {
    val palette = Palette.from(imageBitmap).generate()
    val dominantSwatch = palette.dominantSwatch
    return dominantSwatch?.rgb?.let { Color(it) } ?: Color.Gray
}

@Composable
fun StarRatingBar(
    rating: Double,   // 0.0 to 5.0
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
fun MovieItem(
    movie: Movie,
    navHostController: NavHostController
){
    val posterPath=movie.posterPath
    val imageUrl = if(!posterPath.isNullOrEmpty()){
        MovieApi.IMAGE_BASE_URL + movie.posterPath
    }
    else{
        null
    }
    Log.d("MovieItem", "Movie: ${movie.title}, posterPath ${movie.posterPath}, Full URL: $imageUrl")

    val imageState= rememberAsyncImagePainter(
        model= ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .allowHardware(false)
            .crossfade(true)
            .build()
    ).state
    val defaultColor= MaterialTheme.colorScheme.secondaryContainer
    var dominantColor by remember {
        mutableStateOf(defaultColor)
    }
    Column (modifier = Modifier
        .wrapContentHeight()
        .width(200.dp)
        .padding(8.dp)
        .clip(
            RoundedCornerShape(28.dp)
        )
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.secondaryContainer,
                    dominantColor
                )

            )
        )
        .clickable {
            movie.id?.let { navHostController.navigate(Screen.Details.createRoute(it)) }

        }
    ){

            if(imageState is  AsyncImagePainter.State.Error){
                Log.e("CoilError", "Image load failed for $imageUrl")
                Box(modifier= Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .height(250.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center){
                    Icon(modifier = Modifier.size(70.dp),
                        imageVector = Icons.Rounded.ImageNotSupported, contentDescription = movie.title
                    )
                }

            }
            if(imageState is  AsyncImagePainter.State.Success){
                dominantColor =getAverageColor(
                    imageBitmap =imageState.result.drawable.toBitmap())
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                        .height(250.dp)
                        .clip(RoundedCornerShape(22.dp)),
                    painter = imageState.painter,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop)
        }
        Spacer(modifier= Modifier.height(6.dp))
        Text(modifier = Modifier.padding(start=16.dp,end=8.dp),
            text =movie.title.toString(),
            color = Color.White,
            fontSize = 15.sp,
            maxLines = 1
            )
        Row(modifier=Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 12.dp, top = 4.dp)){
            movie.voteAverage?.let {
                StarRatingBar(
                    modifier = Modifier.size(18.dp),
                    rating =it/2)
            }
            Text(modifier = Modifier.padding(start=4.dp),
                text =movie.voteAverage.toString().take(3),
                color = Color.LightGray,
                fontSize = 15.sp,
                maxLines = 1

            )

        }

    }

}

