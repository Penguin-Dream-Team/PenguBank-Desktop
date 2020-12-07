package utils.bluetooth

import com.intel.bluetooth.BluetoothConsts
import javax.bluetooth.DiscoveryAgent
import javax.bluetooth.LocalDevice
import javax.bluetooth.UUID
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnectionNotifier

object WaitForDevicesThread : Runnable {

    override fun run() {
        try {
            val localDevice = LocalDevice.getLocalDevice()
            localDevice.discoverable = DiscoveryAgent.GIAC


            //val local = BluetoothUtils.getBluetoothAddress().replace(":", "")
            val local = "localhost"
            val url = "btspp://${local}:${BluetoothConsts.RFCOMM_PROTOCOL_UUID};name=PenguBankDesktop"
            val notifier = Connector.open(url) as StreamConnectionNotifier
            localDevice.getRecord(notifier)

            while (true) {
                println("Waiting for connection... So lonely")
                println("My address is: ${localDevice.bluetoothAddress}")
                println("My url is: $url")
                val connection = notifier.acceptAndOpen()
                println("hello")
                Thread(ProcessConnectionThread(connection)).start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

}