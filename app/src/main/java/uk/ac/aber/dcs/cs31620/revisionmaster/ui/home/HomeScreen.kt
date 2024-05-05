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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen


/**
 * Represents the home screen
 * @author Lauren Davis
 */

/**
 * Composable function for the Home screen.
 *
 * @param navController NavController for navigation.
 * @param flashcardViewModel ViewModel for flashcard operations. Default is viewModel().
 */
@Composable
fun HomeScreen(
    navController: NavHostController,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    val decksState by flashcardViewModel.decks.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
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
                            text = stringResource(R.string.revise),
                            subtext = stringResource(R.string.testYourself),
                            onClick = { navController.navigate(Screen.CreateExam.route) }
                        )
                        CardWithIcon(
                            icon = Icons.Default.LibraryAdd,
                            text = stringResource(R.string.newMaterial),
                            subtext = stringResource(R.string.createNewDeck),
                            onClick = { navController.navigate(Screen.AddDeck.route) }
                        )
                    }

                    // Suggested Decks
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        // Carousel
                        if (decksState.isNotEmpty()) {
                            CarouselWithPager(
                                title = stringResource(R.string.suggestedDecks),
                                icon = Icons.AutoMirrored.Filled.ArrowForward,
                                decks = decksState,
                                onClick = {navController.popBackStack()
                                    navController.navigate(Screen.Library.route)
                                          },
                                onCreateDeckClick = {navController.popBackStack()
                                                    navController.navigate(Screen.AddDeck.route)},
                                navController = navController
                            )
                        } else {
                            CreateDeckPrompt(onCreateDeckClick = { navController.navigate(Screen.AddDeck.route) })
                        }
                    }

                    CardWithListAndButton()
                }
            }
        }
    )
}

/**
 * Composable function for the card with icon.
 *
 * @param icon ImageVector icon for the card.
 * @param text Text displayed on the card.
 * @param subtext Subtext displayed on the card.
 * @param onClick Callback for clicking on the card.
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
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
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
 * Composable function for the carousel with pager.
 *
 * @param title Title of the carousel.
 * @param icon ImageVector icon for the carousel.
 * @param decks List of suggested decks.
 * @param onCreateDeckClick Callback for clicking on creating a new deck.
 * @param onClick Callback for clicking on the carousel.
 * @param navController
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselWithPager(
    title: String,
    icon: ImageVector,
    decks: List<Deck>,
    onCreateDeckClick: () -> Unit,
    onClick: () -> Unit,
    navController: NavController
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick.invoke() }
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.padding(horizontal = 115.dp))
            Icon(imageVector = icon, contentDescription = "Next")
        }
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = rememberPagerState(pageCount = { decks.size }),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { page ->
            CardWithCarouselItem(decks[page], navController = navController)
        }
    }
}

/**
 * Composable function for the create deck prompt.
 *
 * @param onCreateDeckClick Callback for clicking on creating a new deck.
 */
@Composable
fun CreateDeckPrompt(onCreateDeckClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "No suggested decks available. Create one?",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCreateDeckClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Create Deck")
        }
    }
}

/**
 * Composable function for the card with carousel item.
 *
 * @param deck The suggested deck.
 */
@Composable
fun CardWithCarouselItem(
    deck: Deck,
    navController: NavController,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Collects the flashcards state
    val flashcardsState by flashcardViewModel.flashcards.observeAsState(initial = emptyList())

    // Fetch flashcards for the deck
    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardsForDeck(deck.id)
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = deck.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = deck.description,
                style = MaterialTheme.typography.bodyMedium,
            )

            // Info Section with Labels
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoLabel(text = "Subject:", value = deck.subject)
                Spacer(Modifier.weight(1f))
                InfoLabel(text = "Difficulty:", value = deck.averageDifficulty.toString().lowercase().capitalize())
                Spacer(Modifier.weight(1f))
                if(flashcardsState.isNotEmpty()){
                    InfoLabel(text = "Cards:", value = flashcardsState.count().toString())
                }

            }
        }
    }
}

/**
 * Composable function for the info label.
 *
 * @param text Label text.
 * @param value Value text.
 */
@Composable
fun InfoLabel(text: String, value: String) {
    Column {
        Text(text = text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(text = value, style = MaterialTheme.typography.bodySmall)
    }
}

/**
 * Composable function for the card with list and button.
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
