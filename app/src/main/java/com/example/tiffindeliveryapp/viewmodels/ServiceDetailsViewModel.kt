package com.example.tiffindeliveryapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiffindeliveryapp.datamodels.TiffinService
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ServiceDetailsViewModel(val id:String):ViewModel() {

    var servicesList: MutableLiveData<ArrayList<TiffinService>> = MutableLiveData(ArrayList())
    init {
        Log.d("TAG", "id: ${id}")
        getServicesData()
    }

    private fun getServicesData() = runBlocking{
        val list = ArrayList<TiffinService>()
        viewModelScope.async(Dispatchers.Default) {
            val db = Firebase.firestore
            db.collection("TiffinServices")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if(id == document.id){
                            val service = document.toObject(TiffinService::class.java)
                            Log.d("TAG", "getServicesData: ${service}")
                            list.add(service)
                        }
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