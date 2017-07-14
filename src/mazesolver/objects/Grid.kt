package mazesolver.objects

class Grid(val columns: Int, val rows: Int) {
    private val grid = Array(columns, { Array(rows, { Marker.DEFAULT }) })
    private var startPos: MarkerPos? = null
    private var endPos: MarkerPos? = null

    enum class Marker {
        DEFAULT, WALL, OVER, START, END, PATH, VISITED
    }

    private data class MarkerPos(val x: Int, val y: Int)

    init {
        set(0, 0, Marker.START)
        set(columns - 1, rows - 1, Marker.END)
    }

    fun get(x: Int, y: Int) = grid[x][y]

    fun set(x: Int, y: Int, value: Marker) {
        val currentValue = grid[x][y]
        if (value == Marker.DEFAULT || value == Marker.WALL) {
            if (currentValue == Marker.START || currentValue == Marker.END) {
                return
            }
        }
        if (value == Marker.START) {
            startPos?.let { (x1, y1) ->
                grid[x1][y1] = Marker.DEFAULT
            }
            startPos = MarkerPos(x, y)
        }
        if (value == Marker.END) {
            endPos?.let { (x1, y1) ->
                grid[x1][y1] = Marker.DEFAULT
            }
            endPos = MarkerPos(x, y)
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