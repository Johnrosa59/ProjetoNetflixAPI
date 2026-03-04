package com.jamiltondamasceno.projetonetflixapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.jamiltondamasceno.projetonetflixapi.adapter.FilmeAdapter
import com.jamiltondamasceno.projetonetflixapi.api.FilmeAPI
import com.jamiltondamasceno.projetonetflixapi.api.RetrofitService
import com.jamiltondamasceno.projetonetflixapi.api.ViaCepAPI
import com.jamiltondamasceno.projetonetflixapi.databinding.ActivityMainBinding
import com.jamiltondamasceno.projetonetflixapi.model.Endereco
import com.jamiltondamasceno.projetonetflixapi.model.FilmeRecente
import com.jamiltondamasceno.projetonetflixapi.model.FilmeResposta
import com.jamiltondamasceno.projetonetflixapi.model.Genero
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var paginaAtual = 1
    private val TAG = "info_filme"
    private val binding by lazy {
        ActivityMainBinding.inflate( layoutInflater )
    }

    private val filmeAPI by lazy {
        //RetrofitService.filmeAPI
        RetrofitService.recuperarApi( FilmeAPI::class.java )
    }
    private val viaCepAPI by lazy {
        RetrofitService.recuperarViaCep()
        //RetrofitService.recuperarApi( ViaCepAPI::class.java )
    }

    var jobFilmeRecente: Job? = null
    var jobFilmesPopulares: Job? = null
    var gridLayoutManager: GridLayoutManager? = null
    private lateinit var filmeAdapter: FilmeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        inicializarViews()
        recuperarEndereco()



        //Várias instâncias
        /*val genero1 = Genero(1, "Comédia")
        val genero2 = Genero(2, "Ação")*/
        /*val usuario1 = Usuario()
        usuario1.nome = "Jamlton"

        val usuario2 = Usuario()
        usuario2.nome = "Maria"

        //Uma única Instância
        val retro = RetrofitSingleton.API_KEY
        Log.i("api_filme", "retrofit: $retro")
        Log.i("api_filme", "usuario1: $usuario1 - usuario2: $usuario2")*/
    }

    private fun recuperarEndereco() {

        CoroutineScope( Dispatchers.IO ).launch {

            var resposta: Response<Endereco>? = null

            try {
                resposta = viaCepAPI.recuperarEndereco()
            }catch (e: Exception){
                exibirMensagem("Erro ao fazer a requisição")
            }

            if( resposta != null ){
                if( resposta.isSuccessful ){

                    val endereco = resposta.body()
                    if( endereco != null ){
                        val logradouro = endereco.logradouro
                        val bairro = endereco.bairro
                        val complemento = endereco.complemento
                        val localidade = endereco.localidade
                        Log.i("viacep", "recuperarEndereco: $logradouro - $bairro - $complemento - $localidade")
                    }

                }else{
                    exibirMensagem("Não foi possível recuperar o filme recente CODIGO: ${resposta.code()}")
                }
            }else{
                exibirMensagem("Não foi possível fazer a requisição")
            }

        }
    }

    private fun inicializarViews() {

        filmeAdapter = FilmeAdapter{ filme ->
            val intent = Intent(this, DetalhesActivity::class.java)
            intent.putExtra("filme", filme )
            startActivity( intent )
        }
        binding.rvPopulares.adapter = filmeAdapter
        gridLayoutManager = GridLayoutManager(
            this,
            2
        )
        /*gridLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )*/
        binding.rvPopulares.layoutManager = gridLayoutManager

        binding.rvPopulares.addOnScrollListener( object : OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val podeDescerVerticalmente = recyclerView.canScrollVertically(1)
                //1) Chegar ao final da lista
                if( !podeDescerVerticalmente ){//Não NÃO puder descer
                    //Carregar mais 20 itens
                    Log.i("recycler_api", "paginaAtual: $paginaAtual")
                    recuperarFilmesPopularesProximaPagina()
                }



               /*//0..19 (20)
                val ultimoItemVisivel = linearLayoutManager?.findLastVisibleItemPosition()
                val totalItens = recyclerView.adapter?.itemCount
                //Log.i("recycler_test", "ultimo: $ultimoItemVisivel total: $totalItens")
                if( ultimoItemVisivel != null && totalItens != null ){
                    if( totalItens-1 == ultimoItemVisivel ){//Chegou no último item
                        binding.fabAdicionar.hide()
                    }else{//Não chegou no último item
                        binding.fabAdicionar.show()
                    }
                }*/
                /*Log.i("recycler_test", "onScrolled: dx: $dx dy: $dy")
                if( dy > 0 ){//descendo
                    binding.fabAdicionar.hide()
                }else{//subindo
                    binding.fabAdicionar.show()
                }*/

            }

        })

    }

    /*class ScrollCustomizado : OnScrollListener(){

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        }

    }*/


    override fun onStart() {
        super.onStart()
        recuperarFilmeRecente()
        recuperarFilmesPopulares()
    }

    private fun recuperarFilmesPopularesProximaPagina(){

        if( paginaAtual < 1000 ){
            paginaAtual++
            recuperarFilmesPopulares(paginaAtual)
        }

    }

    private fun recuperarFilmesPopulares(pagina: Int = 1) {

        jobFilmesPopulares = CoroutineScope( Dispatchers.IO ).launch {

            var resposta: Response<FilmeResposta>? = null

            try {
                resposta = filmeAPI.recuperarFilmesPopulares(pagina)
            }catch (e: Exception){
                exibirMensagem("Erro ao fazer a requisição")
            }

            if( resposta != null ){
                if( resposta.isSuccessful ){

                    val filmeResposta = resposta.body()
                    val listaFilmes = filmeResposta?.filmes
                    if( listaFilmes != null && listaFilmes.isNotEmpty() ){

                        withContext(Dispatchers.Main){
                            filmeAdapter.adicionarLista( listaFilmes )
                        }

                        /*Log.i("filmes_api", "lista Filmes:")
                        listaFilmes.forEach { filme ->
                            Log.i("filmes_api", "Titulo: ${filme.title}")
                        }*/

                    }


                }else{
                    exibirMensagem("Não foi possível recuperar o filme recente CODIGO: ${resposta.code()}")
                }
            }else{
                exibirMensagem("Não foi possível fazer a requisição")
            }

        }
    }

    private fun recuperarFilmeRecente() {
        jobFilmeRecente = CoroutineScope( Dispatchers.IO ).launch {

            var resposta: Response<FilmeRecente>? = null

            try {
                resposta = filmeAPI.recuperarFilmeRecente()
            }catch (e: Exception){
                 exibirMensagem("Erro ao fazer a requisição")
            }

            if( resposta != null ){
                if( resposta.isSuccessful ){

                    val filmeRecente = resposta.body()
                    val nomeImagem = filmeRecente?.poster_path
                    val titulo = filmeRecente?.title
                    val url = RetrofitService.BASE_URL_IMAGEM + "w780" + nomeImagem

                    withContext( Dispatchers.Main ){
                        /*val texto = "titulo: $titulo url: $url"
                        binding.textPopulares.text = texto*/
                        Picasso.get()
                            .load( url )
                            .error( R.drawable.capa )
                            .into( binding.imgCapa )

                    }

                }else{
                    exibirMensagem("Não foi possível recuperar o filme recente CODIGO: ${resposta.code()}")
                }
            }else{
                exibirMensagem("Não foi possível fazer a requisição")
            }

        }
    }

    private fun exibirMensagem( mensagem: String ) {
        Toast.makeText(
            applicationContext,
            mensagem,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onStop() {
        super.onStop()
        jobFilmeRecente?.cancel()
        jobFilmesPopulares?.cancel()
    }

}