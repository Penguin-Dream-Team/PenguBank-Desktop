package utils

import org.apache.http.client.utils.URIBuilder
import security.SecurityUtils
import java.net.URLEncoder
import java.nio.charset.Charset

private const val QR_GENERATOR_URI_FORMAT = "https://chart.googleapis.com/chart?chs=500x500&chld=M%%7C0&cht=qr&chl=%s"
private const val issuer = "PenguBank Inc"

fun generateQrCode(accountName: String): String {
    val url = createDesktopConnectionUrl(accountName)
    return String.format(QR_GENERATOR_URI_FORMAT, encode(url))
}

private fun createDesktopConnectionUrl(accountName: String): String {
    require(accountName.isNotBlank()) { "Account name must not be not null or empty." }
    require(!issuer.contains(":")) { "Issuer cannot contain the \':\' character." }

    val builder = URIBuilder()
        .setScheme("penguBank")
        .setHost("desktop")
        .setPath("/" + formatLabel(accountName))
        .setParameter("bluetoothMac", BluetoothUtils.getBluetoothAddress())
        .setParameter("kPub", SecurityUtils.writePublicKey(SecurityUtils.getPublicKey()))
    issuer.let { builder.setParameter("issuer", issuer) }
    return builder.toString()
}

private fun formatLabel(accountName: String) = listOfNotNull(issuer, accountName).joinToString(":")

private fun encode(decoded: String, charset: Charset = Charsets.UTF_8) = URLEncoder.encode(decoded, charset.name())
