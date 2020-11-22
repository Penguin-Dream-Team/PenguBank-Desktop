package models.requests

import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject

class Verify2FARequest : JsonModel {

    val codeProperty = SimpleStringProperty()
    private var code: String by codeProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            code = string("code")!!
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("code", code)
        }
    }
}

class Verify2FARequestModel(verify2FARequest: Verify2FARequest = Verify2FARequest()) : ItemViewModel<Verify2FARequest>(verify2FARequest) {
    val code = bind(Verify2FARequest::codeProperty)
}


