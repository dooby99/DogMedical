package com.example.petmedical.image

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petmedical.ListActivity
import com.example.petmedical.MainActivity
import com.example.petmedical.R
import com.example.petmedical.data.AppDatabase
import com.example.petmedical.data.Image
import com.example.petmedical.data.ImageDao
import com.example.petmedical.databinding.ActivityResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.format.DateTimeFormatter

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageSegmentation: ImageSegmentation

    private lateinit var imageDao: ImageDao
    private lateinit var db: AppDatabase

    private var segmentedBitmapFilePath: String? = null // segmentedBitmapFilePath 변수 추가

    private val home = View.OnClickListener {
        val intent = Intent(this, MainActivity:: class.java)
        startActivity(intent)
        finishAffinity()
    }

    private val back = View.OnClickListener {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        imageDao = db.imageDao()
        imageSegmentation = ImageSegmentation(this)

        segmentedBitmapFilePath = intent.getStringExtra("segmented_bitmap_file")

        if (segmentedBitmapFilePath != null) {
            val segmentedBitmap = BitmapFactory.decodeFile(segmentedBitmapFilePath)
            binding.imageViewResult.setImageBitmap(segmentedBitmap)
        } else {
            Log.e("ResultActivity", "Failed to receive segmented bitmap file path.")
        }

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener {
            saveImageToDatabase()
            Toast.makeText(this, "리스트에 추가 되었습니다.", Toast.LENGTH_SHORT).show()

        }

        binding.homebutton.setOnClickListener(home)

        binding.buttonBack.setOnClickListener(back)
    }

    private fun saveImageToDatabase() {
        if (segmentedBitmapFilePath != null) {
            val segmentedBitmap = BitmapFactory.decodeFile(segmentedBitmapFilePath)
            binding.imageViewResult.setImageBitmap(segmentedBitmap)

            val byteArrayOutputStream = ByteArrayOutputStream()
            segmentedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val imageByteArray = byteArrayOutputStream.toByteArray()

            val currentDateTime = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

            val image = Image(
                image = imageByteArray,
                dateTime = currentDateTime
            )

            GlobalScope.launch(Dispatchers.IO) {
                imageDao = db.imageDao() // imageDao 초기화
                imageDao.insertImage(image)
            }
        } else {
            Log.e("ResultActivity", "분할된 비트맵 파일 경로를 받아오는 데 실패했습니다.")
        }
    }



}
