package view

import Styles
import tornadofx.*

class MainView : View("Hello TornadoFX") {

    override val root = hbox {
        prefWidth = 1280.0
        prefHeight = 720.0

        label(title) {
            addClass(Styles.heading)
        }
    }
}
