package com.example.tiffindeliveryapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tiffindeliveryapp.viewmodels.SplashScreenViewModel

class SplashScreenFragment : Fragment() {

    private lateinit var splashScreenViewModel:SplashScreenViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        splashScreenViewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)
        splashScreenViewModel.isLoading.observe(viewLifecycleOwner){
            if(!it){
                findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
            }
        }
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }
}