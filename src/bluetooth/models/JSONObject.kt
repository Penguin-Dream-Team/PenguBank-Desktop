package bluetooth.models

import com.google.gson.Gson

inline fun <reified T : JSONObject> String.toObject(): T = Gson().fromJson(this, T::class.java)

interface JSONObject {
    fun toJSON(): String = Gson().toJson(this)
}

