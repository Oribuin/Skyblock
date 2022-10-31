package xyz.oribuin.skyblock.island

import java.util.*

class Report(val reporter: UUID, val island: Island, val reason: String) {

    var date: Long = System.currentTimeMillis()

}