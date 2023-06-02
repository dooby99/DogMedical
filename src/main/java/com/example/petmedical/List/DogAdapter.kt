package com.example.petmedical.List

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petmedical.R

class DogAdapter(private val imageList: ArrayList<DogItem>) : RecyclerView.Adapter<DogAdapter.DogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_list_view, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val dogItem = imageList[position]

        holder.imageView.setImageBitmap(dogItem.image)
        holder.textViewName.text = dogItem.id
        holder.textViewDate.text = dogItem.dateTime
    }


    override fun getItemCount(): Int {
        return imageList.size
    }

    class DogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.dog_image)
        val textViewName: TextView = itemView.findViewById(R.id.dog_date)
        val textViewDate: TextView = itemView.findViewById(R.id.dog_date2)
    }
}