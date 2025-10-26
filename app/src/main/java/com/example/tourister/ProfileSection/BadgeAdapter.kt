package com.example.tourister.ProfileSection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tourister.R

class BadgeAdapter(private val badges: List<Badge>) :
    RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val badgeIcon: ImageView = itemView.findViewById(R.id.badge_icon)
        val badgeName: TextView = itemView.findViewById(R.id.badge_name)
        val badgeDescription: TextView = itemView.findViewById(R.id.badge_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.badgeName.text = badge.name
        holder.badgeDescription.text = badge.description
        holder.badgeIcon.setImageResource(badge.iconRes)

        // Gray out locked badges
        val context = holder.itemView.context
        if (!badge.isUnlocked) {
            holder.badgeIcon.setColorFilter(
                ContextCompat.getColor(context, R.color.black),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            holder.badgeName.alpha = 0.5f
            holder.badgeDescription.alpha = 0.5f
        } else {
            holder.badgeIcon.clearColorFilter()
            holder.badgeName.alpha = 1f
            holder.badgeDescription.alpha = 1f
        }
    }

    override fun getItemCount(): Int = badges.size
}
