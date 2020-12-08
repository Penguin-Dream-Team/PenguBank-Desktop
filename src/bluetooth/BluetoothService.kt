package bluetooth

import bluetooth.models.BluetoothMessage
import bluetooth.models.JSONObject
import bluetooth.models.messages.RetrievePendingTransactionsRequest
import bluetooth.models.messages.UpdatePendingTransactionRequest
import bluetooth.models.toObject
import exceptions.BluetoothMessageDuplicateNonceFailedException
import exceptions.BluetoothMessageSignatureFailedException
import security.SecurityUtils
import security.SignatureConnectionHandler
import java.io.DataInputStream
import java.io.DataOutputStream
import javax.crypto.SecretKey

abstract class BluetoothService(
    protected val securityConnection: SignatureConnectionHandler,
    protected val inputStream: DataInputStream,
    private val outputStream: DataOutputStream
) {
    private val usedNonces = mutableListOf<Long>()

    protected fun <T : JSONObject> sendMessage(data: T) {
        val message = BluetoothMessage(data.toJSON(), securityConnection.signData(data.toJSON()))
        outputStream.writeUTF(message.toJSON())
        outputStream.flush()
        println("SENT: ${message.data}")
    }

    protected fun <T : JSONObject> cipherAndSendMessage(secretKey: SecretKey, data: T) {
        val cipheredData = SecurityUtils.cipher(secretKey, data.toJSON())
        val message = BluetoothMessage(cipheredData, securityConnection.signData(data.toJSON()))
        outputStream.writeUTF(message.toJSON())
        outputStream.flush()
    }

    protected fun receiveAndDecipherMessage(secretKey: SecretKey): JSONObject {
        println("before")
        val message = inputStream.readUTF().toObject<BluetoothMessage>()
        println(message)

        val data = SecurityUtils.decipher(secretKey, message.data)
        println(data)

        if (!securityConnection.verifySignature(data, message.signature))
            throw BluetoothMessageSignatureFailedException

        // expect to receive always
        val receivedData = data.toObject<UpdatePendingTransactionRequest>()

        if(usedNonces.contains(receivedData.nonce))
            throw BluetoothMessageDuplicateNonceFailedException

        usedNonces.add(receivedData.nonce)

        // if not update request, then the id is always 0
        if(receivedData.id == 0)
            return RetrievePendingTransactionsRequest(receivedData.nonce)

        return receivedData
    }

    protected inline fun <reified T: JSONObject> receiveMessage(): T {
        val message = inputStream.readUTF().toObject<BluetoothMessage>()
        if (!securityConnection.verifySignature(message.data, message.signature))
            throw BluetoothMessageSignatureFailedException

        println("RECEIVED: ${message.data}")

        return message.data.toObject()
    }
}

