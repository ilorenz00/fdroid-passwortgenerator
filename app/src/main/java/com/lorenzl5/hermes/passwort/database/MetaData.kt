package com.lorenzl5.hermes.passwort.database

class MetaData(
    var id: Int = 0,
    var domain: String = "",
    var username: String = "",
    var hasLettersLow: Int = 1,
    var hasLettersUp: Int = 1,
    var hasNumbers: Int = 1,
    var hasSymbols: Int = 1,
    var length: Int = 10,
    var iteration: Int = 1,
    var positionId: Int = 0
) {
    fun characterSetDescription(): String {
        val sb = StringBuilder()
        if (hasLettersLow == 1) sb.append("abc ")
        if (hasLettersUp == 1) sb.append("ABC ")
        if (hasNumbers == 1) sb.append("123 ")
        if (hasSymbols == 1) sb.append("+!#")
        return sb.toString().trim()
    }
}
