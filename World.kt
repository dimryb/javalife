class World(private val width: Int, private val height: Int) {
    private var worldMap = Array(width) { FloatArray(height) }
    private val noise = PerlinNoise(width, height)

    companion object {
        const val LOW_THRESHOLD = 0.2
        const val MEDIUM_THRESHOLD = 0.4
        const val HIGH_THRESHOLD = 0.6
        const val VERY_HIGH_THRESHOLD = 0.8
    }

    object HeightLevel {
        const val NEGATIVE: Float = -1.0f
        const val ZERO: Float = 0.0f
        const val ONE: Float = 1.0f
        const val TWO: Float = 2.0f
        const val THREE: Float = 3.0f
    }

    data class HeightData(val x: Int, val y: Int, val height: HeightLevel)

    fun heightInMap(x: Int, y: Int) = worldMap[x][y]

    private fun convertNoiseToHeight(value: Float): Float {
        return when {
            value < LOW_THRESHOLD -> HeightLevel.NEGATIVE
            value < MEDIUM_THRESHOLD -> HeightLevel.ZERO
            value < HIGH_THRESHOLD -> HeightLevel.ONE
            value < VERY_HIGH_THRESHOLD -> HeightLevel.TWO
            else -> HeightLevel.THREE
        }
    }

    fun generateHeightMap() {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val noiseValue = noise.getValue(i, j)
                val heightLevel = convertNoiseToHeight(noiseValue)
                worldMap[i][j] = heightLevel
            }
        }
    }
}
