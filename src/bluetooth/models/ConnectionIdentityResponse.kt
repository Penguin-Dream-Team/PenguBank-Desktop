package bluetooth.models

data class ConnectionIdentityResponse(val ok: Boolean, val digest: String? = null) : JSONObject