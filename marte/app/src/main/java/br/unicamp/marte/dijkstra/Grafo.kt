// Gabriel Willian Bartmanovicz - 21234
// João Pedro Ferreira Barbosa - 21687

package br.unicamp.marte.dijkstra

import br.unicamp.marte.DadosCaminho
import java.util.*


// classe grafo, cujo construtor
// assume como parâmetros o número
// de vértices do mesmo e a matriz de
// adjacências já criada
class Grafo(
    numeroDeVertices: Int,
    val matrizDeAdjacencias: Array<Array<DadosCaminho>>
) {
    val vertices: Array<Vertice?> = arrayOfNulls(numeroDeVertices)
    val percurso: Array<DadosOriginais?> = arrayOfNulls(numeroDeVertices)

    // variáveis para o controle da busca do caminho
    var verticeAtual = 0
    var doInicioAteAtual = 0
    var quantosVertices = numeroDeVertices

    fun acharCaminho(inicioDoPercurso: Int, finalDoPercurso: Int): String {
        verticeAtual = 0
        doInicioAteAtual = 0

        for (i in 0 until quantosVertices)
        {
            percurso[i] = null
            vertices[i] = Vertice(i.toString())
        }

        vertices[inicioDoPercurso]!!.foiVisitado = true
        for (i in 0 until quantosVertices)
        {
            val dados = matrizDeAdjacencias[inicioDoPercurso][i]
            percurso[i] = DadosOriginais(inicioDoPercurso, dados)
        }

        for (i in 0 until quantosVertices)
        {
            val indiceDoMenor = obterMenor()

            if (indiceDoMenor >= 0)
            {
                val distanciaMinima = percurso[indiceDoMenor]!!.dados.distancia

                verticeAtual = indiceDoMenor
                doInicioAteAtual = distanciaMinima
                vertices[verticeAtual]!!.foiVisitado = true

                ajustarMenorCaminho()
            }
        }

        return exibirPercursos(inicioDoPercurso, finalDoPercurso)
    }

    private fun obterMenor(): Int {
        var distanciaMinima = Int.MAX_VALUE
        var indiceDaMinima = -1

        for (i in 0 until quantosVertices)
            if (!vertices[i]!!.foiVisitado && percurso[i]!!.dados.distancia < distanciaMinima) {
                distanciaMinima = percurso[i]!!.dados.distancia
                indiceDaMinima = i
            }

        return indiceDaMinima
    }

    private fun ajustarMenorCaminho() {
        for (i in 0 until quantosVertices)
            if (!vertices[i]!!.foiVisitado)
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
        var onde = finalDoPercurso
        val pilha = Stack<String>()
        var contador = 0

        while (onde != inicioDoPercurso){
            onde = percurso[onde]!!.verticePai

            pilha.push(vertices[onde]!!.rotulo)
            contador++
        }

        var resultado = ""
        while (!pilha.empty())
        {
            resultado += pilha.pop()
            if (!pilha.empty())
                resultado += " --> "
        }

        if (contador == 1 && percurso[finalDoPercurso]!!.dados.distancia == Int.MAX_VALUE)
            resultado = "Não há caminho"

        else
            resultado += " --> " + vertices[finalDoPercurso]!!.rotulo

        return resultado
    }
}