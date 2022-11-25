package br.unicamp.marte

class AlgoritmoRecursivo (adjacencias: Array<Array<DadosCaminho?>>) {
    var adjacencias: Array<Array<DadosCaminho?>> = adjacencias
//    var visitados = mk.zeros<Int>(adjacencias.size, adjacencias.size


    fun executar(cidadeOrigem: Int, cidadeDestino: Int): Array<Int> {

        var caminho = ArrayList<Int>()
        var distanciaPercorrida: Int = 0

        fun acharCaminho(x: Int, y: Int, distancia: Int) {
            if (y == cidadeDestino) {
                distanciaPercorrida
            }
            if (adjacencias[x][y] != null) {
                distancia
                acharCaminho(y, 0)
            }
            else {
                acharCaminho(x + 1, y)
            }
        }

        acharCaminho(0, cidadeOrigem)

        return caminho;

//        fun acharCaminho(x: Int, y: Int, visitados: Array<Array<Boolean>>, caminho: Array<Int>, distanciaPercorrida: Int) {}
//
//        if (x >= 0 && x < adjacencias.size && y >= 0 && y < adjacencias.size) {
//            if (adjacencias[x][y] == null) {
//                visitados[x][y] = true
//            }
//            else {
//                if (x == cidadeDestino) {
//                    return caminho
//                }
//                else {
//                    if (!visitados[x][y]) {
//                        distanciaPercorrida += adjacencias[x][y]!!.distancia
//                        visitados[x][y] = true
//                        y = x
//                        x = 0
//                    }
//
//
//                }
//            }
//        }
    }
}