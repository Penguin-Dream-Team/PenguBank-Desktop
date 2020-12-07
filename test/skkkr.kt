import org.bouncycastle.jce.provider.BouncyCastleProvider
import utils.dh.Dh
import java.security.Security

fun main() {
    Security.addProvider(BouncyCastleProvider())
    val aPair = Dh.initDH()
    val bPair = Dh.initDH()

    val aSecretKey = Dh.generateSecretKey(aPair.first, bPair.second.public)
    val bSecretKey = Dh.generateSecretKey(bPair.first, aPair.second.public)

    if (aSecretKey == bSecretKey) {
        println("KO pinguins não são fixes - sry")
    }
}