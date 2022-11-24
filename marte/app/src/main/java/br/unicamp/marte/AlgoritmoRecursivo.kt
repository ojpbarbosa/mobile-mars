package br.unicamp.marte

class AlgoritmoRecursivo (adjacencias: Array<Array<DadosCaminho?>>, cidadeInicio: Int, cidadeDestino: Int) {
    var adjacencias: Array<Array<DadosCaminho?>> = adjacencias;
    var cidadeInicio: Int = cidadeInicio
    var cidadeDestino: Int = cidadeDestino
//    var visitados = mk.zeros<Int>(adjacencias.size, adjacencias.size


    fun executar(x: Int, y: Int, visitados: Array<Array<Boolean>>, caminho: Array<Int>, distanciaPercorrida: Int): Array<Int> {
        if (x > 0 && x < adjacencias.size && y > 0 && y < adjacencias.size) {
            if (adjacencias[x][y] == null) {
                visitados[x][y] = true
            }
            else {
                if (x == cidadeDestino) {
                    return caminho
                }
                else {
                    if (!visitados[x][y]) {
                        distanciaPercorrida += adjacencias[x][y]!!.distancia
                        visitados[x][y] = true
                        y = x
                        x = 0
                    }


                }
            }
        }
    }
}