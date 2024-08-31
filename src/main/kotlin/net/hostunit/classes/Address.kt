package net.hostunit.classes

class Address(
    var id: String = "",
    var temporary: Boolean = false,
    var direct: Boolean = false,
    var code: String = ""
) {
    var links: MutableList<Link> = mutableListOf()
        set(value) {
            field = value.take(4).toMutableList()
        }
}