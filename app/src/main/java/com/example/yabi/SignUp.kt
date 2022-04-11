package com.example.yabi

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import java.lang.RuntimeException



class SignUp : AppCompatActivity() {

    private val TAG = "Sign up activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        if(sharedPreferences.getString("userID", "guest") != "guest")
        {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }

    }

    fun onPressSignUp(view: android.view.View) {

        val email = editTextEmailAddress.text
        val pass = editTextPassword.text
        val name = editTextName.text

        val db = Firebase.firestore

        when {
            email.isEmpty() -> {
                Toast.makeText(
                    applicationContext,
                    "Error: Email cannot be blank.",
                    Toast.LENGTH_LONG
                ).show()
            }
            pass.isEmpty() -> {
                Toast.makeText(
                    applicationContext,
                    "Error: Password cannot be blank.",
                    Toast.LENGTH_LONG
                ).show()
            }
            name.isEmpty() -> {
                Toast.makeText(
                    applicationContext,
                    "Error: Name cannot be blank.",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                val emailText: String = editTextEmailAddress.text.toString().trim { it <= ' '}
                val passwordText : String = editTextPassword.text.toString().trim { it <= ' '} //trims spaces
                val nameText : String = editTextName.text.toString().trim {it <= ' '}

                val user = hashMapOf(
                    "email" to emailText,
                    "name" to nameText,
                    "password" to passwordText,
                    "creationTimestamp" to FieldValue.serverTimestamp()
                )

                db.collection("users")
                    .add(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "User document added with ID: ${documentReference.id}")
                        val userID = documentReference.id
                        Toast.makeText(
                            this@SignUp,
                            "Registration successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        onSignUpSuccess(userID, emailText)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding user document", e)
                        Toast.makeText(
                            this@SignUp,
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun onSignUpSuccess(userID: String, acctEmail: String)
    {

        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userID", userID)
        editor.putString("acctEmail", acctEmail)
        editor.apply()

        val intent = Intent(this, Settings::class.java)
        intent.putExtra("SignUp", true)
        startActivity(intent)
    }

    fun onPressLogIn(view: android.view.View) {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }

    fun onPressBrowseAsGuest(view: android.view.View)
    {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userID", "guest")
        editor.putString("acctEmail", "guest")
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}