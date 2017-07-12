package mazesolver.objects.algorithms

import mazesolver.objects.Point
import mazesolver.objects.State

interface SearchAlgorithm {
    val name: String
    fun execute(state: State): List<Point>
}