package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel


import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.Response
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository.UserRepository
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.goToHome

class UserViewModel: ViewModel() {
    var listener by mutableStateOf<ValueEventListener?>(null)

    var addUserToDBResponse by mutableStateOf<Response<User>>(Response.Success(null))
    private set

    fun addUserToDB(user: User) = viewModelScope.launch(Dispatchers.IO) {
        addUserToDBResponse = Response.Loading
        addUserToDBResponse = UserRepository.addUser(user)
    }

    // Function to handle user sign-in after registration (private)
    private fun signInUser(user: User, context: Activity, navController: NavHostController) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(context) { task ->
                if (task.isSuccessful) {
                    goToHome(navController)
                } else {
                    Toast.makeText(context, "Sign-in failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}