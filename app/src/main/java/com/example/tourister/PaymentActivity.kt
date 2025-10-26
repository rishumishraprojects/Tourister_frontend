package com.example.tourister

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.tourister.databinding.ActivityPaymentBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Checkout.preload(applicationContext)

        val amountInPaise = intent.getIntExtra("AMOUNT", 0)
        val orderId = intent.getStringExtra("ORDER_ID")

        if (orderId != null && amountInPaise > 0) {
            startRazorpayPayment(orderId, amountInPaise)
        } else {
            Toast.makeText(this, "Failed to get order details.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun startRazorpayPayment(orderId: String, amount: Int) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_RHvwk5Rw7jgqs1") // IMPORTANT: Replace with your actual Test Key ID

        try {
            val options = JSONObject()
            options.put("name", "Tourister")
            options.put("description", "Flight Booking")
            options.put("order_id", orderId)
            options.put("currency", "INR")
            options.put("amount", amount.toString())

            val prefill = JSONObject()
            prefill.put("email", "testuser@example.com")
            prefill.put("contact", "7905111269")
            options.put("prefill", prefill)

            checkout.open(this, options)

        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        Toast.makeText(this, "Payment successful! ID: $razorpayPaymentID", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onPaymentError(code: Int, response: String) {
        try {
            Toast.makeText(this, "Payment failed: $response", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("PaymentActivity", "Payment failed with code: $code")
        }
        finish()
    }
}