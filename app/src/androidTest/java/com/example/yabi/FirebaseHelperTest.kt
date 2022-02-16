package com.example.yabi

import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import org.junit.Assert.*
import org.junit.Test

class FirebaseHelperTest {

    @Test
    fun authenticateUser() {


    }

    @Test
    fun createUser() {
        var db = Firebase.firestore
        var testFirebaseHelper = FirebaseHelper(db)
        testFirebaseHelper.createUser("darian.fisher@snhu.edu", "Darian Fisher", "DJANGO")
        while(!testFirebaseHelper.previousTaskFinished) {}
        assert(testFirebaseHelper.previousTaskSuccess)
        testFirebaseHelper.resetStatusFlags()
    }
}