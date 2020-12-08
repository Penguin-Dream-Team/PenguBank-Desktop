import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import utils.DiffieHellmanUtils
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PublicKey
import java.security.Security
import javax.crypto.spec.DHPublicKeySpec

fun main() {
    Security.addProvider(BouncyCastleProvider())
    val aPair = DiffieHellmanUtils.init()
    val bPair = DiffieHellmanUtils.init()

    // INSIDE OF B MACHINE
    val aPublicY = (aPair.second.public as BCDHPublicKey).y
    val publicAKey = receivePublicKeyValue(bPair.second, aPublicY)
    val bSecretKey = DiffieHellmanUtils.generateSecretKey(bPair.first, publicAKey)

    // INSIDE OF A MACHINE
    val bPublicY = (bPair.second.public as BCDHPublicKey).y
    val publicBKey = receivePublicKeyValue(aPair.second, bPublicY)
    val aSecretKey = DiffieHellmanUtils.generateSecretKey(aPair.first, publicBKey)


    if (aSecretKey == bSecretKey) {
        println("KO pinguins não são fixes - sry")
    }
}

fun receivePublicKeyValue(pair: KeyPair, y: BigInteger): PublicKey {
    val public = pair.public as BCDHPublicKey
    println(y)

    val kf = KeyFactory.getInstance("DH", "BC")
    val spec = DHPublicKeySpec(
        y,
        public.params.p, public.params.g
    )

    return kf.generatePublic(spec)
}