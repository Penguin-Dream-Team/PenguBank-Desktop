package view.settings

import controllers.Activate2FAController
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ButtonBar
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import models.requests.Verify2FARequestModel
import tornadofx.*

class Enable2FAModal : View("PenguBank | Enable 2FA") {
    private val model = Verify2FARequestModel()
    private val activate2FAController: Activate2FAController by inject()

    override val root = borderpane {
        prefWidth = 380.0

        top = vbox(5) {
            paddingVertical = 20.0
            paddingHorizontal = 40.0

            style {
                backgroundColor += Color.WHITE
                borderColor += box(Color.TRANSPARENT, Color.TRANSPARENT, c("#00000033"), Color.TRANSPARENT)
            }

            label("Enable 2FA") {
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

                text += "To enable 2FA verification, you need to scan the displayed QRCode using our mobile application."
                text += "\n\nUpon doing so, you have to insert the code inside the box below in order to validate the entire process."
            }
        }

        center = vbox {
            paddingTop = 20.0
            paddingHorizontal = 40.0
            alignment = Pos.CENTER

            imageview(Image(activate2FAController.qrCodeURL, 320.0, 320.0, true, true))

            form {
                fieldset {
                    field("Code") {
                        paddingHorizontal = 200.0

                        style {
                            fontSize = 18.px
                        }

                        textfield(model.code) {
                            filterInput { it.controlNewText.isInt() && it.controlNewText.length <= 6 }
                        }.validator { if (it.isNullOrBlank() || it.length != 6) error("Invalid code") else null }
                    }
                }

                label(activate2FAController.statusProperty) {
                    addClass(Styles.status)
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
                action(activate2FAController::cancel)
            }

            button("Verify", type = ButtonBar.ButtonData.FINISH) {
                enableWhen(model.valid)
                isDefaultButton = true

                action {
                    runAsyncWithProgress {
                        model.commit()
                        activate2FAController.confirmActivate2FA(model.item)
                    }
                }
            }
        }
    }

    override fun onUndock() {
        model.code.set("")
        model.commit()
        model.clearDecorators()
    }
}

