import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import view.MainView

class PenguBankApplication: App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            isResizable = false
        }
        super.start(stage)
    }
}

fun main() = launch<PenguBankApplication>()
