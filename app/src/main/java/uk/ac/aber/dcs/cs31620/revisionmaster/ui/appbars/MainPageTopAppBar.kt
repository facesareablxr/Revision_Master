package uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.theme.RevisionMasterTheme
import java.time.LocalTime

/**
 * This is the main page top app bar, it has multiple components to it. The greeting will change
 * depending on what time of day it is.
 * The streak will show how many days the user has been active on the application.
 * The notification bell will show a badge if there is a notification for the user to see.
 * The profile circle will show the users profile picture, and when clicked - it will open their
 * profile.
 */
@Composable
fun MainPageTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val context = LocalContext.current
    val currentTime = LocalTime.now()
    val greeting = getGreeting(currentTime)
    val steakCounter = 10
    val notifications = 1

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Greeting on the left
                Text(
                    text = greeting,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
                // Streak, notification, profile) on the right
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        StreakCounter(steakCounter)
                        Spacer(modifier = Modifier.width(8.dp))
                        NotificationsBell(notifications)
                        Spacer(modifier = Modifier.width(8.dp))
                        ProfileCircle(context = context)
                        Spacer(modifier = Modifier.width(4.dp))
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        scrollBehavior = scrollBehavior
    )
}

/**
 * Notification bell code which will indicate whether the user has unread notifications
 */
@Composable
private fun NotificationsBell(notifications: Int) {
    IconButton(onClick = {}) {
        Icon(
            imageVector = Icons.Filled.Notifications, // Placeholder icon
            contentDescription = "Notifications"
        )
        if (notifications > 0) {
            Badge(modifier = Modifier.offset(x = 8.dp, y = (-8).dp)) // Position badge on top right
        }
    }
}

/**
 * Streak counter which will track how many days the user has been active on the application for
 */
@Composable
private fun StreakCounter(steakCounter: Int) {
    IconButton(onClick = {}) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = "Streak"
        )
    }
    Text(
        text = "$steakCounter",
        fontWeight = FontWeight.SemiBold
    )
}

/**
 * This is the greeting function which will display the greeting which will change depending on the
 * time of day
 */
@Composable
private fun getGreeting(currentTime: LocalTime): String {
    return when (currentTime) {
        in LocalTime.of(0, 0)..LocalTime.of(12, 0) -> stringResource(R.string.morning)
        in LocalTime.of(12, 0)..LocalTime.of(18, 0) -> stringResource(R.string.afternoon)
        else -> stringResource(R.string.evening)
    }
}

/**
 * This is the profile circle which will show the user their profile picture unless it there isn't
 * one and will display a placeholder
 */
@Composable
private fun ProfileCircle(context: Context) {
    Box(
        modifier = Modifier.padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        val image = painterResource(id = R.drawable.profile_image_placeholder) // Placeholder image
        Image(
            painter = image,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape) // Add outline
        )
    }
}

@Preview
@Composable
private fun MainPageTopAppBarPreview() {
    RevisionMasterTheme(dynamicColor = false) {
        MainPageTopAppBar()
    }
}
