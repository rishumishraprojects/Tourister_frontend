package com.example.tourister.ApiRequest


import com.example.tourister.RecommendationSection.Recommendation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("generate-suggestion")
    suspend fun generateSuggestion(@Body request: ConversationRequest): TripSuggestion

    @POST("create-razorpay-order")
    suspend fun createOrder(@Body request: PaymentRequest): Response<OrderResponse>

    // ADD THIS NEW FUNCTION for the recommendations screen
   // @GET("recommendations")
    //suspend fun getRecommendations(): List<Recommendation>

    @GET("trending-recommendation")
    suspend fun getTrendingRecommendation(): Recommendation

}