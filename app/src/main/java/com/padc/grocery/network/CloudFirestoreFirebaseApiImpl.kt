package com.padc.grocery.network

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.padc.grocery.data.vos.GroceryVO

object CloudFirestoreFirebaseApiImpl : FirebaseApi{

    val db = Firebase.firestore

    override fun getGroceries(onSuccess: (List<GroceryVO>) -> Unit, onFailure: (String) -> Unit) {
        //read data from cloudfirestore normally
//        db.collection("groceries").get()
//            .addOnSuccessListener {result->
//                var list : MutableList<GroceryVO> = arrayListOf()
//                for (document in result){
//                    var data = document.data
//                    val grocery = GroceryVO()
//                    grocery.name=data["name"]as String
//                    grocery.description=data["description"] as String
//                    grocery.amount= (data["amount"] as Long).toInt()
//                    list.add(grocery)
//                }
//                onSuccess(list)
//            }
//            .addOnFailureListener {
//                onFailure(it.localizedMessage ?: "Please check connection!")
//            }

        //read data from cloudfirestore syncronizely
        db.collection("groceries")
            .addSnapshotListener { value, error ->
                error?.let {
                    onFailure(it.localizedMessage ?: "something wrong!")
                } ?: run{

                    var groceryList : MutableList<GroceryVO> = arrayListOf()
                    val result = value?.documents ?: arrayListOf()

                    for (document in result){
                        val data = document.data
                        val grocery = GroceryVO()
                        grocery.name = data?.get("name") as String
                        grocery.description= data["description"] as String
                        grocery.amount= (data["amount"] as Long).toInt()
                        groceryList.add(grocery)
                    }
                    onSuccess(groceryList)
                }
            }

    }

    override fun addGrocery(name: String, description: String, amount: Int) {
        val groceryMap = hashMapOf(
            "name" to name,
            "description" to description,
            "amount" to amount
        )
        db.collection("groceries")
            .document(name)
            .set(groceryMap)
            .addOnSuccessListener { Log.d("Success","Successfully Data Entry") }
            .addOnFailureListener { Log.d("Failure","Failure Data Entry") }
    }

    override fun deleteGrocery(name: String) {

        db.collection("groceries")
            .document(name)
            .delete()
            .addOnFailureListener { Log.d("Failure" ,"Failure delete data") }
            .addOnSuccessListener { Log.d("Success","Success delete data") }
    }

}