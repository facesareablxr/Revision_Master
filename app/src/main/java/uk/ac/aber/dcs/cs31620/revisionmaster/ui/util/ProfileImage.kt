package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.aber.dcs.cs31620.revisionmaster.R


/**
 * Composable function for displaying a profile picture.
 *
 * @param imagePath The path to the image to display.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfilePicture(
    imagePath: String?,
) {
    // Default placeholder image resource
    val defaultImageRes = R.drawable.profile_image_placeholder

    // Box composable to contain the profile picture
    Box(
        modifier = Modifier
            .size(120.dp) // Set size of the profile picture
            .clip(CircleShape) // Clip the image to a circular shape
    ) {
        // Display placeholder image or loaded image
        if (imagePath != null) {
            GlideImage(
                model = Uri.parse(imagePath), // Load image from URI
                contentDescription = null,
                modifier = Modifier.wrapContentSize(), // Wrap content size of the image
                contentScale = ContentScale.Crop // Scale image to fit within the bounds while maintaining aspect ratio
            )
        } else {
            GlideImage(
                model = defaultImageRes, // Load placeholder image from resources
                contentDescription = null,
                modifier = Modifier.wrapContentSize(), // Wrap content size of the image
                contentScale = ContentScale.Crop // Scale image to fit within the bounds while maintaining aspect ratio
            )
        }
    }
}
