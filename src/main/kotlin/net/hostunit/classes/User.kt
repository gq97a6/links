package net.hostunit.classes

import java.util.Date

class User(
    var id: String = "",
    var name: String = "",
    var pass: String = "",
    var roles: List<String> = listOf(),
    var lastChange: Date = Date()
)