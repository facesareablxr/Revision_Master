package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository.FlashcardRepository
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository.UserRepository

class ExploreViewModel() : ViewModel() {
    private val userRepository = UserRepository
    private val flashcardRepository = FlashcardRepository
    private val _searchResults = MutableStateFlow<List<Any>>(emptyList())
    val searchResults: StateFlow<List<Any>> get() = _searchResults

    fun search(query: String) {
        viewModelScope.launch {
            val users = userRepository.searchUsers(query)
            val decks = flashcardRepository.searchPublicDecks(query)
            val combinedResults = mutableListOf<Any>()
            combinedResults.addAll(users)
            combinedResults.addAll(decks)
            _searchResults.value = combinedResults
        }
    }
}