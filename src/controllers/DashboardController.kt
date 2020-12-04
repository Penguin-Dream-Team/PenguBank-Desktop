package controllers

import javafx.beans.property.SimpleStringProperty
import models.requests.TransactionRequest
import models.requests.UpdateTransactionRequest
import tornadofx.*
import utils.safeExecute
import utils.toEuros
import view.settings.NewTransactionModal
import view.settings.QueuedTransactionModal

class DashboardController : Controller() {
    private val api: Rest by inject()

    private val store: Store by inject()

    private val statusProperty = SimpleStringProperty()
    var status: String by statusProperty

    fun cancelTransaction() {
        status = ""
        find<NewTransactionModal>().close()
    }

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
                    information("Transaction Queued") {
                        find<NewTransactionModal>().replaceWith<QueuedTransactionModal>(sizeToScene = true, centerOnScreen = true, transition = ViewTransition.FadeThrough(.3.seconds))
                    }
                } else {
                    status = json.string("message") ?: "Oops, something went wrong!"
                    error(status)
                }
            }
        }
    }

    fun newQueuedTransaction() {
        status = ""
        find<NewTransactionModal>().close()
        find<QueuedTransactionModal>().openModal(resizable = false)
    }

    fun updateTransaction(action: String, updateTransactionRequest: UpdateTransactionRequest) {
        runLater { status = "" }

        safeExecute(statusProperty) {
            val response = api.patch("transaction/${action}", updateTransactionRequest)
            val json = response.one()

            runLater {
                if(response.ok()) {
                    val message = if (action == "approve") { "Transaction Approved" } else { "Transaction Canceled" }
                    information(message) {
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