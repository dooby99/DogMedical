package com.example.petmedical.image

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.petmedical.databinding.ActivityAlbumBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AlbumActivity : AppCompatActivity(), View.OnClickListener {

    private var selectedBitmap: Bitmap? = null
    private lateinit var binding: ActivityAlbumBinding
    private val imageSegmentation: ImageSegmentation by lazy {
        ImageSegmentation(this)
    }

    private val home = View.OnClickListener {
        finish()
    }

    private val requestPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
        if (result != null) {
            if (result is Uri) {
                Log.d("AlbumActivity", "Selected image is Uri: $result")
                try {
                    val inputStream = contentResolver.openInputStream(result)
                    selectedBitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                } catch (e: IOException) {
                    Log.e("AlbumActivity", "Failed to decode Uri image: ${e.message}")
                }
            }

            if (selectedBitmap != null) {
                binding.imageViewPhoto.setImageBitmap(selectedBitmap)
            } else {
                Log.d("AlbumActivity", "Failed to decode selected image.")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPhoto.setOnClickListener {
            requestPhoto.launch("image/*")
        }

        binding.buttonComfirm.setOnClickListener {
            selectedBitmap?.let { bitmap ->
                //아래 코드 사용시 segment이미지 확인
                val segmentedBitmap = imageSegmentation.segmentImage(bitmap)

                //아래 코드 사용시 인풋이미지 확인
//                val segmentedBitmap = imageSegmentation.preprocessImage(bitmap)


//                // Save segmented bitmap to a file
                val cacheDir = cacheDir
                val segmentedBitmapFile = File(cacheDir, "segmented_bitmap.jpg")
                try {
                    val outputStream = FileOutputStream(segmentedBitmapFile)
                    segmentedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()
                } catch (e: IOException) {
                    Log.e("AlbumActivity", "Failed to save segmented bitmap: ${e.message}")
                }

                // Start ResultActivity and pass the file location
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("segmented_bitmap_file", segmentedBitmapFile.absolutePath)
                startActivity(intent)
            }
        }
        binding.buttonCancel.setOnClickListener(home)
        Log.i("onCreate", "Home")
    }

    override fun onClick(v: View?) {
        // Handle other clicks
    }
}
