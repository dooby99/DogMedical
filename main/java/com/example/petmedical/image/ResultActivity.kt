package com.example.petmedical.image

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.petmedical.R

class ResultActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        imageView = findViewById(R.id.image_view_result)

        val bitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
        bitmap?.let {
            imageView.setImageBitmap(bitmap)
        }
    }
}
