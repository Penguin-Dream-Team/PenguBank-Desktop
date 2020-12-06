package utils.bluetooth

import java.util.*
import javax.bluetooth.DiscoveryAgent
import javax.bluetooth.LocalDevice
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnectionNotifier

object WaitForDevicesThread : Runnable {

    override fun run() {
        try {
            val localDevice = LocalDevice.getLocalDevice()
            localDevice.discoverable = DiscoveryAgent.GIAC

            val uuid = UUID.fromString("dd448ec5-5c6b-49e6-91d4-bb0e64bc0701")
            val url = "btspp://localhost:$uuid;name=PenguBank"
            val notifier = Connector.open(url) as StreamConnectionNotifier

            while (true) {
                println("Waiting for connection... So lonely")
                val connection = notifier.acceptAndOpen()
                Thread(ProcessConnectionThread(connection)).start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

}