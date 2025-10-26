package com.example.tourister

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourister.ApiRequest.RetrofitInstance
import com.example.tourister.ApiRequest.PaymentRequest
import com.example.tourister.databinding.ActivitySearchResultsBinding
import kotlinx.coroutines.launch
import com.example.tourister.PaymentActivity
import com.example.tourister.ApiRequest.OrderResponse

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fromLocation = intent.getStringExtra("FROM_LOCATION") ?: "N/A"
        val toLocation = intent.getStringExtra("TO_LOCATION") ?: "N/A"
        val isOneWay = intent.getBooleanExtra("IS_ONE_WAY", false)
        val dynamicColor = intent.getIntExtra("DYNAMIC_COLOR", ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary))

        binding.flightsRecyclerView.layoutManager = LinearLayoutManager(this)

        val mockFlights = generateMockFlights(fromLocation, toLocation)

        val onBookClicked: (Flight) -> Unit = { flight ->
            val amount = flight.price.replace("Rs. ", "").replace(",", "").toInt()

            lifecycleScope.launch {
                try {
                    val orderRequest =
                        PaymentRequest(amount = amount * 100, receipt_id = flight.flightNumber)
                    val orderResponse = RetrofitInstance.api.createOrder(orderRequest)

                    val intent = Intent(this@SearchResultsActivity, PaymentActivity::class.java).apply {
                        putExtra("ORDER_ID", orderResponse.body()?.order_id)
                        putExtra("AMOUNT", orderResponse.body()?.amount)
                    }
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this@SearchResultsActivity, "Error creating order: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        val adapter = FlightResultsAdapter(mockFlights, dynamicColor, onBookClicked)
        binding.flightsRecyclerView.adapter = adapter
    }



    private fun generateMockFlights(from: String, to: String): List<Flight> {
        return listOf(
            Flight("IndiGo", "6E-234", from, to, "10:00", "12:30", "Rs. 4,500"),
            Flight("Air India", "AI-567", from, to, "11:15", "13:45", "Rs. 5,100"),
            Flight("Vistara", "UK-890", from, to, "14:00", "16:20", "Rs. 6,800"),
            Flight("Go First", "G8-112", from, to, "16:30", "19:00", "Rs. 4,200"),
            Flight("IndiGo", "6E-234", from, to, "10:00", "12:30", "Rs. 4,500"),
            Flight("Air India", "AI-567", from, to, "11:15", "13:45", "Rs. 5,100"),
            Flight("Vistara", "UK-890", from, to, "14:00", "16:20", "Rs. 6,800"),
            Flight("Go First", "G8-112", from, to, "16:30", "19:00", "Rs. 4,200"),
            Flight("IndiGo", "6E-555", from, to, "18:00", "20:30", "Rs. 4,800"),
            Flight("SpiceJet", "SG-456", from, to, "20:00", "22:15", "Rs. 3,900"),
            Flight("Air India", "AI-789", from, to, "08:30", "11:00", "Rs. 5,300"),
            Flight("Vistara", "UK-123", from, to, "12:45", "15:00", "Rs. 6,500"),
            Flight("Go First", "G8-345", from, to, "15:30", "17:45", "Rs. 4,100"),
            Flight("IndiGo", "6E-789", from, to, "19:10", "21:40", "Rs. 4,650"),
            Flight("SpiceJet", "SG-901", from, to, "21:30", "23:55", "Rs. 3,850")
        )
    }
}