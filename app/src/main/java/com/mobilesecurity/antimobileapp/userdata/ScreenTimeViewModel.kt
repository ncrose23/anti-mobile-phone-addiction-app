package com.mobilesecurity.antimobileapp.userdata

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class ScreenTimeViewModel: ViewModel() {
    public var screenTime by mutableLongStateOf(0L)
}