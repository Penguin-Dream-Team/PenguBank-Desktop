package models

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import javax.json.JsonObject

class User : JsonModel {

    val idProperty = SimpleIntegerProperty()
    private var id by idProperty

    val emailProperty = SimpleStringProperty()
    private var email by emailProperty

    val registeredAtProperty = SimpleStringProperty()
    private var registeredAt by registeredAtProperty

    val enabled2FAProperty = SimpleBooleanProperty()
    var enabled2FA by enabled2FAProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            id = int("id")!!
            email = string("email")!!
            registeredAt = string("registeredAt")!!
            enabled2FA = boolean("enabled2FA")!!
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("email", email)
            add("registeredAt", registeredAt)
            add("enabled2FA", enabled2FA)
        }
    }
}

class UserModel(user: User? = null) : ItemViewModel<User>(user) {
    val id = bind(User::idProperty)
    val email = bind(User::emailProperty)
    val registeredAt = bind(User::registeredAtProperty)
    val enabled2FA = bind(User::enabled2FAProperty)
}


