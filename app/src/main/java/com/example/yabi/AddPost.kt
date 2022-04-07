package com.example.yabi

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.constraintlayout.widget.ConstraintSet
//stuff used in tags
import android.annotation.SuppressLint
import androidx.annotation.NonNull
import android.view.View
import  android.view.ViewGroup
import android.widget.Toast
import  android.widget.Spinner
import  android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.AdapterView
import  android.widget.TextView
//end of used in tags
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_log_in.*
import pub.devrel.easypermissions.EasyPermissions

class AddPost : AppCompatActivity() {

    private val TAG = "Add post actvity"
    private var remoteImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        toolbar.setNavigationOnClickListener {
            finish()
            // Create an ArrayAdapter
            val adapter = ArrayAdapter.createFromResource(this, R.array.tag_list, android.R.layout.simple_spinner_item)
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        fillScreen()
    }


    fun onPressPost(view: android.view.View) {

        if (!intent.hasExtra("isEdit") && !intent.hasExtra("isCounter")) {
            val itemName = editTextItemName.text.toString()
            val requestedPrice = editTextRequestingPrice.text.toString().toDouble()
            val coverShipping = buyerCoverShippingButton.isChecked
            val coveredShipping = if (coverShippingFullButton.isChecked) {
                -1.0
            } else {
                editTextCoverShippingUntil.text.toString().toDouble()
            }
            val itemDescription = editTextTextMultiLine.text.toString()
            val shippingStreet =
                editTextStreetNumber.text.toString() + " " + editTextStreet.text.toString()
            val shippingCity = editTextCity.text.toString()
            val shippingState = editTextState.text.toString()
            val shippingCountry = editTextCountry.text.toString()
            val postalCode = editTextPostal.text.toString().toInt()
            val tag = spinner.selectedItem.toString()
            val db = Firebase.firestore


            val listingUserID = intent.getStringExtra("userID")

            //TODO: add listing and buyer IDs as to seperate buyer and seller per listing
            val listing = hashMapOf(
                "userID" to listingUserID,
                "itemName" to itemName,
                "requestedPrice" to requestedPrice,
                "coverShipping" to coverShipping,
                "coveredShipping" to coveredShipping,
                "itemDescription" to itemDescription,
                "shippingStreet" to shippingStreet,
                "shippingCity" to shippingCity,
                "shippingState" to shippingState,
                "shippingCountry" to shippingCountry,
                "postalCode" to postalCode,
                "creationTimestamp" to FieldValue.serverTimestamp(),
                "tag" to tag
            )

            if (remoteImagePath != null) {
                listing["imagePath"] = remoteImagePath
            }

            db.collection("listings")
                .add(listing)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Listing document added with ID: ${documentReference.id}")
                    Toast.makeText(
                        applicationContext,
                        "Listing created successfully.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding listing document", e)
                    Toast.makeText(
                        applicationContext,
                        "Error: failed to create listing.",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }



        //TODO: For Offers
        if (intent.hasExtra("isCounter")) {
            val db = Firebase.firestore

            val buyerID = intent.getStringExtra("userID")
            val itemName = editTextItemName.text.toString()
            val youroffer = editTextRequestingPrice.text.toString().toDouble()
            val coverShipping = buyerCoverShippingButton.isChecked
            val coveredShipping = if (coverShippingFullButton.isChecked) { -1.0 } else { editTextCoverShippingUntil.text.toString().toDouble() }
            val offer = hashMapOf(
                "BuyerID" to buyerID,
                "itemName" to itemName,
                "youroffer" to youroffer,
            )
            db.collection("offer")
                .add(offer)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Offer document added with ID:)${documentReference.id}")
                    Toast.makeText(applicationContext, "Offer Submitted",
                        Toast.LENGTH_LONG
                    ).show()
                    onOfferSubmit()
                }
                .addOnFailureListener { e ->
                Log.w(TAG, "Error adding offer document", e)
                Toast.makeText(
                    applicationContext,
                    "Error: failed to create offer.",
                    Toast.LENGTH_LONG
                ).show()

            }
        }




        if(intent.hasExtra("isEdit")){
            val db = Firebase.firestore

            // TODO: edit posts
            val listingUserID = intent.getStringExtra("userID")
            val itemName = editTextItemName.text.toString()
            val requestedPrice = editTextRequestingPrice.text.toString().toDouble()
            val coverShipping = buyerCoverShippingButton.isChecked
            val coveredShipping = if (coverShippingFullButton.isChecked) {
                -1.0
            } else {
                editTextCoverShippingUntil.text.toString().toDouble()
            }
            val itemDescription = editTextTextMultiLine.text.toString()
            val shippingStreet =
                editTextStreetNumber.text.toString() + " " + editTextStreet.text.toString()
            val shippingCity = editTextCity.text.toString()
            val shippingState = editTextState.text.toString()
            val shippingCountry = editTextCountry.text.toString()
            val postalCode = editTextPostal.text.toString().toInt()
            val tag = spinner.selectedItem.toString()


            val editlisting = hashMapOf(
                "userID" to listingUserID,
                "itemName" to itemName,
                "requestedPrice" to requestedPrice,
                "coverShipping" to coverShipping,
                "coveredShipping" to coveredShipping,
                "itemDescription" to itemDescription,
                "shippingStreet" to shippingStreet,
                "shippingCity" to shippingCity,
                "shippingState" to shippingState,
                "shippingCountry" to shippingCountry,
                "postalCode" to postalCode,
                "creationTimestamp" to FieldValue.serverTimestamp(),
                "tag" to tag
            )

            if (remoteImagePath != null) {
                editlisting["imagePath"] = remoteImagePath
            }
            //TODO:: fix edit Listings
            /*
            if (listingUserID != null) {
                db.collection("listings").document(listingUserID)
                    .set(editlisting, SetOptions.merge())
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "Item Updated : $itemName")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating listing", e)
                    }

            }

             */
        }
    }
private fun onOfferSubmit(){

    val intent = Intent(this, PostChat::class.java)
    startActivity(intent)
}

    fun fillScreen()
    {
        // If Statement chain to autofill counter offers
        if(intent.hasExtra("isEdit"))
        {
            toolbar.title = "Edit Posting"
            postButton.text = getString(R.string.save_changes)
        }
        if(intent.hasExtra("isCounter"))
        {
            toolbar.title = "Create Offer"
           // editTextItemName.visibility = View.GONE
            spinner.visibility = View.GONE
            tagView.visibility = View.GONE
            buttonLocationLineOne.visibility = View.GONE
            editTextStreetNumber.visibility = View.GONE
            editTextStreet.visibility = View.GONE
            editTextCity.visibility = View.GONE
            editTextState.visibility = View.GONE
            editTextCountry.visibility = View.GONE
            editTextPostal.visibility = View.GONE
            textViewMessage.visibility = View.GONE
            editTextTextMultiLine.visibility = View.GONE
            textViewLocation.visibility = View.GONE
            presetLocationLineOne.visibility = View.GONE
            buttonLocationLineOne.visibility = View.GONE
            radioGroupLocation.visibility = View.GONE
            priceText.text = getString(R.string.your_offer)
            postButton.text = getString(R.string.submit_offer)
        }
        if(intent.hasExtra("title"))
        {
            editTextItemName.setText(intent.getStringExtra("title"))
        }
        if(intent.hasExtra("price"))
        {
            editTextRequestingPrice.setText(String.format("%.2f",intent.getDoubleExtra("price", 0.00)))
        }
        if(intent.hasExtra("location2"))
        {
            presetLocationLineOne.text = intent.getStringExtra("location2").toString()
            presetLocationLineTwo.visibility = View.GONE
        }
        else
        {
            buttonLocationLineOne.isEnabled = false
            presetLocationLineOne.visibility = View.GONE
            presetLocationLineTwo.visibility = View.GONE
        }
        if(intent.hasExtra("shippingSeller"))
        {
            if(intent.getBooleanExtra("shippingSeller", false))
                sellerCoverShippingButton.isChecked = true
            else
                buyerCoverShippingButton.isChecked = true
        }
        if(intent.hasExtra("covering"))
        {
            if(intent.getDoubleExtra("covering", -1.0) == -1.0)
                coverShippingFullButton.isChecked = true
            else {
                coverShippingPartButton.isChecked = true
                editTextCoverShippingUntil.setText(String.format("%.2f",intent.getDoubleExtra("covering", 0.0)))
            }
        }
    }

    fun onPressAddImage(view: android.view.View) {

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            val storage = Firebase.storage
            var storageRef = storage.reference
            val selectedImage: Uri = data.data!!
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            userImage.visibility = View.VISIBLE
            val cursor = contentResolver.query(
                selectedImage,
                filePathColumn, null, null, null
            )
            cursor?.moveToFirst()
            val columnIndex = cursor!!.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            var imageRef: StorageReference? = storageRef.child("listingImages/${picturePath.replaceBeforeLast('/',"")}")
            userImage.setImageBitmap(BitmapFactory.decodeFile(picturePath))

            val constraintSet = ConstraintSet()
            constraintSet.clone(scrollConstraint)

            constraintSet.clear(R.id.button2, ConstraintSet.BOTTOM)

            constraintSet.applyTo(scrollConstraint)

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
}