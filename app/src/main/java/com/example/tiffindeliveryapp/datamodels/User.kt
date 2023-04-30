package com.example.tiffindeliveryapp.datamodels

data class User(val uid:String?, val username:String?, val email:String?, val phoneNumber:String?, val address:Map<String, String>?){
    constructor():this("","","", "", emptyMap())
}
