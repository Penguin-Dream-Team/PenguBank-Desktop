package models.requests

import tornadofx.*
import javax.json.JsonObject

class UpdateTransactionRequest : JsonModel {
    var transactionId: Int? = null
    var signedToken: String? = null

    override fun updateModel(json: JsonObject) {
        with(json) {
            transactionId = int("transactionId")!!
            signedToken = string("signedToken")!!
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("transactionId", transactionId)
            add("signedToken", signedToken)
        }
    }
}
