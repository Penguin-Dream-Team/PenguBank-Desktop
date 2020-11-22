package view.partials

import PenguBankApplicationConstants
import Styles
import javafx.scene.paint.ImagePattern
import tornadofx.*

class LogoHeader : Fragment() {

    override val root = vbox {
        addClass(Styles.logoHeader)

        circle(radius = 75.0) {
            fill = ImagePattern(PenguBankApplicationConstants.imageLogo)
        }

        label("PenguBank") {
            addClass(Styles.heading)
        }

    }
}