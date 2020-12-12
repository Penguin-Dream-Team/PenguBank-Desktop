package security

import java.security.PrivateKey
import java.security.PublicKey

class SignatureConnectionHandler(
    private val privateKey: PrivateKey,
    private val mobilePublicKey: PublicKey
) {

    fun signData(data: String): String = SecurityUtils.signData(data, privateKey)

    fun verifySignature(data: String, signature: String): Boolean =
        SecurityUtils.verifySignature(data, signature, mobilePublicKey)
}