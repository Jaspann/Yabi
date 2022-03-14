package com.example.yabi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.toolbar

class Settings : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val stateUSList = arrayOf(
        "--", "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN",
        "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH",
        "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT",
        "VT", "VA", "WA", "WV","WI","WY")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        //TODO: load in the current values from the database

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
                        createAccount()
                    }

                    !intent.getBooleanExtra("SignUp", false) -> {

                        //TODO: add ability to change state and city of account

                        finish()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createAccount()
    {
        val db = Firebase.firestore
        val helper = FirebaseHelper(db)

        val email = intent.getStringExtra("email")
        val pass = intent.getStringExtra("pass")
        val name = intent.getStringExtra("name")

        val state: String = spinnerState.selectedItem as String
        val cityVal = editTextCity.text.toString()
        val zipVal = editTextZip.text.toString().toInt()

            if (intent.getBooleanExtra("SignUp", false))
            {
                if (name != null && pass != null && email != null)
                {
                    helper.createUser(email, name, pass, cityVal, state, zipVal)
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
    }

    // Add required Spinner methods
    override fun onItemSelected(parent: AdapterView<*>?,
                                view: View, position: Int,
                                id: Long) {}

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}