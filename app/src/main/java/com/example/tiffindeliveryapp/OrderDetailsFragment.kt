package com.example.tiffindeliveryapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import com.example.tiffindeliveryapp.datamodels.NewOrder
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OrderDetailsFragment : Fragment() {
    private lateinit var cancelOrder: Button
    private lateinit var orderId:String
    private lateinit var review:EditText
    private lateinit var addReview:Button
    private lateinit var ratingBar:RatingBar
    private lateinit var username: TextView
    private lateinit var addressLine1: TextView
    private lateinit var addressLine2: TextView
    private lateinit var district: TextView
    private lateinit var state: TextView
    private lateinit var pincode: TextView
    private lateinit var emailOrPhone: TextView
    private lateinit var tiffinType: TextView
    private lateinit var tiffinPrice: TextView
    private lateinit var gst: TextView
    private lateinit var totalPrice: TextView
    private lateinit var serviceName: TextView
    private lateinit var progress:ProgressBar
    private lateinit var container: ScrollView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { order ->
            order.getString("orderID")?.let {
                orderId = it
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_details, container, false)
        init(view)
        setOrder()
        setActions()
        return view
    }

    private fun setOrder() {
        val db = Firebase.firestore
        db.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { ordr ->
                db.collection("users")
                    .whereEqualTo("uid", ordr.get("userId"))
                    .get()
                    .addOnSuccessListener { usrs ->
                        val usr = usrs.documents[0]
                        username.text = usr.get("username").toString()
                        val address = ordr.get("deliveryAddress") as HashMap<String, String>
                        addressLine1.text = address.get("Adress line 1")
                        addressLine2.text = address.get("Adress line 2")
                        pincode.text = address.get("Pincode")
                        state.text = address.get("State")
                        district.text = address.get("district")
                        ordr.get("canceled")?.let {cancld ->
                            ordr.get("orderCompleted")?.let{compltd ->
                                if((compltd as Boolean) || cancld as Boolean){
                                    cancelOrder.isGone = true
                                }

                            }

                        }
                        emailOrPhone.text = (usr.get("phoneNumber")?:usr.get("email")).toString()
                        val tiffinTypes= ordr.get("tiffinChoice").toString()
                        db.collection("TiffinServices")
                            .document(ordr.get("tiffinServiceId").toString())
                            .get()
                            .addOnSuccessListener { servs ->
                                var price:Int
                                if(tiffinTypes.startsWith("Large",true)){
                                    price = (servs.get("tiffinTypes") as HashMap<String, String>).get("Large Tiffin").toString().toInt()
                                    tiffinType.text = "Large Tiffin"
                                }else{
                                    price = (servs.get("tiffinTypes") as HashMap<String, String>).get("Small Tiffin").toString().toInt()
                                    tiffinType.text = "Small Tiffin"

                                }
                                tiffinPrice.text = price.toString()
                                gst.text = "${0.0}"
                                serviceName.text = servs.get("title").toString()
                                val diff = (((ordr.get("endDate")as Timestamp).toDate()).time - ((ordr.get("startDate") as Timestamp).toDate()).time)
                                val days = TimeUnit.MILLISECONDS.toDays(diff)+1
                                totalPrice.text = "${days}x${price}: ${days* price}"
                                progress.isGone = true
                                container.isGone = false

                            }
                    }
            }
    }

    private fun setActions() {
        cancelOrder.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Do you want to cancel the order")
                .setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                    val db = Firebase.firestore
                    db.collection("orders")
                        .document(orderId)
                        .update("canceled", true)
                        .addOnSuccessListener {
                            activity?.onBackPressed()
                        }
                }.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->

                }.create()
                .show()
        }
        addReview.setOnClickListener {
            if(review.text.isEmpty() || ratingBar.rating==0.0f){
                review.error = "Review cannot be empty"
                ratingBar
            }else{
                val db = Firebase.firestore
                db.collection("orders")
                    .document(orderId)
                    .get()
                    .addOnSuccessListener {doc->
                        db.collection("TiffinServices")
                            .document(doc.get("tiffinServiceId").toString())
                            .get()
                            .addOnSuccessListener { servs->
                                val reviews = servs.get("customerReviews") as HashMap<String,String>
                                var rating = servs.get("rating") as Double
                                db.collection("users")
                                    .whereEqualTo("uid", doc.get("userId").toString())
                                    .get()
                                    .addOnSuccessListener {usr->
                                        val usrname = usr.documents.get(0).get("username").toString()
                                        reviews.put(usrname, review.text.toString())
                                        rating = (rating+ratingBar.rating.toDouble())/reviews.size
                                        db.collection("TiffinServices")
                                            .document(doc.get("tiffinServiceId").toString())
                                            .update("rating", rating, "customerReviews", reviews)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Review added", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Review not added", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                            }
                    }
            }
        }
    }

    private fun init(view:View) {
        cancelOrder = view.findViewById(R.id.order_cancel)
        addReview = view.findViewById(R.id.add_review)
        review = view.findViewById(R.id.review)
        ratingBar = view.findViewById(R.id.rating)
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
        serviceName = view.findViewById(R.id.mess_name)
        progress = view.findViewById(R.id.progress)
        container = view.findViewById(R.id.container)
    }
}
