package DiceGame

class Dice (var diceValue:Int) {

    var value: Int = diceValue
    var img:String = "${value}.png"
    var reRoll: Boolean = true

    fun toggleReRoll(){
        reRoll = !reRoll
    }
}

