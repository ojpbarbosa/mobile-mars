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

        val cidades: List<Cidade> = Gson()
            .fromJson(lerArquivo("CidadesMarte.json"), object : TypeToken<List<Cidade>>() {}.type)

        val caminhos: List<Caminho> = Gson()
            .fromJson(lerArquivo("CaminhoEntreCidadesMarte.json"), object : TypeToken<List<Caminho>>() {}.type)
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