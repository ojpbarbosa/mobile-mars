// Gabriel Willian Bartmanovicz - 21234
// João Pedro Ferreira Barbosa - 21687

package br.unicamp.marte

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import br.unicamp.marte.dijkstra.Grafo
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import android.graphics.*
import androidx.core.graphics.drawable.toBitmap
import java.util.Stack
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {
    // o algoritmo selecionado por padrão é o recursivo
    var algoritmoSelecionado = Algoritmo.Recursivo

    // declaração das variáveis globais
    // relacionadas à interface e busca
    lateinit var cidades: Array<Cidade>
    lateinit var caminhos: Array<Caminho>

    lateinit var matrizDeAdjacencias: Array<Array<DadosCaminho>>

    lateinit var caminhosListViewAdapter: ArrayAdapter<String>
    lateinit var caminhoSelecionadoTextView: TextView
    lateinit var dadosCaminhoSelecionadoTextView: TextView
    lateinit var mapaImageView: ImageView

    val caminhosEncontrados = ArrayList<ArrayList<Movimento>>()

    var indiceCaminhoSelecionado = -1

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

        // atribuição das views referentes ao mapa e à interface
        mapaImageView = findViewById(R.id.image_view_mapa)
        caminhoSelecionadoTextView = findViewById(R.id.text_view_caminho_selecionado)
        dadosCaminhoSelecionadoTextView = findViewById(R.id.text_view_dados_caminho_selecionado)

        // desenho das cidades no mapa
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

        // atribuição do adaptador da list view
        caminhosListViewAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            ArrayList()
        )
        val caminhosListView: ListView = findViewById(R.id.list_view_caminhos)
        caminhosListView.adapter = caminhosListViewAdapter
        caminhosListView.setOnItemClickListener { _, _, i, _ ->
            indiceCaminhoSelecionado = i
            exibirCaminhoSelecionado()
        }

        // leitura e conversão do arquivo JSON de caminhos
        // em um array de objetos da classe Caminho
        caminhos = Gson()
            .fromJson(
                lerArquivo("CaminhoEntreCidadesMarte.json"),
                object : TypeToken<Array<Caminho>>() {}.type
            )

        instanciarMatrizDeAdjacencias()

        // exibe todos os caminhos da matriz de adjacências
        exibirTodosCaminhos()

        val buscarButton: Button = findViewById(R.id.button_buscar)

        // quando o botão de buscar for clicado,
        // achamos o caminho de acordo com o algoritmo selecionado
        buscarButton.setOnClickListener {
            // caso o usuário tente buscar um caminho entre a mesma cidade de origem e destino
            if (origemSpinner.selectedItemPosition == destinoSpinner.selectedItemPosition)
            {
                // é exibido um toast
                Toast.makeText(this, "As cidades de origem e de destino não podem ser as mesmas!", Toast.LENGTH_LONG).show()
                // as variáveis relacionadas à busca são redefinidas
                redefinirBusca()
            }

            else
            {
                // os índices das cidades de origem e
                // destino referentes às cidades selecionadas
                // equivalem à posição das mesmas como itens do spinner
                val cidadeOrigem = origemSpinner.selectedItemPosition
                val cidadeDestino = destinoSpinner.selectedItemPosition

                when (algoritmoSelecionado) {
                    Algoritmo.Recursivo -> {
                        // se o algoritmo selecionado for
                        // o recursivo, então o método referente
                        // a busca recursiva é chamado
                        acharCaminhosComRecursao(
                            cidadeOrigem,
                            cidadeDestino
                        )
                    }
                    Algoritmo.Dijkstra -> {
                        // se o algoritmo selecionado for
                        // dijkstra, então o método referente
                        // a busca recursiva é chamado
                        acharCaminhoComDijkstra(
                            cidadeOrigem,
                            cidadeDestino
                        )
                    }
                }
            }
        }
    }

    // método que instancia a matriz de adjacências novamente
    private fun instanciarMatrizDeAdjacencias() {
        // instância a matriz de adjacências
        matrizDeAdjacencias =
            Array(cidades.size) {
                Array(cidades.size) {
                    DadosCaminho(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
                }
            }

        caminhos.forEach {
            // para cada caminho, obtém-se o índice
            // da cidade de origem e destino através
            // de uma pesquisa binária
            val indiceOrigem = obterIndiceCidade(it.cidadeOrigem!!)
            val indiceDestino = obterIndiceCidade(it.cidadeDestino!!)

            // atribui-se à matriz os dados
            // correspondentes do caminho
            matrizDeAdjacencias[indiceOrigem][indiceDestino] =
                DadosCaminho(it.distancia!!, it.tempo!!, it.custo!!)
        }
    }

    // redefine, visualmente, a busca,
    // limpando a list view e as text views
    // e exibindo novamente todos os caminhos
    @SuppressLint("SetTextI18n")
    private fun redefinirBusca() {
        caminhosListViewAdapter.clear()
        caminhosListViewAdapter.notifyDataSetChanged()
        caminhoSelecionadoTextView.text = "Caminho selecionado: ---"
        dadosCaminhoSelecionadoTextView.text = "Dados caminho selecionado: ---"
        exibirTodosCaminhos()
    }

    // método para leitura de um arquivo
    // nos assets do aplicativo
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

    // busca usando dijkstra pelo
    // menor caminho existente
    // entre a cidade de origem e a
    // cidade de destino
    fun acharCaminhoComDijkstra(
        cidadeOrigem: Int,
        cidadeDestino: Int
    ) {
        // zera a matriz de adjacências
        instanciarMatrizDeAdjacencias()

        // instancia um novo objeto da classe grafo
        val grafo = Grafo(cidades.size, matrizDeAdjacencias)

        // busca pelo menor caminho através do algoritmo de dijsktra
        val menorCaminho = grafo.acharCaminho(cidadeOrigem, cidadeDestino)

        // limpa a list view
        // e adiciona o menor caminho retornado na list view
        caminhosListViewAdapter.clear()
        caminhosListViewAdapter.add(menorCaminho)
        caminhosListViewAdapter.notifyDataSetChanged()
    }

    // busca recursiva pelos caminhos
    // existentes entre a cidade de origem
    // e a cidade de destino
    fun acharCaminhosComRecursao(
        cidadeOrigem: Int,
        cidadeDestino: Int
    ) {
        // variáveis relacionadas à busca
        // recursiva
        var cidadeAtual = cidadeOrigem
        // pilha de movimentos
        val movimentos = Stack<Movimento>()
        // array contendo todos os caminhos encontrados, consistindo de pilhas de movimento
        val caminhosEncontrados = ArrayList<Stack<Movimento>>()

        // zera a matriz de adjacências
        instanciarMatrizDeAdjacencias()

        // função recursiva que não assume
        // parâmetros por estar dentro do escopo
        // da função que definiu as variáveis para a busca
        fun acharCaminho() {
            // for que percorre a matriz de adjacências, portanto, todas as cidades
            for (i in matrizDeAdjacencias.indices)
            {
                // verificando se há um caminho entre a cidade atual e a cidade i
                if (matrizDeAdjacencias[cidadeAtual][i].distancia != Int.MAX_VALUE)
                {
                    // se houver, é empilhado um novo movimento partindo da cidade
                    // atual para a cidade i, com os dados referentes da matriz de adjacências
                    movimentos.push(Movimento(cidadeAtual, i, matrizDeAdjacencias[cidadeAtual][i]))
                    cidadeAtual = i // define a cidade atual como i para iniciar uma nova
                                    // busca com parâmetros diferentes

                    // se chegamos ao destino, adicionamos a nossa pilha de movimentos
                    // ao array list de caminhos e desempilhamos nosso último movimento
                    // - backtracking -, para encontrarmos, dessa forma, todos os caminhos
                    // possíveis
                    cidadeAtual = if (cidadeAtual == cidadeDestino) {
                        caminhosEncontrados.add(movimentos.clone() as Stack<Movimento>)
                        movimentos.pop().cidadeOrigem
                    } else {
                        // caso contrário, é feita uma nova busca com a cidade atual
                        // valendo i
                        acharCaminho()
                        // após isso, é desempilhado um movimento cuja cidade de origem
                        // é atribuída à cidade atual, iniciando novamente a busca
                        movimentos.pop().cidadeOrigem
                    }
                }
            }
        }

        // chamada 0 da função recursiva
        // para achar os caminhos
        acharCaminho()

        // limpa o array de caminhos encontrados
        this.caminhosEncontrados.clear()
        // para cada caminho encontrado na busca recursiva
        caminhosEncontrados.forEach {
            // cria-se um vetor de movimentos relativo ao caminho
            val movimentosCaminho = ArrayList<Movimento>()
            // desempilhamos a pilha de movimentos no vetor
            while (!it.empty())
                movimentosCaminho.add(it.pop())

            // e voltamos os movimentos a sua ordem
            // original invertendo o vetor
            movimentosCaminho.reverse()

            // após isso, o vetor com os movimentos
            // é adicionado ao nosso array de caminhos encontrados
            this.caminhosEncontrados.add(movimentosCaminho)
        }

        // se pelo menos um caminho foi encontrado
        if (caminhosEncontrados.size > 0) {
            indiceCaminhoSelecionado = 0
            exibirCaminhosEncontrados()
        }

        else {
            Toast.makeText(this, "Nenhum caminho foi encontrado!", Toast.LENGTH_LONG).show()
            redefinirBusca()
        }
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
        paint.textSize = resources.displayMetrics.density.times(24)

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
            canvas.drawCircle(x, y, 14f, paint)
        }

        // define o bitmap da image view do mapa como o novo bitmap com as cidades
        mapaImageView.setImageBitmap(mapaComCidades)
    }

    private fun limparMapa() {
        mapaImageView.setImageBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.mapa_de_marte)
        ) // redefine o bitmap da image view do mapa
          // como o bitmap do arquivo original

        // desenha somente as cidades novamente
        desenharCidades()
    }

    @SuppressLint("SetTextI18n")
    private fun exibirTodosCaminhos() {
        limparMapa()

        // para cada caminho
        caminhos.forEach {
            // obtém-se os índices de origem e destino e indexa-se
            // o vetor de cidades para obter, como resultado final,
            // os objetos referentes às cidades
            val cidadeOrigem = cidades[obterIndiceCidade(it.cidadeOrigem!!)]
            val cidadeDestino = cidades[obterIndiceCidade(it.cidadeDestino!!)]

            // desenha o caminho entre as cidades de origem e destino
            desenharCaminho(cidadeOrigem, cidadeDestino, Color.BLACK)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun exibirCaminhosEncontrados() {
        limparMapa()

        caminhoSelecionadoTextView.text = "Caminho selecionado: ---"
        dadosCaminhoSelecionadoTextView.text = "Dados caminho selecionado: ---"

        // limpa a list view de caminhos
        caminhosListViewAdapter.clear()
        // para cada caminho encontrado
        caminhosEncontrados.forEach {
            var caminhoString = ""
            // concatena-se na string do caminho
            // o nome da cidade de origem de cada movimento
            for (movimentoCaminho in it)
                caminhoString += cidades[movimentoCaminho.cidadeOrigem].nome + " > "
            // e o nome da cidade de destino
            caminhoString += cidades[it.last().cidadeDestino].nome

            // adiciona a string do caminho na list view
            caminhosListViewAdapter.add(caminhoString)
        }

        // atualiza a list view
        caminhosListViewAdapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    private fun exibirCaminhoSelecionado() {
        // se o caminho selecionado for válido
        if (indiceCaminhoSelecionado != -1) {
            limparMapa()

            // variáveis referentes
            // aos dados do caminho selecionado
            val movimentosCaminho = caminhosEncontrados[indiceCaminhoSelecionado]
            var distanciaCaminho = 0
            var tempoCaminho = 0
            var custoCaminho = 0

            var caminhoString = ""
            movimentosCaminho.forEach {
                // obtém a cidade de origem e de destino
                val cidadeOrigem = cidades[it.cidadeOrigem]
                val cidadeDestino = cidades[it.cidadeDestino]

                // concatena-se na string do caminho o nome da cidade de origem do movimento
                caminhoString += cidadeOrigem.nome + " > "

                // soma aos valores previamente
                // registrados a distância, o tempo
                // e o custo do movimento
                val dados = it.dados
                distanciaCaminho += dados.distancia
                tempoCaminho += dados.tempo
                custoCaminho += dados.custo

                // desenha o caminho entre a cidade de origem e de destino
                desenharCaminho(cidadeOrigem, cidadeDestino, Color.RED)
            }

            // concatena-se na string do caminho o nome da cidade de destino
            caminhoString += cidades[movimentosCaminho.last().cidadeDestino].nome
            // atribui à text view a string do caminho
            caminhoSelecionadoTextView.text = caminhoString
            // e à text view dos dados seus respectivos valores
            dadosCaminhoSelecionadoTextView.text =
                "Distância: $distanciaCaminho\t\tTempo: $tempoCaminho\t\tCusto: $custoCaminho"
        }
    }

    private fun desenharCaminho(cidadeOrigem: Cidade, cidadeDestino: Cidade, cor: Int) {
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
        paint.color = cor
        paint.strokeWidth = resources.displayMetrics.density
            .times(2)

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

        // define o bitmap da view como o novo bitmap com o caminho
        mapaImageView.setImageBitmap(novoMapa)
    }
}
