package utils.dh

import java.math.BigInteger
import java.security.*
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.DHParameterSpec
import javax.crypto.spec.SecretKeySpec

object Dh {

    private val publicAlpha = BigInteger("71E19B2F5B39A11422304851CD4EA588D2BB397BA1D84DEBE64841789BF75B65487F3AB8B08288BB397912DAE03C76E631B90DE98CDC55FDD5F73BE194C75DD286DA2CAA485C3464C386E66E815449B9683BE72E5D25418088F027837BE0B680D504BAD7D67105C403A2FAFAF8AF6FA88F4196B726778E7C63A314E0B93B426EB83244C6644D48A861769E74439BA496DA28332DEF73167E0174A6496E88D6D5", 16)
    private val publicQ = BigInteger("88EEDB64E04DFD6E6258584398B312007B73CAD3211569FA52644195154771294BB6196FCE53D1ED78EB0D5C1B1458A14BF0208D67E613A0E971377A930878798FABEF4C3EC13B9E3CE73BD79CE312C68334B274B442365475CD9CADE9B7AD3D358673E96D5E8B831FE4FF54F4444963E59D1038B4C29ED82D362BF488E9CBCB4F17ACCF77A270F70302D0DD48E98674D913732A4A130B6F2D486EC6A93B71B3", 16)

    fun initDH(): Pair<KeyAgreement, KeyPair> {
        val dhParams = DHParameterSpec(publicQ, publicAlpha)
        val keyGen = KeyPairGenerator.getInstance("DH", "BC")

        keyGen.initialize(dhParams, SecureRandom())

        val keyAgree = KeyAgreement.getInstance("DH", "BC")
        val keyPair = keyGen.generateKeyPair()

        keyAgree.init(keyPair.private)

        return Pair(keyAgree, keyPair)
    }

    fun generateSecretKey(keyAgree: KeyAgreement, otherPublicKey: PublicKey): SecretKey {
        keyAgree.doPhase(otherPublicKey, true)
        keyAgree.generateSecret("AES")

        return SecretKeySpec(keyAgree.generateSecret(),"AES")
    }
}