package com.flamyoad.basicpaginationconcatadapter


sealed interface MainViewModelEvent {
    object InitialLoad: MainViewModelEvent
    object LoadNext : MainViewModelEvent
    object Refresh : MainViewModelEvent
}