package com.jamiltondamasceno.projetonetflixapi.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitService {

    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val BASE_URL_IMAGEM = "https://image.tmdb.org/t/p/"
    const val API_KEY = "f1d4bdabc6dcb6cb665675fdf8ffc665"
    const val TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1MTg5OTJhZGUxYWE0NWYwN2Y2NmNjMmQ4ZDRjNWE4MyIsInN1YiI6IjYzNDVjZGE1MWI3YzU5MDA4MWMwYWI3NiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.RDKlYGxZwNHfDnEDVgCVq1pvKPt4CRWDX0i4aX2JyJc"

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .writeTimeout(10, TimeUnit.SECONDS)// Escrita (salvando na API)
        .readTimeout(20, TimeUnit.SECONDS)// Leitura (recuperando dados da API)
        .connectTimeout(20, TimeUnit.SECONDS) //Conexão máxima
        .addInterceptor( AuthInterceptor() )
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl( BASE_URL )
        .addConverterFactory( GsonConverterFactory.create() )
        .client( okHttpClient )
        .build()

    val filmeAPI = retrofit.create( FilmeAPI::class.java )

    fun <T> recuperarApi( classe: Class<T>) : T {
        return retrofit.create( classe )
    }

    fun recuperarViaCep() : ViaCepAPI {

        return Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory( SimpleXmlConverterFactory.create() )
            .build()
            .create( ViaCepAPI::class.java )

    }

}