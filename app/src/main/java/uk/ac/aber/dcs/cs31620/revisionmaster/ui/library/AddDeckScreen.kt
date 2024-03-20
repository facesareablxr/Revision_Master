package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun AddDeckScreen(
    viewModel: NavHostController
) {

    var selectedSubject by remember { mutableStateOf("") }
    var selectedModule by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("") }
    var isClassTextFieldVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Add Deck",
            style = MaterialTheme.typography.headlineSmall
        )

    }
}

