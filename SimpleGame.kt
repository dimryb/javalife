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
    var mouseclickx = 0
    var mouseclicky = 0
    var dy = 0
    var selectgenom = Array(10) { IntArray(6) }
    var maxid = 101
    val widthMap = 320
    val heightMap = 320
    private val timer: Timer // Таймер для обновления экрана
    var cells = HashMap<String, Cell>() //список клеток
    var cellmap = Array(widthMap) { IntArray(heightMap) } //карты
    private val rand = Random()
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
        g.color = Color.lightGray
        for (cell1 in cells.values) { //перераспределение энергии
            for (kid in cell1.relations) {
                if (cells.keys.contains(kid)) {
                    val cell2 = cells[kid]
                    if (abs((cell1.x + cell1.y - cell2!!.x - cell2.y).toDouble()) <= 2) {
                        g.drawLine(
                            Math.round((cell1.x * 5 + 2 + dx) * dsize),
                            Math.round((cell1.y * 5 + 2 + dy) * dsize),
                            Math.round((cell2.x * 5 + 2 + dx) * dsize),
                            Math.round((cell2.y * 5 + 2 + dy) * dsize)
                        )
                    }
                }
            }
        }
        g.color = Color.black //время суток
        g.fillRect(1200, 0, 1000, 1200)
        g.font = Font("Arial", Font.PLAIN, 20)
        g.color = Color.white
        g.drawString("таймер: $lulz освещенность: $daynight", 1210, 30)
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

    private fun paintCells(g: Graphics) {
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
            world.generateMap()
            generateSeedsMap(widthMap, heightMap)
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
        for (i in 0 until widthMap) {
            for (j in 0 until heightMap) {
                cellmap[i][j] = 0
            }
        }
        for (cell in cells.values) { //заполняем карту клеток
            cellmap[cell.x][cell.y] = cell.id
        }
        val cells1 = cells.clone() as HashMap<String, Cell>
        for (cell in cells.values) { //рисуем и обрабатываем клетки
            cell.lifetime -= 1
            if (cell.type != 3) { //не для семечек
                cell.energy -= 1f
                if ((world.food.inMap(cell.x, cell.y, 0) >= 200 || world.food.inMap(
                        cell.x, cell.y, 1
                    ) >= 200) && cell.type != 0 && cell.type != 1
                ) //клетку дамажит плохая земля
                {
                    cell.lifetime -= world.food.inMap(cell.x, cell.y, 0) / 10
                    cell.energy -= 5f
                }
                if (cell.type == 4) { //хищник
                    val neigbors = arrayOf(
                        intArrayOf(cell.x + 1, cell.y),
                        intArrayOf(cell.x - 1, cell.y),
                        intArrayOf(cell.x, cell.y + 1),
                        intArrayOf(cell.x, cell.y - 1),
                        intArrayOf(cell.x + 1, cell.y + 1),
                        intArrayOf(cell.x - 1, cell.y - 1),
                        intArrayOf(cell.x - 1, cell.y + 1),
                        intArrayOf(cell.x + 1, cell.y - 1)
                    )
                    for (pos in neigbors) {
                        if (pos[0] == widthMap) {
                            pos[0] = 0
                        }
                        if (pos[0] == -1) {
                            pos[0] = widthMap - 1
                        }
                        if (pos[1] == heightMap) {
                            pos[1] = 0
                        }
                        if (pos[1] == -1) {
                            pos[1] = heightMap - 1
                        }
                        if (cells1.keys.contains(cellmap[pos[0]][pos[1]].toString())) {
                            val food = cells1[cellmap[pos[0]][pos[1]].toString()]
                            if (food!!.parent_id != cell.parent_id && !(food.relations.contains(cell.id.toString()) || cell.relations.contains(
                                    food.id.toString()
                                )) && food.type != 0
                            ) { //корни и родственников есть нельзя
                                cell.energy += food.energy
                                cells1.remove(cellmap[pos[0]][pos[1]].toString())
                            }
                        }
                    }
                } else {
                    world.food.set(cell.x, cell.y, cell.Eat(world.food.inMap(cell.x, cell.y)))  //клетка кушает всегда
                }
            } else { //семечки летают
                val nextpos = cell.Move()
                if (cellmap[nextpos[0]][nextpos[1]] == 0) {
                    cellmap[cell.x][cell.y] = 0
                    cellmap[nextpos[0]][nextpos[1]] = cell.id

                    if (world.heightInMap(cell.x, cell.y) != (world.heightInMap(nextpos[0], nextpos[1]))) {
                        cell.lifetime /= 2
                    }
                    cell.x = nextpos[0]
                    cell.y = nextpos[1]
                } else {
                    cell.lifetime -= 3 //семечка дамажит всех, с кем сталкивается
                    val jertva = cells1[cellmap[nextpos[0]][nextpos[1]].toString()]
                    if (jertva != null) {
                        jertva.lifetime -= 10
                        for (kid in jertva.relations) {
                            if (cells1.keys.contains(kid)) {
                                cells1[kid]!!.fraction += 1
                            }
                        }
                    }
                }
            }
            if (cell.CanGrow() && cell.energy >= 3) { //клетка делится
                val ncell = cell.Mitoz()
                if (cellmap[ncell.x][ncell.y] == 0 && (world.heightInMap(cell.x, cell.y) == world.heightInMap(
                        ncell.x, ncell.y
                    ) || rand.nextInt(
                        10
                    ) == 0) && world.heightInMap(ncell.x, ncell.y) != -1f && world.heightInMap(ncell.x, ncell.y) != 3f
                ) { //переход границы биомов карается
                    cells1[maxid.toString()] = ncell
                    cellmap[ncell.x][ncell.y] = ncell.id
                }
                if (cell.type == 3) {
                    cell.lifetime = -100
                    cell.energy = -100f
                }
                //                else if (cellmap[ncell.x][ncell.y]!=0 && cell.relations.size()<= 1){//маленький шанс на сращицание TODO: попробуй эту фигню заменить проверкой на наличие relation между клетками
//                    if(cells1.keySet().contains(Integer.toString(cellmap[ncell.x][ncell.y]))){
//                        if(cell.parent_id == cells1.get(Integer.toString(cellmap[ncell.x][ncell.y])).parent_id  && cell.fraction != cells1.get(Integer.toString(cellmap[ncell.x][ncell.y])).fraction){
//                            cell.relations.add(Integer.toString(cellmap[ncell.x][ncell.y]));
//                        }
//                    }
//                }
                maxid++
            }
            if (cell.energy <= 0 || cell.lifetime <= 0) { //клетка сдохла
                if (cell.energy > 0) {
                    world.food.set(
                        cell.x, cell.y, 0, (world.food.inMap(cell.x, cell.y, 0) + cell.energy / 2).toInt()
                    )//трупик
                    world.food.set(cell.x, cell.y, 1, (world.food.inMap(cell.x, cell.y, 1) + cell.energy / 2).toInt())
                    world.food.set(cell.x, cell.y, 0, min(world.food.inMap(cell.x, cell.y, 0), 255))
                    world.food.set(cell.x, cell.y, 1, min(world.food.inMap(cell.x, cell.y, 1), 255))
                }
                cells1.remove(cell.id.toString())
            }
        }
        cells = cells1
        for (cell1 in cells1.values) { //перераспределение энергии
            val dead = ArrayList<String>()
            for (kid in cell1.relations) {
                if (cells1.keys.contains(kid)) {
                    val cell2 = cells1[kid]
                    val sume = (cell1.energy + cell2!!.energy) / 2
                    cell1.energy = sume
                    cell2.energy = sume
                } else { //больше не обрабатываем мертвецов
                    dead.add(kid)
                }
            }
            for (kid in dead) {
                cell1.relations.remove(kid)
            }
        }
        cells = cells1
        repaint() // Перерисовываем экран
    }

    private fun generateSeedsMap(width: Int, height: Int) {
        for (i in 0..99) { //генерируем семена
            val ncell = Cell(rand.nextInt(width), rand.nextInt(height), rand.nextFloat(40f, 50f), 0)
            if (world.heightInMap(ncell.x, ncell.y) != -1f && world.heightInMap(ncell.x, ncell.y) != 3f) {
                ncell.parent_id = i
                ncell.world = this
                ncell.id = i + 1
                ncell.lifetime = 1
                cells[i.toString()] = ncell
            }
        }
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
    fun createCell() {
        val ncell = Cell(mouseclickx, mouseclicky, rand.nextFloat(40f, 50f), 0)
        ncell.genom = selectgenom
        if (world.heightInMap(ncell.x, ncell.y) != -1f && world.heightInMap(ncell.x, ncell.y) != 3f) {
            ncell.parent_id = maxid
            ncell.world = this
            ncell.id = maxid
            ncell.lifetime = 1
            cells[maxid.toString()] = ncell
            maxid++
        }
    }

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