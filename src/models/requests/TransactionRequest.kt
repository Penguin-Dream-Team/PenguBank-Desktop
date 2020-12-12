package models.requests

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject

class TransactionRequest : JsonModel {

    val destinationEmailProperty = SimpleStringProperty()
    private var destinationEmail: String by destinationEmailProperty

    val amountProperty = SimpleIntegerProperty()
    private var amount: Int by amountProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            destinationEmail = string("destinationEmail")!!
            amount = int("amount")!!
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("destinationEmail", destinationEmail)
            add("amount", amount)
        }
    }
}

class TransactionRequestModel(transactionRequest: TransactionRequest = TransactionRequest()) : ItemViewModel<TransactionRequest>(transactionRequest) {
    var destinationEmail = bind(TransactionRequest::destinationEmailProperty)
    val amount = bind(TransactionRequest::amountProperty)
}
