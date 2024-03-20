import java.util.*

class Cell(//координаты
    var x: Int,
    var y: Int, //энергия
    var energy: Float,
    activeGen: Int
) {
    //класс клетки
    var parentId = 0
    private var prevCell: String? = null
    var fraction = 0 //индикатор порванности организма
    private var mranges = intArrayOf(5, 10, 10, 4, 1000, 3) //пределы генов
    var world: SimpleGame? = null
    var direction = 0 //направление роста
    var id = 0
    var lifetime = 200
    var type = 3 //тип клетки 0-корень 1-антенна 2 - лист, 3 - семачка
    var genom: Array<IntArray> //генокод
    var relations = ArrayList<String>()
    private var activeGen: Int
    private val rand = Random() //ГПСЧ
    var color = intArrayOf(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255))

    init {
        val randomGenom = Array(10) { IntArray(4) } //генерируем случайный геном
        for (j in 0..9) {
            val newGen = intArrayOf(
                rand.nextInt(mranges[0]), rand.nextInt(mranges[1]), rand.nextInt(mranges[2]), rand.nextInt(
                    mranges[3]
                ), rand.nextInt(mranges[4]), rand.nextInt(mranges[5])
            )
            randomGenom[j] = newGen
        }
        genom = randomGenom
        this.activeGen = activeGen
    }

    fun canGrow(): Boolean { //проверка возможности делится
        if (type == 3) {
            return lifetime <= 0
        }
        when (genom[activeGen][5]) {
            0 -> return true
            1 -> if (energy >= 10) {
                return true
            }

            2 -> if (lifetime <= 0) {
                return true
            }
        }
        return false
    }

    fun move(): IntArray { //движение
        val width = world!!.widthMap
        val height = world!!.heightMap
        return if (rand.nextInt(2) == 0) {
            var nx = x + rand.nextInt(-1, 2)
            if (nx == -1) {
                nx = width - 1
            }
            if (nx == width) {
                nx = 0
            }
            var ny = y + rand.nextInt(-1, 2)
            if (ny == -1) {
                ny = height - 1
            }
            if (ny == height) {
                ny = 0
            }
            intArrayOf(nx, ny)
        } else {
            val pos = arrayOf(
                intArrayOf(0, 1),
                intArrayOf(1, 0),
                intArrayOf(0, -1),
                intArrayOf(-1, 0)
            )
            var nx = x + pos[direction][0]
            if (nx == -1) {
                nx = width - 1
            }
            if (nx == width) {
                nx = 0
            }
            var ny = y + pos[direction][1]
            if (ny == -1) {
                ny = height - 1
            }
            if (ny == height) {
                ny = 0
            }
            intArrayOf(nx, ny)
        }
    }

    fun mitoz(): Cell { //Деление
        if (genom[activeGen][0] == 3 && type == 3) {
            genom[activeGen][0] = 2
        }
        val width = world!!.widthMap
        val height = world!!.heightMap
        val maxid = world!!.world.cells.maxId()
        val pos = arrayOf(intArrayOf(0, 1), intArrayOf(0, -1), intArrayOf(1, 0), intArrayOf(-1, 0))
        var nx = x + pos[genom[activeGen][3]][0]
        if (nx == -1) {
            nx = width - 1
        }
        if (nx == width) {
            nx = 0
        }
        var ny = y + pos[genom[activeGen][3]][1]
        if (ny == -1) {
            ny = height - 1
        }
        if (ny == height) {
            ny = 0
        }
        energy /= 2f
        val kid = Cell(
            nx,
            ny,
            energy,
            genom[activeGen][1]
        ) //0 - тип потомка, 1 - активный ген потомка, 2 - след. активный ген, 3 - направление роста потомка, 4 - lifetime потомка 5 - условия роста
        kid.lifetime = genom[activeGen][4]
        for (i in 0..9) {
            kid.genom[i] = genom[i].clone()
        }
        kid.direction = genom[activeGen][3]
        kid.world = world
        kid.type = genom[activeGen][0]
        kid.color = color.clone()
        kid.id = maxid
        if (kid.type != 3) {
            kid.prevCell = id.toString()
            relations.add(maxid.toString())
            kid.parentId = parentId
            kid.activeGen = 0
            if (rand.nextInt(10) == 0) { //мутация
                val mpos = rand.nextInt(6)
                kid.genom[rand.nextInt(10)][mpos] = rand.nextInt(mranges[mpos])
                kid.color[rand.nextInt(3)] += rand.nextInt(-1, 1) * 5
            }
        } else {
            kid.activeGen = 0
            kid.parentId = kid.id
            if (rand.nextInt(2) == 0) { //мутация
                val mpos = rand.nextInt(6)
                kid.genom[rand.nextInt(10)][mpos] = rand.nextInt(mranges[mpos])
                kid.color[rand.nextInt(3)] += rand.nextInt(-1, 2) * 5
            }
        }
        activeGen = genom[activeGen][2]
        return kid
    }

    fun eat(pos: IntArray): IntArray { //кушац
        val height = world!!.world.heightInMap(x, y)
        when (type) {
            0 -> {
                if (pos[0] > 0) {
                    pos[0] -= 1
                    if (height == 0f) {
                        energy += 2.25.toFloat()
                    }
                    if (height == 1f) {
                        energy += 2f
                    }
                    if (height == 2f) {
                        energy += 1.75.toFloat()
                    }
                    energy += 2f
                }
                return pos
            }

            1 -> {
                if (pos[1] > 0) {
                    pos[1] -= 1
                    if (height == 0f) {
                        energy += 2.25.toFloat()
                    }
                    if (height == 1f) {
                        energy += 2f
                    }
                    if (height == 2f) {
                        energy += 1.75.toFloat()
                    }
                }
                return pos
            }

            2 -> {
                if (height == 0f) {
                    energy += (world!!.daynight - 0.1).toFloat()
                }
                if (height == 1f) {
                    energy += world!!.daynight
                }
                if (height == 2f) {
                    energy += (world!!.daynight + 0.1).toFloat()
                }
                return pos
            }
        }
        return pos
    }
}
