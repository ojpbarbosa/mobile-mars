// Gabriel Willian Bartmanovicz - 21234
// João Pedro Ferreira Barbosa - 21687

package br.unicamp.marte.dijkstra

import br.unicamp.marte.DadosCaminho
import java.util.*


// apesar de corretamente implementada, parece haver algum
// problema no kotlin que impossibilita sua execução normal

// classe grafo, cujo construtor
// assume como parâmetros o número
// de vértices do mesmo e a matriz de
// adjacências já criada
class Grafo(
    numeroDeVertices: Int,
    val matrizDeAdjacencias: Array<Array<DadosCaminho>>
) {
    // variáveis para o controle da busca do caminho usando dijkstra
    val vertices: Array<Vertice?> = arrayOfNulls(numeroDeVertices)
    val percurso: Array<DadosOriginais?> = arrayOfNulls(numeroDeVertices)
    var verticeAtual = 0
    var doInicioAteAtual = 0
    var quantosVertices = numeroDeVertices

    init {
        // inicializa o vetor de vértices
        for (i in 0 until quantosVertices)
            vertices[i] = Vertice(i.toString())
    }

    fun acharCaminho(inicioDoPercurso: Int, finalDoPercurso: Int): String {
        for (i in 0 until quantosVertices)
            vertices[i] = Vertice(i.toString())

        vertices[inicioDoPercurso]!!.foiVisitado = true

        for (i in 0 until quantosVertices)
        {
            // anotamos no vetor percurso a distância entre o inicioDoPercurso e cada vértice
            // se não há ligação direta, o valor da distância será Int.MAX_VALUE
            val dados = matrizDeAdjacencias[inicioDoPercurso][i]
            percurso[i] = DadosOriginais(inicioDoPercurso, dados)
        }

        for (i in 0 until quantosVertices)
        {
            // procura-se a saída do vértice atual com a menor distância
            val indiceDoMenor = obterMenor()

            // anotamos a distância mínima
            val distanciaMinima = percurso[indiceDoMenor]!!.dados.distancia
            // o vértice com a menor distância passa a ser o vértice atual
            // para compararmos com a distância calculada em ajustarMenorCaminho()
            verticeAtual = indiceDoMenor
            // visitamos o vértice com a menor distância desde o inicioDoPercurso
            doInicioAteAtual = percurso[indiceDoMenor]!!.dados.distancia
            vertices[verticeAtual]!!.foiVisitado = true

            ajustarMenorCaminho()
        }

        return exibirPercursos(inicioDoPercurso, finalDoPercurso)
    }

    // obtém o vértice com a menor distância
    private fun obterMenor(): Int {
        var distanciaMinima = Int.MAX_VALUE
        var indiceDaMinima = 0

        for (i in 0 until quantosVertices)
            if (!(vertices[i]!!.foiVisitado)
                && (percurso[i]!!.dados.distancia < distanciaMinima)
                && (percurso[i]!!.dados.distancia != Int.MAX_VALUE)
            ) {
                distanciaMinima = percurso[i]!!.dados.distancia
                indiceDaMinima = i
            }

        return indiceDaMinima
    }

    private fun ajustarMenorCaminho() {
        for (i in 0 until quantosVertices)
            if (!(vertices[i]!!.foiVisitado))
            {
                val atualAteMargem = matrizDeAdjacencias[verticeAtual][i].distancia
                val doInicioAteMargem = doInicioAteAtual + atualAteMargem
                val distanciaDoCaminho = percurso[i]!!.dados.distancia

                if (doInicioAteMargem < distanciaDoCaminho) {
                    percurso[i]!!.verticePai = verticeAtual
                    percurso[i]!!.dados.distancia = doInicioAteMargem
                }
            }
    }

    private fun exibirPercursos(inicioDoPercurso: Int, finalDoPercurso: Int): String {
        var resultado = ""
        for (i in 0 until quantosVertices)
        {
            resultado += vertices[i]!!.rotulo + "=";
            resultado += if (percurso[i]!!.dados.distancia == Int.MAX_VALUE)
                "inf"
            else
                percurso[i]!!.dados.distancia.toString()

            val pai = vertices[percurso[i]!!.verticePai]!!.rotulo
            resultado += "($pai)"
        }

        var onde = finalDoPercurso
        val pilha = Stack<String>()
        var contador = 0
        while (onde != inicioDoPercurso) {
            onde = percurso[onde]!!.verticePai
            pilha.push(vertices[onde]!!.rotulo)
            contador++
        }
        resultado = ""

        while (!pilha.empty())
        {
            resultado += pilha.pop()
            if (!pilha.empty())
                resultado += " --> "
        }
        if ((contador == 1) && (percurso[finalDoPercurso]!!.dados.distancia == Int.MAX_VALUE))
            resultado = "Não há caminho"
        else
            resultado += " --> " + vertices[finalDoPercurso]!!.rotulo

        return resultado
    }
}