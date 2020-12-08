package utils.bluetooth

import bluetooth.BluetoothService
import bluetooth.models.JSONObject
import com.google.gson.Gson
import security.SecurityConnection
import java.io.*
import javax.microedition.io.StreamConnection

class ProcessConnectionThread(private val connection: StreamConnection, private val securityConnection: SecurityConnection) : Runnable {

    override fun run() {
        try {
            val verificationBluetooth = VerificationBluetooth(connection, securityConnection)
            val response = verificationBluetooth.receiveTest()
            println(response)
            verificationBluetooth.sendTest(VerifyPublicKeyResponse(true))
            val response2 = verificationBluetooth.receiveTest()
            println(response2)
            verificationBluetooth.sendTest(VerifyPublicKeyResponse(false))
            connection.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }
}

data class VerifyPublicKeyRequest(val pubKey: String) : JSONObject
data class VerifyPublicKeyResponse(val ok: Boolean) : JSONObject

class VerificationBluetooth(connection: StreamConnection, securityConnection: SecurityConnection): BluetoothService(connection, securityConnection) {
    fun sendTest(data: VerifyPublicKeyResponse) = sendMessage(data)
    fun receiveTest() = receiveMessage<VerifyPublicKeyRequest>()
}


