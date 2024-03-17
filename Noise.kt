import java.util.*

class Noise(var scale: Int, width: Int, height: Int) {
    var randtable: Array<IntArray>
    var width: Int
    var height: Int
    var generator = Random()
    private fun rand(x: Int, y: Int): Int {
        return randtable[x][y]
    }

    init {
        randtable = Array(width / scale + 2) { IntArray(height / scale + 2) }
        this.width = width / scale + 1
        this.height = height / scale + 1
        for (i in 0 until this.width) {
            for (j in 0 until this.height) {
                randtable[i][j] = generator.nextInt(2)
            }
        }
        for (i in 0 until this.width) {
            randtable[i][this.height - 1] = randtable[i][0]
        }
        for (i in 0 until this.height) {
            randtable[this.width - 1][i] = randtable[0][i]
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
