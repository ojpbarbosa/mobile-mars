// Gabriel Willian Bartmanovicz - 21234
// João Pedro Ferreira Barbosa - 21687

package br.unicamp.marte

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
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
            .fromJson(
                lerArquivo("CidadesMarte.json"),
                object : TypeToken<Array<Cidade>>() {}.type
            )

        // desenha as cidades na view
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
            .fromJson(
                lerArquivo("CaminhoEntreCidadesMarte.json"),
                object : TypeToken<Array<Caminho>>() {}.type
            )

        // instanciação da matriz de adjacências
        val adjacencias: Array<Array<DadosCaminho?>> = Array(caminhos.size) { arrayOfNulls(caminhos.size) }
        caminhos.forEach {
            // para cada caminho, obtém-se o índice
            // da cidade de origem e destino através
            // de uma pesquisa binária
            val indiceOrigem = obterIndiceCidade(it.cidadeOrigem!!)
            val indiceDestino = obterIndiceCidade(it.cidadeDestino!!)

            // desenha-se o caminho na image view
            desenharCaminho(cidades[indiceOrigem], cidades[indiceDestino], it.distancia!!)

            // e atribui-se à matriz os dados
            // correspondentes do caminho
            adjacencias[indiceOrigem][indiceDestino] =
                DadosCaminho(it.distancia, it.tempo!!, it.custo!!)
        }

        val buscarButton: Button = findViewById(R.id.button_buscar)

        // quando o botão de buscar for clicado,
        // achamos o caminho de acordo com o algoritmo selecionado
        buscarButton.setOnClickListener {
            when (algoritmoSelecionado) {
                Algoritmo.Recursivo -> {
                    acharCaminhosComRecursao(adjacencias, origemSpinner.selectedItemPosition, destinoSpinner.selectedItemPosition)
                }
                Algoritmo.Dijkstra -> {
                    acharCaminhoComDijkstra(adjacencias, origemSpinner.selectedItemPosition, destinoSpinner.selectedItemPosition)
                }
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
        // pesquisa binária pela cidade
        // método recursivo que assume como parâmetros
        // os índices que delimitam o intervalo de busca
        // no vetor
        fun buscarCidade(inicio: Int, fim: Int): Int {
            if (fim >= inicio) {
                // calcula o meio do intervalo procurado
                val meio = (inicio + fim) / 2

                // se a cidade se encontra na posição
                // respectiva ao meio do intervalo procurado
                if (cidades[meio].nome.equals(cidade))
                    return meio // o índice é retornado

                // se a cidade se encontra após o meio
                if (cidades[meio].nome!! > cidade)
                // é feita uma nova pesquisa binária
                // na segunda metade do intervalo
                    return buscarCidade(inicio, meio - 1)

                // se a cidade se encontra antes do meio,
                // é feita uma nova pesquisa binária
                // na primeira metade do intervalo
                return buscarCidade(meio + 1, fim)
            }

            // se a cidade não foi encontrada,
            // não há índice, portanto, retorna-se -1
            return -1
        }

        // inicia-se a busca pela cidade com os
        // índices pertencentes ao intervalo
        // entre 0 e a quantidade de cidades
        return buscarCidade(0, cidades.size)
    }

    fun onAlgoritmoSelecionado(view: View) {
        if (view is RadioButton) {
            val viewSelecionada = view.isChecked

            // obtém-se o id da view e verifica
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

//    Algoritmo de Dijkstra --------------------------------------

    fun acharCaminhoComDijkstra(adjacencias: Array<Array<DadosCaminho?>>, cidadeOrigem: Int, cidadadeDestino: Int) {
        val pesos = mutableMapOf<Pair<Int, Int>, Int>()

        for (i in adjacencias.indices) {
            for (j in adjacencias.indices) {
                pesos.put(Pair(i, j), adjacencias[i][j]!!.distancia)
            }
        }

        val menorCaminho = dijkstra(Grafo(pesos), cidadeOrigem);
    }

    fun <T> dijkstra (adjacencias: Grafo<T>, inicio: T): Map<T, T?> {
        val s: MutableSet<T> = mutableSetOf()

        val delta = adjacencias.vertices.map { it to Int.MAX_VALUE }.toMap().toMutableMap()

        // A distância do vértice fonte dele mesmo é sempre 0
        delta[inicio] = 0

        val anterior: MutableMap<T, T?> = adjacencias.vertices.map { it to null }.toMap().toMutableMap()

        while (s != adjacencias.vertices) {
            val v: T = delta
                .filter { !s.contains(it.key) }
                .minBy { it.value }!!
                .key

            adjacencias.arestas.getValue(v).minus(s).forEach { vizinho ->
                val novoCaminho = delta.getValue(v) + adjacencias.pesos.getValue(Pair(v, vizinho))

                if (novoCaminho < delta.getValue(vizinho)) {
                    delta[vizinho] = novoCaminho
                    anterior[vizinho] = v
                }
            }

            s.add(v);
        }

        return anterior.toMap()
    }

    fun <T> menorCaminho(arvoreMenorCaminho: Map<T, T?>, inicio: T, fim: T): List<T> {
        fun caminhoPara(inicio: T, fim: T): List<T> {
            if (arvoreMenorCaminho[fim] == null) return listOf(fim)
            return listOf(caminhoPara(inicio, arvoreMenorCaminho[fim]!!), listOf(fim)).flatten()
        }

        return caminhoPara(inicio, fim)
    }

//    Algoritmo de Recursão --------------------------------------

    fun acharCaminhosComRecursao(adjacencias: Array<Array<DadosCaminho?>>, cidadeOrigem: Int, cidadeDestino: Int): ArrayList<ArrayList<Caminho?>> {
        var caminhos = ArrayList<ArrayList<Caminho?>>()
        var visitados = Array(adjacencias.size) { BooleanArray(adjacencias.size) { false } }


        fun executar(x: Int, y: Int, caminho: ArrayList<Caminho?>) {
            if (x == cidadeDestino) {
                caminhos.add(caminho)
                caminho.clear()
            }
            else {
                for (i in adjacencias.indices) {
                    if (adjacencias[x][i] != null) {
                        if (!visitados[x][i]) {
                            visitados[x][i] = true
                            caminho.add(Caminho(cidades[x].nome!!, cidades[i].nome!!, adjacencias[x][i]!!.distancia, adjacencias[x][i]!!.tempo, adjacencias[x][i]!!.custo))
                            executar(i, y, caminho)
                            visitados[x][i] = false
                        }
                    }
                }
            }
        }

        executar(cidadeOrigem, 0, ArrayList())

        return caminhos
    }

    private fun desenharCidades() {
        // converte o arquivo do mapa para bitmap
        val mapaSemCidades = BitmapFactory.decodeResource(resources, R.drawable.mapa_de_marte)
        // cria um bitmap novo a partir do bitmap do mapa
        val mapaComCidades = Bitmap
            .createBitmap(mapaSemCidades.width, mapaSemCidades.height, Bitmap.Config.ARGB_8888)
        // cria um canvas com o mapa com cidades
        val canvas = Canvas(mapaComCidades)

        // instanciação da classe Paint
        // e definição da cor e tamanho do texto
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = resources.displayMetrics.density.times(18)

        // desenha o bitmap do mapa no canvas
        canvas.drawBitmap(mapaSemCidades, 0f, 0f, null)

        cidades.forEach {
            // para cada cidade no array de cidades,
            // calcula-se as coordenadas (x, y) respectivas
            // à cidade ao multiplicar as coordenadas relativas
            // (x, y) da cidade pela largura e altura do bitmap do mapa
            val x = it.x!!.times(mapaSemCidades.width).toFloat()
            val y = it.y!!.times(mapaSemCidades.height).toFloat()

            // desenha o nome da cidade
            // deslocado das coordenadas da cidade
            canvas.drawText(
                it.nome!!,
                x.minus(it.nome.length * 12),
                y.minus(18),
                paint
            )
            // desenha um círculo nas coordenadas da cidade
            canvas.drawCircle(x, y, 12f, paint)
        }

        // define o bitmap da image view do mapa como o novo bitmap com as cidades
        findViewById<ImageView>(R.id.image_view_mapa).setImageBitmap(mapaComCidades)
    }

    private fun desenharCaminho(cidadeOrigem: Cidade, cidadeDestino: Cidade, distancia: Int) {
        // converte o drawable exibido na image view com os últimos caminhos para bitmap
        val antigoMapa = findViewById<ImageView>(R.id.image_view_mapa).drawable.toBitmap()
        // cria um bitmap novo a partir do bitmap do mapa exibido
        val novoMapa = Bitmap
            .createBitmap(antigoMapa.width, antigoMapa.height, Bitmap.Config.ARGB_8888)
        // cria um canvas com o novo mapa com o caminho
        val canvas = Canvas(novoMapa)

        // instanciação da classe Paint
        // e definição da cor e tamanho do texto e
        // largura da linha
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = resources.displayMetrics.density.times(16)
        paint.strokeWidth = resources.displayMetrics.density
            .times(1.2).toFloat()

        // desenha o bitmap do mapa exibido no canvas
        canvas.drawBitmap(antigoMapa, 0f, 0f, null)

        // calcula-se as coordenadas (x, y) respectivas
        // às cidades de origem e destino ao multiplicar
        // as coordenadas relativas (x, y) de cada cidade
        // pela largura e altura do bitmap do mapa exibido
        val xOrigem = cidadeOrigem.x!!
            .times(antigoMapa.width).toFloat()
        val yOrigem = cidadeOrigem.y!!
            .times(antigoMapa.height).toFloat()
        val xDestino = cidadeDestino.x!!
            .times(antigoMapa.width).toFloat()
        val yDestino = cidadeDestino.y!!
            .times(antigoMapa.height).toFloat()

        // desenha a linha entre a cidade de origem e a cidade de destino
        canvas.drawLine(xOrigem, yOrigem, xDestino, yDestino, paint)
        // desenha a distância do caminho deslocado da linha
        canvas.drawText(
            distancia.toString(),
            ((xOrigem + xDestino) / 2).minus(72),
            ((yOrigem + yDestino) / 2).minus(16),
            paint
        )

        // define o bitmap da view como o novo bitmap com o caminho
        findViewById<ImageView>(R.id.image_view_mapa).setImageBitmap(novoMapa)
    }
}