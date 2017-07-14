package mazesolver

import mazesolver.objects.State
import mazesolver.objects.ui.Dialog
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.util.logging.Logger
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    SwingUtilities.invokeLater { run() }
}

private val logger = Logger.getLogger("DialogUI")

fun run() {
    val frame: JFrame = JFrame("Maze Solver")
    val state = State(logger)
    val dialog = Dialog(state, logger)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isResizable = false
    val container: Container = frame.contentPane
    container.add(dialog.getGraphContainer(), BorderLayout.CENTER)
    frame.minimumSize = Dimension(state.squaredSize, 200)
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isVisible = true
}
