package com.example.tiffindeliveryapp.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.example.tiffindeliveryapp.R
import com.example.tiffindeliveryapp.datamodels.NewTiffinService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TiffinServiceRegisterationFragment : Fragment() {
    private lateinit var tiffinServiceName:EditText
    private lateinit var tiffinServiceDescription:EditText
    private lateinit var tiffinServiceLargePrice:EditText
    private lateinit var tiffinServiceSmallPrice:EditText
    private lateinit var tiffinServiceToadyMenu:EditText
    private lateinit var tiffinServiceAddressLine1:EditText
    private lateinit var tiffinServiceAddressLine2:EditText
    private lateinit var tiffinServiceDistrict:EditText
    private lateinit var tiffinServiceState:EditText
    private lateinit var tiffinServicePincode:EditText
    private lateinit var vegFoodPref:RadioButton
    private lateinit var nonVegFoodPref:RadioButton
    private lateinit var submitRegistration:Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tiffin_service_registeration, container, false)
        init(view)
        setActions()
        return view
    }

    private fun setActions() {
        submitRegistration.setOnClickListener {
            if(checkForEmptyFields()){
                val tiffinName = tiffinServiceName.text.toString()
                val tiffinDes = tiffinServiceDescription.text.toString()
                val veg = vegFoodPref.isChecked
                val nonVeg = nonVegFoodPref.isChecked
                val menu = tiffinServiceToadyMenu.text.toString()
                val tiffinTypes = mapOf("Large Tiffin" to tiffinServiceLargePrice.text.toString(), "Small Tiffin" to tiffinServiceSmallPrice.text.toString())
                val addressLine1 = tiffinServiceAddressLine1.text.toString()
                val addressLine2 = tiffinServiceAddressLine2.text.toString()
                val addressDistrict = tiffinServiceDistrict.text.toString()
                val addressState = tiffinServiceState.text.toString()
                val addressPincode = tiffinServicePincode.text.toString()
                val address = mapOf("Address Line1" to addressLine1, "Address Line2" to addressLine2, "District" to addressDistrict, "State" to addressState, "Pincode" to addressPincode)
                val service = NewTiffinService(tiffinName, tiffinDes, veg, nonVeg, 0, 0.0, emptyMap(), tiffinTypes, menu, address, false)
                val db = Firebase.firestore
                db.collection("TiffinServices")
                    .add(service)
                    .addOnCompleteListener {
                        setTiffinServiceForAdmin(it.result.id)
                        if (it.isSuccessful){
                            Toast.makeText(context, "Request is received", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, "Request is not received", Toast.LENGTH_SHORT).show()
                        }
                        activity?.onBackPressed()
                    }
            }
        }
    }

    private fun setTiffinServiceForAdmin(id:String) {
        val db = Firebase.firestore
        db.collection("admins")
            .whereEqualTo("aid", Firebase.auth.currentUser?.uid)
            .get()
            .addOnSuccessListener {
                db.collection("admins")
                    .document(it.documents.get(0).id)
                    .update("tiffinServiceId", id)
            }
    }

    private fun checkForEmptyFields():Boolean {
        if (tiffinServiceName.text.isEmpty()){
            tiffinServiceName.error = "Please Fill Name of the Service"
            return false
        }
        if (tiffinServiceDescription.text.isEmpty()){
            tiffinServiceDescription.error = "Please Fill Description of the Service"
            return false
        }
        if (tiffinServiceLargePrice.text.isEmpty()){
            tiffinServiceLargePrice.error = "Please Fill Price for large meal of the Service"
            return false
        }
        if (tiffinServiceSmallPrice.text.isEmpty()){
            tiffinServiceSmallPrice.error = "Please Fill Price for small meal of the Service"
            return false
        }
        if (tiffinServiceToadyMenu.text.isEmpty()){
            tiffinServiceToadyMenu.error = "Please Fill Today's Menu of the Service"
            return false
        }
        if (tiffinServiceAddressLine1.text.isEmpty()){
            tiffinServiceAddressLine1.error = "Please Fill Address Line 1 of the Service"
            return false
        }
        if (tiffinServiceAddressLine2.text.isEmpty()){
            tiffinServiceAddressLine2.error = "Please Fill Address Line 2 of the Service"
            return false
        }
        if (tiffinServiceDistrict.text.isEmpty()){
            tiffinServiceDistrict.error = "Please Fill District of the Service"
            return false
        }
        if (tiffinServiceState.text.isEmpty()){
            tiffinServiceState.error = "Please Fill State of the Service"
            return false
        }
        if (tiffinServicePincode.text.isEmpty()){
            tiffinServicePincode.error = "Please Fill Pincode of the Service"
            return false
        }
        return true
    }

    private fun init(view: View) {
        tiffinServiceName = view.findViewById(R.id.tiffin_service_name)
        tiffinServiceDescription = view.findViewById(R.id.tiffin_service_description)
        tiffinServiceLargePrice = view.findViewById(R.id.price_large)
        tiffinServiceSmallPrice = view.findViewById(R.id.price_small)
        tiffinServiceToadyMenu = view.findViewById(R.id.menu_today)
        tiffinServiceAddressLine1 = view.findViewById(R.id.address_line1)
        tiffinServiceAddressLine2 = view.findViewById(R.id.address_line2)
        tiffinServiceDistrict = view.findViewById(R.id.address_district)
        tiffinServiceState = view.findViewById(R.id.address_state)
        tiffinServicePincode = view.findViewById(R.id.address_pincode)
        vegFoodPref = view.findViewById(R.id.veg_food_pref)
        nonVegFoodPref = view.findViewById(R.id.nonveg_food_pref)
        submitRegistration = view.findViewById(R.id.submit_registration_details)
    }
}