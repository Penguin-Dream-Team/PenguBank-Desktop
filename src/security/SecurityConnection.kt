package security

import org.apache.commons.codec.binary.Base64
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.signers.RSADigestSigner
import org.bouncycastle.crypto.util.PrivateKeyFactory
import org.bouncycastle.crypto.util.PublicKeyFactory
import java.security.PrivateKey
import java.security.PublicKey

class SecurityConnection(
    private val privateKey: PrivateKey,
    private val mobilePublicKey: PublicKey
) {

    fun signData(data: String): String {
        val privateKeyParameter = PrivateKeyFactory.createKey(privateKey.encoded)
        val dataBytes = data.encodeToByteArray()
        val signer = RSADigestSigner(SHA512Digest())

        signer.init(true, privateKeyParameter)
        signer.update(dataBytes, 0, dataBytes.size)

        val signature = signer.generateSignature()
        return Base64.encodeBase64String(signature)
    }

    fun verifySignature(data: String, signature: String): Boolean {
        val dataBytes = data.encodeToByteArray()
        val publicKeyParameter = PublicKeyFactory.createKey(mobilePublicKey.encoded)

        val signer = RSADigestSigner(SHA512Digest())
        signer.init(false, publicKeyParameter)
        signer.update(dataBytes, 0, dataBytes.size)

        return signer.verifySignature(Base64.decodeBase64(signature))
    }
}