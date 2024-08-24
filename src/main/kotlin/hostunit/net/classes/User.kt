package hostunit.net.classes

class User(
    var id: String = "",
    var name: String = "",
    var pass: String = "",
    var roles: List<String> = listOf(),
    var valid: Long = 0L
)