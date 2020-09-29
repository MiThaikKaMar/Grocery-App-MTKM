package com.padc.grocery.network


import android.graphics.Bitmap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.padc.grocery.data.vos.GroceryVO
import java.io.ByteArrayOutputStream
import java.util.*

object RealtimeDatabaseFirebaseApiImpl : FirebaseApi {

    private val database : DatabaseReference = Firebase.database.reference
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference

    override fun getGroceries(onSuccess: (List<GroceryVO>) -> Unit, onFailure: (String) -> Unit) {
        database.child("groceries").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var groceryList = arrayListOf<GroceryVO>()

                snapshot.children.forEach{dataSnapShot->
                    dataSnapShot.getValue(GroceryVO::class.java)?.let {
                        groceryList.add(it)
                    }
                }
                onSuccess(groceryList)
            }
        })
    }

    override fun addGrocery(name: String, description: String, amount: Int,image: String) {
        database.child("groceries").child(name).setValue(GroceryVO(name,description,amount,image))
    }

    override fun deleteGrocery(name: String) {
        database.child("groceries").child(name).removeValue()
    }

    override fun updateImgAndEditGrocery(image: Bitmap, grocery: GroceryVO) {

        val boas = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG,100,boas)
        val data = boas.toByteArray()

        val imageRef = storageReference.child("images/${UUID.randomUUID()}")
        val uploadTask = imageRef.putBytes(data)
            uploadTask.addOnSuccessListener {

            }
            .addOnFailureListener {

            }

        val urlTask = uploadTask.continueWithTask{
            return@continueWithTask imageRef.downloadUrl
        }.addOnCompleteListener {task ->
            val imageUrl = task.result?.toString()
            addGrocery(grocery.name ?: "",grocery.description ?: "",grocery.amount ?: 0, imageUrl ?: "")

        }

    }

}