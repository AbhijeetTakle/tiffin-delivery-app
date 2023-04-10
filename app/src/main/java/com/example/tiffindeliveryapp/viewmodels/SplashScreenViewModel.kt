package com.example.tiffindeliveryapp.viewmodels

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenViewModel:ViewModel() {
    val isLoading = MutableLiveData<Boolean>()

    init {
        isLoading.value = true

        viewModelScope.launch {
            delay(5000)
            isLoading.value = false
        }
    }
}