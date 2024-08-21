package hostunit.net.classes

class Address(
    var temporary: Boolean = false,
    var direct: Boolean = false,
    var code: String = "",
    var links: MutableList<Link> = mutableListOf()
)