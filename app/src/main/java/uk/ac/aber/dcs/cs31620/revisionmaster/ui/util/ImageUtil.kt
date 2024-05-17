package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.UUID

/**
 * Composable function for displaying a clickable box with an icon and a background if there is no image URI.
 * Clicking the box launches image selection, and the selected image is uploaded to Firebase Storage.
 * @param imageUri: The URI of the image to display.
 * @param onImageUploaded: Callback function to handle the uploaded image URL.
 */
@Composable
fun ClickableImageBox(
    imageUri: String?,  // The URI of the image to display
    onImageUploaded: (String) -> Unit  // Callback function to handle the uploaded image URL
) {
    val context = LocalContext.current

    // Define an ActivityResultLauncher to handle image selection
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { it ->
            // Upload the image to Firebase Storage
            uploadImageToFirebaseStorage(it,
                onSuccess = { downloadUrl ->
                    onImageUploaded(downloadUrl)
                },
                onFailure = {
                    showToast(context, "Failed to upload image: ${it.message}")
                }
            )
        }
    }

    // Box composable to display the image or placeholder icon and handle click events
    Box(
        modifier = Modifier
            .size(240.dp)
            .clickable { launcher.launch("image/*") }  // Launch image selection on click
            .border(BorderStroke(1.dp, Color.Gray)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            val painter = rememberAsyncImagePainter(imageUri)  // Asynchronously load the image
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Display placeholder icon and background if no image URI is provided
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                tint = Color.Gray
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.LightGray.copy(alpha = 0.4f))
            )
        }
    }
}

/**
 * Uploads the selected image to Firebase Storage and returns its storage path.
 */
fun uploadImageToFirebaseStorage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imagesRef = storageRef.child("images/${UUID.randomUUID()}")

    // Upload the image to Firebase Storage
    val uploadTask: UploadTask = imagesRef.putFile(imageUri)

    // Register observers to listen for when the upload is done or if it fails
    uploadTask.addOnSuccessListener { _ ->
        // Upload succeeded, get the download URL
        imagesRef.downloadUrl.addOnSuccessListener { uri ->
            onSuccess(uri.toString())
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }.addOnFailureListener { exception ->
        // Handle failure
        onFailure(exception)
    }
}

/**
 * Composable function for displaying a clickable circular area with a placeholder image.
 * Clicking the area launches image selection, and the selected image is displayed using Coil.
 * @param imageUrl: The URL of the image to display.
 * @param onImageSelected: Callback function to handle the selected image.
 */
@Composable
fun ProfileImageSelector(
    imageUrl: String?,
    onImageSelected: (String) -> Unit
) {
    val context = LocalContext.current

    // Define an ActivityResultLauncher to handle image selection
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // Upload the image to Firebase Storage and store the download URL in the database
            uploadImageToFirebaseStorage(uri,
                onSuccess = { downloadUrl ->
                    onImageSelected(downloadUrl)
                },
                onFailure = { exception ->
                    showToast(context, "Failed to upload image: ${exception.message}")
                }
            )
        }
    }

    // Box composable to display the image or placeholder icon and handle click events
    Box(
        modifier = Modifier
            .size(240.dp)
            .clickable { launcher.launch("image/*") }  // Launch image selection on click
            .border(BorderStroke(1.dp, Color.Gray), shape = CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            val painter = rememberAsyncImagePainter(imageUrl)  // Asynchronously load the image
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Display placeholder icon and background if no image URI is provided
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                tint = Color.Gray
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.LightGray.copy(alpha = 0.4f))
            )
        }
    }
}
