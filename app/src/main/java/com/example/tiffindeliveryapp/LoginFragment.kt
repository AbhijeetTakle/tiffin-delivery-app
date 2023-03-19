package com.example.tiffindeliveryapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var loginUsername:EditText
    private lateinit var loginPassword:EditText
    private lateinit var login:Button
    private lateinit var registerNewUser:TextView
    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        loginUsername = view.findViewById(R.id.login_username)
        loginPassword = view.findViewById(R.id.login_password)
        login = view.findViewById(R.id.login_btn)
        registerNewUser = view.findViewById(R.id.register_user_link)
        mAuth = Firebase.auth
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
                        findNavController().navigate(R.id.action_loginFragment_to_tiffinServicesListFragment)
                    }else{
                        Toast.makeText(context,"${it.exception?.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        registerNewUser.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
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