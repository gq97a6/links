package net.hostunit.classes

class Link(
    var payload: String = "",
    var action: Action = Action.TAB,
) {
    enum class Action { COPY, LINK, TAB }
}