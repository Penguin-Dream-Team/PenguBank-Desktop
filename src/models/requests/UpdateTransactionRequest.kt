package models.requests

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject

class UpdateTransactionRequest : JsonModel {

    val transactionIdProperty = SimpleIntegerProperty()
    private var transactionId: Int by transactionIdProperty

    val signedTokenProperty = SimpleStringProperty()
    private var signedToken: String by signedTokenProperty

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

class UpdateTransactionRequestModel(updateTransactionRequest: UpdateTransactionRequest = UpdateTransactionRequest()) : ItemViewModel<UpdateTransactionRequest>(updateTransactionRequest) {
    var transactionId = bind(UpdateTransactionRequest::transactionIdProperty)
    val signedToken = bind(UpdateTransactionRequest::signedTokenProperty)
}
