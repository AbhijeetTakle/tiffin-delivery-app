package com.example.tiffindeliveryapp.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.tiffindeliveryapp.R
import com.example.tiffindeliveryapp.datamodels.Admin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SingupAdminFragment : Fragment() {
    private lateinit var signupUserEmail: EditText
    private lateinit var signupUsername: EditText
    private lateinit var signupPassword: EditText
    private lateinit var signupConfirmPassword: EditText
    private lateinit var signup: Button
    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_singup_admin, container, false)
        signupUsername = view.findViewById(R.id.signup_username)
        signupUserEmail = view.findViewById(R.id.signup_user_email)
        signupPassword = view.findViewById(R.id.signup_password)
        signupConfirmPassword = view.findViewById(R.id.signup_confirm_password)
        signup = view.findViewById(R.id.signup_btn)
        mAuth = Firebase.auth
        mAuth.signOut()
        setActionsToButtons()
        return view
    }

    private fun setActionsToButtons() {
        signup.setOnClickListener {
            if(checkValidInput()){
                val userEmail = signupUserEmail.text.toString().trim()
                val password = signupPassword.text.toString().trim()

                mAuth.createUserWithEmailAndPassword(userEmail, password).addOnCompleteListener { it ->
                    if(it.isSuccessful){
                        addUser()
                    }else{
                        Toast.makeText(context, "${it.exception?.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun addUser(){
        val db  = Firebase.firestore
        val username = signupUsername.text.toString().trim()
        mAuth.currentUser?.let {currentUser ->
            val admin = Admin(currentUser.uid, null, ArrayList<String>())
            db.collection("admins")
                .add(admin)
                .addOnSuccessListener {
                    Toast.makeText(context, "admin Created Successfully", Toast.LENGTH_SHORT).show()
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Unable to create admin!", Toast.LENGTH_SHORT).show()
                    mAuth.currentUser?.delete()
                }
        }
    }

    private fun checkValidInput():Boolean {
        val userEmail = signupUserEmail.text.toString().trim()
        val password = signupPassword.text.toString().trim()
        val confirmPassword = signupConfirmPassword.text.toString().trim()

        if(userEmail.isEmpty()){
            signupUserEmail.error = "Cannot be empty"
            signupUserEmail.requestFocus()
            return false
        }
        if(password.isEmpty()){
            signupPassword.error = "Cannot be empty"
            signupPassword.requestFocus()
            return false
        }
        if(confirmPassword.isEmpty()){
            signupConfirmPassword.error = "Cannot be empty"
            signupConfirmPassword.requestFocus()
            return false
        }

        if(confirmPassword != password){
            signupConfirmPassword.setText("")
            signupConfirmPassword.error = "Confirm password doesn't match with the password"
            signupConfirmPassword.requestFocus()
        }
        return true
    }
}