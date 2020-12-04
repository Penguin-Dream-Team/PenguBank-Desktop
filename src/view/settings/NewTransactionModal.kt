package view.settings

import controllers.DashboardController
import controllers.Store
import javafx.geometry.Pos
import javafx.scene.control.ButtonBar
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import models.requests.TransactionRequestModel
import tornadofx.*
import utils.euroToInt

class NewTransactionModal() : View("PenguBank | New Transaction") {
    private val store: Store by inject()
    private val model = TransactionRequestModel()
    private val dashboardController: DashboardController by inject()

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
                    field("Accound Destination Id") {
                        paddingHorizontal = 200.0

                        style {
                            fontSize = 18.px
                        }

                        textfield(model.destinationId) {
                            filterInput { it.controlNewText.isInt() && it.controlNewText.toInt() >= 0}
                        }.validator { if (it.isNullOrBlank() || model.destinationId.value < 0) error("Account Id must be non-negative.") else null }
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

            button("Cancel Transaction", type = ButtonBar.ButtonData.CANCEL_CLOSE) {
                action(dashboardController::cancelTransaction)
            }

            button("Perform Transaction", type = ButtonBar.ButtonData.FINISH) {
                enableWhen(model.valid)
                isDefaultButton = true

                action {
                    runAsyncWithProgress {
                        model.commit()
                        dashboardController.newTransaction(model.item)
                    }
                }
            }
        }
    }

    override fun onUndock() {
        model.destinationId.set(0)
        model.amount.set(0)
        model.commit()
        model.clearDecorators()
    }
}

