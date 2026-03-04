package com.jamiltondamasceno.projetonetflixapi.api

import com.jamiltondamasceno.projetonetflixapi.model.Endereco
import com.jamiltondamasceno.projetonetflixapi.model.FilmeRecente
import retrofit2.Response
import retrofit2.http.GET

interface ViaCepAPI {

    @GET("01001000/xml")
    suspend fun recuperarEndereco() : Response<Endereco>

}