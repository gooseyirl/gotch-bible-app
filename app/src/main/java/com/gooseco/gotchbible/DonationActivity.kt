package com.gooseco.gotchbible

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class DonationActivity : AppCompatActivity() {

    private lateinit var paymentLinkButton: MaterialButton
    private lateinit var paymentMethodSwitch: SwitchMaterial
    private lateinit var qrCodeImage: ImageView
    private lateinit var paypalLabel: TextView
    private lateinit var revolutLabel: TextView

    private val paypalUrl = "https://www.paypal.com/qrcodes/managed/9cc8cb75-708c-4b7a-839c-64ac2bdcf223?utm_source=consweb_more"
    private val revolutUrl = "http://revolut.me/defaultplayer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)

        paymentLinkButton = findViewById(R.id.paymentLinkButton)
        paymentMethodSwitch = findViewById(R.id.paymentMethodSwitch)
        qrCodeImage = findViewById(R.id.qrCodeImage)
        paypalLabel = findViewById(R.id.paypalLabel)
        revolutLabel = findViewById(R.id.revolutLabel)

        // Set initial state to Revolut (unchecked)
        updatePaymentMethod(false)

        paymentMethodSwitch.setOnCheckedChangeListener { _, isChecked ->
            updatePaymentMethod(isChecked)
        }

        paymentLinkButton.setOnClickListener {
            val url = if (paymentMethodSwitch.isChecked) paypalUrl else revolutUrl
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun updatePaymentMethod(isPayPal: Boolean) {
        if (isPayPal) {
            // PayPal selected
            qrCodeImage.setImageResource(R.drawable.pp_qr_code)
            paymentLinkButton.text = "PayPal Link"
            revolutLabel.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            revolutLabel.setTypeface(null, android.graphics.Typeface.NORMAL)
            paypalLabel.setTextColor(ContextCompat.getColor(this, R.color.orange_accent))
            paypalLabel.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            // Revolut selected
            qrCodeImage.setImageResource(R.drawable.revolut_qr_code)
            paymentLinkButton.text = "Revolut Link"
            paypalLabel.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            paypalLabel.setTypeface(null, android.graphics.Typeface.NORMAL)
            revolutLabel.setTextColor(ContextCompat.getColor(this, R.color.orange_accent))
            revolutLabel.setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }
}
