package models.requests

import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject

class LoginRequest : JsonModel {

    val emailProperty = SimpleStringProperty()
    private var email: String by emailProperty

    val passwordProperty = SimpleStringProperty()
    private var password: String by passwordProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            email = string("email")!!
            password = string("password")!!
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("email", email)
            add("password", password)
        }
    }
}

class LoginRequestModel(loginRequest: LoginRequest = LoginRequest()) : ItemViewModel<LoginRequest>(loginRequest) {
    val email = bind(LoginRequest::emailProperty)
    val password = bind(LoginRequest::passwordProperty)
}


