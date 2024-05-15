package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel


import android.content.ContentValues.TAG
import android.util.Log
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
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository.UserRepository.getUserIdByUsername
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.Follows
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.Schedule
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import java.time.LocalDate

/**
 * This class represents the ViewModel responsible for managing user-related data
 * and actions within the application.
 */
class UserViewModel : ViewModel() {
    // Definition of the user repository call, and the result of the add user to database.
    private val userRepository = UserRepository
    // The user currently logged in on the firebase database
    private val currentUser = FirebaseAuth.getInstance().currentUser

    /** ------------------------------------ USER FUNCTIONS ------------------------------------ **/

    /** INITIALISING VARIABLES **/
    // Holds the users data for displaying
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState

    // Holds the currently retrieved user's data.
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Holds the list of all users
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    /**
     * Adds the user to the database
     */
    fun addUserToDB(user: User) = viewModelScope.launch(Dispatchers.IO) {
        try {
            userRepository.addUser(user)
        } catch (e: Exception) {
            // Handle error:
            Log.e(TAG, "Error adding user to database: ${e.message}")
        }
    }

    /**
     * Checks if there the username is available
     */
    fun checkUsernameAvailability(username: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isAvailable = userRepository.checkUsernameAvailability(username)
            callback(isAvailable)
        }
    }

    /**
     * Fetches the user's data based on the current user's ID.
     */
    fun getUserData() {
        if (currentUser != null) {
            val userId = currentUser.uid
            viewModelScope.launch {
                try {
                    val retrievedUser = userRepository.getUserById(userId)
                    _user.value = retrievedUser
                } catch (e: Exception) {
                    // Handle error:
                    Log.e(TAG, "Error fetching user data: ${e.message}")
                }
            }
        }
    }

    /**
     * Signs the current user out of the application.
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                val auth = FirebaseAuth.getInstance()
                auth.signOut()
                _userState.value
            } catch (e: Exception) {
                // Handle error:
                Log.e(TAG, "Error signing out: ${e.message}")
            }
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
        try {
            val retrievedUser = user.value ?: return@launch
            val updatedUser = retrievedUser.copy(
                firstName = firstName,
                lastName = lastName,
                institution = institution,
                profilePictureUrl = profilePictureUrl
            )
            if (profilePictureUrl != null) {
                userRepository.updateUser(updatedUser, profilePictureUrl)
            }
        } catch (e: Exception) {
            // Handle error gracefully:
            Log.e(TAG, "Error updating profile: ${e.message}")
        }
    }

    fun deleteProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = currentUser?.uid
                signOut()
                UserRepository.deleteUser(userId!!)
            } catch (e: Exception) {
                // Handle error gracefully:
                Log.e(TAG, "Error deleting profile: ${e.message}")
            }
        }
    }


    /** ----------------------------------- STREAK FUNCTIONS ----------------------------------- **/

    /**
     * Updates the user's streak using their ID if necessary. Streak updates only to
     * avoid redundant database writes.
     */
    fun updateStreakIfNeeded() {
        val userId = currentUser?.uid ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.getUserById(userId)
            try {
                // Ensure the user has logged in before
                user?.lastLoginDate ?: let {
                    // If there's no prior record of sign-in, set last login date to today
                    val updatedUserData = user?.copy(lastLoginDate = today())
                    updatedUserData?.let { it1 ->
                        UserRepository.updateUserStreakAndData(
                            currentUser.uid,
                            it1
                        )
                    }
                    return@launch
                }

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
                    UserRepository.updateUserStreakAndData(currentUser.uid, updatedUserData)
                }
            } catch (e: Exception) {
                val updatedUserData = user?.copy(
                    currentStreak = 0,
                    lastLoginDate = today()
                )
                updatedUserData?.let {
                    UserRepository.updateUserStreakAndData(currentUser.uid, it)
                }
                // Handle error
                Log.e(TAG, "Error updating streak: ${e.message}")
            }
        }
    }

    /**
     * Function to calculate what day it was yesterday
     */
    private fun yesterday(): String {
        return try {
            val yesterday = LocalDate.now().minusDays(1)
            yesterday.toString()
        } catch (e: Exception) {
            // Handle error
            Log.e(TAG, "Error calculating yesterday: ${e.message}")
            LocalDate.now().toString() // Return current date as fallback
        }
    }

    /**
     * Calculates what day it is today
     */
    private fun today(): String {
        return try {
            LocalDate.now().toString()
        } catch (e: Exception) {
            // Handle error
            Log.e(TAG, "Error calculating today: ${e.message}")
            LocalDate.now().toString() // Return current date as fallback
        }
    }


    /** ---------------------------------- SCHEDULE FUNCTIONS ---------------------------------- **/

    // Holds the list of all schedules for the current user
    private val _schedules = MutableLiveData<List<Schedule>>(emptyList())
    val schedules: LiveData<List<Schedule>> = _schedules

    // Holds the schedule details
    private val _schedule = MutableLiveData<Schedule>()
    val schedule: LiveData<Schedule> = _schedule

    /**
     * Adds a new schedule for the current user.
     */
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
            try {
                userRepository.addSchedule(userId, schedule)
            } catch (e: Exception) {
                // Handle error
                Log.e(TAG, "Error adding schedule: ${e.message}")
            }
        }
    }

    /**
     * Updates an existing schedule.
     */
    fun updateSchedule(scheduleId: String, schedule: Schedule) {
        val userId = currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                userRepository.updateSchedule(userId, scheduleId, schedule)
            } catch (e: Exception) {
                // Handle error :
                Log.e(TAG, "Error updating schedule: ${e.message}")
            }
        }
    }

    /**
     * Fetches all schedules for the current user.
     */
    fun getSchedules() {
        currentUser?.let { user ->
            viewModelScope.launch {
                try {
                    val retrievedSessions = UserRepository.getSchedules(user.uid)
                    _schedules.value = retrievedSessions
                } catch (e: Exception) {
                    // Handle error:
                    Log.e(TAG, "Error fetching schedules: ${e.message}")
                }
            }
        }
    }

    /**
     * Retrieves schedule details by ID.
     */
    fun getScheduleDetails(scheduleId: String): LiveData<Schedule?> {
        viewModelScope.launch {
            try {
                val schedule = UserRepository.getScheduleDetails(currentUser!!.uid, scheduleId)
                _schedule.value = schedule!!
            } catch (e: Exception) {
                // Handle error:
                Log.e(TAG, "Error fetching schedule details: ${e.message}")
            }
        }
        return _schedule
    }

    /**
     * Deletes a schedule.
     */
    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            try {
                userRepository.deleteSchedule(currentUser!!.uid, scheduleId)
            } catch (e: Exception) {
                // Handle error:
                Log.e(TAG, "Error deleting schedule: ${e.message}")
            }
        }
    }

    /**
     * Deletes schedules containing a specific day.
     */
    fun deleteSchedulesContainingDay(day: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userRepository.deleteSchedulesContainingDay(userId = currentUser!!.uid, day = day)
            } catch (e: Exception) {
                // Handle error:
                Log.e(TAG, "Error deleting schedules containing $day: ${e.message}")
            }
        }
    }


    /** ---------------------------------- EXPLORE FUNCTIONS ---------------------------------- **/

    /** INITIALISING VARIABLES **/
    //Initialises variables to hold user data and their follows lists.
    private val _userFollows = MutableStateFlow<Follows?>(null)
    val userFollows: StateFlow<Follows?> = _userFollows

    private val _userByUsername = MutableLiveData<User?>()
    val userByUsername: LiveData<User?> = _userByUsername

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    fun getUserByUsername(username: String) {
        viewModelScope.launch {
            try {
                val (userId, user) = userRepository.getUserIdAndUserByUsername(username)
                // Use userId and user here
                // For example:
                if (user != null) {
                    _userByUsername.value = user
                    // Use userId for other purposes if needed
                } else {
                    // Handle case where user is not found
                }
            } catch (e: Exception) {
                // Handle error
                Log.e(TAG, "Error fetching user by username: ${e.message}")
            }
        }
    }

    /**
     * Function to handle to get the user by their username
     */
    fun fetchUserIdByUsername(username: String) {
        viewModelScope.launch {
            try {
                val userId = getUserIdByUsername(username)
                _userId.value = userId
            } catch (e: Exception) {
                // Handle error
                Log.e(TAG, "Error fetching user ID by username: ${e.message}")
            }
        }
    }


    /**
     * Function to handle following a user.
     */
    fun followUser(userIdToFollow: String) {
        val currentUserId = currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                userRepository.followUser(currentUserId, userIdToFollow)
            } catch (e: Exception) {
                // Handle error
                Log.e(TAG, "Error following user: ${e.message}")
            }
        }
    }

    /**
     * Function to handle unfollowing a user.
     */
    fun unfollowUser(userIdToUnfollow: String) {
        val currentUserId = currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                userRepository.unfollowUser(currentUserId, userIdToUnfollow)
            } catch (e: Exception) {
                // Handle error
                Log.e(TAG, "Error unfollowing user: ${e.message}")
            }
        }
    }

    /**
     * Function to fetch the user and their followers/following lists.
     */
    fun getUserFollows() {
        val currentUserId = currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val userFollows = userRepository.getUserFollows(currentUserId)
                // Log what is being returned
                Log.d(TAG, "User follows: $userFollows")
                _userFollows.value = userFollows
            } catch (e: Exception) {
                // Handle error
                Log.e(TAG, "Error fetching user follows: ${e.message}")
            }
        }
    }

    /**
     * Function to get all users.
     */
    fun getAllUsers() {
        viewModelScope.launch {
            try {
                userRepository.getAllUsers().collect { users ->
                    _users.value = users
                }
            } catch (e: Exception) {
                // Handle error
                Log.e(TAG, "Error getting all users: ${e.message}")
            }
        }
    }
}