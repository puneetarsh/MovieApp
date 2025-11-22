package com.example.movieapp.movieList.data.repository



import android.util.Log


import coil.network.HttpException
import com.example.movieapp.movieList.data.local.movie.MovieDatabase
import com.example.movieapp.movieList.data.mappers.toMovie
import com.example.movieapp.movieList.data.mappers.toMovieEntity
import com.example.movieapp.movieList.data.remote.MovieApi
import com.example.movieapp.movieList.domain.VideoResponse
import com.example.movieapp.movieList.domain.model.Movie
import com.example.movieapp.movieList.domain.repository.MovieListRepository
import com.example.movieapp.movieList.util.Resource
import com.example.movieapp.movieList.util.Resource.Error
import com.example.movieapp.movieList.util.Resource.Loading
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import java.io.IOException
import javax.inject.Inject



class MovieListRepositoryImpl@Inject constructor(
    private val movieApi: MovieApi,
    private val movieDatabase: MovieDatabase
) : MovieListRepository{
    override suspend fun getMovieList(

        forceFetchFromRemote: Boolean,
        category: String,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        Log.d("MovieRepo", "getMovieList called — category=$category, page=$page, force=$forceFetchFromRemote")
        return flow{

            emit(Loading(true))
           val response = movieApi.getMoviesList(category, page)
            val gson = Gson()
            val jsonResponse = gson.toJson(response)
            Log.d("RawJsonResponse", jsonResponse)
            //made a offline app
            val localMovieList=movieDatabase.movieDao().getMovieCategory(category)
            //we should check movies from local database
            //in case if not possible we move to API
            val shouldLoadLocalMovie=localMovieList.isNotEmpty() && !forceFetchFromRemote
            if(shouldLoadLocalMovie){
                Log.d("MovieRepo", "SUCCESS: Loaded from local cache.")
                emit(Resource.Success(
                    data=localMovieList.map { movieEntity ->
                        movieEntity.toMovie(category)
                    }
                ))
                emit(Loading(false))
                return@flow


            }
            //if upper case is false
            //now i want from API
            Log.d("MovieRepo", "INFO: Fetching from remote API.")
            val apiCategory = when (category.lowercase()) {
                "popular" -> "popular"
                "upcoming" -> "upcoming"
                else -> "popular" // fallback
            }
           Log.d("MovieAPI", "Full URL: https://api.themoviedb.org/3/movie/$apiCategory?api_key=${MovieApi.API_KEY}&page=$page")


            val movieListFromApi=try {


                val response = movieApi.getMoviesList(apiCategory, page)
                Log.d("MovieRepo", "Raw API Response Sample: ${response.results.take(3)}")
                response.results.forEach {
                    Log.d("MovieRepo", "Poster path: ${it.posterPath}")
                }
                response


           }catch (e: IOException){
               e.printStackTrace()
                Log.e("MovieRepo", "ERROR: IOException - ${e.message}")
               emit(Error("Error Loading Data"))
               return@flow
           }catch (e: HttpException){
               e.printStackTrace()
                Log.e("MovieRepo", "ERROR: HttpException - ${e.message}")
               emit(Error("Error Loading Data"))
               return@flow
           }catch (e: Exception){
               e.printStackTrace()
                Log.e("MovieRepo", "ERROR: Exception - ${e.message}")
               emit(Error("Error Loading Data"))
                return@flow
           }
            //first we map to entity then...
            //here we store inside the database
            val movieEntities=movieListFromApi.results.let {
                it.map { movieDto ->
                    movieDto.toMovieEntity(category)
                }

            }
            movieDatabase.movieDao().upsertMovieList(movieEntities)
            emit(Resource.Success(
                movieEntities.map{
                    it.toMovie(category)
                }
            ))
            emit(Loading(false))

        }


    }
    //here we get one movie

    override suspend fun getMovie(id: Int): Flow<Resource<Movie>> {
        return flow{
            emit(Loading(true))
            val movieEntity=movieDatabase.movieDao().getMovieById(id)
            if(movieEntity !=null){
                emit(Resource.Success(movieEntity.toMovie(movieEntity.category))
                )
                    emit(Loading(false))
                    return@flow


            }
            //in case we dont already have this movie inside database
            emit(Error("Error no such movie"))

        }

    }

    override suspend fun getMovieTrailer(id: Int): Flow<Resource<VideoResponse>> {
        return flow {
            Log.d("RepoFlow", "Emitting Loading(true)")
            emit(Loading(true))
            Log.d("RepoFlow", "Emitting Success with data: ${movieApi.getMovieVideos(id)}")

            try {
                val response = movieApi.getMovieVideos(id)
                Log.d("RepoFlow", "Emitting Success with data: $response")
                // TMDB video list me "type = Trailer" wala item dhundte hain
                val trailer = response.results.firstOrNull { it.type == "Trailer" }

                if (trailer != null) {
                    //val trailerUrl = "https://www.youtube.com/watch?v=${trailer.key}"
                    emit(Resource.Success(response))
                } else {
                    // It's better to handle the "not found" case explicitly
                    emit(Error("Trailer not found for this movie."))
                }
            }catch(e:IOException) {
                    e.printStackTrace()
                    emit(Error("Could not load trailer. Please check your network connection."))
                    Log.d("RepoFlow", "Emitting IOException Error: ${e.message}")
                } catch (e: HttpException) {
                    e.printStackTrace()
                    emit(Error("An unexpected error occurred while fetching the trailer."))
                    Log.d("RepoFlow", "Emitting HttpException Error: ${e.message}")
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Error(e.message ?: "An unknown error occurred."))
                    Log.d("RepoFlow", "Emitting General Exception Error: ${e.message}")
                } finally {
                    // This block will ALWAYS be executed.
                    emit(Loading(false))
                    Log.d("RepoFlow", "Emitting Loading(false) from finally block")
                }


            }
        }

    }


