package com.flamyoad.basicpaginationconcatadapter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.basicpaginationconcatadapter.common.State
import com.flamyoad.basicpaginationconcatadapter.model.NumberPojo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val numberRepository = NumberRepository()

    private val currentState: MutableStateFlow<MainViewModelState> = MutableStateFlow(MainViewModelState.Initial)
    fun currentState() = currentState.asStateFlow()

    init {
        onEvent(MainViewModelEvent.InitialLoad)
    }

    fun onEvent(event: MainViewModelEvent) {
        viewModelScope.launch {
            when (event) {
                is MainViewModelEvent.InitialLoad -> {
                    load(1)
                }
                is MainViewModelEvent.LoadNext -> {
                    loadNext()
                }
                is MainViewModelEvent.Refresh -> {
                    currentState.emit(MainViewModelState.Initial)
                    load(1)
                }
            }
        }
    }

    private suspend fun loadNext() {
        if (currentState.value is MainViewModelState.Loading) {
            return
        }

        val currentList = getCurrentList()
        val seed = currentList.lastOrNull()?.seed?.plus(1) ?: 1
        currentState.emit(MainViewModelState.Loading(currentList))
        load(seed, currentList)
    }

    private suspend fun load(seed: Int, currentList: List<NumberPojo> = emptyList()) {
        when (val res = numberRepository.getNumbers(seed)) {
            is State.Success -> {
                currentState.emit(MainViewModelState.Progress(currentList + res.value))
            }
            is State.Error -> {
                currentState.emit(MainViewModelState.Error(currentList, res.exception))
            }
        }
    }

    private fun getCurrentList(): List<NumberPojo> {
        return when (val currentState = currentState.value) {
            is MainViewModelState.Initial -> emptyList()
            is MainViewModelState.Error -> currentState.items
            is MainViewModelState.Loading -> currentState.items
            is MainViewModelState.Progress -> currentState.items
        }
    }
}