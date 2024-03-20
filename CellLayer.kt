import java.util.*
import kotlin.math.min

class CellLayer(
    private val width: Int,
    private val height: Int,
    private val foodMap: FoodLayer,
    private val world: HeightLayer,
) {
    private val cellMap = Array(width) { IntArray(height) }
    private var cellsList = mutableMapOf<String, Cell>() //список клеток
    private var maxId: Int = 101
    private val rand = Random()

    private fun clean() {
        cellMap.forEach { it.fill(0) }
    }

    private fun fillCellMap() {
        cellsList.values.forEach { cell ->
            cellMap[cell.x][cell.y] = cell.id
        }
    }

    fun cellAction() {
        clean()
        fillCellMap()
        val cells1 = cellsList.toMutableMap()

        cellsList.values.forEach { cell -> //рисуем и обрабатываем клетки
            cell.lifetime -= 1
            if (cell.type != 3) { //не для семечек
                cell.energy -= 1f
                if ((foodMap.inMap(cell.x, cell.y, 0) >= 200 || foodMap.inMap(
                        cell.x, cell.y, 1
                    ) >= 200) && cell.type != 0 && cell.type != 1
                ) //клетку дамажит плохая земля
                {
                    cell.lifetime -= foodMap.inMap(cell.x, cell.y, 0) / 10
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
                        if (pos[0] == width) {
                            pos[0] = 0
                        }
                        if (pos[0] == -1) {
                            pos[0] = width - 1
                        }
                        if (pos[1] == height) {
                            pos[1] = 0
                        }
                        if (pos[1] == -1) {
                            pos[1] = height - 1
                        }
                        if (cells1.keys.contains(cellMap[pos[0]][pos[1]].toString())) {
                            val food = cells1[cellMap[pos[0]][pos[1]].toString()]
                            if (food!!.parentId != cell.parentId && !(food.relations.contains(cell.id.toString()) || cell.relations.contains(
                                    food.id.toString()
                                )) && food.type != 0
                            ) { //корни и родственников есть нельзя
                                cell.energy += food.energy
                                cells1.remove(cellMap[pos[0]][pos[1]].toString())
                            }
                        }
                    }
                } else {
                    foodMap.set(cell.x, cell.y, cell.eat(foodMap.inMap(cell.x, cell.y)))  //клетка кушает всегда
                }
            } else { //семечки летают
                val nextpos = cell.move()
                if (cellMap[nextpos[0]][nextpos[1]] == 0) {
                    cellMap[cell.x][cell.y] = 0
                    cellMap[nextpos[0]][nextpos[1]] = cell.id

                    if (world.heightInMap(cell.x, cell.y) != (world.heightInMap(nextpos[0], nextpos[1]))) {
                        cell.lifetime /= 2
                    }
                    cell.x = nextpos[0]
                    cell.y = nextpos[1]
                } else {
                    cell.lifetime -= 3 //семечка дамажит всех, с кем сталкивается
                    val jertva = cells1[cellMap[nextpos[0]][nextpos[1]].toString()]
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
            if (cell.canGrow() && cell.energy >= 3) { //клетка делится
                val ncell = cell.mitoz()
                if (cellMap[ncell.x][ncell.y] == 0 && (world.heightInMap(cell.x, cell.y) == world.heightInMap(
                        ncell.x, ncell.y
                    ) || rand.nextInt(
                        10
                    ) == 0) && world.heightInMap(ncell.x, ncell.y) != -1 && world.heightInMap(ncell.x, ncell.y) != 3
                ) { //переход границы биомов карается
                    cells1[maxId.toString()] = ncell
                    cellMap[ncell.x][ncell.y] = ncell.id
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
                maxId++
            }
            if (cell.energy <= 0 || cell.lifetime <= 0) { //клетка сдохла
                if (cell.energy > 0) {
                    foodMap.set(
                        cell.x, cell.y, 0, (foodMap.inMap(cell.x, cell.y, 0) + cell.energy / 2).toInt()
                    )//трупик
                    foodMap.set(cell.x, cell.y, 1, (foodMap.inMap(cell.x, cell.y, 1) + cell.energy / 2).toInt())
                    foodMap.set(cell.x, cell.y, 0, min(foodMap.inMap(cell.x, cell.y, 0), 255))
                    foodMap.set(cell.x, cell.y, 1, min(foodMap.inMap(cell.x, cell.y, 1), 255))
                }
                cells1.remove(cell.id.toString())
            }
        }
        cellsList = cells1
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
        cellsList = cells1
    }

    fun getCells(): Map<String, Cell> = cellsList
    fun getCellsMap(): Array<IntArray> = cellMap

    private fun toCell(key: String, cell: Cell) {
        cellsList[key] = cell
    }

    fun maxId() = maxId

    fun generateSeeds(context: SimpleGame) {
        (0 until 100).forEach { i ->
            //генерируем семена
            val ncell = Cell(rand.nextInt(width), rand.nextInt(height), rand.nextFloat(40f, 50f), 0)
            if (world.heightInMap(ncell.x, ncell.y) != -1 && world.heightInMap(ncell.x, ncell.y) != 3) {
                ncell.parentId = i
                ncell.world = context
                ncell.id = i + 1
                ncell.lifetime = 1
                toCell(i.toString(), ncell)
            }
        }
    }

    fun createCell(x: Int, y: Int, selectGenom: Array<IntArray>, context: SimpleGame) {
        val ncell = Cell(x, y, rand.nextFloat(40f, 50f), 0)
        ncell.genom = selectGenom
        if (world.heightInMap(ncell.x, ncell.y) != -1 && world.heightInMap(ncell.x, ncell.y) != 3) {
            ncell.parentId = maxId
            ncell.world = context
            ncell.id = maxId
            ncell.lifetime = 1
            toCell(maxId.toString(), ncell)
            maxId++
        }
    }
}