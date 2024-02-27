package lightsOut

import kotlin.random.Random

data class LightsOutBoard (
    val size: Int =5,
    val cells: Array<Array<Cell>>
) {
companion object {
    fun populateBoard(size: Int): LightsOutBoard {
        val cells = Array(size) { y ->
            Array(size) { x ->
                Cell(x, y, Random.nextBoolean())
            }
        }

        

        return LightsOutBoard(size, cells)
    }
}
}