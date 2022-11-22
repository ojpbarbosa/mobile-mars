// Gabriel Willian Bartmanovicz - 21234
// João Pedro Ferreira Barbosa - 21687

package br.unicamp.marte

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var algoritmoSelecionado: Algoritmo
    lateinit var cidades: Array<Cidade>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // leitura e conversão do arquivo JSON de cidades
        // em um array de objetos da classe Cidade
        cidades = Gson()
            .fromJson(lerArquivo("CidadesMarte.json"), object : TypeToken<Array<Cidade>>() {}.type)

        desenharCidades()

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
        destinoSpinner.setSelection(1)

        // leitura e conversão do arquivo JSON de caminhos
        // em um array de objetos da classe Caminho
        val caminhos: Array<Caminho> = Gson()
            .fromJson(lerArquivo("CaminhoEntreCidadesMarte.json"), object : TypeToken<Array<Caminho>>() {}.type)

        // instanciação da matriz de adjacências
        val adjacencias: Array<Array<DadosCaminho?>> = Array(caminhos.size) { arrayOfNulls(caminhos.size) }
        caminhos.forEach {
            // desenha-se o caminho na image view
            desenharCaminho(it)

            // para cada caminho, obtém-se o índice
            // da cidade de origem e destino através
            // de uma pesquisa binária
            val indiceOrigem = obterIndiceCidade(it.cidadeOrigem!!)
            val indiceDestino = obterIndiceCidade(it.cidadeDestino!!)

            // e atribui-se à matriz os dados
            // correspondentes do caminho
            adjacencias[indiceOrigem][indiceDestino] =
                DadosCaminho(it.distancia!!, it.tempo!!, it.custo!!)
        }

        val buscarButton: Button = findViewById(R.id.button_buscar)

        // quando o botão de buscar for clicado,
        // achamos o caminho de acordo com o algoritmo selecionado
        buscarButton.setOnClickListener {
            when (algoritmoSelecionado) {
                Algoritmo.Recursivo -> acharCaminhoComRecursao(adjacencias)
                Algoritmo.Dijkstra -> acharCaminhoComDijkstra(adjacencias)
            }
        }
    }

    private fun lerArquivo(arquivo: String): String {
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

    private fun obterIndiceCidade(cidade: String): Int {
        // busca binária pela cidade
        fun buscarCidade(inicio: Int, fim: Int): Int {
            if (fim >= inicio) {
                val meio = (inicio + fim) / 2

                if (cidades[meio].nome.equals(cidade))
                    return meio

                if (cidades[meio].nome!! > cidade)
                    return buscarCidade(inicio, meio - 1)

                return buscarCidade(meio + 1, fim)
            }

            return -1
        }

        return buscarCidade(0, cidades.size)
    }

    fun acharCaminhoComDijkstra(adjacencias: Array<Array<DadosCaminho?>>) {}

    fun acharCaminhoComRecursao(adjacencias: Array<Array<DadosCaminho?>>) {}

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

    private fun desenharCidades() {
        val mapaImageView: ImageView = findViewById(R.id.image_view_mapa)

        val mapa = BitmapFactory.decodeResource(resources, R.drawable.mapa_de_marte)
        val bitmap = Bitmap.createBitmap(mapa.width, mapa.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.color = Color.BLUE
        paint.textSize = 24 * resources.displayMetrics.density

        canvas.drawBitmap(mapa, 0f, 0f, null)

        cidades.forEach {
            it.nome?.let { nome ->
                it.x?.times(mapa.width)?.let { x ->
                    it.y?.times(mapa.height)?.let { y ->
                        canvas.drawText(
                            nome,
                            x.times(mapa.width).toFloat(),
                            y.times(mapa.height).toFloat(),
                            paint
                        )
                    }
                }
            }
        }

        mapaImageView.setImageDrawable(BitmapDrawable(resources, bitmap))
    }

    private fun desenharCaminho(caminho: Caminho) {

    }
}