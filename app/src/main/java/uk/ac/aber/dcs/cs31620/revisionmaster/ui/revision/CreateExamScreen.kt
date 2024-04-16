package uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.ExamType
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner

@Composable
fun CreateExamScreen(navController: NavController) {
    var selectedSubject by remember { mutableStateOf("Subject") }
    var allSubjectsSelected by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf("Difficulty") }
    var selectedExamType by remember { mutableStateOf(ExamType.SELF_MARKED) }
    val subjects = stringArrayResource(R.array.subjects).toList()
    val difficulties = stringArrayResource(R.array.difficulties).toList()


    Scaffold(
        topBar = {
            SmallTopAppBar(navController = navController, title = "Create Exam")
        }
    ) { innerPadding -> // Use Scaffold padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply scaffold padding
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!allSubjectsSelected) {
                SubjectSpinner(
                    subjects = subjects,
                    selectedSubject = selectedSubject,
                    onSubjectChange = { newValue -> selectedSubject = newValue }
                )
            }
            AllSubjectSelect(allSubjectsSelected)
            DifficultySelect(
                difficulty = difficulties,
                selectedSubject = selectedDifficulty.toString(),
                onSubjectChange = { _ -> selectedDifficulty}
            )

            // Create Exam Button
            Button(
                onClick = {
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            ) {
                Text("Create Exam")
            }
        }
    }
}

@Composable
private fun SubjectSpinner(
    subjects: List<String>,
    selectedSubject: String,
    onSubjectChange: (String) -> Unit
) {
    ButtonSpinner(
        items = subjects,
        label = selectedSubject,
        itemClick = onSubjectChange
    )
}

@Composable
private fun AllSubjectSelect(allSubjectsSelected: Boolean) {
    var allSubjectsSelected1 = allSubjectsSelected
    Row {
        Checkbox(
            checked = allSubjectsSelected1,
            onCheckedChange = { allSubjectsSelected1 = it }
        )
        Text("Include All Subjects")
    }
}

@Composable
fun ExamTypeSelect(
    examType:List<String>,
    selectedExamType: String,
    onSubjectChange: (String) -> Unit
) {
    ButtonSpinner(
        items = examType,
        label = selectedExamType,
        itemClick = onSubjectChange
    )
}


@Composable
private fun DifficultySelect(
    difficulty: List<String>,
    selectedSubject: String,
    onSubjectChange: (String) -> Unit
) {
     ButtonSpinner(
         items = difficulty,
         label = selectedSubject,
         itemClick = onSubjectChange
     )
}

