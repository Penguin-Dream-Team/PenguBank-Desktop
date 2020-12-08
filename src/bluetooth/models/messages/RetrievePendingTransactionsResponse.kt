package bluetooth.models.messages

import bluetooth.models.JSONObject
import tornadofx.*
import javax.json.JsonObject

data class RetrievePendingTransactionsResponse(val pendingTransactions: List<PendingTransaction>) : JSONObject

class PendingTransaction : JsonModel {
    private var id: Int? = null
    private var amount: Int? = null
    private var account: Int? = null
    private var destination: Int? = null
    private var createdAt: String? = null
    private var expiredAt: String? = null
    private var token: String? = null

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
            account = int("account")!!
            destination = int("destination")!!
            createdAt = string("createdAt")!!
            expiredAt = string("expiredAt")!!
            token = string("token")!!
        }
    }
}
