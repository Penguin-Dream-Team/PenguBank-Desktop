package bluetooth

import bluetooth.models.JSONObject
import bluetooth.models.toObject
import security.SecurityConnection
import java.io.DataInputStream
import java.io.DataOutputStream
import javax.microedition.io.StreamConnection

abstract class BluetoothService(connection: StreamConnection, securityConnection: SecurityConnection) {
    protected val inputStream: DataInputStream = connection.openDataInputStream()
    private val outputStream: DataOutputStream = connection.openDataOutputStream()

    protected fun <T : JSONObject> sendMessage(data: T) {
        outputStream.writeUTF(data.toJSON())
        outputStream.flush()
    }
    protected inline fun <reified T : JSONObject> receiveMessage(): T =
        inputStream.readUTF().toObject()
}

