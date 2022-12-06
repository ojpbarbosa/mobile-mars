// Gabriel Willian Bartmanovicz - 21234
// João Pedro Ferreira Barbosa - 21687

package br.unicamp.marte

import com.google.gson.annotations.SerializedName

class Caminho(
    // atributos serializáveis para
    // atribuição a partir de arquivos
    @SerializedName("CidadeOrigem") val cidadeOrigem: String? = "",
    @SerializedName("CidadeDestino") val cidadeDestino: String? = "",
    @SerializedName("Distancia") val distancia: Int? = 0,
    @SerializedName("Tempo") val tempo: Int? = 0,
    @SerializedName("Custo") val custo: Int? = 0
) {
    // implementação do tipo override do método obrigatório toString
    override fun toString(): String {
        return "$cidadeOrigem $cidadeDestino $distancia $tempo $custo"
    }

    // implementação do tipo override do método obrigatório equals
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Caminho

        if (cidadeOrigem != other.cidadeOrigem) return false
        if (cidadeDestino != other.cidadeDestino) return false
        if (distancia != other.distancia) return false
        if (tempo != other.tempo) return false
        if (custo != other.custo) return false

        return true
    }

    // implementação do tipo override do método obrigatório hashCode
    override fun hashCode(): Int {
        var result = cidadeOrigem?.hashCode() ?: 0
        result = 31 * result + (cidadeDestino?.hashCode() ?: 0)
        result = 31 * result + (distancia ?: 0)
        result = 31 * result + (tempo ?: 0)
        result = 31 * result + (custo ?: 0)
        return result
    }
}