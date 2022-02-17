package com.example.yabi

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.view.View
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import android.net.Uri
import pub.devrel.easypermissions.EasyPermissions

class AddPost : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // If Statement chain to autofill counter offers
        if(intent.hasExtra("isCounter"))
        {
            toolbar.title = "Create Offer"
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
        if(intent.hasExtra("location"))
        {
            presetLocationLineOne.text = intent.getStringExtra("location")
            presetLocationLineTwo.visibility = View.GONE
        }
        else
        {
            buttonLocationLineOne.visibility = View.GONE
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


    fun onPressPost(view: android.view.View) {
        //TODO: Have post go to database, with logic for if post or offer
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
            val selectedImage: Uri = data.data!!
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            userImage.visibility = View.VISIBLE
            val cursor = contentResolver.query(
                selectedImage,
                filePathColumn, null, null, null
            )
            if (cursor != null) {
                cursor.moveToFirst()
            }
            val columnIndex = cursor!!.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            userImage.setImageBitmap(BitmapFactory.decodeFile(picturePath))
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