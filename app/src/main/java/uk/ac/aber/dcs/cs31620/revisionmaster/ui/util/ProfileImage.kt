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


@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfilePicture(
    imagePath: String?,
) {
    val defaultImageRes = R.drawable.profile_image_placeholder

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
    ) {
        // Display placeholder image or loaded image
        if (imagePath != null) {
            GlideImage(
                model = Uri.parse(imagePath),
                contentDescription = null,
                modifier = Modifier.wrapContentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            GlideImage(
                model = defaultImageRes,
                contentDescription = null,
                modifier = Modifier.wrapContentSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
