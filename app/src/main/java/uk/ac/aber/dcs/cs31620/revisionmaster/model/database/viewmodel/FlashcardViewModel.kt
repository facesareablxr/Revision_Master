package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository.FlashcardRepository
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Module
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Subject
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.UserClasses
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

    fun addClassFlashcard(classId: String, flashcard: Flashcard, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.addClassFlashcard(classId, flashcard, callback)
        }
    }

    fun addModuleFlashcard(moduleId: String, flashcard: Flashcard, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.addModuleFlashcard(moduleId, flashcard, callback)
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

    fun getClassInformation(classId: String, callback: (UserClasses?) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getClassInformation(classId, callback)
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

    fun updateClassInformation(
        classId: String,
        updatedClass: UserClasses,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            FlashcardRepository.updateClassInformation(classId, updatedClass, callback)
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

    fun getUserClasses(userId: String, callback: (List<UserClasses>) -> Unit) {
        viewModelScope.launch {
            FlashcardRepository.getUserClasses(userId, callback)
        }
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
}