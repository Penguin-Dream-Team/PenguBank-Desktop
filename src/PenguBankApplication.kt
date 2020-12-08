import controllers.Store
import javafx.scene.image.Image
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import security.SecurityUtils
import tornadofx.*
import utils.BluetoothUtils
import view.dialogs.InitDialog
import view.userforms.LoginView
import kotlin.system.exitProcess

class PenguBankApplication : App(LoginView::class, Styles::class) {
    private val api: Rest by inject()
    val store: Store by inject()

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

        println(BluetoothUtils.getBluetoothAddress())

        super.start(stage)
        if (!SecurityUtils.hasKey()) {
            stage.close()
            find<InitDialog>().openModal(
                stageStyle = StageStyle.UNDECORATED,
                escapeClosesWindow = false,
                block = true,
                resizable = false,
                modality = Modality.WINDOW_MODAL
            )
        }
    }
}

fun main() {
     launch<PenguBankApplication>()
    exitProcess(0)
}

object PenguBankApplicationConstants {
    val imageLogo = Image("images/logo.jpg")
    const val USER_FORM_WIDTH = 380.0
    const val USER_FORM_HEIGHT = 630.0
}
