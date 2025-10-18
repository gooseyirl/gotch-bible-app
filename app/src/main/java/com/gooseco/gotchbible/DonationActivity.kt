package com.gooseco.gotchbible

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class DonationActivity : AppCompatActivity() {

    private lateinit var paypalLinkButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)

        paypalLinkButton = findViewById(R.id.paypalLinkButton)

        paypalLinkButton.setOnClickListener {
            val url = "https://www.paypal.com/qrcodes/managed/9cc8cb75-708c-4b7a-839c-64ac2bdcf223?utm_source=consweb_more"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}
