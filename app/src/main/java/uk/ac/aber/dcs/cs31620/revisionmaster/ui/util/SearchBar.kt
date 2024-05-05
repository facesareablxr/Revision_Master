package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.ac.aber.dcs.cs31620.revisionmaster.R

/**
 * This composable function represents a search bar.
 * @param searchQuery: Current search query.
 * @param onSearchQueryChanged: Callback function to update the search query.
 */
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        placeholder = { Text(text = stringResource(id = R.string.search)) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null)
        }
    )
}