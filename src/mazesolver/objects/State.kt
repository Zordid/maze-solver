package mazesolver.objects

import mazesolver.objects.algorithms.SearchAlgorithm
import java.awt.Color
import java.awt.Dimension
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger
import javax.swing.SwingUtilities

class State(val logger: Logger) {
    val squaredSize: Int = 600
    val squaredGridCount: Int = 30
    val boxSize: Int = squaredSize / squaredGridCount
    val canvasDimension: Dimension = Dimension(squaredSize, squaredSize)
    val canvasBG: Color = Color.WHITE
    val lineColor: Color = Color(213, 222, 217)
    val wallColor: Color = Color(85, 98, 112)
    val startColor: Color = Color(255, 107, 107)
    val endColor: Color = Color(78, 205, 196)
    val overColor: Color = Color(194, 200, 193)
    val pathColor: Color = Color(199, 244, 100)
    val visitedColor: Color = Color(255, 255, 208)
    val grid: Grid = Grid(squaredGridCount, squaredGridCount)
    val stateChangeListeners: ArrayList<Runnable> = ArrayList()
    val statusChangeListeners: ArrayList<((String) -> Unit)> = ArrayList()
    var executor: ExecutorService = Executors.newFixedThreadPool(1)


    fun run(al: SearchAlgorithm) {
        val self = this
        val startTime: Long = System.currentTimeMillis()
        var distance = 0
        executor.submit({
            grid.clearPath()
            try {
                val solution = al.execute(self)
                for ((x, y) in solution) {
                    grid[x, y] = Grid.Marker.PATH
                }
                distance = solution.size
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            SwingUtilities.invokeLater({
                for (stateChangeListener in stateChangeListeners) {
                    stateChangeListener.run()
                }
                val endTime: Long = System.currentTimeMillis()
                for (statusListener in statusChangeListeners) {
                    statusListener("Time: %d ms. Distance: %d. Algorithm: %s".format(
                            endTime - startTime, distance, al.name))
                }
            })
        })
    }

    fun addChangeListener(runnable: Runnable) {
        stateChangeListeners.add(runnable)
    }

    fun addStatusListener(statusListener: (String) -> Unit) {
        statusChangeListeners.add(statusListener)
    }
}