package bluetooth.models.messages

import bluetooth.models.JSONObject

enum class PendingTransactionOperation {
    APPROVE,
    CANCEL
}

data class UpdatePendingTransactionRequest(val nonce: Long, val id: Int, val signedToken: String, val type: PendingTransactionOperation) : JSONObject