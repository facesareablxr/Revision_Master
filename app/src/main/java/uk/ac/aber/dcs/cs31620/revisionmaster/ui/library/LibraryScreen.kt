package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.NonMainTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LibraryScreen(
    navController: NavHostController,
    flashcardViewModel: FlashcardViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val decksState by flashcardViewModel.decks.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        userViewModel.getUserData() // Fetch user data
    }

    flashcardViewModel.getUserDecks()


    Scaffold(
        topBar = { NonMainTopAppBar(navController, stringResource(R.string.library)) },
        bottomBar = { MainPageNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddDeck.route) }
            ) {
                Icon(imageVector = Icons.Default.Add, stringResource(id = R.string.addData))
            }
        }
    ) { innerPadding ->
        if (decksState.isNotEmpty()) {
            DeckList(decksState, navController, innerPadding, flashcardViewModel)
        } else {
            NoDataMessage(innerPadding)
        }
    }
}

@Composable
fun DeckList(
    decks: List<Deck>,
    navController: NavHostController,
    paddingValues: PaddingValues,
    flashcardViewModel: FlashcardViewModel
) {
    LazyColumn(contentPadding = paddingValues) {
        items(decks) { deck ->
            DeckItem(deck, navController, flashcardViewModel)
        }
    }
}

@Composable
fun DeckItem(deck: Deck, navController: NavHostController, flashcardViewModel: FlashcardViewModel) {
    LaunchedEffect(deck.id) {
        flashcardViewModel.getDeckWithFlashcards(deck.id)
    }

    val detailedDeck by flashcardViewModel.deckWithFlashcards.observeAsState()


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate(Screen.DeckDetails.route + "/${deck.id}")
            },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = deck.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = deck.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = {}) {
                    Text(
                        text = deck.subject,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))

                OutlinedButton(onClick = { }) {
                    Text(
                        text = detailedDeck?.averageDifficulty?.toString()?.lowercase()?.capitalize()
                            ?: deck.averageDifficulty.toString().lowercase().capitalize(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }
}


@Composable
fun NoDataMessage(contentPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.addData),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}