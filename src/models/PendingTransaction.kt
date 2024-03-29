package models

import tornadofx.*
import utils.toEuros
import utils.toPrettyDate
import javax.json.JsonObject

class PendingTransaction : JsonModel {
    private var id: Int? = null
    private var amount: Int? = null
    private var account: String? = null
    private var destination: String? = null
    private var createdAt: String? = null
    private var expiredAt: String? = null
    private var token: String? = null

    override fun toString() =
        "To: $destination | Amount: ${amount!!.toEuros()} | Created: ${createdAt!!.toPrettyDate()} | Expires: ${expiredAt!!.toPrettyDate()}"

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("amount", amount)
            add("account", account)
            add("destination", destination)
            add("createdAt", createdAt)
            add("expiredAt", expiredAt)
            add("token", token)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            id = int("id")!!
            amount = int("amount")!!
            account = string("account")!!
            destination = string("destination")!!
            createdAt = string("createdAt")!!
            expiredAt = string("expiredAt")!!
            token = string("token")!!
        }
    }
}