package com.example.tiffindeliveryapp.datamodels

import java.util.*

data class NewOrder(var orderId:String, val tiffinServiceId:String, var serviceName:String, val userId:String, var userName:String, val startDate: Date, val endDate: Date, val tiffinChoice:String, val foodPreference:String, val deliveryAddress:Map<String, String>, val orderCompleted:Boolean, var paymentMethod:String, val canceled:Boolean){
    constructor():this("","", "", "","", Date(), Date(), "", "", emptyMap(), false, "", false)
}
