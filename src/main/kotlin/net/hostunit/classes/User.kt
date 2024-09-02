package net.hostunit.classes

import net.hostunit.epochNow

class User(
    var id: String = "",
    var name: String = "",
    var pass: String = "",
    var roles: List<String> = listOf(),
    var lastChange: Long = epochNow
)