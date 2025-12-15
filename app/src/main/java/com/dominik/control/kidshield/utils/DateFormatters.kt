package com.dominik.control.kidshield.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateTime(timestampMillis: Long): String {
    val date = Date(timestampMillis)
    val format = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("pl", "PL"))
    return format.format(date)
}
