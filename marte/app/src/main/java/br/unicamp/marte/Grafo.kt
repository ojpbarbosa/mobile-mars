package br.unicamp.marte

fun <T> List<Pair<T, T>>.pegarValorUnicoDosPares(): Set<T> = this
    .map { (a, b) -> listOf(a, b) }
    .flatten()
    .toSet()

fun <T> List<Pair<T, T>>.pegarValorUnicoDosPares(predicate: (T) -> Boolean): Set<T> = this
    .map { (a, b) -> listOf(a, b) }
    .flatten()
    .filter(predicate)
    .toSet()

data class Grafo<T>(
    val vertices: Set<T>,
    val arestas: Map<T, Set<T>>,
    val pesos: Map<Pair<T, T>, Int>
) {
    constructor(pesos: Map<Pair<T, T>, Int>): this(
        vertices = pesos.keys.toList().pegarValorUnicoDosPares(),
        arestas = pesos.keys
            .groupBy { it.first }
            .mapValues { it.value.pegarValorUnicoDosPares { x -> x !== it.key } }
            .withDefault { emptySet() },
        pesos = pesos
    )
}