package hostunit.net.logic

import hostunit.net.classes.Address
import hostunit.net.repository.findAddressByCode
import io.quarkus.mongodb.reactive.ReactiveMongoClient

val letters = ('A'..'Z').filterNot { it in listOf('Q', 'V', 'J', 'I', 'O', 'L', 'N', 'M') }
val digits = ('1'..'9')

suspend fun Address.generateCode(db: ReactiveMongoClient, temporary: Boolean = this.temporary): String? {
    var code = ""
    for (i in 0..2) {
        code = if (temporary) generateTemporaryCode()
        else generatePermanentCode()

        if (findAddressByCode(db, code) == null) break
        if (i == 2) return null
    }

    this.code = code
    return code
}

private fun generateTemporaryCode(): String {
    val digits = ('1'..'9').toList()
    return (1..4).map { digits.random() }.joinToString("")
}

private fun generatePermanentCode(l: List<Char> = letters, d: CharRange = digits): String {
    return "${l.random()}${d.random()}${l.random()}${d.random()}"
}