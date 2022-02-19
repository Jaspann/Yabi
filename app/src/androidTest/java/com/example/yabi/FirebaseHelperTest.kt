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
        testFirebaseHelper.createListing("Used Socks", 10.00, true,
            10, "Your finest used socks please and thank you.",
            "12 Fairview Ln", "Laketown", "Vermont",
        "USA", 12345)
        while(!testFirebaseHelper.previousTaskFinished) {}
        assert(testFirebaseHelper.previousTaskSuccess)
        testFirebaseHelper.resetStatusFlags()
    }

    @Test
    fun createOffer() {
        val db = Firebase.firestore
        val testFirebaseHelper = FirebaseHelper(db)
        testFirebaseHelper.createOffer("XOJNnk9yYQYNOHL4vxDQ",
            "hz2V6qq3IOl37ZIdMQIQ",
            "DOtX3muKKWCuGFbJN9E8",
            50.00)
        while(!testFirebaseHelper.previousTaskFinished) {}
        assert(testFirebaseHelper.previousTaskSuccess)
        testFirebaseHelper.resetStatusFlags()
    }

    @Test
    fun getOfferByDocumentID(offerDocumentID: String) {
        val db = Firebase.firestore
        val testFirebaseHelper = FirebaseHelper(db)
        testFirebaseHelper.getOfferByDocumentID("uV61MPy02xpASSc1LaoM")
        while(!testFirebaseHelper.previousTaskFinished) {}
        assert(testFirebaseHelper.previousTaskSuccess)
        testFirebaseHelper.resetStatusFlags()
    }

    @Test
    fun getOffersByListingDocumentID(listingDocumentID: String) {
        val db = Firebase.firestore
        val testFirebaseHelper = FirebaseHelper(db)
        testFirebaseHelper.getOffersByListingDocumentID("hz2V6qq3IOl37ZIdMQIQ")
        while(!testFirebaseHelper.previousTaskFinished) {}
        assert(testFirebaseHelper.previousTaskSuccess)
        testFirebaseHelper.resetStatusFlags()
    }
}