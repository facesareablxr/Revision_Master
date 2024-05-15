package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

/**
 * Formats a time represented in milliseconds into a human-readable string.
 *
 * @param timeInMillis The time to format, in milliseconds.
 * @return A formatted string representing the time.
 */
fun formatTime(timeInMillis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

/**
 * Composable function to format a date in the desired format.
 *
 * @param date: Date string in the format "yyyy-MM-dd".
 */
fun formattedDate(date: String): String {
    // Parse the input date string
    val parsedDate = LocalDate.parse(date)

    // Define a date formatter to get day and month name
    val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())

    // Format the date using the formatter
    return parsedDate.format(formatter)
}