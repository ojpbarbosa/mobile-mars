package br.unicamp.marte

//https://github.com/alexhwoods/alexhwoods.com/blob/master/kotlin-algorithms/src/main/kotlin/com/alexhwoods/graphs/algorithms/Dijkstra.kt

class AlgoritmoDeDijkstra() {
    // Todo("Revisar nome de variáveis")
    fun <T> dijkstra (adjacencias: Grafo<T>, inicio: T): Map<T, T?> {
        val s: MutableSet<T> = mutableSetOf()

        val delta = adjacencias.vertices.map { it to Int.MAX_VALUE }.toMap().toMutableMap()

        // A distância do vértice fonte dele mesmo é sempre 0
        delta[inicio] = 0

        val anterior: MutableMap<T, T?> = adjacencias.vertices.map { it to null }.toMap().toMutableMap()

        while (s != adjacencias.vertices) {
            val v: T = delta
                .filter { !s.contains(it.key) }
                .minBy { it.value }!!
                .key

            adjacencias.arestas.getValue(v).minus(s).forEach { vizinho ->
                val novoCaminho = delta.getValue(v) + adjacencias.pesos.getValue(Pair(v, vizinho))

                if (novoCaminho < delta.getValue(vizinho)) {
                    delta[vizinho] = novoCaminho
                    anterior[vizinho] = v
                }
            }

            s.add(v);
        }

        return anterior.toMap()
    }

    fun <T> menorCaminho(arvoreMenorCaminho: Map<T, T?>, inicio: T, fim: T): List<T> {
        fun caminhoPara(inicio: T, fim: T): List<T> {
            if (arvoreMenorCaminho[fim] == null) return listOf(fim)
            return listOf(caminhoPara(inicio, arvoreMenorCaminho[fim]!!), listOf(fim)).flatten()
        }

        return caminhoPara(inicio, fim)
    }
}