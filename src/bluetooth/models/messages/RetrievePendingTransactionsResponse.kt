package bluetooth.models.messages

import bluetooth.models.JSONObject
import models.PendingTransaction

data class RetrievePendingTransactionsResponse(val pendingTransactions: List<PendingTransaction>) : JSONObject

