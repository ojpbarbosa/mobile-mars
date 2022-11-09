package br.unicamp.marte

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cidades: Array<Cidade> = Gson()
            .fromJson(lerArquivo("CidadesMarte.json"), object : TypeToken<Array<Cidade>>() {}.type)

        val caminhos: Array<Caminho> = Gson()
            .fromJson(lerArquivo("CaminhoEntreCidadesMarte.json"), object : TypeToken<Array<Caminho>>() {}.type)

        val adjacencias = Array(cidades.size) { IntArray(cidades.size) }

        caminhos.forEach { caminho ->
            val origem = cidades.indexOf(cidades.find { cidade ->
                cidade.nome == caminho.cidadeOrigem
            })

            val destino = cidades.indexOf(cidades.find { cidade ->
                cidade.nome == caminho.cidadeDestino
            })

            adjacencias[origem][destino] = caminho.distancia!!
        }
    }

    fun lerArquivo(arquivo: String): String {
        lateinit var conteudo: String

        try {
            conteudo = assets.open(arquivo)
                .bufferedReader()
                .use {
                    it.readText()
                }
        } catch (erro: IOException) {
            Toast.makeText(this, erro.message, Toast.LENGTH_LONG).show()
        }

        return conteudo
    }
}