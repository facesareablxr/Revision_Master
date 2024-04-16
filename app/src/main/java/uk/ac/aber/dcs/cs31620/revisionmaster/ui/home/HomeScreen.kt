package uk.ac.aber.dcs.cs31620.revisionmaster.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen


/**
 * Represents the home screen
 * @author Lauren Davis
 */

/**
 *
 */
@Composable
fun HomeScreen(
    navController: NavHostController,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    val decksState by flashcardViewModel.decks.collectAsState(initial = emptyList())

    LaunchedEffect(Unit){
        flashcardViewModel.getUserDecks()
    }

    TopLevelScaffold(
        navController = navController,
        pageContent = { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Two Cards in a Row
                    Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)) {
                        CardWithIcon(
                            icon = Icons.Default.Book,
                            text = "Mock Test",
                            subtext = "Test Yourself",
                            onClick = {navController.navigate(Screen.CreateExam.route)}
                        )
                        CardWithIcon(
                            icon = Icons.Default.LibraryAdd,
                            text = stringResource(R.string.newMaterial),
                            subtext = stringResource(R.string.createNewDeck),
                            onClick = { navController.navigate(Screen.AddDeck.route)}
                        )
                    }
                    Card (
                        modifier = Modifier.padding(horizontal= 16.dp),
                        colors = CardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {

                        // Carousel
                        CarouselWithPager(
                            title = stringResource(R.string.suggestedDecks),
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            decks = decksState.filter { it.averageDifficulty == Difficulty.HARD || it.averageDifficulty == Difficulty.MEDIUM },
                            onClick = {  }
                        )
                    }

                        CardWithListAndButton()
                    }
            }
        }
    )
}

/**
 *
 */
@Composable
fun CardWithIcon(
    icon: ImageVector,
    text: String,
    subtext: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.padding(8.dp),
        onClick = onClick,
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .width(150.dp)
        ) {
            Icon(imageVector = icon, contentDescription = "Icon")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(text = subtext, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 *
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselWithPager(
    title: String,
    icon: ImageVector,
    decks: List<Deck>,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick.invoke() }
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.padding(horizontal = 115.dp))
            Icon(imageVector = icon, contentDescription = "Next")
        }
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = rememberPagerState(pageCount = { decks.size }),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { page ->
            CardWithCarouselItem(decks[page])
        }
    }
}


/**
 *
 */
@Composable
fun CardWithCarouselItem(deck: Deck) {
    Card(
        modifier = Modifier.padding(8.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = deck.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Subject: ${deck.subject}, Average Difficulty: ${deck.averageDifficulty}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


/**
 *
 */
@Composable
fun CardWithListAndButton() {
    Card(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Group Suggestions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            // Individual items with join button
            repeat(3) { index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Group, contentDescription = "Group Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Group $index",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { /* Handle join button click */ },
                    ) {
                        Text(text = "Join")
                    }
                }
            }
        }
    }
}
