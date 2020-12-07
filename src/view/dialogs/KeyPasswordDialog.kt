package view.dialogs

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Orientation
import models.KeyStorePasswordViewModel
import security.SecurityUtils
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import view.settings.QueuedTransactionModal

class KeyPasswordDialog : View("Insert Global Password") {

    private val model = KeyStorePasswordViewModel()

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
                            SecurityUtils.init(model.password.valueSafe)
                            runLater {
                                find<QueuedTransactionModal>().openModal(resizable = false, block = true)
                                close()
                            }
                        } catch (_: Exception) {
                            runLater {
                                error("Wrong password") {
                                    model.password.value = ""
                                    model.commit()
                                    model.clearDecorators()
                                }
                            }
                            enabled = true
                        }
                    }
                }
            }
        }
    }
}
