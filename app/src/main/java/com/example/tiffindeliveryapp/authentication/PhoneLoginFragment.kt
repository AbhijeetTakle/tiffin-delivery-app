package com.example.tiffindeliveryapp.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.tiffindeliveryapp.AuthenticationActivity
import com.example.tiffindeliveryapp.R
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PhoneLoginFragment : Fragment() {
    private lateinit var phoneNumber: EditText
    private lateinit var sendOTP: Button
    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_phone_login, container, false)
        phoneNumber = view.findViewById(R.id.phone_number)
        sendOTP = view.findViewById(R.id.send_otp)
        mAuth = FirebaseAuth.getInstance()
        Firebase.auth.signOut()
        sendOTP.setOnClickListener{
            val phone = "+91"+phoneNumber.text.toString().trim()
            if(phone.isNotEmpty()){
                val owner = context as AuthenticationActivity
                owner.sendOTPForVerification(phone)
            }
        }
        return view
    }





    fun checkIfUserExists(){
        val phone = "+91"+phoneNumber.text.toString().trim()
        val db = Firebase.firestore
        db.collection("Users")
            .whereEqualTo("phoneNumber",phone)
    }
}