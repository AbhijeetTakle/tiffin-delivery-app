package com.example.tiffindeliveryapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date
import java.util.concurrent.TimeUnit

class AdminOrderDetailsFragment : Fragment() {
    private lateinit var orderId:String
    private lateinit var username:TextView
    private lateinit var addressLine1:TextView
    private lateinit var addressLine2:TextView
    private lateinit var district:TextView
    private lateinit var state:TextView
    private lateinit var pincode:TextView
    private lateinit var emailOrPhone:TextView
    private lateinit var tiffinType:TextView
    private lateinit var tiffinPrice:TextView
    private lateinit var gst:TextView
    private lateinit var totalPrice:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString("orderId")?.let { id ->
                orderId = id
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_order_details, container, false)
        init(view)
        setData()
        return view
    }

    private fun setData() {
        val db = Firebase.firestore
        db.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { ordr ->
                db.collection("users")
                    .document(ordr.get("userId").toString())
                    .get()
                    .addOnSuccessListener { usr ->
                        username.text = usr.get("username").toString()
                        val address = ordr.get("deliveryAddress") as HashMap<String, String>
                        addressLine1.text = address.get("Adress line 1")
                        addressLine2.text = address.get("Adress line 2")
                        pincode.text = address.get("Pincode")
                        state.text = address.get("State")
                        district.text = address.get("district")
                        emailOrPhone.text = (usr.get("phoneNumber")?:usr.get("email")).toString()
                        val tiffinTypes= ordr.get("tiffinChoice").toString()
                        db.collection("TiffinServices")
                            .document(ordr.get("tiffinServiceId").toString())
                            .get()
                            .addOnSuccessListener { servs ->
                                var price:Int
                                if(tiffinTypes.startsWith("Large",true)){
                                    price = (servs.get("tiffinTypes") as HashMap<String, String>).get("Large Tiffin").toString().toInt()
                                }else{
                                    price = (servs.get("tiffinTypes") as HashMap<String, String>).get("Small Tiffin").toString().toInt()
                                }
                                tiffinPrice.text = price.toString()
                                gst.text = "${0.0}"
                                val diff = (((ordr.get("endDate")as Timestamp).toDate()).time - ((ordr.get("startDate") as Timestamp).toDate()).time)
                                val days = TimeUnit.MILLISECONDS.toDays(diff)
                                totalPrice.text = (days* price!!).toString()
                            }
                    }
            }
    }

    private fun init(view:View) {
        username = view.findViewById(R.id.username)
        addressLine1 = view.findViewById(R.id.address_line1)
        addressLine2 = view.findViewById(R.id.address_line2)
        district = view.findViewById(R.id.address_district)
        state = view.findViewById(R.id.address_state)
        pincode = view.findViewById(R.id.address_pincode)
        emailOrPhone = view.findViewById(R.id.email_or_phone)
        tiffinType = view.findViewById(R.id.tiffin_type)
        tiffinPrice = view.findViewById(R.id.tiffin_price)
        gst = view.findViewById(R.id.gst)
        totalPrice = view.findViewById(R.id.total_price)
    }
}