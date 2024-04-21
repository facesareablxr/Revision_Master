package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.content.FileProvider
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import java.io.File
import java.io.IOException


fun showImagePickerDialog(context: Context, resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val options = arrayOf(context.getString(R.string.takePhoto), context.getString(R.string.chooseFromGallery))
    val builder = AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.selectImage))
        .setItems(options) { dialog, which ->
            when (which) {
                0 -> takePicture(context, resultLauncher)
                1 -> openGallery(context, resultLauncher)
            }
        }
    builder.create().show()
}

private fun takePicture(context: Context, resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    var photoFile: File? = null

    try {
        photoFile = ResourceUtil.createImageFile(context)
        Log.d("take picture","success")
    } catch (ex: IOException) {
            Log.e("take picture","fail")
        Toast.makeText(
            context,
            context.getString(R.string.imageError),
            Toast.LENGTH_SHORT
        ).show()
    }

    photoFile?.let {
        val photoUri = FileProvider.getUriForFile(
            context,
            context.packageName,
            it
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        try {
            resultLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.photoerror, Toast.LENGTH_LONG)
                .show()
        }
    }
}

private fun openGallery(context: Context, resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    resultLauncher.launch(intent)
}