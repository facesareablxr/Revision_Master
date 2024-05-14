package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel


import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository.UserRepository
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.Schedule
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.model.util.Response
import java.util.Calendar
import java.util.Date

/**
 * This class represents the ViewModel responsible for managing user-related data
 * and actions within the application.
 */
class UserViewModel(): ViewModel() {
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
        profilePictureUrl: String?,
        imagePath: Uri?
    ) = viewModelScope.launch(Dispatchers.IO) {
        val retrievedUser = user.value ?: return@launch
        val updatedUser = retrievedUser.copy(
            firstName = firstName,
            lastName = lastName,
            institution = institution,
            profilePictureUrl = profilePictureUrl
        )
        if (imagePath != null) {
            userRepository.updateUser(updatedUser, imagePath)
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

    // Holds the list of all users
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun getAllUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _users.value = users
            }
        }
    }

    fun addSchedule(
        dayOfWeek: String,
        startTime: Long,
        endTime: Long,
        eventType: String,
        selectedDecks: List<Deck>,
        description: String,
        repeating: Boolean
    ) {
        val schedule = Schedule(
            dayOfWeek = dayOfWeek,
            startTime = startTime,
            endTime = endTime,
            focus = eventType,
            decks = selectedDecks,
            description = description,
            repeat = repeating
        )
        val userId = currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.addSchedule(userId, schedule)
        }
    }

    fun updateSchedule(scheduleId: String, schedule: Schedule) {
        val userId = currentUser?.uid ?: return
        viewModelScope.launch {
            val response = userRepository.updateSchedule(userId, scheduleId, schedule)
        }
    }

    // Holds the list of all schedules for the current user
    private val _schedules = MutableLiveData<List<Schedule>>(emptyList())
    val schedules: LiveData<List<Schedule>> = _schedules

    fun getSchedules() {
        currentUser?.let { user ->
            viewModelScope.launch {
                val retrievedSessions = UserRepository.getSchedules(user.uid)
                _schedules.value = retrievedSessions
            }
        }
    }


    // Holds the list of all schedules for the current user
    private val _schedule = MutableLiveData<Schedule>()
    val schedule: LiveData<Schedule> = _schedule

    // Function to get schedule details by ID
    fun getScheduleDetails(scheduleId: String): LiveData<Schedule?> {
        viewModelScope.launch {
            try {
                val schedule = UserRepository.getScheduleDetails(currentUser!!.uid, scheduleId)
                _schedule.value = schedule!!
            } catch (e: Exception) {
                // Handle error gracefully, for example:
                Log.e(TAG, "Error fetching schedule details: ${e.message}")
            }
        }
        return _schedule
    }


    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            try {
                userRepository.deleteSchedule(currentUser!!.uid, scheduleId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteSchedulesContainingDay(day: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteSchedulesContainingDay(userId = currentUser!!.uid, day = day)
        }
    }

    fun deleteProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentUser?.uid
            signOut()
            UserRepository.deleteUser(userId!!)
        }
    }

    // Holds the user's followers list
    private val _followers = MutableStateFlow<List<String>>(emptyList())
    val followers: StateFlow<List<String>> = _followers


    // Holds the list of all schedules for the current user
    private val _following = MutableStateFlow<List<String>>(emptyList())
    val following: StateFlow<List<String>> = _following


    // Function to handle following a user
    fun followUser(userIdToFollow: String) {
        val currentUserId = currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                userRepository.followUser(currentUserId, userIdToFollow)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Function to handle unfollowing a user
    fun unfollowUser(userIdToUnfollow: String) {
        val currentUserId = currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                userRepository.unfollowUser(currentUserId, userIdToUnfollow)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Function to get the user's followers list
    private fun getFollowersList() {
        viewModelScope.launch {
            try {
                val followersIds = userRepository.getFollowers()
                val followersList = mutableListOf<String>()
                for (followerId in followersIds) {
                    userRepository.getUserById(followerId)?.let { followersList.add(it.toString()) }
                }
                _followers.value = followersList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Function to get the user's following list
    fun getFollowingList() {
        viewModelScope.launch {
            try {
                val followingIds = userRepository.getFollowingList()
                val followingList = mutableListOf<String>()
                for (followingId in followingIds) {
                    userRepository.getUserById(followingId)?.let { followingList.add(it.toString()) }
                }
                _following.value = followingList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
