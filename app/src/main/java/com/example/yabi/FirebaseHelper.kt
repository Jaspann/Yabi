package com.example.yabi

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects


class FirebaseHelper(var db: FirebaseFirestore) {

    var previousTaskFinished: Boolean = false
    var previousTaskSuccess: Boolean = false
    var sessionPassword: String = ""
    var queryResult = mutableListOf<DocumentSnapshot>()

    fun resetStatusFlags() {
        previousTaskFinished = false
        previousTaskSuccess = false
    }

    //Gets a user password associated with a given email and stores it in sessionPassword
    fun getPassword(email: String) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { results ->
                Log.d(TAG, "Successfully found account with matching email.")
                previousTaskFinished = true
                previousTaskSuccess = true
                sessionPassword = results.documents[0].get("password").toString()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting authenticating user: ", e)
                previousTaskFinished = true
                previousTaskSuccess = false
            }
    }

    //Gets all listings
    fun getListings() {
        db.collection("listings")
            .get()
            .addOnSuccessListener { results ->
                Log.d(TAG, "Listings retrieved.")
                queryResult = results.documents
            }
            .addOnFailureListener{ e ->
                Log.w(TAG, "Failed to retrieve listings.", e)
            }
    }

    fun createUser(email: String, name: String, password: String,){

        //TODO Add code that converts raw password to SHA512 (actually should be

        //TODO Check if email is already used

        //TODO Add creation date

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

    fun createListing(itemName: String,
                      requestedPrice: Int,
                      coverShipping: Boolean,
                      coveredShipping: Int,
                      itemDescription: String,
                      shippingStreet: String,
                      shippingCity: String,
                      shippingState: String,
                      shippingCountry: String,
                      postalCode: Int) {

        //TODO Add creation date

        val listing = hashMapOf(
            "itemName" to itemName,
            "requestedPrice" to requestedPrice,
            "coverShipping" to coverShipping,
            "coveredShipping" to coveredShipping,
            "itemDescription" to itemDescription,
            "shippingStreet" to shippingStreet,
            "shippingCity" to shippingCity,
            "shippingState" to shippingState,
            "shippingCountry" to shippingCountry,
            "postalCode" to postalCode
        )

        db.collection("listings")
            .add(listing)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Listing document added with ID: ${documentReference.id}")
                previousTaskFinished = true
                previousTaskSuccess = true
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding listing document", e)
                previousTaskFinished = true
                previousTaskSuccess = false
            }

    }

}