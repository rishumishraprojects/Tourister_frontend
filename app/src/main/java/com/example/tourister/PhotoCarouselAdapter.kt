package com.example.tourister

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tourister.databinding.ItemPhotoCarouselBinding
import coil.load // <-- Add this import statement

class PhotoCarouselAdapter(private val photos: List<Photo>) :
    RecyclerView.Adapter<PhotoCarouselAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoCarouselBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        holder.bind(photo)
    }

    override fun getItemCount(): Int = photos.size

    class PhotoViewHolder(private val binding: ItemPhotoCarouselBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.carouselImageView.load(photo.imageUrl) {
                crossfade(true)
                // placeholder(R.drawable.placeholder_image) // Make sure this resource exists
            }
        }
    }
}