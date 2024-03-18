package uk.ac.aber.dcs.cs31620.revisionmaster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
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
    var itemText by rememberSaveable { mutableStateOf(label) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val cornerRadius = 4.dp

    Surface(
        modifier = modifier
            .clickable(onClick = { expanded = !expanded })
            .padding(16.dp),
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = itemText,
                fontSize = fontSize,
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(8.dp),
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = stringResource(id = R.string.dropdown)
            )
        }
        if (expanded) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it) },
                        onClick = {
                            expanded = false
                            itemText = it
                            itemClick(it)
                        }
                    )
                }
            }
        }

    }
}