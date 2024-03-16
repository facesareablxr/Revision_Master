package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository.UserRepository
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User
import uk.ac.aber.dcs.cs31620.revisionmaster.model.util.Response

class UserViewModel: ViewModel() {
    private val userRepository = UserRepository
    private var addUserToDBResponse by mutableStateOf<Response<User>>(Response.Success(null))

    private val _userState = MutableStateFlow<Response<User>?>(null)
    val userState: StateFlow<Response<User>?> = _userState

    fun addUserToDB(user: User) = viewModelScope.launch(Dispatchers.IO) {
        addUserToDBResponse = Response.Loading
        addUserToDBResponse = userRepository.addUser(user)
    }

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun getUserData() {
        if (currentUser != null) {
            val email = currentUser.email ?: return
            viewModelScope.launch {
                val retrievedUser = userRepository.getUserByEmail(email)
                _user.value = retrievedUser
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            // Firebase sign out logic
            val auth = FirebaseAuth.getInstance()
            auth.signOut()
            // Update user state to reflect sign-out (optional)
            _userState.value = null
        }
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        institution: String,
        profilePictureUrl: String?
    ) = viewModelScope.launch(Dispatchers.IO) {
        val retrievedUser = user.value ?: return@launch
        val updatedUser = retrievedUser.copy(
            firstName = firstName,
            lastName = lastName,
            institution = institution,
            profilePictureUrl = profilePictureUrl
        )
        userRepository.updateUser(updatedUser)
    }

}