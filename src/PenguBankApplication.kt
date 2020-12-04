import controllers.Store
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*
import view.userforms.LoginView
import javax.bluetooth.LocalDevice

class PenguBankApplication : App(LoginView::class, Styles::class) {
    private val api: Rest by inject()
    private val store: Store by inject()

    init {
        api.baseURI = "https://pengubank.club"
        api.engine.requestInterceptor = { request ->
            val token = store.token.token
            if (token.valueSafe.isNotBlank())
                request.addHeader("Authorization", "Bearer ${token.value}")
        }
    }

    override fun start(stage: Stage) {
        with(stage) {
            isResizable = false
        }

        /*var bluetoothAddress = LocalDevice.getLocalDevice().bluetoothAddress.toUpperCase().replace("(.{2})".toRegex(), "$1:")
        bluetoothAddress = bluetoothAddress.substring(0, bluetoothAddress.length - 1)
        println(bluetoothAddress)*/

        super.start(stage)
    }
}

fun main() = launch<PenguBankApplication>()

object PenguBankApplicationConstants {
    val imageLogo = Image("images/logo.jpg")
    const val USER_FORM_WIDTH = 380.0
    const val USER_FORM_HEIGHT = 630.0
}
