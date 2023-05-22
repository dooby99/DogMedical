package com.example.petmedical

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListActivity : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)



        val dogList = arrayListOf(
            List(R.drawable.before, "A1", "2023/05/07" ),
            List(R.drawable.dog2, "A2", "2023/05/01"),
            List(R.drawable.dog, "A3", "2023/05/01" ),
            List(R.drawable.dog2, "A4", "2023/04/01"),
            List(R.drawable.dog, "A5", "2023/04/01" ),
            List(R.drawable.dog2, "A6", "2023/04/01"),
            List(R.drawable.dog, "A3", "2023/04/01" ),
            List(R.drawable.dog2, "A4", "2023/04/01"),
            List(R.drawable.dog, "A5", "2023/04/01" ),
            List(R.drawable.dog2, "A6", "2023/04/01")
        )

        val rv_doglist: RecyclerView = findViewById(R.id.rv_doglist)
        rv_doglist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_doglist.setHasFixedSize(true)

        rv_doglist.adapter = DogAdapter(dogList)

    }
}