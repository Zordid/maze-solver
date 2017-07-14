package mazesolver.objects.ui

import mazesolver.objects.Grid
import mazesolver.objects.State
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.util.logging.Logger
import javax.swing.*

class Canvas(val state: State, val logger: Logger) : JPanel() {
    val popupMenu = JPopupMenu()
    var lastLoc: EventLocation? = null
    var over: EventLocation? = null
    var dragMarker: Grid.Marker? = null

    class EventLocation(e: MouseEvent, state: State) {
        val x: Int
        val y: Int
        val valid: Boolean

        init {
            val s = state.boxSize
            x = e.x / s
            y = e.y / s
            valid = (x in 0..state.grid.columns && y in 0..state.grid.rows)
        }

        override fun toString(): String {
            return "EventLocation(x=$x, y=$y, valid=$valid)"
        }
    }

    init {
        val self = this
        minimumSize = state.canvasDimension
        maximumSize = state.canvasDimension
        preferredSize = state.canvasDimension
        size = state.canvasDimension
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        addMouseMotionListener(object : MouseMotionListener {

            override fun mouseDragged(e: MouseEvent) {
                if (dragMarker == null) {
                    return
                }
                val eventLocation = EventLocation(e, state)
                if (eventLocation.x >= state.grid.columns || eventLocation.y >= state.grid.rows) {
                    return
                }
                if (eventLocation.x < 0 || eventLocation.y < 0) {
                    return
                }
                val dM = dragMarker!!
                state.grid.set(eventLocation.x, eventLocation.y, dM)
                repaint()
            }

            override fun mouseMoved(e: MouseEvent) {
                over = EventLocation(e, state)
                repaint()
            }
        })
        addMouseListener(object : MouseListener {

            override fun mouseClicked(e: MouseEvent) {
                val loc = EventLocation(e, state)
                lastLoc = loc
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(self, e.x, e.y)
                    return
                }
                if (!loc.valid) {
                    return
                }
                if (state.grid.get(loc.x, loc.y) == Grid.Marker.WALL) {
                    state.grid.set(loc.x, loc.y, Grid.Marker.DEFAULT)
                } else {
                    state.grid.set(loc.x, loc.y, Grid.Marker.WALL)
                }
                repaint()
            }

            override fun mousePressed(e: MouseEvent) {
                val eventLocation = EventLocation(e, state)
                dragMarker = Grid.Marker.WALL
                if (state.grid.get(eventLocation.x, eventLocation.y) == Grid.Marker.WALL) {
                    dragMarker = Grid.Marker.DEFAULT
                }
                over = null
            }

            override fun mouseReleased(e: MouseEvent) {
                dragMarker = null
            }

            override fun mouseEntered(e: MouseEvent) {
            }

            override fun mouseExited(e: MouseEvent) {
                over = null
                repaint()
            }
        })
        var menuItem = JMenuItem("Set to start")
        fun setMarker(marker: Grid.Marker) {
            lastLoc?.let {
                state.grid.set(it.x, it.y, marker)
            }
        }
        menuItem.addActionListener({
            run {
                logger.info("Setting to start - " + lastLoc)
                setMarker(Grid.Marker.START)
                repaint()
            }
        })
        popupMenu.add(menuItem)
        menuItem = JMenuItem("Set to end")
        menuItem.addActionListener({
            run {
                logger.info("Setting to end - " + lastLoc)
                setMarker(Grid.Marker.END)
                repaint()
            }
        })
        popupMenu.add(menuItem)
        // as right mouse click is explicitly handled in mouseclicked
        //self.componentPopupMenu = popupMenu
    }


    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        doDrawing(g)
    }

    private fun doDrawing(g: Graphics) {
        val g2d = g as Graphics2D
        val width = size.width
        val height = size.height
        g2d.color = state.canvasBG
        g2d.fillRect(0, 0, width, height)
        val columnWidth = state.canvasDimension.width / state.grid.columns
        val columnHeight = state.canvasDimension.height / state.grid.rows
        for (x in 0..state.grid.columns - 1) {
            for (y in 0..state.grid.rows - 1) {
                paintGrid(x, y, g2d)
            }
        }
        g2d.color = state.lineColor
        for (x in 1..state.grid.columns) {
            val xPos = x * columnWidth
            g2d.drawLine(xPos, 0, xPos, state.canvasDimension.height)
        }
        for (y in 1..state.grid.rows) {
            val yPos = y * columnHeight
            g2d.drawLine(0, yPos, state.canvasDimension.width, yPos)
        }
        if (over != null) {
            val o = over!!
            g2d.color = state.overColor
            val s = state.boxSize
            g2d.fillRect(o.x * s, o.y * s, s, s)
        }
    }

    private fun paintGrid(x: Int, y: Int, g2d: Graphics2D) {
        when (state.grid.get(x, y)) {
            Grid.Marker.WALL -> g2d.color = state.wallColor
            Grid.Marker.OVER -> g2d.color = state.overColor
            Grid.Marker.START -> g2d.color = state.startColor
            Grid.Marker.END -> g2d.color = state.endColor
            Grid.Marker.PATH -> g2d.color = state.pathColor
            Grid.Marker.VISITED -> g2d.color = state.visitedColor
            else -> return
        }
        val s = state.boxSize
        g2d.fillRect(x * s, y * s, s, s)
    }
}