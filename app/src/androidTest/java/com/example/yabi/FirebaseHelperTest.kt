package com.example.yabi

import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import org.junit.Assert.*
import org.junit.Test

class FirebaseHelperTest {

    @Test
    fun getPassword() {
        val db = Firebase.firestore
        val testFirebaseHelper = FirebaseHelper(db)
        testFirebaseHelper.getPassword("darian.fisher@snhu.edu")
        while(!testFirebaseHelper.previousTaskFinished) {}
        assert(testFirebaseHelper.previousTaskSuccess)
        testFirebaseHelper.resetStatusFlags()
    }

    @Test
    fun getListings() {
        val db = Firebase.firestore
        val testFirebaseHelper = FirebaseHelper(db)
        testFirebaseHelper.getListings()
        while(!testFirebaseHelper.previousTaskFinished) {}
        assert(testFirebaseHelper.previousTaskSuccess)
        testFirebaseHelper.resetStatusFlags()
    }

    @Test
    fun createUser() {
        val db = Firebase.firestore
        val testFirebaseHelper = FirebaseHelper(db)
        testFirebaseHelper.createUser("darian.fisher@snhu.edu", "Darian Fisher", "DJANGO")
        while(!testFirebaseHelper.previousTaskFinished) {}
        assert(testFirebaseHelper.previousTaskSuccess)
        testFirebaseHelper.resetStatusFlags()
    }

    @Test
    fun createListing() {
        val db = Firebase.firestore
        val testFirebaseHelper = FirebaseHelper(db)
        testFirebaseHelper.createListing("Used Socks", 10, true,
            10, "Your finest used socks please and thank you.",
            "12 Fairview Ln", "Laketown", "Vermont",
        "USA", 12345)
        while(!testFirebaseHelper.previousTaskFinished) {}
        assert(testFirebaseHelper.previousTaskSuccess)
        testFirebaseHelper.resetStatusFlags()
    }
}