package com.example.tiffindeliveryapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.tiffindeliveryapp.datamodels.Order
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit


class PaymentFragment : Fragment() {
    private lateinit var paymentChoices:RadioGroup
    private lateinit var placeOrder:Button
    private lateinit var order:Order
    private lateinit var totalPrice:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mPrefs = activity?.getSharedPreferences("order", Context.MODE_PRIVATE)
        val sOrder = mPrefs?.getString("currentOrder", "")
        val gson = Gson()
        order = gson.fromJson(sOrder, Order::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)
        init(view)
        setActions()
        setCheckoutPrice()
        return view
    }

    private fun setActions() {
        placeOrder.setOnClickListener {
            val db = Firebase.firestore
            val paymentMethod = when(paymentChoices.checkedRadioButtonId){
                R.id.payment_method_cash_on_delivery -> "COD"
                else -> "COD"
            }
            order.paymentMethod = paymentMethod
            db.collection("orders")
                .add(order).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(context, "order placed", Toast.LENGTH_SHORT).show()
                        val bundle = bundleOf("orderStatus" to true)
                        findNavController().navigate(R.id.action_paymentFragment_to_orderPlacedFragment, bundle)
                    }else{
                        Toast.makeText(context, "order was not placed", Toast.LENGTH_SHORT).show()
                        val bundle = bundleOf("orderStatus" to false)
                        findNavController().navigate(R.id.action_paymentFragment_to_orderPlacedFragment, bundle)
                    }
                }
        }
    }


    private fun setCheckoutPrice(){
        val diff = order.endDate.time - order.startDate.time
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        totalPrice.text = "${totalPrice.text}${40*(days+1)}"
    }

    private fun init(view: View) {
        paymentChoices = view.findViewById(R.id.payment_methods)
        placeOrder = view.findViewById(R.id.place_order)
        totalPrice = view.findViewById(R.id.total_price)
    }


}