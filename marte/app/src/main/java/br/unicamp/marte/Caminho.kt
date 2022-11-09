package br.unicamp.marte

import com.google.gson.annotations.SerializedName

class Caminho(
    cidadeOrigem: String? = "",
    cidadeDestino: String? = "",
    distancia: Int? = 0,
    tempo: Int? = 0,
    custo: Int? = 0
) {
    @SerializedName("CidadeOrigem")
    val cidadeOrigem = cidadeOrigem
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("CidadeDestino")
    val cidadeDestino = cidadeDestino
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("Distancia")
    val distancia = distancia
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("Tempo")
    val tempo = tempo
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("Custo")
    val custo = custo
        get() = field
        set(value) {
            field = value
        }

    override fun toString(): String {
        return cidadeOrigem + " " + cidadeDestino + " " + distancia + " " + tempo + " " + custo
    }

    override fun equals(caminho: Caminho): Boolean {
        return (cidadeOrigem + cidadeDestino)
            .equals(caminho.cidadeOrigem + caminho.cidadeDestino)
    }
}