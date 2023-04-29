package com.example.tiffindeliveryapp.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.tiffindeliveryapp.AuthenticationActivity
import com.example.tiffindeliveryapp.MainActivity
import com.example.tiffindeliveryapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class PhoneLoginFragment : Fragment() {
    private lateinit var phoneNumber: EditText
    private lateinit var sendOTP: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var number: String
    private lateinit var mProgressBar : ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_phone_login, container, false)
        phoneNumber = view.findViewById(R.id.phone_number)
        sendOTP = view.findViewById(R.id.send_otp)
        auth = FirebaseAuth.getInstance()
        mProgressBar = view.findViewById(R.id.phoneProgressBar)
        mProgressBar.visibility = View.INVISIBLE
//        Firebase.auth.signOut()

        sendOTP.setOnClickListener {
            number = phoneNumber.text.trim().toString()
            if (number.isNotEmpty()) {
                if (number.length == 10) {
                    number = "+91$number"
                    mProgressBar.visibility = View.VISIBLE
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity((context as AuthenticationActivity))                 // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)

                } else {
                    Toast.makeText(context, "Please Enter correct Number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please Enter Number", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }


    fun checkIfUserExists() {
        val phone = "+91" + phoneNumber.text.toString().trim()
        val db = Firebase.firestore
        db.collection("Users")
            .whereEqualTo("phoneNumber", phone)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(context, "Authenticate Successfully", Toast.LENGTH_SHORT).show()
                    sendToMain()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                    mProgressBar.visibility = View.INVISIBLE
                }
            }
    }
    private fun sendToMain() {
        startActivity(Intent(context, MainActivity::class.java))
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }
            // Show a message and update the UI
            mProgressBar.visibility = View.VISIBLE
        }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Save verification ID and resending token so we can use them later
                val bundle = bundleOf(
                    "OTP" to verificationId,
                    "resendToken" to token,
                    "phoneNumber" to number,
                )
                mProgressBar.visibility = View.INVISIBLE
//                Log.d("TAG", "onCodeSent: ${bundle.getString("OTP")} ")
                findNavController().navigate(R.id.action_phoneLoginFragment_to_OTPLoginFragment, bundle)
            }
        }
}
