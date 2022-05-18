package com.flamyoad.basicpaginationconcatadapter

import com.flamyoad.basicpaginationconcatadapter.model.NumberPojo
import java.lang.Exception


sealed interface MainViewModelState {
    object Initial : MainViewModelState
    data class Loading(val items: List<NumberPojo>) : MainViewModelState
    data class Progress(val items: List<NumberPojo>) : MainViewModelState
    data class Error(val items: List<NumberPojo> = emptyList(), val exception: Exception? = null) : MainViewModelState
}
