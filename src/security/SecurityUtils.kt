package security

import org.apache.commons.codec.binary.Base64
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v1CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters
import org.bouncycastle.crypto.params.RSAKeyParameters
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
import org.bouncycastle.crypto.signers.RSADigestSigner
import org.bouncycastle.crypto.util.PrivateKeyFactory
import org.bouncycastle.crypto.util.PublicKeyFactory
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder
import java.io.File
import java.io.StringReader
import java.io.StringWriter
import java.math.BigInteger
import java.security.*
import java.security.cert.X509Certificate
import java.security.spec.RSAKeyGenParameterSpec
import java.security.spec.RSAPrivateCrtKeySpec
import java.security.spec.RSAPublicKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


object SecurityUtils {

    private var keyStore: KeyStore
    private val home = "${System.getProperty("user.home")}${File.separator}.pengubank${File.separator}"
    private val folder = File(home)
    private var file: File
    private var needSetup = true

    init {
        Security.addProvider(BouncyCastleProvider())
        keyStore = KeyStore.getInstance("BKS", "BC")
        folder.mkdirs()
        file = File(folder, "keystore.bks")
    }

    fun hasKey(): Boolean = file.exists()

    fun init(password: String) {
        val passwordCharArray = password.toCharArray()
        val fis = if (file.exists()) file.inputStream() else null

        // Create new keystore
        keyStore.load(fis, passwordCharArray)
        if (!keyStore.containsAlias("pengubank-cert") || !keyStore.containsAlias("pengubank-privkey")) {
            val keyPair = generateKeyPair()
            val certificate = arrayOf(generateCertificate(keyPair))

            keyStore.setKeyEntry("pengubank-privkey", keyPair.private, passwordCharArray, certificate)
            keyStore.setCertificateEntry("pengubank-cert", certificate.first())
            save(file, keyStore, passwordCharArray)
        }
        needSetup = false
    }

    fun signData(data: String, privateKey: PrivateKey): String {
        val privateKeyParameter = PrivateKeyFactory.createKey(privateKey.encoded)
        val dataBytes = data.encodeToByteArray()
        val signer = RSADigestSigner(SHA512Digest())

        signer.init(true, privateKeyParameter)
        signer.update(dataBytes, 0, dataBytes.size)

        val signature = signer.generateSignature()
        return Base64.encodeBase64String(signature)
    }

    fun verifySignature(data: String, signature: String, publicKey: PublicKey): Boolean {
        val dataBytes = data.encodeToByteArray()
        val publicKeyParameter = PublicKeyFactory.createKey(publicKey.encoded)

        val signer = RSADigestSigner(SHA512Digest())
        signer.init(false, publicKeyParameter)
        signer.update(dataBytes, 0, dataBytes.size)

        return signer.verifySignature(Base64.decodeBase64(signature))
    }

    fun getPublicKey(): PublicKey {
        return keyStore.getCertificate("pengubank-cert").publicKey
    }

    fun getPrivateKey(password: String): PrivateKey {
        if (needSetup) init(password)
        return keyStore.getKey("pengubank-privkey", password.toCharArray()) as PrivateKey
    }

    fun parsePublicKey(publicKeyPEM: String): PublicKey {
        val textReader = StringReader(publicKeyPEM)
        val pemParser = PEMParser(textReader)
        val converter = JcaPEMKeyConverter()
        return converter.getPublicKey(
            SubjectPublicKeyInfo.getInstance(
                pemParser.readObject()
            )
        )
    }

    fun writePublicKey(publicKey: PublicKey): String {
        val stringWriter = StringWriter()
        val pemWriter = JcaPEMWriter(stringWriter)
        pemWriter.writeObject(publicKey)
        pemWriter.flush()
        return stringWriter.toString()
    }

    private fun generateCertificate(keyPair: KeyPair): X509Certificate {
        val dnName = X500Name("CN=PenguBank")

        val startCalendar = Calendar.getInstance()
        startCalendar.add(Calendar.DAY_OF_MONTH, -1) // yesterday
        val validityBeginDate = startCalendar.time

        val endCalendar = Calendar.getInstance()
        endCalendar.add(Calendar.YEAR, 2) // 2 years from now
        val validityEndDate = endCalendar.time

        val certGen = X509v1CertificateBuilder(
            dnName,
            BigInteger.valueOf(System.currentTimeMillis()),
            validityBeginDate,
            validityEndDate,
            dnName,
            SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)
        )

        val privateKeyParameter = PrivateKeyFactory.createKey(keyPair.private.encoded)
        val sigAlgId = DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA")
        val digAlgId = DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId)
        val certHolder = certGen.build(BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(privateKeyParameter))

        return JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder)
    }

    private const val RSA_KEY_SIZE = 4096
    private fun generateKeyPair(): KeyPair {
        val rsaKeyGen = RSAKeyPairGenerator()

        rsaKeyGen.init(
            RSAKeyGenerationParameters(
                RSAKeyGenParameterSpec.F4, SecureRandom(),
                RSA_KEY_SIZE, PrimeCertaintyCalculator.getDefaultCertainty(RSA_KEY_SIZE)
            )
        )

        val keyPair = rsaKeyGen.generateKeyPair()

        val publicKey = keyPair.public as RSAKeyParameters
        val privateKey = keyPair.private as RSAPrivateCrtKeyParameters

        val pubKey = KeyFactory.getInstance("RSA", "BC").generatePublic(
            RSAPublicKeySpec(publicKey.modulus, publicKey.exponent)
        )

        val privKey = KeyFactory.getInstance("RSA", "BC").generatePrivate(
            RSAPrivateCrtKeySpec(
                publicKey.modulus,
                publicKey.exponent,
                privateKey.exponent,
                privateKey.p,
                privateKey.q,
                privateKey.dp,
                privateKey.dq,
                privateKey.qInv
            )
        )

        return KeyPair(pubKey, privKey)
    }

    private fun save(file: File, keyStore: KeyStore, password: CharArray) {
        keyStore.store(file.outputStream(), password)
    }

    fun cipher(secretKey: SecretKey, data: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return Base64.encodeBase64String(cipher.doFinal(data.encodeToByteArray()))
    }

    fun decipher(secretKey: SecretKey, data: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return String(cipher.doFinal(Base64.decodeBase64(data)))
    }

    fun gcmCipher(secretKey: SecretKey, data: String): Pair<String, GCMParameterSpec> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val parameterSpec = cipher.parameters.getParameterSpec(GCMParameterSpec::class.java)
        return Pair(
            Base64.encodeBase64String(cipher.doFinal(data.encodeToByteArray())),
            parameterSpec
        )
    }

    fun gcmDecipher(secretKey: SecretKey, data: String, iv: ByteArray, tagLen: Int): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            GCMParameterSpec(tagLen, iv)
        )
        return String(cipher.doFinal(Base64.decodeBase64(data)))
    }
}