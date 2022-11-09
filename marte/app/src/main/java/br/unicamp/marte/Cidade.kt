package br.unicamp.marte

class Cidade(
    nome: String? = "",
    x: Double? = 0,
    y: Double? = 0
) {
    val nome = nome
        get() = field
        set(value) {
            field = value
        }

    val x = x
        get() = field
        set(value) {
            field = value
        }

    val y = y
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