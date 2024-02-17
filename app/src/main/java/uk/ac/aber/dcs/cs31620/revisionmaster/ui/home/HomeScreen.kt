package uk.ac.aber.dcs.cs31620.revisionmaster.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.TopLevelScaffold
import java.time.LocalDate

/**
 * Represents the home screen, has individual cards for each exercise for the current day.
 * @author Lauren Davis
 */
@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()

    TopLevelScaffold(
        navController = navController,
        pageContent = { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                HomeScreenContent(
                    modifier = Modifier.padding(8.dp)
                    )
            }
        }
    )
}

@Composable
fun HomeScreenContent(modifier: Modifier) {
   Column(modifier = modifier.padding(8.dp)) {
            val currentDayCaps = LocalDate.now().dayOfWeek.name
            val currentDay = currentDayCaps.lowercase().replaceFirstChar { it.uppercase() }
            Text(
                text = currentDay,
                style = MaterialTheme.typography.headlineSmall,
                modifier = modifier
                    .padding(bottom = 16.dp)
                    .align(CenterHorizontally)
            )

    }
}
