package com.example.yabi

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import java.lang.RuntimeException



class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

    }

    fun onPressSignUp(view: android.view.View) {

        val db = Firebase.firestore
        val helper = FirebaseHelper(db)
        val email = editTextEmailAddress.text
        val pass = editTextPassword.text
        val name = editTextName.text


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
                //user creation
                helper.createUser(emailText, nameText, passwordText)
                Toast.makeText(
                    this@SignUp,
                    "register attempt Successful",
                    Toast.LENGTH_SHORT
                ).show()
                onSignUpSuccess()
            }
        }
    }

    private fun onSignUpSuccess()
    {
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

        editor.putInt("userID", -1)
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}