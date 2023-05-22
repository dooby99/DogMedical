package com.example.petmedical

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.petmedical.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy{ ActivityResultBinding.inflate(layoutInflater)}

    private val home = View.OnClickListener {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private val list = View.OnClickListener {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.homeButton.setOnClickListener(home)

        binding.recentButton.setOnClickListener(list)
    }




    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}