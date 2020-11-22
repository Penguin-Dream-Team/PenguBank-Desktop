package models.requests

import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject

class RegisterRequest : JsonModel {

    val emailProperty = SimpleStringProperty()
    private var email: String by emailProperty

    val passwordProperty = SimpleStringProperty()
    private var password: String by passwordProperty

    val confirmPasswordProperty = SimpleStringProperty()
    private var confirmPassword: String by confirmPasswordProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            email = string("email")!!
            password = string("password")!!
            confirmPassword = string("confirmPassword")!!
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("email", email)
            add("password", password)
            add("confirmPassword", confirmPassword)
        }
    }
}

class RegisterRequestModel(registerRequest: RegisterRequest = RegisterRequest()) : ItemViewModel<RegisterRequest>(registerRequest) {
    val email = bind(RegisterRequest::emailProperty)
    val password = bind(RegisterRequest::passwordProperty)
    val confirmPassword = bind(RegisterRequest::confirmPasswordProperty)
}
