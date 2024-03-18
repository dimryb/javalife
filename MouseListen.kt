import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import kotlin.math.floor

class MouseListen(var world: SimpleGame) : MouseAdapter() {
    var select: Cell? = null
    override fun mouseClicked(e: MouseEvent) {
        val mouseClickX = floor(((e.x / world.dsize - world.dx - 1) / 5).toDouble()).toInt()
        val mouseClickY = floor(((e.y / world.dsize - world.dy - 1) / 5).toDouble()).toInt()
        val cells = world.world.cells.getCells()
        val cellMap = world.world.cells.getCellsMap()
        when (e.button) {
            1 -> if (cells.containsKey(cellMap[mouseClickX][mouseClickY].toString())) {
                select = cells[cellMap[mouseClickX][mouseClickY].toString()]
                world.selectgenom = select!!.genom
            }

            3 -> world.world.cells.createCell(mouseClickX, mouseClickY, world.selectgenom, world)
        }
    }
}
