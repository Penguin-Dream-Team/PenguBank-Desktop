package models

import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import javax.json.JsonObject

class UserToken : JsonModel {
    val tokenProperty = SimpleStringProperty()
    private var token by tokenProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            token = string("token")
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("token", token)
        }
    }
}

class UserTokenModel(userToken: UserToken? = null) : ItemViewModel<UserToken>(userToken) {
    val token = bind(UserToken::tokenProperty)
}
