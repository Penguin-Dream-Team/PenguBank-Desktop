package controllers

import javafx.beans.property.SimpleStringProperty
import models.requests.TransactionRequest
import tornadofx.*
import utils.safeExecute
import utils.toEuros
import view.userforms.LoginView
import view.userforms.RegisterView

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

    fun newTransaction(transactionRequest: TransactionRequest) {
        runLater { status = "" }

        safeExecute(statusProperty) {
            val response = api.put("transaction", transactionRequest)
            val json = response.one()

            runLater {
                if(response.ok()) {
                    information("Transaction Succeeded") {
                        //find(RegisterView::class).replaceWith<LoginView>(sizeToScene = true, centerOnScreen = true, transition = ViewTransition.FadeThrough(.3.seconds))
                    }
                } else {
                    status = json.string("message") ?: "Oops, something went wrong!"
                    error(status)
                }
            }
        }
    }
}