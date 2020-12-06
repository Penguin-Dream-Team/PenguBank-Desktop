package utils.bluetooth

import javafx.application.Platform
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.bluetooth.DiscoveryAgent
import javax.bluetooth.LocalDevice
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnection
import javax.microedition.io.StreamConnectionNotifier

class ProcessConnectionThread(private val connection: StreamConnection) : Runnable {

    override fun run() {
        try {
            val inputStream = connection.openInputStream()
            val outputStream = connection.openOutputStream()

            while (true) {
                val data = String(readByteArrayCommand(inputStream))

                if (data == "") {
                    println("Exit")
                    break
                } else {
                    processCommand(data)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }

    private fun processCommand(data: String) {
        Platform.runLater {
            println(data)
        }
    }

    @Throws(IOException::class)
    private fun readByteArrayCommand(inputStream: InputStream): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        var read = inputStream.read()

        while (read != -1 && read != 0) {
            byteArrayOutputStream.write(read)
            read = inputStream.read()
        }

        return byteArrayOutputStream.toByteArray()
    }

}