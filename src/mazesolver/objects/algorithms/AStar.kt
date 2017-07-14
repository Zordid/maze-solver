package mazesolver.objects.algorithms

import mazesolver.objects.Grid
import mazesolver.objects.Point
import java.util.*

class AStar : Dijkstra() {
    private var endPoint: Point? = null

    inner class Node(marker: Grid.Marker, x: Int, y: Int) : Dijkstra.Node(marker, x, y) {
        override var totalDistance: Double = Double.POSITIVE_INFINITY

        val projectedDistance: Double = Double.NEGATIVE_INFINITY
        get() {
            if (field > Double.NEGATIVE_INFINITY) {
                return field
            }
            if (this.totalDistance == Double.POSITIVE_INFINITY) {
                return this.totalDistance
            }
            // Haven't calculated projected distance yet, doing it now, and only once.
            val ep = endPoint ?: throw Exception("There must be an endpoint!")
            val manhattanDistance = Math.abs(this.x - ep.x) + Math.abs(this.y - ep.y)
            field = totalDistance + manhattanDistance

            return field
        }

        override fun compareTo(other: Dijkstra.Node): Int {
            val n = other as Node
            return when {
                this.projectedDistance == n.projectedDistance -> 0
                this.projectedDistance < n.projectedDistance -> -1
                else -> +1
            }
        }

        fun getNodeDistanceToEnd(node: Dijkstra.Node) : Double {
            if (endPoint == null) {
                throw Exception("There must be an endpoint!")
            }
            val ep = endPoint!!
            if (node.totalDistance == Double.POSITIVE_INFINITY) {
                return node.totalDistance
            }
            val manhattanDistance = Math.abs(node.x - ep.x) + Math.abs(node.y - ep.y)
            return node.totalDistance + manhattanDistance
        }
    }

    override fun createNodes(grid: Grid): ArrayList<Dijkstra.Node> {
        val nodes = ArrayList<Dijkstra.Node>()
        for (x in 0..(grid.columns - 1)) {
            for (y in 0..(grid.rows - 1)) {
                val marker = grid.get(x, y)
                if (marker == Grid.Marker.END) {
                    endPoint = Point(x, y)
                }
                nodes.add(Node(grid.get(x, y), x, y))
            }
        }
        if (endPoint == null) {
            throw Exception("There must be an endpoint!")
        }
        return nodes
    }

    override val name = "A*"

}