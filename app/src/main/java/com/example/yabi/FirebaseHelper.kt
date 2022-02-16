package com.example.yabi

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase


class FirebaseHelper(var db: FirebaseFirestore) {

    var previousTaskFinished: Boolean = false
    var previousTaskSuccess: Boolean = false

    fun resetStatusFlags() {
        previousTaskFinished = false
        previousTaskSuccess = false
    }

    fun authenticateUser(email: String, password: String): Boolean {

        var pwordmatch = false

        var doc = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { results ->
                for (document in results) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    var user = document.data
                    var pword = user.getValue("password")
                    if (pword == password) {
                        pwordmatch = true
                        break
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        return pwordmatch
    }

    fun createUser(email: String, name: String, password: String,){

        //TODO Add code that converts raw password to SHA512

        //TODO Check if email is already used

        val user = hashMapOf(
            "email" to email,
            "name" to name,
            "password" to password
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "User document added with ID: ${documentReference.id}")
                previousTaskFinished = true
                previousTaskSuccess = true
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user document", e)
                previousTaskFinished = true
                previousTaskSuccess = false
            }
    }

    fun createListing(itemname: String, requestedprice: String, covershipping: Boolean, coveredshipping: Int,
        itemdescription: String, shippingstreet: String, shippingcity: String, shippingcountry: String,
        postalcode: String) {

        val listing = hashMapOf(
            "itemname" to itemname,
            "requestedprice" to requestedprice,
            "covershipping" to covershipping,
            "coveredshipping" to coveredshipping,
            "itemdescription" to itemdescription,
            "shippingstreet" to shippingstreet,
            "shippingcity" to shippingcity,
            "shippingcountry" to shippingcountry,
            "postalcode" to postalcode
        )

        db.collection("listings")
            .add(listing)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }


    }



}