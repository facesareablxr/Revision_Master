package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import java.io.File
import java.io.IOException
import java.util.UUID

fun showImagePickerDialog(context: Context, resultLauncher: ActivityResultLauncher<Intent>) {
    val options = arrayOf(
        context.getString(R.string.takePhoto),
        context.getString(R.string.chooseFromGallery)
    )
    val builder = AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.selectImage))
        .setItems(options) { _, which ->
            when (which) {
                0 -> takePicture(context, resultLauncher)
                1 -> openGallery(context, resultLauncher)
            }
        }
    builder.create().show()
}

private fun takePicture(context: Context, resultLauncher: ActivityResultLauncher<Intent>) {
    var photoFileUri: Uri? = null // Declare photoFileUri here

    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    var photoFile: File? = null

    try {
        photoFile = ResourceUtil.createImageFile(context)
    } catch (ex: IOException) {
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
        photoFileUri = photoUri // Assign photoUri to photoFileUri
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


private fun openGallery(context: Context, resultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    resultLauncher.launch(intent)
}


fun uploadImageToFirebase(context: Context, imageUri: Uri, updateImageUrl: (String) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageName = UUID.randomUUID().toString()

    val imageRef = storageRef.child("images/$imageName")

    val uploadTask = imageRef.putFile(imageUri)

    uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
            task.exception?.let {
                throw it
            }
        }
        imageRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            updateImageUrl(downloadUri.toString()) // Update the image URL in the UI or database
        } else {
            // Handle failures
            Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }
}