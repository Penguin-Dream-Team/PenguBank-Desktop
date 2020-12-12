package controllers

import bluetooth.BluetoothConnectionMaster
import models.PendingTransaction
import bluetooth.models.messages.PendingTransactionOperation
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import models.requests.UpdateTransactionRequest
import security.SecurityUtils
import security.SignatureConnectionHandler
import tornadofx.*
import utils.safeExecute
import view.settings.BluetoothConnectionModal

class BluetoothConnectionController : Controller() {
    private val api: Rest by inject()

    private val store: Store by inject()

    private val statusProperty = SimpleStringProperty()
    var status: String by statusProperty

    val connectedProperty = SimpleBooleanProperty(false)
    var connected: Boolean by connectedProperty

    fun stop() {
        store.bluetoothConnectionMaster?.quit()
        store.hasBluetoothConnection = false
        connected = false
    }

    fun start(password: String) {
        SecurityUtils.init(password)

        store.bluetoothConnectionMaster = BluetoothConnectionMaster(
            SignatureConnectionHandler(
                SecurityUtils.getPrivateKey(password),
                store.mobilePublicKey
            ),
            this
        )

        store.hasBluetoothConnection = true
        store.bluetoothConnectionMaster?.startServer()

        runLater {
            find<BluetoothConnectionModal>().openModal(resizable = false, block = true)
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
                        store.token.item = json.toModel()
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

    fun updateTransaction(
        id: Int,
        signedToken: String,
        type: PendingTransactionOperation
    ) {
        try {
            val request = UpdateTransactionRequest()
            request.transactionId = id
            request.signedToken = signedToken

            val response = if (type == PendingTransactionOperation.APPROVE) {
                api.put(
                    "transaction/${type.toString().toLowerCase()}",
                    request
                )
            } else {
                api.delete(
                    "transaction/${type.toString().toLowerCase()}",
                    request
                )
            }

            if (!response.ok())
                throw RuntimeException("Oops, something went wrong!")
            else {
                store.token.item = response.one().toModel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw java.lang.RuntimeException(e.message ?: "Oops, something went wrong!")
        }
    }

    fun retrievePendingTransactions(): List<PendingTransaction> {
        val response = api.get("transactions/pending")
        val json = response.one()

        try {
            if (response.ok()) {
                store.token.item = json.toModel()
                return json.jsonArray("data")!!.toModel()
            } else
                throw RuntimeException("Oops, something went wrong!")
        } catch (e: Exception) {
            throw java.lang.RuntimeException(response.one().string("message") ?: "Oops, something went wrong!")
        }
    }
}