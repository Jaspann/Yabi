package com.myYabi.yabi

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import myYabi.yabi.R
import org.json.JSONObject


class ChatAdapter(
    var context: Context,
    var activity: Activity,
    var accounts: Array<String>,
    var messages: Array<String>,
    var isOffer: Array<Double>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var billingClient: BillingClient? = null
    override fun getItemViewType(position: Int): Int {
        return 1
        /*
        try {
            return if (offers[position]) {
                1
            } else {
                0
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return -1

         */
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(context)
        val view: View
        view = inflater.inflate(R.layout.message, parent, false)
        return MessageViewHolder(view)
    }

    //@SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as MessageViewHolder).userText.text = messages[position]
        val constraintSet = ConstraintSet()
        constraintSet.clone(holder.constraint)
        if(accounts[position] == "them") {
            constraintSet.connect(
                R.id.cardBox,
                ConstraintSet.LEFT,
                R.id.messageConstraint,
                ConstraintSet.LEFT,
                5
            )
            holder.cardBox.setCardBackgroundColor(Color.DKGRAY)
        }
        if(accounts[position] == "us") {
            constraintSet.connect(
                R.id.cardBox,
                ConstraintSet.RIGHT,
                R.id.messageConstraint,
                ConstraintSet.RIGHT,
                5
            )
        }
        if(messages[position].substring(0, "listingImages/".length + 1) == "listingImages/")
        {
            holder.userImage.visibility = View.VISIBLE
            holder.userText.visibility = View.GONE

            val mStorage = FirebaseStorage.getInstance().reference

            mStorage.child(messages[position]).downloadUrl.addOnSuccessListener { results ->
                Picasso.get().load(results).into(holder.userImage)
            }
        }
        constraintSet.applyTo(holder.constraint)

        if(isOffer[position] != -1.0)
        {
            holder.offerButton.visibility = View.VISIBLE

            billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener { billingResult, list ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                        for (purchase in list) {
                            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                                !purchase.isAcknowledged
                            ) {
                                verifyPurchase(purchase)
                            }
                        }
                    }
                }.build()
            connectToGooglePlayBilling(context, holder.offerButton, isOffer[position])
        }

    }



    override fun getItemCount(): Int {
        return messages.size
    }

    internal class MessageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val constraint: androidx.constraintlayout.widget.ConstraintLayout = itemView.findViewById(R.id.messageConstraint)
        val cardBox: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardBox)
        val userText: TextView = itemView.findViewById(R.id.userText)
        val userImage: ImageView = itemView.findViewById(R.id.imageView)
        val offerButton: Button = itemView.findViewById(R.id.offerButton)
    }

    private fun verifyPurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                .build()
        billingClient!!.consumeAsync(
            consumeParams
        ) { billingResult, s ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Toast.makeText(activity, "Consumed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun connectToGooglePlayBilling(context: Context, button: Button, price: Double) {
        billingClient!!.startConnection(
            object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    connectToGooglePlayBilling(context, button, price)
                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        getProductDetails(context, button, price)
                    }
                }
            }
        )
    }

    private fun getProductDetails(context: Context, button: Button, price: Double) {
        val productIds: MutableList<String> = java.util.ArrayList()
        productIds.add("1_dollars")
        productIds.add("2_dollars")
        productIds.add("3_dollars")
        productIds.add("4_dollars")
        productIds.add("5_dollars")
        productIds.add("10_dollars")
        productIds.add("15_dollars")
        productIds.add("20_dollars")
        productIds.add("25_dollars")
        productIds.add("50_dollars")
        productIds.add("75_dollars")
        productIds.add("100_dollars")
        val getProductDetailsQuery = SkuDetailsParams
            .newBuilder()
            .setSkusList(productIds)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient?.querySkuDetailsAsync(
            getProductDetailsQuery,
            SkuDetailsResponseListener { billingResult, list ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                    list != null
                ) {
                    val itemInfo :SkuDetails
                    if(price <= 1.5)
                        itemInfo = list[productIds.indexOf("1_dollars")]
                    else if (price > 1.5 && price <= 2.5)
                        itemInfo = list[1]
                    else if (price > 2.5 && price <= 3.5)
                        itemInfo = list[2]
                    else if (price > 3.5 && price <= 4.5)
                        itemInfo = list[3]
                    else if (price > 4.5 && price <= 7.0)
                        itemInfo = list[4]
                    else if (price > 7.0 && price <= 12.5)
                        itemInfo = list[5]
                    else if (price > 12.5 && price <= 17.5)
                        itemInfo = list[6]
                    else if (price > 17.5 && price <= 22.5)
                        itemInfo = list[7]
                    else if (price > 22.5 && price <= 35.0)
                        itemInfo = list[8]
                    else if (price > 35.0 && price <= 65.0)
                        itemInfo = list[9]
                    else if (price > 65.0 && price <= 85.0)
                        itemInfo = list[10]
                    else if (price > 85.0)
                        itemInfo = list[11]
                    else
                        itemInfo = list[5]
                    button.setOnClickListener {
                        billingClient!!.launchBillingFlow(
                            activity,
                            BillingFlowParams.newBuilder().setSkuDetails(itemInfo).build()
                        )
                    }
                }
            }
        )
    }

}