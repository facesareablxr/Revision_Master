package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import android.content.Context
import android.widget.Toast

/**
 * Display a toast message.
 *
 * @param context The context from which the toast will be displayed.
 * @param message The message to be displayed in the toast.
 */
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
