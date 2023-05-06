package com.example.tiffindeliveryapp.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tiffindeliveryapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrderPlacedFragment : Fragment() {
    private var orderStatus:Boolean? = false
    private lateinit var orderPlacedStatus:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderStatus = arguments?.getBoolean("orderStatus", false)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_placed, container, false)
        init(view)
        if (orderStatus ==  true){
            orderPlacedStatus.text = "Order Placed"
        }else{
            orderPlacedStatus.text = "Order Not Placed"
        }
        gotoList()
        return view
    }

    private fun init(view: View) {
        orderPlacedStatus = view.findViewById(R.id.order_status)
    }

    private fun gotoList(){
        lifecycleScope.launch(Dispatchers.Main){
            delay(2000)
            findNavController().navigate(R.id.action_orderPlacedFragment_to_tiffinServicesListFragment)
        }
    }
}