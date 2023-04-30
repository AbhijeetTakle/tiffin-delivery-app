package com.example.tiffindeliveryapp.datamodels

data class Admin(val aid:String, val tiffinServiceId: String?, val orders:ArrayList<String>){
    constructor():this("", "", ArrayList())
}
