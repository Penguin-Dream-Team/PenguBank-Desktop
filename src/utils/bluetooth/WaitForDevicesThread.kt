package utils.bluetooth

import com.intel.bluetooth.BluetoothConsts
import security.SecurityConnection
import javax.bluetooth.DiscoveryAgent
import javax.bluetooth.LocalDevice
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnectionNotifier

class WaitForDevicesThread(private val securityConnection: SecurityConnection) : Runnable {

    override fun run() {
        try {
            val localDevice = LocalDevice.getLocalDevice()
            localDevice.discoverable = DiscoveryAgent.GIAC


            val local = "localhost"
            val url = "btspp://${local}:${BluetoothConsts.RFCOMM_PROTOCOL_UUID};name=PenguBankDesktop"
            val notifier = Connector.open(url) as StreamConnectionNotifier
            localDevice.getRecord(notifier)

            while (true) {
                println("Waiting for connection... So lonely")
                println("My address is: ${localDevice.bluetoothAddress}")
                println("My url is: $url")
                val connection = notifier.acceptAndOpen()
                Thread(ProcessConnectionThread(connection, securityConnection)).start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

}