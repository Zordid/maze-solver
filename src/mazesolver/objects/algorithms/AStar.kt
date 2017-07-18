package mazesolver.objects.algorithms

import mazesolver.objects.Grid
import mazesolver.objects.Point
import mazesolver.objects.State

class AStar : Dijkstra() {
    private var endPoint: Point = Point(0, 0)

    inner class Node(marker: Grid.Marker, x: Int, y: Int) : Dijkstra.Node(marker, x, y) {

        val projectedDistance: Double = Double.NEGATIVE_INFINITY
            get() {
                if (field > Double.NEGATIVE_INFINITY) {
                    return field
                }
                if (this.totalDistance == Double.POSITIVE_INFINITY) {
                    return this.totalDistance
                }
                // Haven't calculated projected distance yet, doing it now, and only once.
                val manhattanDistance = Math.abs(this.x - endPoint.x) + Math.abs(this.y - endPoint.y)
                field = totalDistance + manhattanDistance

                return field
            }

        override fun compareTo(other: Dijkstra.Node): Int {
            val n = other as Node
            return when {
                projectedDistance == n.projectedDistance -> 0
                projectedDistance < n.projectedDistance -> -1
                else -> +1
            }
        }

    }

    override fun createNode(marker: Grid.Marker, x: Int, y: Int): Dijkstra.Node = Node(marker, x, y)

    override fun execute(state: State): List<Point> {
        endPoint = state.grid.endPoint
        return super.execute(state)
    }

    override val name = "A*"
}