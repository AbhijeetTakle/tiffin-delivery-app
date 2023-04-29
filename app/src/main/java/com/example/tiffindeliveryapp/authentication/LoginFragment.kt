package com.example.tiffindeliveryapp.authentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.tiffindeliveryapp.AuthenticationActivity
import com.example.tiffindeliveryapp.R
import com.example.tiffindeliveryapp.utils.AuthenticationListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var loginUsername:EditText
    private lateinit var loginPassword:EditText
    private lateinit var login:Button
    private lateinit var loginWithOTP:ImageButton
    private lateinit var loginWithGoogle: ImageButton
    private lateinit var registerNewUser:TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: AuthenticationListener
    private lateinit var loginAsAdmin:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        loginUsername = view.findViewById(R.id.login_username)
        loginPassword = view.findViewById(R.id.login_password)
        loginWithOTP = view.findViewById(R.id.login_with_otp)
        login = view.findViewById(R.id.login_btn)
        registerNewUser = view.findViewById(R.id.register_user_link)
        loginAsAdmin = view.findViewById(R.id.login_as_admin)
        mAuth = Firebase.auth
        if (mAuth.currentUser != null){
            mAuth.signOut()
        }
        setActionToButtons()
        return view
    }

    private fun setActionToButtons() {
        login.setOnClickListener{
            val username = loginUsername.text.toString().trim()
            val password = loginPassword.text.toString().trim()
            if(checkValidInput()){
                mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        try {
                            mAuthListener = context as AuthenticationActivity
                            mAuthListener.loginSuccess()
                        }catch (e: java.lang.ClassCastException){
                            Log.d("TAG", "setActionToButtons: "+e)
                        }
                    }else{
                        Toast.makeText(context,"${it.exception?.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        registerNewUser.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
        loginAsAdmin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment2_to_loginAdminFragment)
        }
        loginWithOTP.setOnClickListener {
            findNavController().navigate((R.id.action_loginFragment2_to_phoneLoginFragment))
        }
    }

    private fun checkValidInput():Boolean {
        val username = loginUsername.text.toString().trim()
        val password = loginPassword.text.toString().trim()

        if(username.isEmpty()){
            loginUsername.error = "Cannot be empty"
            loginUsername.requestFocus()
            return false
        }
        if(password.isEmpty()){
            loginPassword.error = "Cannot be empty"
            loginPassword.requestFocus()
            return false
        }
        return true
    }
}