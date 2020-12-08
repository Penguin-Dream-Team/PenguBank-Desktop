package utils

import javafx.scene.control.Alert
import tornadofx.*
import javax.bluetooth.BluetoothStateException
import javax.bluetooth.LocalDevice
import kotlin.system.exitProcess

object BluetoothUtils {
    fun getBluetoothAddress(): String {
        var bluetoothAddress = ""

        try {
            bluetoothAddress = LocalDevice.getLocalDevice().bluetoothAddress.toUpperCase().replace("(.{2})".toRegex(), "$1:")
            bluetoothAddress = bluetoothAddress.substring(0, bluetoothAddress.length - 1)
        } catch (e: BluetoothStateException) {
            alert(
                Alert.AlertType.ERROR,
                "PenguBank Error",
                "This device does not currently support bluetooth. Please activate or change device",
            ) {
                exitProcess(-1)
            }
        }

        return bluetoothAddress
    }
}