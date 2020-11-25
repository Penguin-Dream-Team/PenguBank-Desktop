import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val logoHeader by cssclass()

        val status by cssclass()
        val linkButton by cssclass()
        val userFormView by cssclass()
        val formBottom by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        logoHeader {
            padding = box(50.px, 0.px, 20.px, 0.px)
            alignment = Pos.CENTER
        }

        userFormView {
            prefWidth = PenguBankApplicationConstants.USER_FORM_WIDTH.px
            prefHeight = PenguBankApplicationConstants.USER_FORM_HEIGHT.px
            padding = box(15.px, 50.px)

            formBottom {
                alignment = Pos.CENTER
            }
        }

        form {
            alignment = Pos.TOP_CENTER
            padding = box(20.px, 0.px)

            label and status {
                padding = box(10.px, 0.px, 0.px, 0.px)
                fontWeight = FontWeight.BOLD
                textFill = Color.RED
            }
        }

        button and linkButton {
            backgroundColor += Color.TRANSPARENT
            borderStyle += BorderStrokeStyle.NONE
            textFill = Color.DEEPSKYBLUE
            cursor = Cursor.HAND
        }
    }
}
