class PerlinNoise(width: Int, height: Int) {
    private val big: Noise = Noise(64, width, height)
    private val middle: Noise = Noise(32, width, height)
    private val small: Noise = Noise(16, width, height)
    private val smaller: Noise = Noise(8, width, height)

    fun getValue(x: Int, y: Int): Float {
        return big.geValue(x, y) * 0.45f + middle.geValue(x, y) * 0.25f + small.geValue(x, y) * 0.15f + smaller.geValue(
            x,
            y
        ) * 0.15f
    }
}
