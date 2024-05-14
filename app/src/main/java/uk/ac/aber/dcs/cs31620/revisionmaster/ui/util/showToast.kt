package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import android.content.Context
import android.widget.Toast

/**
 * Function to display a toast message.
 */
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}