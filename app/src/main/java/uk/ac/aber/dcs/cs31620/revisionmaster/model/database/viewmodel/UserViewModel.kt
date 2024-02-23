package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ValueEventListener
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User

class UserViewModel : ViewModel() {

    var listener by mutableStateOf<ValueEventListener?>(null)
    var addUserToDBResponse by mutableStateOf<Result<User>>(Result.success(null))
    private set

}