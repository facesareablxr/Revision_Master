package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.FlashcardMode
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBarWithMenu
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import java.util.Locale

/**
 * Composable function for the screen where the user can view the details of their deck.
 *
 * @param navController NavController for navigation.
 * @param deckId ID of the deck being viewed.
 * @param flashcardViewModel ViewModel for flashcard operations. Default is viewModel().
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DeckDetailsScreen(
    navController: NavController, // NavController for navigation
    deckId: String, // ID of the deck being viewed
    flashcardViewModel: FlashcardViewModel = viewModel() // ViewModel for flashcard operations
) {
    // Collects the deck state
    val deckState by flashcardViewModel.deckDetails.observeAsState(initial = null)
    // Flag to control delete confirmation dialog visibility
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Fetch deck details from ViewModel
    LaunchedEffect(Unit) {
        flashcardViewModel.getDeckDetails(deckId)
    }

    // Fetch flashcards for the deck when deck state changes
    LaunchedEffect(deckState) {
        if (deckState != null) {
            flashcardViewModel.getFlashcardsForDeck(deckId)
        }
    }

    // Scaffold for the screen layout
    Scaffold(
        topBar = {
            deckState?.let { deck ->
                // Top app bar with deck title and edit/delete actions
                SmallTopAppBarWithMenu(
                    title = deck.name,
                    navController = navController,
                    onEditDeckClick = {
                        navController.navigate(Screen.EditDeck.route + "/${deck.id}")
                    },
                    onDeleteClick = {
                        showDeleteDialog = true
                    }
                )
            }
        },
        content = { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                val tabs = listOf("Materials", "Progress")
                val selectedTabIndex = remember { mutableStateOf(0) }

                // Tab row for switching between content tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex.value,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value])
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTabIndex.value == index,
                            onClick = { selectedTabIndex.value = index },
                            text = { Text(text = tab) }
                        )
                    }
                }

                // Content based on selected tab
                Box {
                    when (selectedTabIndex.value) {
                        0 -> {
                            MaterialsContent(flashcardViewModel, navController, deckId)
                        }

                        1 -> {
                            ProgressContent(flashcardViewModel, deckId, navController)
                        }
                    }
                }
            }

            // Delete confirmation dialog
            if (showDeleteDialog) {
                deckState?.let { deck ->
                    DeleteConfirmationDialog(
                        deckName = deck.name,
                        onDismiss = { showDeleteDialog = false },
                        onConfirm = {
                            flashcardViewModel.deleteDeck(deck.id)
                            showDeleteDialog = false
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    )
}

/**
 * Composable function for the materials content tab.
 */
/**
 * Composable function for the materials content tab.
 */
@Composable
fun MaterialsContent(
    flashcardViewModel: FlashcardViewModel,
    navController: NavController,
    deckId: String
) {
    // Collects the flashcards state
    val flashcardsState by flashcardViewModel.flashcards.observeAsState(initial = null)

    // Fetch flashcards for the deck
    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardsForDeck(deckId)
    }

    // Display flashcards or message if none exist
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            flashcardsState?.let { flashcardList ->
                if (flashcardList.isEmpty()) {
                    Text(
                        "No flashcards in this deck yet.",
                        Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    // LazyColumn to display flashcards
                    LazyColumn(
                        verticalArrangement = Arrangement.Top
                    ) {
                        items(flashcardList) { flashcard ->
                            FlashcardItem(flashcard, navController, deckId)
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate(Screen.AddFlashcards.route + "/${deckId}") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.addData))
        }
    }
}

/**
 * Composable function for displaying a flashcard item.
 *
 * @param flashcard The flashcard to display.
 * @param navController NavController for navigation.
 * @param deckId ID of the deck containing the flashcard.
 */
@Composable
fun FlashcardItem(flashcard: Flashcard, navController: NavController, deckId: String) {
    // Card displaying flashcard details
    Card(modifier = Modifier
        .padding(vertical = 8.dp, horizontal = 16.dp)
        .fillMaxWidth()
        .clickable { navController.navigate(Screen.EditFlashcards.route + "/${flashcard.id}" + "/${deckId}") }) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Flashcard question
            Text(
                text = flashcard.question,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp)) // Spacer for vertical spacing
            // Flashcard answer
            Text(text = flashcard.answer, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp)) // Spacer for vertical spacing
            // Flashcard difficulty
            Surface(
                modifier = Modifier
                    .padding(4.dp)
                    .wrapContentSize(),
                shape = RoundedCornerShape(2.dp),
            ) {
                val difficulty = flashcard.difficulty.toString()
                // Convert difficulty label to lowercase with a capital first letter
                val difficultyCase = difficulty.lowercase().capitalize(Locale.ROOT)
                Row(
                    modifier = Modifier.padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = difficultyCase,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Composable function for the delete confirmation dialog.
 *
 * @param deckName Name of the deck to be deleted.
 * @param onDismiss Callback for dismissing the dialog.
 * @param onConfirm Callback for confirming the deck deletion.
 */
@Composable
fun DeleteConfirmationDialog(
    deckName: String, // Name of the deck to be deleted
    onDismiss: () -> Unit, // Callback for dismissing the dialog
    onConfirm: () -> Unit // Callback for confirming the deck deletion
) {
    // AlertDialog for confirming deck deletion
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { R.string.deleteDeck },
        text = {
            Text("Are you sure you want to delete the deck '$deckName'? This action cannot be undone.")
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
            }) {
                Text(stringResource(R.string.delete)) // Confirm button text
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel)) // Dismiss button text
            }
        }
    )
}

/**
 * Composable function for the progress content tab.
 */
@Composable
fun ProgressContent(
    flashcardViewModel: FlashcardViewModel,
    deckId: String,
    navController: NavController
) {
    // Collects the deck state
    val deckState by flashcardViewModel.deckDetails.observeAsState(initial = null)
    // Flag to control bottom sheet visibility
    var showBottomSheet by remember { mutableStateOf(false) }
    // Selected mode for interacting with flashcards
    var selectedMode by remember { mutableStateOf(FlashcardMode.VIEW) }

    // State for loading indicator
    var showLoading by remember { mutableStateOf(false) }

    // Fetch flashcards for the deck (with loading indicator)
    LaunchedEffect(Unit) {
        showLoading = true // Show indicator while loading
        flashcardViewModel.getFlashcardsForDeck(deckId)
        showLoading = false // Hide indicator after loading
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressBar(deckState)
            // Display top 5 flashcards with the lowest mastery
            val flashcards = deckState?.cards ?: emptyList()
            val top5LowestMastery = flashcards.sortedBy { it.mastery }.take(5)
            LazyColumn(
                modifier = Modifier.padding(vertical = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(top5LowestMastery) { flashcard ->
                    FlashcardItem(
                        flashcard = flashcard,
                        deckId = deckId,
                        navController = navController
                    )
                }
            }
        }
        // Modal bottom sheet for selecting flashcard interaction mode
        if (showBottomSheet) {
            ModalBottomSheet(
                content = {
                    FlashcardModeSelectionSheet(
                        selectedMode = selectedMode,
                        onModeSelected = { mode ->
                            selectedMode = mode
                            when (mode) {
                                FlashcardMode.VIEW -> {
                                    // Navigate to view flashcards screen
                                    navController.navigate(Screen.ViewFlashcards.route + "/$deckId")
                                }

                                FlashcardMode.TEST_SELF -> {
                                    // Navigate to test yourself screen
                                    navController.navigate(Screen.TestYourself.route + "/$deckId")
                                }

                                FlashcardMode.MATCH_GAME -> {
                                    // Navigate to match game screen
                                    navController.navigate(Screen.MatchGame.route + "/$deckId")
                                }

                                FlashcardMode.FILL_IN_BLANKS -> {
                                    // Navigate to fill in the blanks screen
                                    navController.navigate(Screen.FillInBlanks.route + "/$deckId")
                                }
                            }
                            showBottomSheet = false
                        }
                    )
                },
                sheetState = rememberModalBottomSheetState(false),
                onDismissRequest = { showBottomSheet = false }
            )
        }
        // Floating action button to select interaction mode
        FloatingActionButton(
            onClick = {
                showBottomSheet = true
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.selectMode))
        }
    }
}

/**
 * Composable function to draw a custom circular progress UI.
 *
 * @param size The diameter of the circular progress UI.
 * @param strokeWidth The thickness of the progress stroke.
 * @param backgroundArcColor The color of the background arc that shows the full extent of the progress circle.
 */
@Composable
fun CircularProgressBar(
    deckState: Deck?,
    size: Dp = 150.dp,
    strokeWidth: Dp = 12.dp,
    backgroundArcColor: Color = Color.LightGray
) {
    val masteryLevel = deckState?.mastery ?: 0.0
    Canvas(modifier = Modifier.size(size)) {
        drawArc(
            color = backgroundArcColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            size = Size(size.toPx(), size.toPx()),
            style = Stroke(width = strokeWidth.toPx())
        )
    }
    Text(
        text = "Mastery Level: ${(masteryLevel).toInt()}%",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
    )
}


/**
 * Composable function for the flashcard mode selection sheet.
 */
@Composable
fun FlashcardModeSelectionSheet(
    selectedMode: FlashcardMode,
    onModeSelected: (FlashcardMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Interaction Mode:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Display mode selection buttons
        FlashcardMode.values().forEach { mode ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                RadioButton(
                    selected = mode == selectedMode,
                    onClick = { onModeSelected(mode) }
                )
                Text(
                    text = mode.label,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
