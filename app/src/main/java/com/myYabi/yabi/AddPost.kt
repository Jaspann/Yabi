package com.myYabi.yabi


import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import myYabi.yabi.R
import pub.devrel.easypermissions.EasyPermissions


class AddPost : AppCompatActivity() {

    private val TAG = "Add post activity"
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

        if(intent.hasExtra("fromChat"))
        {
            val returnIntent = Intent()
            returnIntent.putExtra("price", editTextRequestingPrice.text.toString())
            returnIntent.putExtra("buyerShipping", buyerCoverShippingButton.isChecked)
            returnIntent.putExtra("coverInFull", coverShippingFullButton.isChecked)
            returnIntent.putExtra("coveredInPart", editTextCoverShippingUntil.text.toString())
            setResult(RESULT_OK, returnIntent)
            finish()
        }

        else if(!intent.hasExtra("isEdit") && !intent.hasExtra("isCounter"))
        {
            //TODO:: For Preset Location Listing Creation
            if(buttonLocationLineOne.isChecked == true){
                val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
                val listingUserID = sharedPreferences.getString("userID", "guest")
                val shippingStreet = sharedPreferences.getString("street", "")
                val shippingCity = sharedPreferences.getString("city","")
                val stateUSList = arrayOf(
                    "--", "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN",
                    "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH",
                    "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT",
                    "VT", "VA", "WA", "WV","WI","WY")
                val i : Int = sharedPreferences.getInt("state",0)
                val shippingState = stateUSList[i]
                val shippingCountry = "US"
                val postalCode = sharedPreferences.getInt("zipCode",0)
                val itemName = editTextItemName.text.toString()
                val requestedPrice = editTextRequestingPrice.text.toString().toDouble()
                val coverShipping = buyerCoverShippingButton.isChecked
                val coveredShipping = if (coverShippingFullButton.isChecked) { -1.0 } else {editTextCoverShippingUntil.text.toString().toDouble() }
                val itemDescription = editTextTextMultiLine.text.toString()
                val tag = spinner.selectedItem.toString()
                val db = Firebase.firestore

                if(listingUserID != "guest") {
                    val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
                    val acctEmail = sharedPreferences.getString("acctEmail", "guest")
                    val listing = hashMapOf(
                        "userID" to listingUserID,
                        "acctEmail" to acctEmail,
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
                        "tag" to tag,
                        "creationTimestamp" to FieldValue.serverTimestamp()
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
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Must be signed in to create listings.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else if (buttonLocationCustom.isChecked == false && buttonLocationLineOne.isChecked == false){
                Toast.makeText(
                    applicationContext,
                    "Error:Please select Custom or Preset Shipping",
                    Toast.LENGTH_LONG
                ).show()
            }


            //TODO:: For Custom Location creation
            else if(buttonLocationCustom.isChecked == true){
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

            val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val listingUserID = sharedPreferences.getString("userID", "guest")

            if(listingUserID != "guest") {
                val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
                val acctEmail = sharedPreferences.getString("acctEmail", "guest")
                val listing = hashMapOf(
                    "userID" to listingUserID,
                    "acctEmail" to acctEmail,
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
                    "tag" to tag,
                    "creationTimestamp" to FieldValue.serverTimestamp()
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
            } else {
                Toast.makeText(
                    applicationContext,
                    "Must be signed in to create listings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        }
        //TODO: For Offers
        if (intent.hasExtra("isCounter")) {
            val db = Firebase.firestore
            val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val buyerID = sharedPreferences.getString("userID", "guest")
            val listingID = intent.getStringExtra("listingID")
            val sellerID = intent.getStringExtra("sellerID")
            val itemName = editTextItemName.text.toString()
            val yourOffer = editTextRequestingPrice.text.toString().toDouble()
            val coverShipping = buyerCoverShippingButton.isChecked
            val coveredShipping = if (coverShippingFullButton.isChecked) { -1.0 } else { editTextCoverShippingUntil.text.toString().toDouble() }
            val acctEmail = sharedPreferences.getString("acctEmail", "guest")
            val offer = hashMapOf(
                "buyerID" to buyerID,
                "sellerID" to sellerID,
                "buyerEmail" to acctEmail,
                "listingID" to listingID,
                "itemName" to itemName,
                "yourOffer" to yourOffer,
            )
            db.collection("offers")
                .add(offer)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Offer document added with ID:)${documentReference.id}")
                    Toast.makeText(applicationContext, "Offer Submitted",
                        Toast.LENGTH_LONG
                    ).show()
                    onOfferSubmit(listingID.toString(), itemName, yourOffer)
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

    }
    private fun onOfferSubmit(listingID: String, itemName: String, yourOffer: Double){
        val db = Firebase.firestore
        val listingID = intent.getStringExtra("listingID")
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val userID = sharedPreferences.getString("userID", "guest")
        val userMessage = "I would like to sell you $itemName for $$yourOffer."
        val message = hashMapOf(
            "listingID" to listingID,
            "userID" to userID,
            "message" to userMessage,
            "price" to yourOffer,
            "creationTimestamp" to FieldValue.serverTimestamp()
        )

        db.collection("chats")
            .add(message)
            .addOnSuccessListener {
                val intent = Intent(this, PostChat::class.java)
                intent.putExtra("listingID", listingID)
                startActivity(intent)
            }
    }
    private fun fillScreen()
    {
        // If Statement chain to autofill counter offers
        if(intent.hasExtra("isEdit"))
        {
            toolbar.title = "Edit Posting"
            postButton.text = getString(R.string.save_changes)
        }
        if(intent.hasExtra("isCounter") || intent.hasExtra("fromChat"))
        {
            toolbar.title = "Create Offer"
            postButton.text = getString(R.string.submit_offer)
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
        if(intent.hasExtra("fromChat"))
        {
            button2.visibility = View.GONE
        }
        if(intent.hasExtra("title"))
        {
            editTextItemName.setText(intent.getStringExtra("title"))
        }
        if(intent.hasExtra("price"))
        {
            editTextRequestingPrice.setText(String.format("%.2f",intent.getDoubleExtra("price", 0.00)))
        }
        if(intent.hasExtra("location"))
        {
            presetLocationLineOne.text = intent.getStringExtra("location")
            presetLocationLineTwo.visibility = View.GONE
        }
        else {
            val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val stateUSList = arrayOf(
                "--", "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN",
                "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH",
                "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT",
                "VT", "VA", "WA", "WV","WI","WY")
            val i : Int = sharedPreferences.getInt("state",0)
            val State : String = stateUSList[i]
            presetLocationLineOne.text = sharedPreferences.getString("street", "")
            presetLocationLineTwo.text = sharedPreferences.getString("city","") + ", " + State + ", " + sharedPreferences.getInt("zipCode",0).toString()

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