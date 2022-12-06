// Gabriel Willian Bartmanovicz - 21234
// João Pedro Ferreira Barbosa - 21687

package br.unicamp.marte.dijkstra

// classe de dados relativa vértice,
// armazenando seu rótulo, se foi
// visitado e se está ativo
data class Vertice(
    val rotulo: String,
    var foiVisitado: Boolean = false,
    var estaAtivo: Boolean = false
)
