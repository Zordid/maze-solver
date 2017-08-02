package mazesolver.objects

import kotlin.coroutines.experimental.buildIterator

class Grid(val columns: Int, val rows: Int) {
    private val grid = Array(columns, { Array(rows, { Marker.DEFAULT }) })
    var startPoint: Point = Point(0, 0)
    var endPoint: Point = Point(columns - 1, rows - 1)

    val coordinates: Iterator<Point> = buildIterator {
        for (x in 0..(columns - 1)) {
            for (y in 0..(rows - 1)) {
                yield(Point(x, y))
            }
        }
    }

    enum class Marker {
        DEFAULT, WALL, OVER, START, END, PATH, VISITED
    }

    init {
        grid[startPoint.x][startPoint.y] = Marker.START
        grid[endPoint.x][endPoint.y] = Marker.END
    }

    operator fun get(x: Int, y: Int) = grid[x][y]

    operator fun set(x: Int, y: Int, value: Marker) {
        val currentValue = grid[x][y]
        if (value == Marker.DEFAULT || value == Marker.WALL) {
            if (currentValue == Marker.START || currentValue == Marker.END) {
                return
            }
        }
        if (value == Marker.START) {
            grid[startPoint.x][startPoint.y] = Marker.DEFAULT
            startPoint = Point(x, y)
        }
        if (value == Marker.END) {
            grid[endPoint.x][endPoint.y] = Marker.DEFAULT
            endPoint = Point(x, y)
        }
        grid[x][y] = value
    }

    fun clearAll() {
        grid.forEach { row ->
            (0..row.size - 1)
                    .filter { row[it] != Marker.START && row[it] != Marker.END }
                    .forEach { row[it] = Marker.DEFAULT }
        }
    }

    fun clearPath() {
        grid.forEach { row ->
            (0..row.size - 1)
                    .filter { row[it] == Marker.PATH || row[it] == Marker.VISITED }
                    .forEach { row[it] = Marker.DEFAULT }
        }
    }

}