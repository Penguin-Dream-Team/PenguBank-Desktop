package bluetooth.models

data class BluetoothMessage(
    val data: String,
    val signature: String,
    val iv: ByteArray? = null,
    val tLen: Int? = null
) : JSONObject