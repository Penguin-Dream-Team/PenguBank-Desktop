package models.requests

import javafx.beans.property.SimpleIntegerProperty
import tornadofx.*
import javax.json.JsonObject

class TransactionRequest : JsonModel {

    val destinationIdProperty = SimpleIntegerProperty()
    private var destinationId: Int by destinationIdProperty

    val amountProperty = SimpleIntegerProperty()
    private var amount: Int by amountProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            destinationId = int("destinationId")!!
            amount = int("amount")!!
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("destinationId", destinationId)
            add("amount", amount)
        }
    }
}

class TransactionRequestModel(transactionRequest: TransactionRequest = TransactionRequest()) : ItemViewModel<TransactionRequest>(transactionRequest) {
    var destinationId = bind(TransactionRequest::destinationIdProperty)
    val amount = bind(TransactionRequest::amountProperty)
}
