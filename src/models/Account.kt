package models

import javafx.beans.property.SimpleIntegerProperty
import tornadofx.*
import javax.json.JsonObject

class Account : JsonModel {

    val idProperty = SimpleIntegerProperty()
    private var id by idProperty

    val balanceProperty = SimpleIntegerProperty()
    var balance by balanceProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            id = int("id")!!
            balance = int("balance")!!
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("balance", balance)
        }
    }
}

class AccountModel(account: Account? = null) : ItemViewModel<Account>(account) {
    val id = bind(Account::idProperty)
    val balance = bind(Account::balanceProperty)
}


