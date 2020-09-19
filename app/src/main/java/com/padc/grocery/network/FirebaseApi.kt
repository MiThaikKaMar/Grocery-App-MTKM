package com.padc.grocery.network

import com.padc.grocery.data.vos.GroceryVO

interface FirebaseApi {

    fun getGroceries(onSuccess : (List<GroceryVO>) ->Unit,onFailure : (String)->Unit)
 fun addGrocery(name: String,description:String,amount:Int)
    fun deleteGrocery(name:String)
}