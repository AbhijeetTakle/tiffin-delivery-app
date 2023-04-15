package com.example.tiffindeliveryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tiffindeliveryapp.viewmodels.SplashScreenViewModel

class SpalshScreenActivity : AppCompatActivity() {
    private lateinit var splashScreenViewModel:SplashScreenViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh_screen)
        splashScreenViewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)
        splashScreenViewModel.isLoading.observe(this){
            if(!it){
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }
        }
    }
}