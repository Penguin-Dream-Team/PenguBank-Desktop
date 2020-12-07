package utils.bluetooth

import java.io.IOException
import javax.microedition.io.StreamConnection

class ProcessConnectionThread(private val connection: StreamConnection) : Runnable {

    override fun run() {
        try {
            val inputStream = connection.openInputStream().bufferedReader()
            val outputStream = connection.openOutputStream().bufferedWriter()

            while (true) {
                val data = inputStream.readLine()

                if (data == "") {
                    println("Exit")
                    break
                } else {
                    println(data)
                    outputStream.write("Okay then. nub")
                    outputStream.newLine()
                    outputStream.flush()
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }
}