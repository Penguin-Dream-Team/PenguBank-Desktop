package models

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject

class Transaction : JsonModel {

    val idProperty = SimpleIntegerProperty()
    private var id by idProperty

    val amountProperty = SimpleIntegerProperty()
    var amount by amountProperty

    val otherProperty = SimpleStringProperty()
    private var other by otherProperty

    val createdAtProperty = SimpleStringProperty()
    private var createdAt by createdAtProperty

    val typeProperty = SimpleObjectProperty<TransactionType>()
    private var type by typeProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            id = int("id")!!
            amount = int("amount")!!
            other = string("other")!!
            createdAt = string("createdAt")!!
            type = TransactionType.valueOf(string("type")!!)
        }
    }

    override fun toString() = "Transaction #$id | ${
        if (type == TransactionType.RECEIVED) "From" else "To"
    }: $other | Amount: $amount | Date: $createdAt"

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("amount", amount)
            add("other", other)
            add("createdAt", createdAt)
            add("type", type.toString())
        }
    }
}

enum class TransactionType(private val type: String) {
    RECEIVED("RECEIVED"),
    SENT("SENT");

    override fun toString() = type
}
