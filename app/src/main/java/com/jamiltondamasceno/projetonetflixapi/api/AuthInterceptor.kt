package com.jamiltondamasceno.projetonetflixapi.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //Chain: corrente, cadeia
        // 1 2 3 4

        // 1) Acessar a requisição
        val construtorRequisicao = chain.request().newBuilder()

        //2) Alterar URL ou Rota da requisição
        // https://api.themoviedb.org/3/ + movie/latest + api_key
        /*val urlAtual = chain.request().url()
        val novaUrl = urlAtual.newBuilder()
        novaUrl.addQueryParameter(
            "api_key",
            RetrofitService.API_KEY
        )

        //3) Configurar nova url na requisição
        construtorRequisicao.url( novaUrl.build() )*/

        //Utilizando Bearer Token
        val requisicao = construtorRequisicao.addHeader(
            "Authorization", "Bearer ${RetrofitService.TOKEN}"
        ).build()

        return chain.proceed( requisicao ) //Response
    }
}