package com.example.yabi

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_post_chat.*
import pub.devrel.easypermissions.EasyPermissions


class PostChat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_chat)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        getDatabaseMessages()

        userMessageInput.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {

                sendMessage(userMessageInput.text.toString(), -1.0)

                userMessageInput.text = null
                return@OnKeyListener true
            }
            false
        })
    }

    //price == -1.0 if not an offer
    private fun sendMessage(message: String?, price: Double?)
    {
        val db = Firebase.firestore
        val message = hashMapOf(
            "message" to message,
            "price" to price
        )

        db.collection("chats")
            .add(message)
    }

    private fun getDatabaseMessages() {
        val db = Firebase.firestore
        var queryResult: MutableList<DocumentSnapshot>
        val account = arrayListOf<String>()
        val messages = arrayListOf<String>()
        val offers = arrayListOf<Double>()
        var tempString: String

        db.collection("chats")
            .get()
            .addOnSuccessListener { results ->
                Log.d("Chat", "Chats retrieved.")
                queryResult = results.documents
                for (document in queryResult) {
                    account.add("us")
                    messages.add(document.get("message").toString())
                    tempString = document.get("price").toString()
                    offers.add(tempString.toDouble())
                }

                fillChatMsg(account, messages, offers)
            }
            .addOnFailureListener {
                Log.w("Chat", "Failed to retrieve listings.", it)
            }
    }

    private fun fillChatMsg(accounts: ArrayList<String>, messages: ArrayList<String>, offers: ArrayList<Double>)
    {
        val data = ArrayList<ChatViewModel>()

        for (i in messages.indices) {
            data.add(ChatViewModel(accounts[i], messages[i], offers[i],this))
        }
        val recyclerview = findViewById<RecyclerView>(R.id.chatRecycler)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = ChatAdapter(this, accounts, messages, offers)

        recyclerview.adapter = adapter
    }

    private val options = arrayOf("Add Image", "Make Offer")

    fun onPressCreateOffer(view: View)
    {

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            .setItems(
                options,
            ) { dialog, which ->
                when (which) {
                    0 -> addImage()
                    1 -> makeCounter()
                }
            }
            .setNegativeButton("Cancel"
            ) { dialog, _ ->
                dialog.cancel()
            }


        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(true)
        alert.show()

    }

    private fun makeCounter()
    {
        val intent = Intent(this, AddPost::class.java)
        intent.putExtra("fromChat", true)
        getResult.launch(intent)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){

                var text = "New offer: price of " + it.data?.getStringExtra("price") + "$ with shipping being covered by "
                if(it.data?.getBooleanExtra("buyerShipping", false) == true)
                    text += "buyer "
                else
                    text += "seller "
                if(it.data?.getBooleanExtra("coverInFull", false) == true)
                    text += "in full."
                else
                    text+= "up to " + it.data?.getStringExtra("coveredInPart") + "$."

                sendMessage(text, it.data?.getStringExtra("price")?.toDouble())
            }
        }


    private fun addImage()
    {
        val galleryPermissions = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (!EasyPermissions.hasPermissions(this, *galleryPermissions)) {
            EasyPermissions.requestPermissions(
                this, "Access for storage",
                101, *galleryPermissions
            )
        }
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, 1)
    }

    private var remoteImagePath: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            val storage = Firebase.storage
            var storageRef = storage.reference
            val selectedImage: Uri = data.data!!
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(
                selectedImage,
                filePathColumn, null, null, null
            )
            cursor?.moveToFirst()
            val columnIndex = cursor!!.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            val path = "listingImages/${picturePath.replaceBeforeLast('/',"")}"
            var imageRef: StorageReference? = storageRef.child(path)

            var uploadTask = imageRef?.putFile(selectedImage)
            uploadTask?.addOnSuccessListener { task ->
                Toast.makeText(
                    applicationContext,
                    "Image uploaded.",
                    Toast.LENGTH_SHORT
                ).show()
                remoteImagePath = task.metadata?.path
            }?.addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "Image upload failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            sendMessage(path, -1.0)
        }
        else
        {
            Toast.makeText(
                applicationContext,
                "Something went wrong getting the image.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun onPressAcceptOffer(view: View) {

    }
}