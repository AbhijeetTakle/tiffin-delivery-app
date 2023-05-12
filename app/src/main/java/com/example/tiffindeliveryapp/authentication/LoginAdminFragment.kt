package com.example.tiffindeliveryapp.authentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.tiffindeliveryapp.AuthenticationActivity
import com.example.tiffindeliveryapp.R
import com.example.tiffindeliveryapp.utils.AuthenticationListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginAdminFragment : Fragment() {
    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var login: Button
    private lateinit var registerNewUser: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: AuthenticationListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login_admin, container, false)
        loginUsername = view.findViewById(R.id.login_username)
        loginPassword = view.findViewById(R.id.login_password)
        login = view.findViewById(R.id.login_btn)
        registerNewUser = view.findViewById(R.id.register_user_link)
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
                            checkValidAdmin()
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
            findNavController().navigate(R.id.action_loginAdminFragment_to_singupAdminFragment)
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

    private fun checkValidAdmin(){
        val db = Firebase.firestore
        db.collection("admins")
            .whereEqualTo("aid", "${mAuth.currentUser?.uid}")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    mAuthListener = context as AuthenticationActivity
                    mAuthListener.adminLoginSuccess()
                }else{
                    Toast.makeText(context, "User is not an admin", Toast.LENGTH_SHORT).show()
                    mAuth.signOut()
                }
            }
    }
}