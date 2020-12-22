package me.akainth.tape

annotation class Length(val unit: Units = Units.FEET) {
    companion object {
        enum class Units(ratioToBase: Double = 1.0) {
            FEET, METERS(0.3048)
        }
    }
}

