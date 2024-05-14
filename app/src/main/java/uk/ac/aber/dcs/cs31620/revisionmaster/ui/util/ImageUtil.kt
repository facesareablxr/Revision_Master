package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

/**
 * Composable function for displaying a clickable box with a placeholder image.
 * Clicking the box launches image selection, and the selected image is displayed using Glide.
 * @param imageUrl: The URL of the image to display.
 * @param onImageSelected: Callback function to handle the selected image.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ClickableImageBox(
    imageUrl: Uri?,
    onImageSelected: (Uri) -> Unit,
) {
    val context = LocalContext.current

    // Use ActivityResultContracts.GetContent() to launch image selection
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { // If URI is not null, process the selected image
            onImageSelected(uri)
        } ?: Toast.makeText(context, "Image selection failed", Toast.LENGTH_SHORT).show() // Show toast if image selection fails
    }

    Box(
        modifier = Modifier
            .size(360.dp)
            .clickable {
                // Launch image selection when clicked
                launcher.launch("image/*")
            }
            .border(width = 1.dp, color = Color.Gray)
    ) {
        // Display placeholder image or loaded image
        if (imageUrl != null) {
            GlideImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.wrapContentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    tint = Color.Gray
                )
        }
    }
}


/**
 * Composable function for displaying a clickable box with a placeholder image.
 * Clicking the box launches image selection, and the selected image is displayed using Glide.
 * @param imageUrl: The URL of the image to display.
 * @param onImageSelected: Callback function to handle the selected image.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileImageSelector(
    imageUrl: Uri?,
    onImageSelected: (Uri) -> Unit,
) {
    val context = LocalContext.current

    // Use ActivityResultContracts.GetContent() to launch image selection
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { // If URI is not null, process the selected image
            onImageSelected(uri)
        } ?: Toast.makeText(context, "Image selection failed", Toast.LENGTH_SHORT).show() // Show toast if image selection fails
    }

    Box(
        modifier = Modifier
            .size(240.dp)
            .clip(CircleShape)
            .clickable {
                // Launch image selection when clicked
                launcher.launch("image/*")
            }
    ) {
        // Display placeholder image or loaded image
        if (imageUrl != null) {
            GlideImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.wrapContentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.size(150.dp)) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    tint = Color.Gray
                )
            }
        }
    }
}


