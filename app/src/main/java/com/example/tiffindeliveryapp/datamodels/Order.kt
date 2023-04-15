package com.example.tiffindeliveryapp.datamodels

import android.os.Parcelable
import java.io.Serializable
import java.util.*

data class Order(val tiffinServiceId:String, val userId:String, val startDate:Date, val endDate:Date, val tiffinChoice:String, val foodPreference:String, val deliveryAddress:Map<String, String>)
