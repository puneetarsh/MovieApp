package com.example.movieapp.movieList.domain

import android.provider.MediaStore
import com.google.gson.annotations.SerializedName

data class VideoResponse(
    val id: Int,
    @SerializedName("results")
    val results: List<VideoItem>
)

data class VideoItem(
    @SerializedName("key")
    val key: String,     // YouTube video ID
    @SerializedName("type")
    val type: String ,    // "Trailer"
    @SerializedName("name")
    val name: String,     // Video title
    @SerializedName("site")
    val site: String, // "YouTube"
    @SerializedName("official")
    val official: Boolean?=null,
    @SerializedName("published_at")
    val publishedAt: String
)
