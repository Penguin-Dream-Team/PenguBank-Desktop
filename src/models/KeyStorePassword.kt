package models

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class KeyStorePassword {
    val passwordProperty = SimpleStringProperty()
    private val password by passwordProperty
}

class KeyStorePasswordViewModel : ItemViewModel<KeyStorePassword>(KeyStorePassword()) {
    val password = bind(KeyStorePassword::passwordProperty)
}

