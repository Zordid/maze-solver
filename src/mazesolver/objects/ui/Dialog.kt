package mazesolver.objects.ui

import mazesolver.dialogs.DialogContainer
import mazesolver.objects.State
import mazesolver.objects.algorithms.AStar
import mazesolver.objects.algorithms.Dijkstra
import java.awt.Color
import java.util.logging.Logger
import javax.swing.BoxLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.JPanel
import javax.swing.border.LineBorder

class Dialog(state: State, logger: Logger) : DialogContainer() {

    init {
        logger.info("Initializing dialog.")
        graphPanelContainer.border = LineBorder(Color.DARK_GRAY, 1)
        graphPanelContainer.layout = BoxLayout(graphPanelContainer, BoxLayout.LINE_AXIS)
        graphPanelContainer.add(Canvas(state, logger))
        val picks = arrayOf("Dijkstra", "A*")
        algorithmPicker.model = DefaultComboBoxModel(picks)
        setStatus("Started.")
        findPathButton.addActionListener {
            val alg: String = algorithmPicker.selectedItem as String
            setStatus("Finding solution.")
            when(alg) {
                picks[0] -> state.run(Dijkstra())
                picks[1] -> state.run(AStar())
            }
        }
        clearAllButton.addActionListener {
            state.grid.clearAll()
            refresh()
        }
        state.addChangeListener(Runnable({refresh()}))
        state.addStatusListener({status: String ->
            setStatus(status)
        })
    }

    fun setStatus(status: String) {
        statusLabel.text = status
    }

    fun getGraphContainer(): JPanel {
        return mainContainer
    }

    fun refresh() {
        mainContainer.invalidate()
        mainContainer.repaint()
    }

}