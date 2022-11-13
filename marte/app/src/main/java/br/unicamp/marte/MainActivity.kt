// Gabriel Willian Bartmanovicz - 21234
// João Pedro Ferreira Barbosa - 21687

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
    lateinit var algoritmoSelecionado: Algoritmo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // leitura e conversão do arquivo JSON de cidades
        // em um array de objetos da classe Cidade
        val cidades: Array<Cidade> = Gson()
            .fromJson(lerArquivo("CidadesMarte.json"), object : TypeToken<Array<Cidade>>() {}.type)

        // atribuição do adaptador do spinner das cidades de origem
        // como um ArrayAdapter de string que assume como valor os
        // nomes das cidades através da função map
        val origemSpinnerAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                cidades.map {
                    it.nome
                }
            )
        val origemSpinner: Spinner = findViewById(R.id.spinner_origem)
        origemSpinner.adapter = origemSpinnerAdapter
        // TODO: set selected spinner element as the first city
        // https://stackoverflow.com/questions/11072576/set-selected-item-of-spinner-programmatically

        // atribuição do adaptador do spinner das cidades de destino
        // como um ArrayAdapter de string que assume como valor os
        // nomes das cidades através da função map
        val destinoSpinnerAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                cidades.map {
                    it.nome
                }
            )
        val destinoSpinner: Spinner = findViewById(R.id.spinner_destino)
        destinoSpinner.adapter = destinoSpinnerAdapter
        // TODO: set selected spinner element as the second city
        // https://stackoverflow.com/questions/11072576/set-selected-item-of-spinner-programmatically

        // leitura e conversão do arquivo JSON de caminhos
        // em um array de objetos da classe Caminho
        val caminhos: Array<Caminho> = Gson()
            .fromJson(lerArquivo("CaminhoEntreCidadesMarte.json"), object : TypeToken<Array<Caminho>>() {}.type)

        // geração da matriz de adjacências
        val adjacencias = Array(cidades.size) { IntArray(cidades.size) }
        caminhos.forEach { caminho ->
            // para cada caminho, obtém-se o índice
            // da cidade de origem e destino
            val indiceOrigem = cidades.indexOf(cidades.find { cidade ->
                cidade.nome == caminho.cidadeOrigem
            })
            val indiceDestino = cidades.indexOf(cidades.find { cidade ->
                cidade.nome == caminho.cidadeDestino
            })

            // e atribui-se à matriz a distância
            // correspondente entre as cidades
            adjacencias[indiceOrigem][indiceDestino] = caminho.distancia!!
        }
    }

    fun lerArquivo(arquivo: String): String {
        lateinit var conteudo: String

        try {
            // abre e lê o arquivo existente no
            // diretório de assets do aplicativo
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
            val viewSelecionada = view.isChecked

            // obtém o id da view e verifica
            // se a mesma foi selecionada
            when (view.getId()) {
                R.id.radio_button_recursivo ->
                    // se o radio button respectivo ao
                    // algoritmo recursivo foi selecionado
                    if (viewSelecionada)
                        algoritmoSelecionado = Algoritmo.Recursivo

                R.id.radio_button_dijkstra ->
                    // se o radio button respectivo ao
                    // algoritmo de dijkstra foi selecionado
                    if (viewSelecionada)
                        algoritmoSelecionado = Algoritmo.Dijkstra
            }
        }
    }
}