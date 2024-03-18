import java.util.*

class FoodLayer(private val width: Int, private val height: Int) {
    private val foodMap = Array(width) { Array(height) { IntArray(2) } }
    private val rand = Random()

    fun generateFoodMap() {
        for (i in 0 until width) { //генерируем карту еды
            for (j in 0 until height) {
                foodMap[i][j][0] = rand.nextInt(10)
                foodMap[i][j][1] = rand.nextInt(10)
            }
        }
    }

    fun inMap(x: Int, y: Int, pos: Int) = foodMap[x][y][pos]
    fun inMap(x: Int, y: Int) = foodMap[x][y]

    fun set(x: Int, y: Int, pos: Int, value: Int) {
        foodMap[x][y][pos] = value
    }

    fun set(x: Int, y: Int, value: IntArray) {
        foodMap[x][y] = value
    }
}