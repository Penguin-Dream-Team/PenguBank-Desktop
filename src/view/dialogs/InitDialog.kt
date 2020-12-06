package view.dialogs

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Orientation
import models.KeyStorePasswordViewModel
import security.SecurityUtils
import tornadofx.*
import kotlin.system.exitProcess
import tornadofx.getValue
import tornadofx.setValue
import view.userforms.LoginView

class InitDialog : View("Initialize PenguBank Application") {

    private val model = KeyStorePasswordViewModel()

    private val enabledProperty = SimpleBooleanProperty(true)
    var enabled by enabledProperty

    override val root = borderpane {
        top = vbox {
            label("Please choose a global password for the application.")
            label("This password will be used to protect the application keys.")
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
                                find<LoginView>().openWindow(resizable = false, escapeClosesWindow = false)
                                close()
                            }
                        } catch (_: Exception) {
                            error("An error occurred.") {
                                exitProcess(-1)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            it.consume()
        }
    }
}
