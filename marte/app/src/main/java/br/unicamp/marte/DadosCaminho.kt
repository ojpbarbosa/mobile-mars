package br.unicamp.marte

// classe de dados relativa
// ao caminho, sem as
// cidades de origem e destino
data class DadosCaminho(
    var distancia: Int,
    val tempo: Int,
    val custo: Int
)
