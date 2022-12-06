// Gabriel Willian Bartmanovicz - 21234
// João Pedro Ferreira Barbosa - 21687

package br.unicamp.marte

// classe de dados relativa
// à um movimento na busca
// por caminhos
data class Movimento(
    val cidadeOrigem: Int,
    val cidadeDestino : Int,
    val dados: DadosCaminho
)