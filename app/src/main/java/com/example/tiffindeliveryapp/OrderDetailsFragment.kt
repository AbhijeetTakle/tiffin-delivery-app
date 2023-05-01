package com.example.tiffindeliveryapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.example.tiffindeliveryapp.datamodels.NewOrder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OrderDetailsFragment : Fragment() {
    private lateinit var cancelOrder: Button
    private lateinit var orderId:String
    private lateinit var review:EditText
    private lateinit var addReview:Button
    private lateinit var ratingBar:RatingBar
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
        setActions()
        return view
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
    }
}
