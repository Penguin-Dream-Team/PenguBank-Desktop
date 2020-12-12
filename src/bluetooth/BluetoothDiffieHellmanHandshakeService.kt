package bluetooth

import bluetooth.models.messages.DiffieHellmanInitRequestMessage
import bluetooth.models.messages.DiffieHellmanInitResponseMessage
import controllers.BluetoothConnectionController
import exceptions.BluetoothMessageSignatureFailedException
import security.SignatureConnectionHandler
import utils.DiffieHellmanUtils
import java.io.DataInputStream
import java.io.DataOutputStream
import javax.crypto.SecretKey

class BluetoothDiffieHellmanHandshakeService(
    securityConnection: SignatureConnectionHandler,
    bluetoothConnectionController: BluetoothConnectionController,
    inputStream: DataInputStream,
    outputStream: DataOutputStream
) : BluetoothService(securityConnection, inputStream, outputStream) {

    init {
        val (keyAgreement, keyPair) = DiffieHellmanUtils.init()

        var signatureFailed = false
        lateinit var secretKey: SecretKey

        do {
            try {
                // send diffie hellman public a
                val myPublicY = DiffieHellmanUtils.retrieveYFromPublicKey(keyPair)
                sendMessage(DiffieHellmanInitRequestMessage(myPublicY))

                // receive diffie hellman public b
                val response = receiveMessage<DiffieHellmanInitResponseMessage>()
                val otherPublicKey = DiffieHellmanUtils.parsePublicKeyFromY(keyPair, response.publicY)

                // generate key
                secretKey = DiffieHellmanUtils.generateSecretKey(keyAgreement, otherPublicKey)
            } catch (e: BluetoothMessageSignatureFailedException) {
                // Do nothing, continue loop
                e.printStackTrace()
                signatureFailed = true
            }
        } while (signatureFailed)

        bluetoothConnectionController.connected = true

        // start secured communication protocol with newly generated shared secret key
        BluetoothCommunicationService(
            secretKey,
            securityConnection,
            bluetoothConnectionController,
            inputStream,
            outputStream
        )

        bluetoothConnectionController.connected = false
    }
}