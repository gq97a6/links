package hostunit.net.logic

import hostunit.net.classes.Address

val blocked = listOf("porn", "sex", "orgasm", "fuck")

//Check if string is on blocked list
fun String.isBlocked(list: List<String> = blocked) = list.any { this.contains(it) }

//Check if address contains blocked links or code
fun Address.containsBlocked(): Boolean {
    return this.links.any { it.payload.isBlocked() } || this.code.isBlocked()
}

//Validate code
fun String.isInvalidCode(): Boolean {
    if (this.length !in 1..10) return true
    if (this.any { !it.isLetter() && !it.isDigit() }) return true
    return false
}

//Validate address
fun Address.isInvalid(): Boolean {
    if (this.containsBlocked()) return true
    if (this.code.isInvalidCode()) return true
    return false
}