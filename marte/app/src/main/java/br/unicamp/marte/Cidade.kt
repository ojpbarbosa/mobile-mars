package br.unicamp.marte

import com.google.gson.annotations.SerializedName

class Cidade(
    nome: String? = "",
    x: Double? = 0,
    y: Double? = 0
) {
    @SerializedName("Nome")
    val nome = nome
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("X")
    val x = x
        get() = field
        set(value) {
            field = value
        }

    @SerializedName("Y")
    val y = y
        get() = field
        set(value) {
            field = value
        }

    override fun toString(): String {
        return nome + " " + x + " " + y
    }

    override fun equals(cidade: Cidade): Boolean {
        return nome.equals(other.nome)
    }
}