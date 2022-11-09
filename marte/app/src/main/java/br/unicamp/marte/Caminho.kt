package br.unicamp.marte

class Caminho(
    cidadeOrigem: String? = "",
    cidadeDestino: String? = "",
    distancia: Int? = 0,
    tempo: Int? = 0,
    custo: Int? = 0
) {
    val cidadeOrigem = cidadeOrigem
        get() = field
        set(value) {
            field = value
        }

    val cidadeDestino = cidadeDestino
        get() = field
        set(value) {
            field = value
        }

    val distancia = distancia
        get() = field
        set(value) {
            field = value
        }

    val tempo = tempo
        get() = field
        set(value) {
            field = value
        }

    val custo = custo
        get() = field
        set(value) {
            field = value
        }

    override fun toString(): String {
        return nome + " " + x + " " + y
    }

    override fun equals(other: Cidade): Boolean {
        return nome.equals(other.nome)
    }
}