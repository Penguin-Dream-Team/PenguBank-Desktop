package view.settings

import controllers.Store
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import utils.generateQrCode

class QueuedTransactionModal() : View("PenguBank | Queued Transaction") {
    private val store: Store by inject()

    override val root = borderpane {
        prefWidth = 580.0

        top = vbox(5) {
            paddingVertical = 20.0
            paddingHorizontal = 40.0

            style {
                backgroundColor += Color.WHITE
                borderColor += box(Color.TRANSPARENT, Color.TRANSPARENT, c("#00000033"), Color.TRANSPARENT)
            }

            label("Confirm Transaction") {
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

                text += "Scan this QRCode to start a Bluetooth connection and confirm your transaction on your smartphone application."
            }
        }

        center = vbox {
            paddingTop = 20.0
            paddingHorizontal = 40.0
            alignment = Pos.CENTER


            form {
                fieldset {
                    imageview(Image(generateQrCode(store.user.email.toString()), 320.0, 320.0, true, true))
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
        }
    }
}

