package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel


import android.graphics.Bitmap
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
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.model.util.Response
import java.util.Calendar
import java.util.Date

/**
 * This class represents the ViewModel responsible for managing user-related data
 * and actions within the application.
 */
class UserViewModel : ViewModel() {
    // Definition of the user repository call, and the result of the add user to database.
    private val userRepository = UserRepository
    private var addUserToDBResponse by mutableStateOf<Response<User>>(Response.Success(null))

    // The user currently logged in on the firebase database
    private val currentUser = FirebaseAuth.getInstance().currentUser

    /**
     * Holds the users data for displaying
     */
    private val _userState = MutableStateFlow<Response<User>?>(null)
    val userState: StateFlow<Response<User>?> = _userState

    /**
     * Adds the user to the database
     */
    fun addUserToDB(user: User) = viewModelScope.launch(Dispatchers.IO) {
        addUserToDBResponse = Response.Loading
        addUserToDBResponse = userRepository.addUser(user)
    }

    /**
     * Holds the currently retrieved user's data.
     */
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user


    /**
     * Fetches the user's data based on the current user's ID.
     */
    fun getUserData() {
        if (currentUser != null) {
            val userId = currentUser.uid
            viewModelScope.launch {
                val retrievedUser = userRepository.getUserById(userId)
                _user.value = retrievedUser
            }
        }
    }

    /**
     * Signs the current user out of the application.
     */
    fun signOut() {
        viewModelScope.launch {
            val auth = FirebaseAuth.getInstance()
            auth.signOut()
            _userState.value = null
        }
    }

    /**
     * Updates the current user's profile data.
     */
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

    // Holds the current user's list of followed users (represented by IDs).
    private val _followingList = mutableStateOf<List<String>>(emptyList())
    val followingList = _followingList

    /**
     * Retrieves the current user's 'following' list.
     */
    fun getFollowingList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val following = userRepository.getFollowingList()
                _followingList.value = following
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     *
     */
    private val _followerList = mutableStateOf<List<String>>(emptyList())
    val followerList = _followerList

    /**
     * Retrieves the current user's 'follower' list.
     */
    fun getFollowerList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val follower = userRepository.getFollowers()
                _followerList.value = follower
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Updates the user's streak using their ID if necessary. Streak updates only to
     * avoid redundant database writes.
     */
    fun updateStreakIfNeeded(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            // Ensure the user has logged in before
            user.lastLoginDate ?: return@launch

            // Get reference date boundaries
            val todayStart = today()
            val yesterdayStart = yesterday()

            // Check if the user needs streak update
            if (user.lastLoginDate!! < todayStart) {
                val updatedStreak = if (user.lastLoginDate!! >= yesterdayStart) {
                    user.currentStreak + 1
                } else {
                    0 // Reset streak
                }

                val updatedUserData = user.copy(
                    currentStreak = updatedStreak,
                    lastLoginDate = todayStart
                )
                UserRepository.updateUserStreakAndData(currentUser!!.uid, updatedUserData)
            }
        }
    }

    /**
     * Function to calculate what day it was yesterday
     */
    private fun yesterday(): Date {
        val yesterdayStart = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        return yesterdayStart
    }

    /**
     * Calculates what day it is today
     */
    private fun today(): Date {
        val todayStart = Calendar.getInstance().apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        return todayStart
    }

    /**
     * Holds the Profile Image URI
     */
    private val _profileImage = MutableStateFlow<Bitmap?>(null)
    val profileImage: StateFlow<Bitmap?> = _profileImage

    /**
     * Downloads the current profile image using the users Profile Image URI
     */
    fun downloadUserProfileImage(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = userRepository.downloadUserProfileImage(user)
            _profileImage.value = bitmap
        }
    }
}