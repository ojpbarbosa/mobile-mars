// Gabriel Willian Bartmanovicz - 21234
// João Pedro Ferreira Barbosa - 21687

package br.unicamp.marte.dijkstra

import br.unicamp.marte.DadosCaminho

// classe de dados relativa aos dados,
// originais de cada vértice, mantendo
// a relação original dos dados dos caminhos
// entre os vértices
data class DadosOriginais(
    var verticePai: Int,
    val dados: DadosCaminho
)
