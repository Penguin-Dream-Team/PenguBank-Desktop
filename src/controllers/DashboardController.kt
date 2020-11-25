package controllers

import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import utils.safeExecute
import utils.toEuros

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
                    store.balance = store.account.balance.value.toEuros()
                    store.transactions.items.setAll()
                    store.transactions.addAll(json.jsonObject("data")!!.jsonArray("transactions")!!.toModel())
                } else {
                    status = json.string("message") ?: "Oops, something went wrong!"
                }
            }
        }
    }
}