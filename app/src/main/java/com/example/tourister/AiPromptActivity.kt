package com.example.tourister

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourister.ApiRequest.ChatMessageHistory
import com.example.tourister.ApiRequest.ConversationRequest
import com.example.tourister.ApiRequest.RetrofitInstance
import com.example.tourister.ChatSection.ChatAdapter
import com.example.tourister.Fragments.HomeFragment
import com.example.tourister.databinding.ActivityAiPromptBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AiPromptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiPromptBinding
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private val homeFragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAiPromptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val defaultColor = ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary)
        val dynamicColor = intent.getIntExtra("DYNAMIC_COLOR", defaultColor)
        applyThemeColor(dynamicColor)

        chatAdapter = ChatAdapter(chatMessages, dynamicColor)
        binding.chatRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerview.adapter = chatAdapter

        binding.sendButton.setOnClickListener {
            val prompt = binding.promptEditText.text.toString()
            if (prompt.isNotBlank()) {
                addUserMessage(prompt)
                generateAiResponseFromApi(prompt)
                binding.promptEditText.text?.clear()
            }
        }
    }

    private fun addUserMessage(message: String) {
        chatMessages.add(ChatMessage(message, MessageType.USER))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
    }

    // This helper function adds a new AI message and removes the typing indicator if present
    private fun addAiMessage(message: String) {
        removeLastMessage() // Remove the typing indicator first
        chatMessages.add(ChatMessage(message, MessageType.AI))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
    }

    // New helper function to just add a typing indicator
    private fun addTypingIndicator() {
        chatMessages.add(ChatMessage("", MessageType.TYPING))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
    }

    // New helper function to remove the last message (useful for removing the typing indicator)
    private fun removeLastMessage() {
        if (chatMessages.isNotEmpty()) {
            chatMessages.removeAt(chatMessages.size - 1)
            chatAdapter.notifyItemRemoved(chatMessages.size)
        }
    }

    private fun generateAiResponseFromApi(prompt: String) {
        addTypingIndicator()

        lifecycleScope.launch {
            try {
                val history = chatMessages
                    .filter { it.type != MessageType.TYPING && it.message.isNotBlank() }
                    .map { ChatMessageHistory(role = if (it.type == MessageType.USER) "user" else "model", text = it.message) }

                val request = ConversationRequest(history = history, prompt = prompt)
                val response = RetrofitInstance.api.generateSuggestion(request)

                // The typing indicator will be removed by addAiMessage

                if (response.error != null) {
                    addAiMessage(response.error)
                } else if (response.location != null) {
                    addAiMessage("Location: ${response.location}")
                    if (response.description != null) {
                        delay(3000)
                        addTypingIndicator()
                        delay(1000)
                        addAiMessage("Description: ${response.description}")
                    }
                    if (response.budget != null) {
                        delay(3000)
                        addTypingIndicator()
                        delay(1000)
                        addAiMessage("Budget: ${response.budget}")
                    }
                } else {
                    addAiMessage(response.description ?: "Sorry, I'm not sure what to say.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Remove the typing indicator before showing the error message
                removeLastMessage()
                val errorMessage = "Sorry, I couldn't connect. Please make sure the server is running."
                addAiMessage(errorMessage)
                Toast.makeText(this@AiPromptActivity, "Failed to get suggestion.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun applyThemeColor(color: Int) {
        binding.sendButton.imageTintList = ColorStateList.valueOf(color)
        binding.promptTextInputLayout.boxStrokeColor = color
    }
    private fun hideStatusBar() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideStatusBar()  // Re-apply immersive mode when focus returns
        }
    }
}