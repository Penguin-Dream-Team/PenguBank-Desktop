package controllers

import bluetooth.BluetoothConnectionMaster
import javafx.beans.property.SimpleStringProperty
import org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi
import org.bouncycastle.jce.provider.BouncyCastleProvider
import security.SecurityConnection
import security.SecurityUtils
import tornadofx.*
import utils.safeExecute
import view.settings.Enable2FAModal
import view.settings.QueuedTransactionModal
import javax.crypto.KeyAgreement

class BluetoothConnectionController : Controller() {
    private val api: Rest by inject()

    private val store: Store by inject()

    private val statusProperty = SimpleStringProperty()
    var status: String by statusProperty

    fun stop() {
        store.bluetoothConnectionMaster?.quit()
        store.hasBluetoothConnection = false
    }

    fun start(password: String) {
        SecurityUtils.init(password)

        store.bluetoothConnectionMaster = BluetoothConnectionMaster(
            SecurityConnection(
                SecurityUtils.getPrivateKey(password),
                store.mobilePublicKey
            )
        )

        store.hasBluetoothConnection = true
        store.bluetoothConnectionMaster?.startServer()

        runLater {
            find<QueuedTransactionModal>().openModal(resizable = false, block = true)
        }
    }

    fun requestPhonePublicKey() {
        runLater { status = "" }

        safeExecute(statusProperty) {
            val response = api.get("dashboard/myphonekey")
            val json = response.one()

            runLater {
                try {
                    if (response.ok()) {
                        store.mobilePublicKey =
                            SecurityUtils.parsePublicKey(json.jsonObject("data")!!.string("phonePublicKey")!!)
                    } else
                        throw RuntimeException("Oops, something went wrong!")
                } catch (e: Throwable) {
                    status = json.string("message") ?: "Oops, something went wrong!"
                }
            }
        }
    }

}