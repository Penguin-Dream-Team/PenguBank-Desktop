package utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.regex.Pattern.compile

fun String.isValidEmail(): Boolean {
    val emailRegex = compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    return emailRegex.matcher(this).matches()
}

fun String.euroToInt(): Int = this.filter { it.isDigit() }.toInt()

fun String.toPrettyDate(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    val formatter = SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
    val dateValue = this.subSequence(0, this.length - 5).toString() // remove milliseconds and Z
    return formatter.format(parser.parse(dateValue))
}