package br.unicamp.marte

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var algoritmo: Algoritmo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cidades: Array<Cidade> = Gson()
            .fromJson(lerArquivo("CidadesMarte.json"), object : TypeToken<Array<Cidade>>() {}.type)

        val origemListViewAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                cidades.map {
                    it.nome
                }
            )
        val origemListView: Spinner = findViewById(R.id.spinner_origem)
        origemListView.adapter = origemListViewAdapter

        val destinoListViewAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                cidades.map {
                    it.nome
                }
            )
        val destinoListView: Spinner = findViewById(R.id.spinner_destino)
        destinoListView.adapter = destinoListViewAdapter

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

    fun onAlgoritmoSelecionado(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.radio_button_recursao ->
                    if (checked) {
                        algoritmo = Algoritmo.RECURSAO
                    }

                R.id.radio_button_dijkstra ->
                    if (checked) {
                        algoritmo = Algoritmo.DIJKSTRA
                    }
            }
        }
    }
}