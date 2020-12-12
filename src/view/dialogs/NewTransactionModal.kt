package view.dialogs

import controllers.DashboardController
import controllers.Store
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.ButtonBar
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import models.requests.TransactionRequestModel
import tornadofx.*
import utils.euroToInt
import utils.isValidEmail

class NewTransactionModal() : View("PenguBank | New Transaction") {
    private val store: Store by inject()
    private val model = TransactionRequestModel()
    private val dashboardController: DashboardController by inject()

    private val enabledProperty = SimpleBooleanProperty(true)
    var enabled by enabledProperty

    override val root = borderpane {
        prefWidth = 580.0

        top = vbox(5) {
            paddingVertical = 20.0
            paddingHorizontal = 40.0

            style {
                backgroundColor += Color.WHITE
                borderColor += box(Color.TRANSPARENT, Color.TRANSPARENT, c("#00000033"), Color.TRANSPARENT)
            }

            label("New Transaction") {
                style {
                    fontSize = 18.px
                    fontWeight = FontWeight.BOLD
                }
            }

            label {
                isWrapText = true
                style {
                    fontSize = 14.px
                }

                text += "Perform a new Transaction."
            }
        }

        center = vbox {
            paddingTop = 20.0
            paddingHorizontal = 40.0
            alignment = Pos.CENTER

            form {
                fieldset {
                    field("Accound Destination Email") {
                        paddingHorizontal = 200.0

                        style {
                            fontSize = 18.px
                        }

                        textfield(model.destinationEmail).validator {
                            if (it.isNullOrBlank()) error("The email cannot be blank")
                            else if (!it.isValidEmail()) error("This is not a valid email")
                            else null
                        }
                    }

                    field("Amount") {
                        paddingHorizontal = 200.0

                        style {
                            fontSize = 18.px
                        }

                        textfield(model.amount) {
                            filterInput { it.controlNewText.isInt() && it.controlNewText.toInt() <= store.balance.euroToInt() }
                        }.validator { if (it.isNullOrBlank() || model.amount.value > store.balance.euroToInt()) error("Not enough money to perform the transaction.") else null }
                    }
                }
            }
        }

        bottom = buttonbar {
            paddingHorizontal = 40.0
            paddingVertical = 25.0

            style {
                backgroundColor += Color.WHITE
                borderColor += box(c("#00000033"), Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT)
            }

            button("Cancel", type = ButtonBar.ButtonData.CANCEL_CLOSE) {
                action(dashboardController::cancelTransaction)
            }

            button("Perform Transaction", type = ButtonBar.ButtonData.FINISH) {
                enableWhen(enabledProperty.and(model.valid))
                isDefaultButton = true

                action {
                    enabled = false
                    runAsyncWithProgress {
                        model.commit()
                        dashboardController.newTransaction(model.item)
                        enabled = true
                    }
                }
            }
        }
    }

    override fun onUndock() {
        model.destinationEmail.set("")
        model.amount.set(0)
        model.commit()
        model.clearDecorators()
    }
}

