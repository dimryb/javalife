class World(width: Int, height: Int) {
    private var worldMap = Array(width) { FloatArray(height) }
    private val noise = PerlinNoise(width, height)

    fun heightInMap(x: Int, y: Int) = worldMap[x][y]

    fun generateHeightMap(width: Int, height: Int) {
        for (i in 0 until width) { //генерируем карту высот
            for (j in 0 until height) {
                worldMap[i][j] = noise.getValue(i, j)
                if (worldMap[i][j] < 0.2) {
                    worldMap[i][j] = -1f
                } else if (worldMap[i][j] < 0.4) {
                    worldMap[i][j] = 0f
                } else if (worldMap[i][j] < 0.6) {
                    worldMap[i][j] = 1f
                } else if (worldMap[i][j] < 0.8) {
                    worldMap[i][j] = 2f
                } else {
                    worldMap[i][j] = 3f
                }
            }
        }
    }
}