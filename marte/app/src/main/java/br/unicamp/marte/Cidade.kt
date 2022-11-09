package br.unicamp.marte

import com.google.gson.annotations.SerializedName

class Cidade(
    @SerializedName("Nome") val nome: String? = "",
    @SerializedName("X") val x: Double? = 0.0,
    @SerializedName("Y") val y: Double? = 0.0
) {
    override fun toString(): String {
        return "$nome $x $y"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cidade

        if (nome != other.nome) return false
        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nome?.hashCode() ?: 0
        result = 31 * result + (x?.hashCode() ?: 0)
        result = 31 * result + (y?.hashCode() ?: 0)
        return result
    }
}