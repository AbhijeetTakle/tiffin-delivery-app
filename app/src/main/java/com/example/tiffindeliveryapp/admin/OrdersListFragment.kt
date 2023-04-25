package com.example.tiffindeliveryapp.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tiffindeliveryapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrdersListFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var registerTiffinService:Button
    private lateinit var ordersList:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_orders_list, container, false)
        init(view)
        setActions()
        checkForTiffinService()
        return view
    }

    private fun setActions() {
        registerTiffinService.setOnClickListener {
            findNavController().navigate(R.id.action_ordersListFragment_to_tiffinServiceRegisterationFragment)
        }
    }

    private fun checkForTiffinService() {
        val db = Firebase.firestore
        db.collection("admins")
            .whereEqualTo("aid", "${Firebase.auth.currentUser?.uid}")
            .get()
            .addOnSuccessListener {
                val service = it.documents.get(0).get("tiffinServiceId")
                Log.d("TAG", "checkForTiffinService: ${service}")
                if (service!=null){
                    getOrders()
                }else{
                    progressBar.isGone = true
                    registerTiffinService.isGone = false
                }
            }
    }

    private fun getOrders() {

    }

    private fun init(view: View) {
        progressBar = view.findViewById(R.id.progress_orders_list_load)
        registerTiffinService = view.findViewById(R.id.register_tiffin_service)
        ordersList = view.findViewById(R.id.orders_list)
    }
}