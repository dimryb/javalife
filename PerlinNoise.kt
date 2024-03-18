class PerlinNoise(width: Int, height: Int) {
    var big: Noise
    var middle: Noise
    var small: Noise
    var smaller: Noise

    init {
        big = Noise(64, width, height)
        middle = Noise(32, width, height)
        small = Noise(16, width, height)
        smaller = Noise(8, width, height)
    }

    fun getValue(x: Int, y: Int): Float {
        return big.geValue(x, y) * 0.45f + middle.geValue(x, y) * 0.25f + small.geValue(x, y) * 0.15f + smaller.geValue(
            x,
            y
        ) * 0.15f
    }
}
