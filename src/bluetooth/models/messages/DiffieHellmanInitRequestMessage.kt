package bluetooth.models.messages

import bluetooth.models.JSONObject
import java.math.BigInteger

data class DiffieHellmanInitRequestMessage(val publicY: BigInteger) : JSONObject