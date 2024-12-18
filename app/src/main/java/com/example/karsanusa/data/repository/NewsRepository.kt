package com.example.karsanusa.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.karsanusa.data.remote.response.ErrorResponse
import com.example.karsanusa.data.remote.response.NewsResponseItem
import com.example.karsanusa.data.remote.retrofit.ApiServiceNews
import com.example.karsanusa.data.result.Result
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class NewsRepository private constructor(
    private val apiServiceNews: ApiServiceNews
) {

    fun getNews(
    ): LiveData<Result<List<NewsResponseItem>>> = liveData {
        emit(Result.Loading)

        try {
            val response = apiServiceNews.getNews()
            emit(Result.Success(response))
        } catch (e: IOException) {
            emit(Result.Error("No internet connection"))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        } catch (e: Exception) {
            emit(Result.Error("Something went wrong: ${e.message}"))
        }
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiServiceNews: ApiServiceNews
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiServiceNews)
            }.also { instance = it }
    }
}