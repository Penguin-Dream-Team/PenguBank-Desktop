package bluetooth.models.messages

import bluetooth.models.JSONObject
import java.math.BigInteger

data class DiffieHellmanInitResponseMessage(val publicY: BigInteger) : JSONObject