package hostunit.net.classes

class Link(
    var payload: String = "",
    var action: Action = Action.TAB,
) {
    enum class Action { COPY, LINK, TAB }
}