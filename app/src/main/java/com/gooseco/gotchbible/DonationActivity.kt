package com.gooseco.gotchbible

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gooseco.gotchbible.databinding.ActivityDonationBinding

class DonationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonationBinding

    private val paypalUrl = "https://www.paypal.com/qrcodes/managed/9cc8cb75-708c-4b7a-839c-64ac2bdcf223?utm_source=consweb_more"
    private val revolutUrl = "http://revolut.me/defaultplayer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updatePaymentMethod(false)

        binding.paymentMethodSwitch.setOnCheckedChangeListener { _, isChecked ->
            updatePaymentMethod(isChecked)
        }

        binding.paymentLinkButton.setOnClickListener {
            val url = if (binding.paymentMethodSwitch.isChecked) paypalUrl else revolutUrl
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    private fun updatePaymentMethod(isPayPal: Boolean) {
        if (isPayPal) {
            binding.qrCodeImage.setImageResource(R.drawable.pp_qr_code)
            binding.paymentLinkButton.text = "PayPal Link"
            binding.revolutLabel.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            binding.revolutLabel.setTypeface(null, android.graphics.Typeface.NORMAL)
            binding.paypalLabel.setTextColor(ContextCompat.getColor(this, R.color.orange_accent))
            binding.paypalLabel.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            binding.qrCodeImage.setImageResource(R.drawable.revolut_qr_code)
            binding.paymentLinkButton.text = "Revolut Link"
            binding.paypalLabel.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            binding.paypalLabel.setTypeface(null, android.graphics.Typeface.NORMAL)
            binding.revolutLabel.setTextColor(ContextCompat.getColor(this, R.color.orange_accent))
            binding.revolutLabel.setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }
}
