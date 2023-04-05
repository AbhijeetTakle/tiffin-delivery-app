package com.example.tiffindeliveryapp.datamodels

data class TiffinService(var id:String, val title:String, val description:String, val veg:Boolean, val nonVeg:Boolean,  val subscriberCount:Int, val rating:Double, val customerReviews:Map<String, String>, val tiffinTypes:Map<String, String>, val todaysMenu:String){
    constructor():this("", "", "", false, false, 0, 0.0, emptyMap(), emptyMap(), "")
}
