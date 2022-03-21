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

        //TODO: load in the current location values from the database
        //To compensate, we will load and save value to SavedPreferences

        spinnerState.onItemSelectedListener = this

        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            stateUSList)

        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)

        spinnerState.adapter = ad

        fillOptions()
    }

    private fun fillOptions()
    {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        buyerOnlySwitch.isChecked = sharedPreferences.getBoolean("buyerOnly", false)
        editTextStreet.setText(sharedPreferences.getString("street", ""))
        editTextCity.setText(sharedPreferences.getString("city", ""))
        spinnerState.setSelection(sharedPreferences.getInt("state", 0))
        radioButtonFurniture.isChecked = sharedPreferences.getBoolean("Furniture", false)
        radioButtonGames.isChecked = sharedPreferences.getBoolean("Games", false)
        radioButtonCards.isChecked = sharedPreferences.getBoolean("Cards", false)
        radioButtonPaintings.isChecked = sharedPreferences.getBoolean("Paintings", false)
        radioButtonClothing.isChecked = sharedPreferences.getBoolean("Clothing", false)
        radioButtonHomeImprovement.isChecked = sharedPreferences.getBoolean("HomeImprovement", false)
        radioButtonAccessory.isChecked = sharedPreferences.getBoolean("Accessory", false)
        radioButtonCollectable.isChecked = sharedPreferences.getBoolean("Collectable", false)

        //sharedPreferences.edit().remove("zipCode").apply()

        if(sharedPreferences.getInt("zipCode", 0) != 0)
            editTextZip.setText(sharedPreferences.getInt("zipCode", 0).toString())

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_nav_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val state: String = spinnerState.selectedItem as String
        val city = editTextCity.text
        val zip = editTextZip.text

        val userID = intent.getStringExtra("userID")

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
                        storeSettings()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("userID", userID)
                        startActivity(intent)
                    }

                    else -> {

                        updateUserLocation()
                        storeSettings()
                        finish()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun storeSettings()
    {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("buyerOnly", buyerOnlySwitch.isChecked)
        editor.putString("street", editTextStreet.text.toString())
        editor.putString("city", editTextCity.text.toString())
        editor.putInt("state", spinnerState.selectedItemPosition)
        editor.putInt("zipCode", editTextZip.text.toString().toInt())

        editor.putBoolean("Furniture", radioButtonFurniture.isChecked)
        editor.putBoolean("Games", radioButtonGames.isChecked)
        editor.putBoolean("Cards", radioButtonCards.isChecked)
        editor.putBoolean("Paintings", radioButtonPaintings.isChecked)
        editor.putBoolean("Clothing", radioButtonClothing.isChecked)
        editor.putBoolean("HomeImprovement", radioButtonHomeImprovement.isChecked)
        editor.putBoolean("Accessory", radioButtonAccessory.isChecked)
        editor.putBoolean("Collectable", radioButtonCollectable.isChecked)

        editor.apply()
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

    }

    // Add required Spinner methods
    override fun onItemSelected(parent: AdapterView<*>?,
                                view: View, position: Int,
                                id: Long) {}

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}