package com.example.tiffindeliveryapp.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiffindeliveryapp.R
import com.example.tiffindeliveryapp.adapters.AdminOrdersAdapter
import com.example.tiffindeliveryapp.adapters.OrdersListAdapter
import com.example.tiffindeliveryapp.datamodels.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrdersListFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var registerTiffinService:Button
    private lateinit var ordersList:RecyclerView
    private lateinit var adminOrdersListAdapter: AdminOrdersAdapter
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

        adminOrdersListAdapter.setOnItemClickListener(object : AdminOrdersAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = bundleOf("orderId" to adminOrdersListAdapter.ordersList.get(position).orderId)
                Log.d("TAG", "onItemClick: ${adminOrdersListAdapter.ordersList.get(position)}")
                findNavController().navigate(
                    R.id.action_ordersListFragment_to_adminOrderDetailsFragment,
                    bundle
                )
            }
        })
    }

    private fun checkForTiffinService() {
        val db = Firebase.firestore
        db.collection("admins")
            .whereEqualTo("aid", "${Firebase.auth.currentUser?.uid}")
            .get()
            .addOnSuccessListener {
                val service = it.documents.get(0).toObject(Admin::class.java)?.tiffinServiceId
                if (service!=null){
                    getOrders(service as String)
                }else{
                    progressBar.isGone = true
                    registerTiffinService.isGone = false
                }
            }
    }

    private fun getOrders(service:String) {
        val db = Firebase.firestore
        val orders = ArrayList<NewOrder>()
        var orderNums = 0
        db.collection("orders")
            .whereEqualTo("tiffinServiceId", service)
            .get()
            .addOnSuccessListener {docs ->

                if(docs.size() == orderNums){
                    progressBar.isGone = true
                }
                for(doc in docs.documents){
                    val order = doc.toObject(NewOrder::class.java)
                    order?.orderId = doc.id
                    db.collection("TiffinServices")
                        .get()
                        .addOnSuccessListener { servs ->
                            for (serv in servs.documents){
                                if(serv.id == order?.tiffinServiceId){
                                    serv.toObject(TiffinService::class.java)?.let {
                                        order.serviceName = it.title
                                    }
                                }
                            }
                            db.collection("users")
                                .get()
                                .addOnSuccessListener { usrs ->
                                    for (usr in usrs.documents){
                                        val user = usr.toObject(User::class.java)
                                        if(user?.uid==order?.userId){
                                            order?.userName = user?.username.toString()
                                            order?.let {
                                                orders.add(it)
                                                adminOrdersListAdapter.onNewOrderAdd(it, orders.size)
                                            }
                                        }
                                        if(docs.size() == orderNums++){
                                            ordersList.isGone = false
                                            progressBar.isGone = true
                                        }
                                    }
                                }
                        }
                }
            }
    }

    private fun init(view: View) {
        progressBar = view.findViewById(R.id.progress_orders_list_load)
        registerTiffinService = view.findViewById(R.id.register_tiffin_service)
        ordersList = view.findViewById(R.id.orders_list)
        adminOrdersListAdapter = AdminOrdersAdapter(ArrayList())
        ordersList.adapter = adminOrdersListAdapter
        ordersList.layoutManager = LinearLayoutManager(context)
    }
}