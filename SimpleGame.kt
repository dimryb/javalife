import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SimpleGame : JPanel(), ActionListener, KeyListener {
    private var lulz = 0
    private var display = 0
    var daynight = 2f //смена дня и ночи
    private var time = false
    var dsize = 0.6f //движение и увеличение дисплея
    var dx = 0
    var dy = 0
    var selectgenom = Array(10) { IntArray(6) }
    val widthMap = 512
    val heightMap = 512
    private val timer: Timer // Таймер для обновления экрана
    val world = World(widthMap, heightMap)

    init {
        addMouseListener(MouseListen(this))
        addKeyListener(this)
        setFocusable(true)
        setFocusTraversalKeysEnabled(false)
        timer = Timer(50, this) // Тут создаем таймер
        timer.start() // В этой строчке его запускаем
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        paintFood(g, widthMap, heightMap)
        paintCells(g)
        energyRedistribution(g)
        printInfoPanel(g)
    }

    private fun printInfoPanel(g: Graphics) {
        g.color = Color.black //время суток
        g.fillRect(1200, 0, 1000, 1200)
        printTimeInfo(g)
        paintGenomInfo(g)
    }

    private fun printTimeInfo(g: Graphics) {
        g.font = Font("Arial", Font.PLAIN, 20)
        g.color = Color.white
        g.drawString("таймер: $lulz освещенность: $daynight", 1210, 30)
    }

    private fun paintGenomInfo(g: Graphics) {
        if (selectgenom.size != 0) {
            var y = 0
            for (i in selectgenom) {
                y += 1
                var x = 0
                for (j in i) {
                    x += 1
                    if (x == 6) {
                        x += 2
                    }
                    if (x == 1) {
                        when (j) {
                            0 -> {
                                g.color = Color(128, 0, 128)
                                g.fillRect(1215, 15 + y * 30, 4, 4)
                            }

                            1 -> {
                                g.color = Color(128, 255, 251)
                                g.drawRect(1215, 15 + y * 30, 4, 6)
                            }

                            2 -> {
                                g.color = Color.GREEN
                                g.fillRect(1215, 15 + y * 30, 4, 6)
                            }

                            3 -> {
                                g.color = Color.yellow
                                g.drawOval(1215, 15 + y * 30, 6, 6)
                            }

                            4 -> {
                                g.color = Color.red
                                g.fillOval(1215, 15 + y * 30, 6, 6)
                                g.drawOval(1215, 15 + y * 30, 6, 6)
                            }
                        }
                        g.color = Color.white
                    } else {
                        g.drawString(j.toString(), 1210 + x * 20, 30 + y * 30)
                    }
                }
            }
        }
    }

    private fun energyRedistribution(g: Graphics) {
        g.color = Color.lightGray
        val dSize = dsize.toDouble()
        val dxPlus2 = (dx + 2).toDouble()
        val dyPlus2 = (dy + 2).toDouble()

        val cells = world.cells.getCells()
        cells.values.forEach { cell1 ->
            cell1.relations
                .filter { cells.keys.contains(it) }
                .mapNotNull { cells[it] }
                .filter { abs((cell1.x + cell1.y - it.x - it.y).toDouble()) <= 2 }
                .forEach { cell2 ->
                    g.drawLine(
                        ((cell1.x * 5 + dxPlus2) * dSize).roundToInt(),
                        ((cell1.y * 5 + dyPlus2) * dSize).roundToInt(),
                        ((cell2.x * 5 + dxPlus2) * dSize).roundToInt(),
                        ((cell2.y * 5 + dyPlus2) * dSize).roundToInt()
                    )
                }
        }
    }

    private fun paintCells(g: Graphics) {
        val cells = world.cells.getCells()
        for (cell in cells.values) {
            when (display) {
                0 -> when (cell.type) {
                    0 -> {
                        g.color = Color(128, 100, 0)
                        g.fillRect(
                            ((cell.x * 5 + 1 + dx) * dsize).roundToInt(),
                            ((cell.y * 5 + 1 + dy) * dsize).roundToInt(),
                            (3 * dsize).roundToInt(),
                            (3 * dsize).roundToInt()
                        )
                    }

                    1 -> {
                        g.color = Color(128, 255, 251)
                        if (cell.direction < 2) {
                            g.drawRect(
                                ((cell.x * 5 + 1 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + dy) * dsize).roundToInt(),
                                (3 * dsize).roundToInt(),
                                (5 * dsize).roundToInt()
                            )
                        } else {
                            g.drawRect(
                                ((cell.x * 5 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + 1 + dy) * dsize).roundToInt(),
                                (5 * dsize).roundToInt(),
                                (3 * dsize).roundToInt()
                            )
                        }
                    }

                    2 -> {
                        g.color = Color.GREEN
                        if (cell.direction < 2) {
                            g.fillRect(
                                ((cell.x * 5 + 1 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + dy) * dsize).roundToInt(),
                                (3 * dsize).roundToInt(),
                                (5 * dsize).roundToInt()
                            )
                        } else {
                            g.fillRect(
                                ((cell.x * 5 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + 1 + dy) * dsize).roundToInt(),
                                (5 * dsize).roundToInt(),
                                (3 * dsize).roundToInt()
                            )
                        }
                    }

                    3 -> {
                        g.color = Color.yellow
                        g.drawOval(
                            ((cell.x * 5 + dx) * dsize).roundToInt(),
                            ((cell.y * 5 + dy) * dsize).roundToInt(),
                            (5 * dsize).roundToInt(),
                            (5 * dsize).roundToInt()
                        )
                    }

                    4 -> {
                        g.color = Color.red
                        g.fillOval(
                            ((cell.x * 5 + dx) * dsize).roundToInt(),
                            ((cell.y * 5 + dy) * dsize).roundToInt(),
                            (5 * dsize).roundToInt(),
                            (5 * dsize).roundToInt()
                        )
                        g.drawOval(
                            ((cell.x * 5 + dx) * dsize).roundToInt(),
                            ((cell.y * 5 + dy) * dsize).roundToInt(),
                            (5 * dsize).roundToInt(),
                            (5 * dsize).roundToInt()
                        )
                    }
                }

                1 -> {
                    g.color = Color(
                        (abs(cell.parent_id.toDouble()) % 256).toInt(),
                        (abs((cell.parent_id * 4).toDouble()) % 256).toInt(),
                        (abs((cell.parent_id * 16).toDouble()) % 256).toInt()
                    )
                    when (cell.type) {
                        0 -> g.fillRect(
                            ((cell.x * 5 + 1 + dx) * dsize).roundToInt(),
                            ((cell.y * 5 + 1 + dy) * dsize).roundToInt(),
                            (3 * dsize).roundToInt(),
                            (3 * dsize).roundToInt()
                        )

                        1 -> if (cell.direction < 2) {
                            g.drawRect(
                                ((cell.x * 5 + 1 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + dy) * dsize).roundToInt(),
                                (3 * dsize).roundToInt(),
                                (5 * dsize).roundToInt()
                            )
                        } else {
                            g.drawRect(
                                ((cell.x * 5 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + 1 + dy) * dsize).roundToInt(),
                                (5 * dsize).roundToInt(),
                                (3 * dsize).roundToInt()
                            )
                        }

                        2 -> if (cell.direction < 2) {
                            g.fillRect(
                                ((cell.x * 5 + 1 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + dy) * dsize).roundToInt(),
                                (3 * dsize).roundToInt(),
                                (5 * dsize).roundToInt()
                            )
                        } else {
                            g.fillRect(
                                ((cell.x * 5 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + 1 + dy) * dsize).roundToInt(),
                                (5 * dsize).roundToInt(),
                                (3 * dsize).roundToInt()
                            )
                        }

                        3 -> g.drawOval(
                            ((cell.x * 5 + dx) * dsize).roundToInt(),
                            ((cell.y * 5 + dy) * dsize).roundToInt(),
                            (5 * dsize).roundToInt(),
                            (5 * dsize).roundToInt()
                        )

                        4 -> {
                            g.fillOval(
                                ((cell.x * 5 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + dy) * dsize).roundToInt(),
                                (5 * dsize).roundToInt(),
                                (5 * dsize).roundToInt()
                            )
                            g.drawOval(
                                ((cell.x * 5 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + dy) * dsize).roundToInt(),
                                (5 * dsize).roundToInt(),
                                (5 * dsize).roundToInt()
                            )
                        }
                    }
                }

                2 -> {
                    g.color = Color(
                        min(255, max(0, cell.color[0])),
                        min(255, max(0, cell.color[1])),
                        min(255, max(0, cell.color[2]))
                    )
                    when (cell.type) {
                        0 -> g.fillRect(
                            ((cell.x * 5 + 1 + dx) * dsize).roundToInt(),
                            ((cell.y * 5 + 1 + dy) * dsize).roundToInt(),
                            (3 * dsize).roundToInt(),
                            (3 * dsize).roundToInt()
                        )

                        1 -> if (cell.direction < 2) {
                            g.drawRect(
                                ((cell.x * 5 + 1 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + dy) * dsize).roundToInt(),
                                (3 * dsize).roundToInt(),
                                (5 * dsize).roundToInt()
                            )
                        } else {
                            g.drawRect(
                                ((cell.x * 5 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + 1 + dy) * dsize).roundToInt(),
                                (5 * dsize).roundToInt(),
                                (3 * dsize).roundToInt()
                            )
                        }

                        2 -> if (cell.direction < 2) {
                            g.fillRect(
                                ((cell.x * 5 + 1 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + dy) * dsize).roundToInt(),
                                (3 * dsize).roundToInt(),
                                (5 * dsize).roundToInt()
                            )
                        } else {
                            g.fillRect(
                                ((cell.x * 5 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + 1 + dy) * dsize).roundToInt(),
                                (5 * dsize).roundToInt(),
                                (3 * dsize).roundToInt()
                            )
                        }

                        3 -> g.drawOval(
                            ((cell.x * 5 + dx) * dsize).roundToInt(),
                            ((cell.y * 5 + dy) * dsize).roundToInt(),
                            (5 * dsize).roundToInt(),
                            (5 * dsize).roundToInt()
                        )

                        4 -> {
                            g.fillOval(
                                ((cell.x * 5 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + dy) * dsize).roundToInt(),
                                (5 * dsize).roundToInt(),
                                (5 * dsize).roundToInt()
                            )
                            g.drawOval(
                                ((cell.x * 5 + dx) * dsize).roundToInt(),
                                ((cell.y * 5 + dy) * dsize).roundToInt(),
                                (5 * dsize).roundToInt(),
                                (5 * dsize).roundToInt()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun paintFood(g: Graphics, width: Int, height: Int) {
        for (i in 0 until width) { //рисуем еду
            for (j in 0 until height) {
                val green = when ((world.heightInMap(i, j)).roundToInt()) {
                    -1 -> 0
                    0 -> 63
                    1 -> 127
                    2 -> 191
                    else -> 255
                }
                g.color = Color(world.food.inMap(i, j, 0), green, world.food.inMap(i, j, 1))
                g.fillRect(
                    ((i * 5 + dx) * dsize).roundToInt(),
                    ((j * 5 + dy) * dsize).roundToInt(),
                    (5 * dsize).roundToInt(),
                    (5 * dsize).roundToInt()
                )
            }
        }
    }

    override fun actionPerformed(e: ActionEvent) {
        if (lulz == 0) {
            world.generateMap(this)
        }
        lulz += 1
        if (time) { //цикл дня и ночи
            daynight -= 0.001.toFloat()
            if (daynight <= 1) {
                daynight -= 0.006.toFloat()
            }
            if (daynight <= 0.90) {
                time = false
            }
        } else {
            daynight += 0.001.toFloat()
            if (daynight <= 1) {
                daynight += 0.006.toFloat()
            }
            if (daynight >= 1.5) {
                time = true
            }
        }
        world.cells.cellAction()
        repaint() // Перерисовываем экран
    }

    override fun keyTyped(e: KeyEvent) {}
    override fun keyPressed(e: KeyEvent) {
        val key = e.keyCode
        println(key)
        if (key == 49) { //управляем отображением
            display = 0
        } else if (key == 50) {
            display = 1
        } else if (key == 51) {
            display = 2
        } else if (key == 38) { //приближение
            dsize += 0.25.toFloat()
        } else if (key == 40 && dsize > 0.25) {
            dsize -= 0.25.toFloat()
        } else if (key == 87) { //движение
            dy += 5
        } else if (key == 83) {
            dy -= 5
        } else if (key == 65) {
            dx += 5
        } else if (key == 68) {
            dx -= 5
        } else if (key == 10) { //enter - сохранение генома
            val filter = FileNameExtensionFilter("*.*", "*.*")
            val fc = JFileChooser()
            fc.setFileFilter(filter)
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    FileWriter(fc.selectedFile).use { fw ->
                        for (i in selectgenom) {
                            for (j in i) {
                                fw.write("$j ")
                            }
                            fw.write("\n")
                        }
                    }
                } catch (e2: IOException) {
                    println("lolidk")
                }
            }
        } else if (key == 79) { //o - чтение генома
            val filter = FileNameExtensionFilter("*.*", "*.*")
            val fc = JFileChooser()
            fc.setFileFilter(filter)
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    FileReader(fc.selectedFile).use { fr ->
                        var buf = CharArray(256)
                        var c: Int
                        while (fr.read(buf).also { c = it } > 0) {
                            if (c < 256) {
                                buf = buf.copyOf(c)
                            }
                        }
                        val toparse = String(buf).split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        var x = 0
                        for (i in toparse) {
                            var y = 0
                            for (j in i.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                                selectgenom[x][y] = j.toInt()
                                y++
                            }
                            x++
                        }
                    }
                } catch (e2: IOException) {
                    println("lolidk")
                }
            }
        }
    }

    override fun keyReleased(e: KeyEvent) {}

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val frame = JFrame("life simulation")
            val game = SimpleGame()
            frame.add(game)
            frame.setSize(400, 600)
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
            frame.isVisible = true
        }
    }
}