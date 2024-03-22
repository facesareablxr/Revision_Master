package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository.FlashcardRepository
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Module
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Subject
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.UserRevisionData

class FlashcardViewModel : ViewModel() {

    fun getUserRevisionData(callback: (UserRevisionData) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getUserRevisionData(callback)
        }
    }

    fun addUserRevisionData(userRevisionData: UserRevisionData, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.addUserRevisionData(userRevisionData, callback)
        }
    }

    fun addSubjectFlashcard(subjectId: String, flashcard: Flashcard, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.addSubjectFlashcard(subjectId, flashcard, callback)
        }
    }

    fun getModuleInformation(moduleId: String, callback: (Module?) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getModuleInformation(moduleId, callback)
        }
    }

    fun getSubjectInformation(subjectId: String, callback: (Subject?) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getSubjectInformation(subjectId, callback)
        }
    }

    fun getFlashcardInformation(
        parentId: String,
        flashcardId: String,
        callback: (Flashcard?) -> Unit
    ) {
        viewModelScope.launch {
            FlashcardRepository.getFlashcardInformation(parentId, flashcardId, callback)
        }
    }

    fun updateModuleInformation(
        moduleId: String,
        updatedModule: Module,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            FlashcardRepository.updateModuleInformation(moduleId, updatedModule, callback)
        }
    }

    fun updateSubjectInformation(
        subjectId: String,
        updatedSubject: Subject,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            FlashcardRepository.updateSubjectInformation(subjectId, updatedSubject, callback)
        }
    }

    fun updateFlashcardInformation(
        parentId: String,
        flashcardId: String,
        updatedFlashcard: Flashcard,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            FlashcardRepository.updateFlashcardInformation(
                parentId,
                flashcardId,
                updatedFlashcard,
                callback
            )
        }
    }

    fun deleteModule(moduleId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.deleteModule(moduleId, callback)
        }
    }

    fun deleteSubject(subjectId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.deleteSubject(subjectId, callback)
        }
    }

    fun deleteClass(classId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.deleteClass(classId, callback)
        }
    }

    fun deleteFlashcard(parentId: String, flashcardId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.deleteFlashcard(parentId, flashcardId, callback)
        }
    }

    fun getFlashcardsByModule(moduleId: String, callback: (List<Flashcard>) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getFlashcardsByModule(moduleId, callback)
        }
    }

    fun getFlashcardsByClass(classId: String, callback: (List<Flashcard>) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getFlashcardsByClass(classId, callback)
        }
    }

    fun getFlashcardsBySubject(subjectId: String, callback: (List<Flashcard>) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getFlashcardsBySubject(subjectId, callback)
        }
    }

    suspend fun getAllUserFlashcards(username: String): List<Flashcard> {
        return FlashcardRepository.getAllUserFlashcards(username)
    }

    fun getUserModules(userId: String, callback: (List<Module>) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getUserModules(userId, callback)
        }
    }

    fun getUserSubjects(userId: String, callback: (List<Subject>) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getUserSubjects(userId, callback)
        }
    }

    fun getModulesForSubject(subjectId: String, callback: (List<Module>) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getModulesForSubject(subjectId, callback)
        }
    }

    fun addDeck(
        name: String,
        subject: String,
        isPublic: Boolean,
        description: String,
        owner: String
    ) {
        val deck = Deck(
            name = name,
            subject = subject,
            isPublic = isPublic,
            description = description,
            owner = owner
        )
        viewModelScope.launch {
            FlashcardRepository.addDeck(deck)
        }
    }

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> get() = _decks

    fun getUserDecks(username: String) {
        viewModelScope.launch {
            val decks = FlashcardRepository.getUserDecks(username)
            _decks.value = decks
        }
    }

}