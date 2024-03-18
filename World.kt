class World(
    private val width: Int,
    private val height: Int,
) : HeightLayer(width, height) {
    val food = FoodLayer(width, height)
    val cells = CellLayer(width, height, food, this)

    fun generateMap(context: SimpleGame) {
        food.generateFoodMap()
        generateHeightMap()
        cells.generateSeeds(context)
    }
}
