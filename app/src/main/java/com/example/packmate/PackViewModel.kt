package com.example.packmate

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class PackViewModel : ViewModel() {
    val tripType = mutableStateOf("")
    val itemList = mutableStateListOf<String>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    fun generatePackingList() {
        if (tripType.value.isBlank()) return

        isLoading.value = true
        errorMessage.value = ""
        itemList.clear()

        viewModelScope.launch {
            try {
                val prompt = "Generate a detailed packing list for a ${tripType.value} trip."
                val response = FirebaseAiHelper.model.generateContent(prompt)
                val output = response.text ?: "No items found."

                itemList.addAll(
                    output.split("\n")
                        .map { it.trimStart('-', '*', '#', ' ', '\t') }
                        .filter { it.isNotBlank() }
                )
            } catch (e: Exception) {
                errorMessage.value = e.localizedMessage ?: "Unknown error"
            } finally {
                isLoading.value = false
            }
        }
    }
}
