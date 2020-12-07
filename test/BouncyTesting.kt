import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import security.SecurityUtils
import java.io.PrintWriter

fun main() {

    SecurityUtils.init("password")
    val pem = JcaPEMWriter(PrintWriter(System.out))
    pem.writeObject(SecurityUtils.getPublicKey())
    pem.flush()
    println("=".repeat(80))
    pem.writeObject(SecurityUtils.getPrivateKey("password"))
    pem.flush()

}

