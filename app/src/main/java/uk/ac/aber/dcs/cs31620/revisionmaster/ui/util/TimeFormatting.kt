package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun formatTime(timeInMillis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return dateFormat.format(calendar.time)
}
