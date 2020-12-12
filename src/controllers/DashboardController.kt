package controllers

import javafx.beans.property.SimpleStringProperty
import models.requests.TransactionRequest
import tornadofx.*
import utils.safeExecute
import utils.toEuros
import view.dialogs.NewTransactionModal

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
                if (response.ok()) {
                    store.account.item = json.jsonModel("data")
                    store.token.item = json.toModel()
                    store.balance = store.account.balance.value.toEuros()

                    store.transactions.clear()
                    store.transactions.addAll(json.jsonObject("data")!!.jsonArray("transactions")!!.toModel())
                    store.hasTransactions = store.transactions.size > 0

                    store.pendingTransactions.clear()
                    store.pendingTransactions.addAll(
                        json.jsonObject("data")!!.jsonArray("queuedTransactions")!!.toModel()
                    )
                    store.hasPendingTransactions = store.pendingTransactions.size > 0
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
                if (response.ok()) {
                    store.token.item = json.toModel()
                    information("Transaction Queued")
                    find<NewTransactionModal>().close()
                } else {
                    status = json.string("message") ?: "Oops, something went wrong!"
                    error(status)
                }
            }
        }

        refreshDashboard()
    }
}