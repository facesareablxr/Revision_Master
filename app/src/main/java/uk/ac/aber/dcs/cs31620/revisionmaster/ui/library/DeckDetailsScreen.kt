package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.FlashcardMode
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBarWithMenu
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Composable function for the screen where the user can view the details of their deck.
 *
 * @param navController NavController for navigation.
 * @param deckId ID of the deck being viewed.
 * @param flashcardViewModel ViewModel for flashcard operations. Default is viewModel().
 */
@Composable
fun DeckDetailsScreen(
    navController: NavController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    val deckState by flashcardViewModel.deckDetails.observeAsState(initial = null)
    var showDeleteDialog by remember { mutableStateOf(false) }
    // Collects the flashcards state
    val flashcardsState by flashcardViewModel.flashcards.observeAsState(initial = null)
    LaunchedEffect(Unit) {
        flashcardViewModel.getDeckDetails(deckId)
        flashcardViewModel.getFlashcardsForDeck(deckId)
    }




    Scaffold(
        topBar = {
            deckState?.let { deck ->
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

                Box {
                    when (selectedTabIndex.value) {
                        0 -> {
                            MaterialsContent(flashcardsState, navController, deckId)
                        }

                        1 -> {
                            deckState?.let { _ ->
                                if (flashcardsState != null) {
                                    ProgressContent(flashcardViewModel, deckId, navController)
                                } else {
                                    Text(
                                        text = "No progress available because there are no flashcards.",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

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
 *
 * @param navController NavController for navigation.
 * @param deckId ID of the deck being viewed.
 * @param flashcardViewModel ViewModel for flashcard operations. Default is viewModel().
 */
@Composable
fun MaterialsContent(
    flashcardsState: List<Flashcard>?,
    navController: NavController,
    deckId: String
) {

    // Display flashcards or message if none exist
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            flashcardsState.let { flashcardList ->
                if (flashcardList.isNullOrEmpty()) {
                    Text(
                        stringResource(R.string.noCards),
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        textAlign = TextAlign.Center
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
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FlashcardItem(flashcard: Flashcard, navController: NavController, deckId: String) {
    // Card displaying flashcard details
    Card(modifier = Modifier
        .padding(vertical = 8.dp, horizontal = 16.dp)
        .fillMaxWidth()
        .clickable { navController.navigate(Screen.EditFlashcards.route + "/${flashcard.id}" + "/${deckId}") }) {
        Row(modifier = Modifier.padding(16.dp)) {
            if (flashcard.imageUri != null) {
                GlideImage(
                    model = flashcard.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .align(CenterVertically),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Spacer for horizontal spacing
            Column {
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
                        .wrapContentSize(),
                    shape = RoundedCornerShape(2.dp),
                ) {
                    val difficulty = flashcard.difficulty.toString()
                    // Convert difficulty label to lowercase with a capital first letter
                    val difficultyCase = difficulty.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    Row(
                        modifier = Modifier.padding(6.dp),
                        verticalAlignment = CenterVertically
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
    deckName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
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
 *
 * @param navController NavController for navigation.
 * @param deckId ID of the deck being viewed.
 * @param flashcardViewModel ViewModel for flashcard operations. Default is viewModel().
 */
@Composable
fun ProgressContent(
    flashcardViewModel: FlashcardViewModel,
    deckId: String,
    navController: NavController
) {
    val deckState by flashcardViewModel.deckDetails.observeAsState(initial = null)
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(FlashcardMode.VIEW) }
    var showLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showLoading = true
        flashcardViewModel.getFlashcardsForDeck(deckId)
        showLoading = false
    }
    Scaffold(content = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CircularProgressBar(deckState)
            Spacer(modifier = Modifier.padding(8.dp))
            // Display recent test results in cards
            RecentTestResults(deckId, flashcardViewModel, navController)
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                content = {
                    FlashcardModeSelectionSheet(
                        onModeSelected = { mode ->
                            selectedMode = mode
                            when (mode) {
                                FlashcardMode.VIEW -> {
                                    navController.navigate(Screen.ViewFlashcards.route + "/$deckId")
                                }

                                FlashcardMode.TEST_SELF -> {
                                    navController.navigate(Screen.TestYourself.route + "/$deckId")
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
    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showBottomSheet = true
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = stringResource(R.string.selectMode)
                )
            }
        }
    )
}

@Composable
fun CircularProgressBar(
    deckState: Deck?,
    size: Dp = 150.dp,
    strokeWidth: Dp = 12.dp,
    backgroundArcColor: Color = Color.LightGray,
) {
    val masteryLevel = (deckState?.mastery ?: 0.0).toFloat()
    val progressColor = when {
        masteryLevel < 50 -> Color.Red // Novice
        masteryLevel < 75 -> Color.Yellow // Intermediate
        masteryLevel < 90 -> Color.Green // Proficient
        else -> Color.Blue // Mastery
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawArc(
                color = backgroundArcColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                size = Size(size.toPx(), size.toPx()),
                style = Stroke(width = strokeWidth.toPx())
            )
            drawArc(
                color = progressColor,
                startAngle = 270f,
                sweepAngle = ((masteryLevel * 3.6).toFloat()),
                useCenter = false,
                size = Size(size.toPx(), size.toPx()),
                style = Stroke(width = strokeWidth.toPx())
            )
        }
        Text(
            text = "Mastery Level: ${(masteryLevel)}%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Composable
fun RecentTestResults(
    deckId: String,
    flashcardViewModel: FlashcardViewModel,
    navController: NavController
) {
    val allTestResults by flashcardViewModel.allTestResultsForDeck.observeAsState()

    LaunchedEffect(Unit) {
        flashcardViewModel.getAllTestResultsForDeck(deckId)
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = (stringResource(R.string.recentResults)),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (allTestResults != null) {
            allTestResults!!.take(5).forEach { result ->
                val totalMatches = result.correct + result.incorrect
                var accuracy by remember { mutableIntStateOf(0) }
                accuracy =
                    if (totalMatches > 0) ((result.correct.toFloat() / totalMatches.toFloat()) * 100).roundToInt() else 0
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate(Screen.TestResults.route + "/${deckId}")
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Accuracy: ${accuracy}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        val time = result.elapsedTime
                        val minutes = time / 60
                        val seconds = time % 60
                        Text(
                            text = "Time: $minutes min $seconds sec",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable function for displaying the flashcard mode selection sheet.
 *
 * @param onModeSelected: Callback function to handle mode selection.
 */
@Composable
fun FlashcardModeSelectionSheet(
    onModeSelected: (FlashcardMode) -> Unit
) {
    // Column layout to arrange items vertically
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Text indicating to select interaction mode
        Text(
            text = "Select Interaction Mode:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Display mode selection buttons
        FlashcardMode.values().forEach { mode ->
            // Row layout to arrange items horizontally
            Row(
                verticalAlignment = CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // RadioButton to represent each mode
                RadioButton(
                    selected = false,
                    onClick = { onModeSelected(mode) }
                )
                // Text label for the mode
                Text(
                    text = mode.label,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.padding(16.dp))
    }
}