package com.example.tiffindeliveryapp.utils

interface AuthenticationListener {
    fun sendOTPForVerification(phone:String)
    fun loginSuccess()
    fun adminLoginSuccess()
}