package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    val user by userViewModel.user.collectAsState(initial = null)
    val decksState by flashcardViewModel.decks.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        userViewModel.getUserData() // Fetch user data
    }

    LaunchedEffect(user) {
        user?.let { user ->
            user.username?.let { username ->
                flashcardViewModel.getUserDecks(username)
            }
        }
    }

    Scaffold(
        topBar = { NonMainTopAppBar(navController, stringResource(R.string.library)) },
        bottomBar = { MainPageNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddDeck.route) }
            ) {
                Icon(imageVector = Icons.Default.Add, stringResource(id = R.string.addData))
            }
        },
        isFloatingActionButtonDocked = false,
        floatingActionButtonPosition = FabPosition.End
    ) {
        if (decksState.isNotEmpty()) {
            DeckList(decks = decksState, navController = navController)
        } else {
            NoDataMessage()
        }
    }
}

@Composable
fun DeckList(decks: List<Deck>, navController: NavHostController) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(decks) { deck ->
            DeckItem(deck = deck, navController = navController)
        }
    }
}

@Composable
fun DeckItem(deck: Deck, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Navigate to deck details
                // navController.navigate(Screen.DeckDetails.route + "/${deck.id}")
            },
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = deck.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = deck.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Surface(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 6.dp)
                    .wrapContentWidth(),
                shape = RoundedCornerShape(4.dp),
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Row(
                    modifier = Modifier.padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = deck.subject,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Composable
fun NoDataMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.addData),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
