package view.dialogs

import controllers.BluetoothConnectionController
import controllers.Store
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Orientation
import models.KeyStorePasswordViewModel
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue

class KeyPasswordDialog : View("Insert Global Password") {

    private val model = KeyStorePasswordViewModel()
    private val bluetoothConnectionController: BluetoothConnectionController by inject()

    private val enabledProperty = SimpleBooleanProperty(true)
    var enabled by enabledProperty

    override val root = borderpane {
        top = vbox {
            label("Please input the global password you defined for the application.")
        }

        center = form {
            fieldset("KeyStore Password", labelPosition = Orientation.VERTICAL) {

                field("Password") {
                    passwordfield(model.password).validator {
                        when {
                            it.isNullOrBlank() -> error("The password cannot be blank")
                            it.length < 8 -> error("The password needs to be at least 8 characters long")
                            else -> null
                        }
                    }
                }
            }

            button("Finish") {
                enableWhen(model.valid.and(enabledProperty))
                isDefaultButton = true
                useMaxWidth = true

                action {
                    enabled = false
                    runAsyncWithProgress {
                        model.commit()

                        try {
                            bluetoothConnectionController.start(model.password.valueSafe)
                            runLater {
                                close()
                            }
                        } catch (e: Exception) {
                            runLater {
                                error("Wrong password") {
                                    clean()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun clean() {
        model.password.value = ""
        model.commit()
        model.clearDecorators()
        enabled = true
    }

    override fun onUndock() {
        clean()
    }
}
