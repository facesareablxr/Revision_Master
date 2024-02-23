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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.TopLevelScaffold


/**
 * Represents the home screen, has individual cards for each exercise for the current day.
 * @author Lauren Davis
 */
@Composable
fun HomeScreen(
    navController: NavHostController
) {
    TopLevelScaffold(
        navController = navController,
        pageContent = { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Search Bar
                    SearchBars()

                    // Two Cards in a Row
                    Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)) {
                        CardWithIcon(
                            icon = Icons.Default.Book,
                            text = "Mock Test",
                            subtext = "Test Yourself",
                            onClick = {}
                        )
                        CardWithIcon(
                            icon = Icons.Default.LibraryAdd,
                            text = "New Material",
                            subtext = "Create New Flashcards",
                            onClick = {}
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
                            title = "Subjects",
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            items = listOf("Hello", "Test", "World"),
                            onClick = {}
                        )
                    }

                        // Card with List of Items and Trailing Button
                        CardWithListAndButton()
                    }
            }
        }
    )
}

//Only temp,actual search bar implementation to be done when firebase in place
@Composable
fun SearchBars() {
    var searchText by remember { mutableStateOf("") }
    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        placeholder = { Text("Search") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        shape = SearchBarDefaults.inputFieldShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun CardWithIcon(
    icon: ImageVector,
    text: String,
    subtext: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.padding(8.dp),
        onClick = onClick, // Pass the onClick lambda to Card
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
            Text(text = text, style = MaterialTheme.typography.headlineSmall)
            Text(text = subtext, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselWithPager(
    title: String,
    icon: ImageVector,
    items: List<String>,
    onClick: () -> Unit // Add optional onClick for the entire carousel
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick.invoke() } // Add onClick if provided
        ) {


            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.padding(horizontal = 115.dp))
            Icon(imageVector = icon, contentDescription = "Next")

        }
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = rememberPagerState(pageCount = { items.size }),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { page ->
            CardWithCarouselItem(item = items[page])
        }
    }
}

@Composable
fun CardWithCarouselItem(item: String) {
    Card(
        modifier = Modifier.padding(8.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer))
    {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = item,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            // Add additional content inside the carousel item if needed
        }
    }
}

@Composable
fun CardWithListAndButton() {
    Card(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Group Suggestions",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
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
