package bluetooth.models.messages

import bluetooth.models.JSONObject

data class RetrievePendingTransactionsRequest(val nonce: Long) : JSONObject