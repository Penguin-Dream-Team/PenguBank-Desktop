package controllers

import javafx.beans.property.SimpleStringProperty
import models.requests.LoginRequest
import models.requests.Verify2FARequest
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import utils.safeExecute
import view.DashboardView
import view.userforms.LoginView
import view.userforms.Verify2FAView

class DashboardController : Controller() {
    private val api: Rest by inject()

    private val store: Store by inject()

    private val statusProperty = SimpleStringProperty()
    var status: String by statusProperty

    fun refreshDashboard() {
        runLater { status = "" }

        safeExecute(statusProperty) {
            val response = api.get("dashboard")
            val json = response.one()

            runLater {
                if(response.ok()) {
                    store.account.item = json.jsonModel("data")
                    store.token.item = json.toModel()
                } else {
                    status = json.string("message") ?: "Oops, something went wrong!"
                }
            }
        }
    }
}