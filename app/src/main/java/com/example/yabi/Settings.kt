package com.example.yabi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import  android.widget.Spinner
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.toolbar

class Settings : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val TAG = "Settings activity"

    private val stateUSList = arrayOf(
        "--", "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN",
        "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH",
        "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT",
        "VT", "VA", "WA", "WV","WI","WY")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        setSupportActionBar(toolbar)

        //TODO: load in the current values from the database

        // Create an ArrayAdapter
        val adapter = ArrayAdapter.createFromResource(this, R.array.tag_list, android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner2.adapter = adapter


        spinnerState.onItemSelectedListener = this

        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            stateUSList)

        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)

        spinnerState.adapter = ad
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_nav_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if(buyerOnlySwitch.isChecked)
            editor.putBoolean("buyerOnly", true)
        else
            editor.putBoolean("buyerOnly", false)
        editor.apply()

        val state: String = spinnerState.selectedItem as String
        val city = editTextCity.text
        val zip = editTextZip.text

        when (item.itemId) {
            R.id.confirm -> {
                when {
                    state == "--" -> {
                        Toast.makeText(
                            applicationContext,
                            "Error: State cannot be blank.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    city.isEmpty() -> {
                        Toast.makeText(
                            applicationContext,
                            "Error: City cannot be blank.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    zip.isEmpty() -> {
                        Toast.makeText(
                            applicationContext,
                            "Error: Zip code cannot be blank.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    intent.getBooleanExtra("SignUp", false) -> {
                        updateUserLocation()
                    }

                    !intent.getBooleanExtra("SignUp", false) -> {
                        updateUserLocation()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUserLocation()
    {
        val db = Firebase.firestore

        val state: String = spinnerState.selectedItem as String
        val cityVal = editTextCity.text.toString()
        val zipVal = editTextZip.text.toString().toInt()
        val userID = intent.getStringExtra("userID")

        val location = hashMapOf(
            "city" to cityVal,
            "state" to state,
            "zip" to zipVal
        )
        if (userID != null) {
            db.collection("users").document(userID)
                .set(location, SetOptions.merge())
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "User location added to user ID: $userID")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding user document", e)
                }
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)

    }

    // Add required Spinner methods
    override fun onItemSelected(parent: AdapterView<*>?,
                                view: View, position: Int,
                                id: Long) {}

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}