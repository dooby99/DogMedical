package com.example.petmedical

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class DogAdapter (val dogList: ArrayList<List>) : RecyclerView.Adapter<DogAdapter.CustomViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_list_view,parent,false)
        return CustomViewHolder(view)

    }

    override fun onBindViewHolder(holder: DogAdapter.CustomViewHolder, position: Int) {
        holder.gender.setImageResource(dogList.get(position).gender)
        holder.name.text = dogList.get(position).name
        holder.date.text = dogList.get(position).date

    }

    override fun getItemCount(): Int {
        return dogList.size
    }


    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gender = itemView.findViewById<ImageView>(R.id.dog_image) // 이미지
        val name = itemView.findViewById<TextView>(R.id.dog_date) // 병명
        val date = itemView.findViewById<TextView>(R.id.dog_date2) //날짜

    }

}