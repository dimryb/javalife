import java.util.*

class Noise(private val scale: Int, width: Int, height: Int) {
    private val randomTable: Array<IntArray> = Array(width / scale + 2) { IntArray(height / scale + 2) }
    private val width: Int = width / scale + 1
    private val height: Int = height / scale + 1
    private var generator = Random()
    private fun rand(x: Int, y: Int): Int {
        return randomTable[x][y]
    }

    init {
        (0 until this.width).forEach { i ->
            for (j in 0 until this.height) {
                randomTable[i][j] = generator.nextInt(2)
            }
        }
        for (i in 0 until this.width) {
            randomTable[i][this.height - 1] = randomTable[i][0]
        }
        for (i in 0 until this.height) {
            randomTable[this.width - 1][i] = randomTable[0][i]
        }
    }

    fun geValue(x: Int, y: Int): Float {
        val xgs = x / scale
        val ygs = y / scale
        val xge = x / scale + 1
        val yge = y / scale + 1
        val xs = xgs * scale
        val xe = xge * scale
        val ys = ygs * scale
        val ye = yge * scale
        val k = ((xe - xs) * (ye - ys)).toFloat()
        val w11 = (xe - x) * (ye - y) / k
        val w12 = (xe - x) * (y - ys) / k
        val w21 = (x - xs) * (ye - y) / k
        val w22 = (x - xs) * (y - ys) / k
        return rand(xgs, ygs) * w11 + rand(xgs, yge) * w12 + rand(xge, ygs) * w21 + rand(xge, yge) * w22
    }
}
