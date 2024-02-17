package uk.ac.aber.dcs.cs31620.revisionmaster.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.aber.dcs.cs31620.revisionmaster.R

/**
 * Composable for a button spinner used in edit and add screens. It displays a list of items for the
 * user to choose from and updates the text to display the chosen item.
 *
 * Adapted from https://github.com/chriswloftus/feline-adoption-agency-v10 with the addition of a default label.
 *
 * @param items is the list of items to choose from
 * @param label is the default label to display
 * @param modifier is the modifier for the button spinner
 * @param fontSize is the font size of the displayed text
 * @param itemClick is the callback function when an item is selected
 */
@Composable
fun ButtonSpinner(
    items: List<String>,
    label: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    itemClick: (String) -> Unit = {}
) {
    // Adds a default label, this was done after encountering issues displaying the days of the week as labels, inefficient, but it works.
    var itemText by rememberSaveable { mutableStateOf(label) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    OutlinedButton(
        modifier = modifier,
        onClick = { expanded = !expanded }
    ) {
        Text(
            text = itemText,
            fontSize = fontSize,
            modifier = Modifier.padding(end = 8.dp)
        )
        // Adds the icon to the button
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = stringResource(R.string.dropdown)
        )
        // Defines the dropdown menu and its items
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        )
        {
            items.forEach {
                DropdownMenuItem(
                    text = { Text(text = it) },
                    onClick = {
                        // Collapse the dropdown
                        expanded = false
                        // Remember the name of the item selected
                        itemText = it
                        // Return the state to the caller
                        itemClick(it)
                    }
                )
            }
        }
    }
}

