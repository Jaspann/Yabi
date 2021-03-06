package com.myYabi.yabi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_log_in.*
import myYabi.yabi.R


class LogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        if(sharedPreferences.getString("emailAddress", "") != "" && sharedPreferences.getString("password", "") != "")
        {
            editTextTextEmailAddress.setText(sharedPreferences.getString("emailAddress", ""))
            editTextTextPassword.setText(sharedPreferences.getString("password", ""))
            tryLogIn()
        }
    }

    fun onPressLogIn(view: android.view.View) {
        tryLogIn()
    }
    private fun tryLogIn()
    {

        //Why are these called *TextText*?
        val email: String = editTextTextEmailAddress.text.toString()
        val pass: String = editTextTextPassword.text.toString()
        var dbpass: String
        var userID: String
        var acctEmail: String
        var acctZip: Int
        var tempString: String

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
                            acctEmail = results.documents[0].get("email").toString()
                            userID = results.documents[0].id
                            tempString = results.documents[0].get("zip").toString()
                            acctZip = tempString.toInt()
                            if (pass == dbpass) {
                                onLogInSuccess(userID, acctEmail, acctZip)
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

    private fun onLogInSuccess(userID: String, acctEmail: String, acctZip: Int) {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userID", userID)
        editor.putString("acctEmail", acctEmail)
        editor.putInt("zipCode", acctZip)
        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if(intent.getBooleanExtra("signedOut", false))
            moveTaskToBack(true)
    }

    fun onPressSignUp(view: android.view.View) {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    fun onPressBrowseAsGuest(view: android.view.View) {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userID", "guest")
        editor.putString("acctEmail", "guest")
        editor.putInt("zipCode", 0)
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}