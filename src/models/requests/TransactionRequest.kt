package models.requests

import javafx.beans.property.SimpleIntegerProperty
import tornadofx.*
import javax.json.JsonObject

class TransactionRequest : JsonModel {

    val accountDestinationIdProperty = SimpleIntegerProperty()
    private var accountDestinationId: Int by accountDestinationIdProperty

    val amountProperty = SimpleIntegerProperty()
    private var amount: Int by amountProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            accountDestinationId = int("accountDestinationId")!!
            amount = int("amount")!!
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("accountDestinationId", accountDestinationId)
            add("amount", amount)
        }
    }
}

class TransactionRequestModel(transactionRequest: TransactionRequest = TransactionRequest()) : ItemViewModel<TransactionRequest>(transactionRequest) {
    var accountDestinationId = bind(TransactionRequest::accountDestinationIdProperty)
    val amount = bind(TransactionRequest::amountProperty)
}
