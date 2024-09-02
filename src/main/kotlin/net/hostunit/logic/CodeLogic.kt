package net.hostunit.logic

import net.hostunit.classes.Address
import net.hostunit.repository.findAddressByCode
import io.quarkus.mongodb.reactive.ReactiveMongoClient

suspend fun Address.generateCode(db: ReactiveMongoClient): String? {
    var code = ""
    for (i in 0..2) {
        code = generateCode()
        if (findAddressByCode(db, code) == null) break
        if (i == 2) return null
    }

    this.code = code
    return code
}

private fun generateCode(chars: CharRange = ('1'..'9')): String {
    return "00000".map { chars.random() }.joinToString("")
}