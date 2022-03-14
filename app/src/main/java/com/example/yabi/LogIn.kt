package com.example.yabi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_log_in.*
import org.json.JSONException
import java.lang.RuntimeException


class LogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
    }

    fun onPressLogIn(view: android.view.View) {

        //Why are these called *TextText*?
        val email: String = editTextTextEmailAddress.text.toString()
        val pass: String = editTextTextPassword.text.toString()
        var dbpass: String
        var userID = ""

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
            else -> {
                val db = Firebase.firestore
                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { results ->
                        Log.d("TAG", "Successfully found account with matching email.")
                        if  (results.documents.isNotEmpty()) {
                            dbpass = results.documents[0].get("password").toString()
                            userID = results.documents[0].id
                            if (pass == dbpass) {
                                onLogInSuccess(userID)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Error: Invalid password.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error: No account matches that email.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error getting authenticating user: ", e)
                        Toast.makeText(
                            applicationContext,
                            "Error: Something went wrong.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }

    private fun onLogInSuccess(userID: String) {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("isGuest", false)
        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }

    fun onPressSignUp(view: android.view.View) {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    fun onPressBrowseAsGuest(view: android.view.View) {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("isGuest", true)
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}