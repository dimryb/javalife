import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import kotlin.math.floor

class MouseListen(var world: SimpleGame) : MouseAdapter() {
    var select: Cell? = null
    override fun mouseClicked(e: MouseEvent) {
        world.mouseclickx = floor(((e.x / world.dsize - world.dx - 1) / 5).toDouble()).toInt()
        world.mouseclicky = floor(((e.y / world.dsize - world.dy - 1) / 5).toDouble()).toInt()
        when (e.button) {
            1 -> if (world.cells.containsKey(world.cellmap[world.mouseclickx][world.mouseclicky].toString())) {
                select = world.cells[world.cellmap[world.mouseclickx][world.mouseclicky].toString()]
                world.selectgenom = select!!.genom
            }

            3 -> world.createCell()
        }
    }
}
