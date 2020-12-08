package bluetooth.models

data class BluetoothMessage(
    val data: String,
    val signature: String
) : JSONObject