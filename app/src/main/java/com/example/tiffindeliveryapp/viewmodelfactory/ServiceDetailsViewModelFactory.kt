package com.example.tiffindeliveryapp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tiffindeliveryapp.viewmodels.ServiceDetailsViewModel

class ServiceDetailsViewModelFactory(val id:String):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ServiceDetailsViewModel(id) as T
    }
}