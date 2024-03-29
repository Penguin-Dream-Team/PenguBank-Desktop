package controllers

import javafx.beans.property.SimpleStringProperty
import models.requests.Verify2FARequest
import tornadofx.*
import utils.safeExecute
import view.DashboardView
import view.settings.Enable2FAModal
import view.userforms.LoginView
import view.userforms.RegisterView
import view.userforms.Verify2FAView

class Activate2FAController : Controller() {
    private val api: Rest by inject()

    private val store: Store by inject()

    val statusProperty = SimpleStringProperty()
    var status: String by statusProperty

    private val qrCodeURLProperty = SimpleStringProperty()
    var qrCodeURL: String by qrCodeURLProperty

    private lateinit var modal: Enable2FAModal

    fun cancel() {
        qrCodeURL = ""
        status = ""
        modal.close()
    }

    fun requestActivate2FA() {
        runLater { status = "" }

        safeExecute(statusProperty) {
            val response = api.post("activate")
            val json = response.one()

            runLater {
                try {
                    if (response.ok()) {
                        qrCodeURL = json.jsonObject("data")!!.string("qrcode")!!
                        store.token.item = json.toModel()

                        modal = Enable2FAModal()
                        modal.openModal(resizable = false)
                    } else
                        throw RuntimeException("Oops, something went wrong!")
                } catch (e: Throwable) {
                    status = json.string("message") ?: "Oops, something went wrong!"
                }
            }
        }
    }

    fun confirmActivate2FA(verify2FARequest: Verify2FARequest) {
        runLater { status = "" }

        safeExecute(statusProperty) {
            val response = api.put("activate", verify2FARequest)
            val json = response.one()

            runLater {
                if(response.ok()) {
                    store.user.item = json.jsonModel("data")
                    store.token.item = json.toModel()

                    information("Successfully verified! Enabled 2FA") {
                        cancel()
                    }
                } else {
                    status = json.string("message") ?: "Oops, something went wrong!"
                }
            }
        }
    }
}