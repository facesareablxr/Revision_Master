package uk.ac.aber.dcs.cs31620.revisionmaster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
    // State to track the currently displayed text
    var itemText by rememberSaveable { mutableStateOf(label) }
    // State to control the dropdown menu's visibility
    var expanded by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            // Box to contain the button-like element
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { expanded = !expanded }) // Toggle dropdown on click
                        .padding(16.dp)

                ) {
                    Text(
                        text = itemText,
                        fontSize = fontSize,
                    )
                    // Icon to indicate dropdown
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.moreOptions)
                    )
                }
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
                .padding(start = 12.dp,end = 12.dp)
        ) {
            items.forEach { dropdownItemText ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        itemText = dropdownItemText
                        itemClick(dropdownItemText)
                    },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 12.dp,end = 12.dp),
                    text = {
                        Text(
                            text = dropdownItemText,
                            maxLines = 1
                        )
                    }
                )
            }
        }
    }
}



