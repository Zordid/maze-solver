package mazesolver.objects.algorithms

import mazesolver.objects.Grid
import mazesolver.objects.Point
import mazesolver.objects.State
import java.util.*

open class Dijkstra : SearchAlgorithm {

    open class Node(val marker: Grid.Marker, val x: Int, val y: Int) : Comparable<Node> {
        open var totalDistance = Double.POSITIVE_INFINITY
        private val edges: ArrayList<Node> = ArrayList()

        fun add(node: Node) {
            edges.add(node)
            Collections.sort(edges)
        }

        override fun compareTo(other: Node): Int {
            if (other.totalDistance == totalDistance) {
                return 0
            }
            return if (totalDistance < other.totalDistance) -1 else 1
        }

        fun getEdges(): List<Node> {
            return ArrayList(edges)
        }
    }

    override fun execute(state: State): List<Point> {
        state.getLogger().info("Starting search")
        val nodes = createNodes(state.grid)
        val startNode = createNodeGraph(nodes, state.grid) ?: return ArrayList<Point>()
        val unvisited = ArrayList<Node>(nodes)
        return findPath(startNode, unvisited, state.grid)
    }

    protected open fun createNodes(grid: Grid): ArrayList<Node> {
        val nodes = ArrayList<Node>()
        for (x in 0..(grid.columns - 1)) {
            for (y in 0..(grid.rows - 1)) {
                nodes.add(Node(grid.get(x, y), x, y))
            }
        }
        return nodes
    }

    protected fun getIndexFromPoint(point: Point, grid: Grid): Int {
        return point.x * grid.rows + point.y
    }

    protected fun createNodeGraph(nodes: ArrayList<Node>, grid: Grid): Node? {
        var start: Node? = null
        for (i in 0..nodes.size - 1) {
            val node = nodes[i]
            if (node.x != 0) {
                node.add(nodes[getIndexFromPoint(Point(node.x - 1, node.y), grid)])
            }
            if (node.x < grid.columns - 1) {
                node.add(nodes[getIndexFromPoint(Point(node.x + 1, node.y), grid)])
            }
            if (node.y != 0) {
                node.add(nodes[getIndexFromPoint(Point(node.x, node.y - 1), grid)])
            }
            if (node.y < grid.rows - 1) {
                node.add(nodes[getIndexFromPoint(Point(node.x, node.y + 1), grid)])
            }
            if (node.marker == Grid.Marker.START) {
                start = node
            }
        }
        return start
    }

    protected fun findPath(startNode: Node, unvisited: ArrayList<Node>, grid: Grid): List<Point> {
        val path = ArrayList<Point>()
        startNode.totalDistance = 0.0
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
        for (edge in node.getEdges()) {
            if (edge.totalDistance < currentMinDistance) {
                currentMinDistance = edge.totalDistance
                currentMinNode = edge
            }
        }
        if (currentMinNode == null) {
            throw(Throwable("currentMindNode should never be null!"))
        }
        if (node.marker != Grid.Marker.END) {
            path.add(Point(node.x, node.y))
        }
        traceback(currentMinNode, path)
    }

    protected fun visitNode(node: Node, grid: Grid) {
        val currentDistance = node.totalDistance + 1
        for (edge in node.getEdges()) {
            if (edge.marker == Grid.Marker.WALL) {
                continue
            }
            if (edge.totalDistance > currentDistance) {
                edge.totalDistance = currentDistance
            }
        }
        if (grid.get(node.x, node.y) == Grid.Marker.END || grid.get(node.x, node.y) == Grid.Marker.START) {
            return
        }
        grid.set(node.x, node.y, Grid.Marker.VISITED)
    }

    override val name = "Dijkstra"
}