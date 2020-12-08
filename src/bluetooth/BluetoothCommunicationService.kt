package bluetooth

import bluetooth.models.messages.*
import controllers.BluetoothConnectionController
import security.SignatureConnectionHandler
import java.io.DataInputStream
import java.io.DataOutputStream
import javax.crypto.SecretKey

class BluetoothCommunicationService(
    private val secretKey: SecretKey,
    securityConnection: SignatureConnectionHandler,
    private val bluetoothConnectionController: BluetoothConnectionController,
    inputStream: DataInputStream,
    outputStream: DataOutputStream
) : BluetoothService(securityConnection, inputStream, outputStream) {

    init {
        while (true) {
            try {
                when (val request = receiveAndDecipherMessage(secretKey)) {
                    is RetrievePendingTransactionsRequest -> handleRetrievePendingTransaction()
                    is UpdatePendingTransactionRequest -> handleUpdatePendingTransaction(request)
                }
            } catch (e: Exception) {
                break
            }
        }
    }

    private fun handleUpdatePendingTransaction(request: UpdatePendingTransactionRequest) {
        try {
            bluetoothConnectionController.updateTransaction(request.id, request.signedToken, request.type)
            cipherAndSendMessage(secretKey, UpdatePendingTransactionResponse())
        } catch (e: Exception) {
            cipherAndSendMessage(secretKey, ErrorResponse(e.message ?: "An error occurred."))
        }
    }

    private fun handleRetrievePendingTransaction() {
        try {
            val pendingTransactions = bluetoothConnectionController.retrievePendingTransactions()
            cipherAndSendMessage(secretKey, RetrievePendingTransactionsResponse(pendingTransactions))
        } catch (e: Exception) {
            cipherAndSendMessage(secretKey, ErrorResponse(e.message ?: "An error occurred."))
        }
    }
}