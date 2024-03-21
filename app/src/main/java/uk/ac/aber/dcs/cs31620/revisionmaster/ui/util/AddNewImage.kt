package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import java.io.File
import java.io.IOException

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddNewImage(
    imagePath: String?,
    modifier: Modifier,
    updateImagePath: (String) -> Unit = {}
) {
    var photoFile: File? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    val resultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                when {
                    data.hasExtra(MediaStore.EXTRA_OUTPUT) -> { // Camera intent
                        updateImagePath(photoFile!!.absolutePath)
                    }
                    data.data != null -> { // Gallery intent
                        updateImagePath(data.data!!.toString())
                    }
                    else -> { }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = { showImagePickerDialog(context, resultLauncher) }) {
            Icon(Icons.Default.AddAPhoto, contentDescription = stringResource(R.string.addImage))
        }
        GlideImage(
            model = if (!imagePath.isNullOrEmpty()) Uri.parse(imagePath) else R.drawable.profile_image_placeholder,
            contentDescription = stringResource(R.string.profilePicture),
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}

private fun showImagePickerDialog(context: Context, resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
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