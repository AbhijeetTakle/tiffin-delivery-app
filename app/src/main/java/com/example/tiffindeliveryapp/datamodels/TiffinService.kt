package com.example.tiffindeliveryapp.datamodels

data class TiffinService(val id:String, val title:String, val description:String, val veg:Boolean, val nonVeg:Boolean,  val subscriberCount:Int, val rating:Double){
    constructor():this("", "", "", false, false, 0, 0.0)
}
