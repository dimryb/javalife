class World(private val width: Int, private val height: Int) : HeightLayer(width, height) {

    val food = FoodLayer(width, height)

    fun generateMap() {
        food.generateFoodMap()
        generateHeightMap()
    }
}
