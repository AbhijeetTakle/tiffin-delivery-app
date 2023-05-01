package com.example.tiffindeliveryapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiffindeliveryapp.adapters.OrdersListAdapter
import com.example.tiffindeliveryapp.adapters.TiffinServiceListAdapter
import com.example.tiffindeliveryapp.datamodels.NewOrder
import com.example.tiffindeliveryapp.datamodels.Order
import com.example.tiffindeliveryapp.datamodels.TiffinService
import com.example.tiffindeliveryapp.datamodels.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OrdersListFragment : Fragment() {
    private lateinit var ordersList:RecyclerView
    private lateinit var ordersListAdapter: OrdersListAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progrss:ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders_list2, container, false)
        progrss = view.findViewById(R.id.progress_orders)
        mAuth = FirebaseAuth.getInstance()
        ordersListAdapter = OrdersListAdapter(ArrayList())
        ordersList = view.findViewById(R.id.orders_list_user)
        ordersList.adapter = ordersListAdapter
        ordersList.layoutManager = LinearLayoutManager(context)
        setContactClickAction()
        setData()
        return view
    }

    private fun setData() {
        val db = Firebase.firestore
        val orders = ArrayList<NewOrder>()
        var orderNums = 0
        db.collection("orders")
            .whereEqualTo("userId", "${mAuth.currentUser?.uid}")
            .get()
            .addOnSuccessListener { docs ->
                if(docs.size() == orderNums){
                    progrss.isGone = true
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
                                                ordersListAdapter.onNewOrderAdd(it, orders.size)
                                            }
                                        }
                                        if(docs.size() == orderNums++){
                                            progrss.isGone = true
                                        }
                                    }
                                }
                        }
                }
            }
    }

    private fun setContactClickAction() {
        ordersListAdapter.setOnItemClickListener(object : OrdersListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = bundleOf("orderID" to ordersListAdapter.ordersList.get(position).orderId)
                findNavController().navigate(
                    R.id.action_ordersListFragment2_to_orderDetailsFragment,
                    bundle
                )
            }
        })
    }
}