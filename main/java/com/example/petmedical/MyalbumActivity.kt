package com.example.petmedical

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.petmedical.databinding.ActivityMyalbumBinding

class MyalbumActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy {
        ActivityMyalbumBinding.inflate(layoutInflater) }

    private val requestPhoto =
        registerForActivityResult(ActivityResultContracts.GetContent()){
            binding.imageView.setImageURI(it)
        }
    private val conti = View.OnClickListener{
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    private val back = View.OnClickListener{
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageBitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
        binding.imageView.setImageBitmap(imageBitmap)
        val layoutParams = binding.imageView.layoutParams as ViewGroup.MarginLayoutParams
        binding.imageView.layoutParams = layoutParams


        if (imageBitmap != null) {
            binding.imageView.setImageBitmap(imageBitmap)
        }

        binding.collectImage.setOnClickListener{
            requestPhoto.launch("image/*")
        }

        binding.button4.setOnClickListener(conti)


        binding.backbutton.setOnClickListener(back)
        Log.i("AlbumActivity", "onCreate")
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}