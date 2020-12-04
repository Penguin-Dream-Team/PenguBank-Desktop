package utils

import org.apache.http.client.utils.URIBuilder
import java.net.URLEncoder
import java.nio.charset.Charset

private const val QR_GENERATOR_URI_FORMAT = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=%s"
private const val issuer = "PenguBank Inc"

fun generateQrCode(accountName: String): String {
    val url = createOTPURL(accountName)
    return String.format(QR_GENERATOR_URI_FORMAT, encode(url))
}

private fun createOTPURL(accountName: String): String {
    require(!accountName.isBlank()) { "Account name must not be not null or empty." }
    require(!issuer.contains(":")) { "Issuer cannot contain the \':\' character." }

    val builder = URIBuilder()
        .setScheme("newTransaction")
        .setHost("bluetooth")
        .setPath("/" + formatLabel(accountName))
        .setParameter("bluetoothMac", "123:123:123:123:123")
        .setParameter("kPub", "dummy")
    issuer.let { builder.setParameter("issuer", issuer) }
    return builder.toString()
}

private fun formatLabel(accountName: String) = listOfNotNull(issuer, accountName).joinToString(":")

private fun encode(decoded: String, charset: Charset = Charsets.UTF_8) = URLEncoder.encode(decoded, charset.name())
