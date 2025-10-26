package com.example.tourister.ChatSection

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.tourister.ChatMessage
import com.example.tourister.MessageType
import com.example.tourister.R

class ChatAdapter(
    private val chatMessages: MutableList<ChatMessage>,
    private val dynamicColor: Int
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (chatMessages[position].type) {
            MessageType.USER -> VIEW_TYPE_USER
            else -> VIEW_TYPE_AI
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = if (viewType == VIEW_TYPE_USER) R.layout.list_item_chat_user else R.layout.list_item_chat_ai
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = chatMessages[position]
        when (message.type) {
            MessageType.USER -> {
                holder.messageText?.text = message.message
                holder.messageText?.backgroundTintList = ColorStateList.valueOf(dynamicColor)
            }
            MessageType.AI -> {
                // When the message is ready, make the background opaque
                holder.bubbleContainer?.background?.alpha = 255
                holder.typingAnimation?.isVisible = false
                holder.messageText?.isVisible = true
                holder.messageText?.text = message.message
            }
            MessageType.TYPING -> {
                // When typing, make the background transparent
                holder.bubbleContainer?.background?.alpha = 0
                holder.messageText?.isVisible = false
                holder.typingAnimation?.isVisible = true
            }
        }
    }

    override fun getItemCount() = chatMessages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Add a reference to the bubble container
        val bubbleContainer: FrameLayout? = itemView.findViewById(R.id.bubble_container)
        val messageText: TextView? = itemView.findViewById(R.id.chat_message_text)
        val typingAnimation: LottieAnimationView? = itemView.findViewById(R.id.typing_animation_view)
    }
}