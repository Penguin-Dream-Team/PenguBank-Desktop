package bluetooth

import bluetooth.models.ConnectionIdentityRequest
import security.SecurityConnection
import javax.microedition.io.StreamConnection

class ConnectionIdentityService(connection: StreamConnection, securityConnection: SecurityConnection) : BluetoothService(connection, securityConnection) {
    fun sendVerificationRequest() {
        val data = "data"
        val digest = "string"
        sendMessage(ConnectionIdentityRequest(data, digest))
    }

    init {
        // start DH service: all messages are signed
        sendVerificationRequest()
    }
}