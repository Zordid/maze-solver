package mazesolver.objects.algorithms

import mazesolver.objects.Grid
import mazesolver.objects.Point
import mazesolver.objects.State
import java.util.*

open class Dijkstra : SearchAlgorithm {

    open class Node(val marker: Grid.Marker, val x: Int, val y: Int) : Comparable<Node> {
        var totalDistance = Double.POSITIVE_INFINITY
        private val neighbours: ArrayList<Node> = ArrayList()

        init {
            if (marker == Grid.Marker.START)
                totalDistance = 0.0
        }

        fun addNeighbour(node: Node) = neighbours.add(node)

        fun getNeighbours(): List<Node> = neighbours.toList()

        override fun compareTo(other: Node): Int {
            return when {
                totalDistance == other.totalDistance -> 0
                totalDistance < other.totalDistance -> -1
                else -> +1
            }
        }
    }

    override fun execute(state: State): List<Point> {
        state.logger.info("Starting search")
        val nodes = createNodes(state.grid)
        createNodeGraph(nodes, state.grid)
        val unvisited = ArrayList<Node>(nodes)
        return findPath(unvisited, state.grid)
    }

    fun createNodes(grid: Grid): ArrayList<Node> {
        val nodes = ArrayList<Node>()
        for (x in 0..(grid.columns - 1)) {
            for (y in 0..(grid.rows - 1)) {
                nodes.add(createNode(grid.get(x, y), x, y))
            }
        }
        return nodes
    }

    fun Grid.getIndexFromPoint(x: Int, y: Int) = x * rows + y

    protected fun createNodeGraph(nodes: List<Node>, grid: Grid) {
        for (node in nodes) {
            if (node.x != 0) {
                node.addNeighbour(nodes[grid.getIndexFromPoint(node.x - 1, node.y)])
            }
            if (node.x < grid.columns - 1) {
                node.addNeighbour(nodes[grid.getIndexFromPoint(node.x + 1, node.y)])
            }
            if (node.y != 0) {
                node.addNeighbour(nodes[grid.getIndexFromPoint(node.x, node.y - 1)])
            }
            if (node.y < grid.rows - 1) {
                node.addNeighbour(nodes[grid.getIndexFromPoint(node.x, node.y + 1)])
            }
        }
    }

    protected fun findPath(unvisited: ArrayList<Node>, grid: Grid): List<Point> {
        val path = ArrayList<Point>()
        Collections.sort(unvisited)
        while (!unvisited.isEmpty() && unvisited.first().totalDistance != Double.POSITIVE_INFINITY) {
            val next = unvisited.first()
            visitNode(next, grid)
            if (next.marker == Grid.Marker.END) {
                traceback(next, path)
                break
            }
            unvisited.remove(next)
            Collections.sort(unvisited)
        }
        return path
    }

    protected fun traceback(node: Node, path: ArrayList<Point>) {
        if (node.marker == Grid.Marker.START) {
            return
        }
        var currentMinDistance = Double.POSITIVE_INFINITY
        var currentMinNode: Node? = null
        for (neighbour in node.getNeighbours()) {
            if (neighbour.totalDistance < currentMinDistance) {
                currentMinDistance = neighbour.totalDistance
                currentMinNode = neighbour
            }
        }
        if (currentMinNode == null) {
            throw(Throwable("currentMinNode should never be null!"))
        }
        if (node.marker != Grid.Marker.END) {
            path.add(Point(node.x, node.y))
        }
        traceback(currentMinNode, path)
    }

    protected fun visitNode(node: Node, grid: Grid) {
        val currentDistance = node.totalDistance + 1
        node.getNeighbours()
                .filter { it.marker != Grid.Marker.WALL && it.totalDistance > currentDistance }
                .forEach { it.totalDistance = currentDistance }
        if (grid.get(node.x, node.y) == Grid.Marker.END || grid.get(node.x, node.y) == Grid.Marker.START) {
            return
        }
        grid.set(node.x, node.y, Grid.Marker.VISITED)
    }

    override val name = "Dijkstra"

    open fun createNode(marker: Grid.Marker, x: Int, y: Int) = Node(marker, x, y)
}