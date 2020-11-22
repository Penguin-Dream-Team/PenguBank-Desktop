package utils

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

fun safeExecute(statusProperty: SimpleStringProperty, request: () -> Unit) {
    try {
        request()
    } catch (e: RestException) {
        runLater { statusProperty.value = "Cannot connect to the server" }
    }
}
