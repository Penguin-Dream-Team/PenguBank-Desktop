package bluetooth

import com.intel.bluetooth.BluetoothConsts
import controllers.BluetoothConnectionController
import security.SignatureConnectionHandler
import java.io.IOException
import java.lang.Exception
import javax.bluetooth.DiscoveryAgent
import javax.bluetooth.LocalDevice
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnection
import javax.microedition.io.StreamConnectionNotifier

class BluetoothConnectionMaster(
    private val securityConnection: SignatureConnectionHandler,
    private val bluetoothConnectionController: BluetoothConnectionController
) {
    private val localDevice = LocalDevice.getLocalDevice()
    private var connection: StreamConnection? = null
    private var notifier: StreamConnectionNotifier? = null

    private var thread: Thread = Thread {
        if (localDevice.discoverable != DiscoveryAgent.GIAC)
            localDevice.discoverable = DiscoveryAgent.GIAC

        // localhost -> server address
        val url = "btspp://localhost:${BluetoothConsts.RFCOMM_PROTOCOL_UUID};name=PenguBankDesktop"
        notifier = Connector.open(url) as StreamConnectionNotifier
        localDevice.getRecord(notifier)

        while (true) {
            try {
                connection = notifier!!.acceptAndOpen()
                BluetoothDiffieHellmanHandshakeService(
                    securityConnection,
                    bluetoothConnectionController,
                    connection!!.openDataInputStream(),
                    connection!!.openDataOutputStream()
                )
                connection?.close()
            } catch (e: IOException) {
                connection?.close()
                if (notifier == null)
                    break
            }
        }
    }

    fun startServer() {
        thread.start()
    }

    fun quit() {
        try {
            connection?.close()
            notifier?.close()
            connection = null
            notifier = null
            thread.join(1000)
        } catch (e: Exception) {
            // do nothing
        }
    }
}