package com.example.tiffindeliveryapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiffindeliveryapp.datamodels.TiffinService
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class TiffinServicesListViewModel:ViewModel() {
    var servicesList:MutableLiveData<ArrayList<TiffinService>> = MutableLiveData(ArrayList())
    init {
        getServicesData()
    }

    private fun getServicesData() = runBlocking{
        val list = ArrayList<TiffinService>()
        viewModelScope.async(Dispatchers.Default) {
            val db = Firebase.firestore
            db.collection("TiffinServices")
                .whereEqualTo("verified", true)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val service = document.toObject(TiffinService::class.java)
                        service.id = document.id
                        list.add(service)
                    }
                    viewModelScope.async {
                        withContext(Dispatchers.Main){
                            servicesList.value = list
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }
        }
    }
}