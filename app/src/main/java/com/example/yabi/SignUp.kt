package com.example.yabi

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONException
import java.lang.RuntimeException

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


    }

    fun onPressSignUp(view: android.view.View) {

        val email = editTextEmailAddress.text
        val pass = editTextPassword.text
        val name = editTextName.text

        val queue = Volley.newRequestQueue(this)
        val url = "www.example.com/signup?email=$email&password=$pass&name=$name"


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
                // Request a string response from the provided URL.
                val stringRequest = JsonObjectRequest(Request.Method.GET, url, null,
                    { response ->
                        try {

                            if (response.getBoolean("accountCreated")) {
                                val sharedPreferences =
                                    getSharedPreferences("sharedPrefs", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()

                                editor.putInt("userID", response.getInt("userId"))
                                editor.apply()

                                this.onSignUpSuccess()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Error: Account Creation failed.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                applicationContext,
                                "Error: There was an error in the response.",
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: RuntimeException) {
                            Toast.makeText(
                                applicationContext,
                                "Error: There was an error in the runtime.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    {
                        Toast.makeText(
                            applicationContext,
                            "Error: The request failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                queue.add(stringRequest)
            }
        }
    }

    private fun onSignUpSuccess()
    {
        val intent = Intent(this, MainActivity::class.java)
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
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}