package com.example.petmedical.guideline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.petmedical.MainActivity
import com.example.petmedical.databinding.ActivityGuideLineBinding
import com.example.petmedical.databinding.ActivityGuideLineBinding.inflate


class GuideLine : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy {
        inflate(layoutInflater) }

    private val back = View.OnClickListener{
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener(back)
        Log.i("AlbumActivity", "onCreate")
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}

