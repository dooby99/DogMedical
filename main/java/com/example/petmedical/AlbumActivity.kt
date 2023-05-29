package com.example.petmedical.image

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.petmedical.databinding.ActivityAlbumBinding
import java.io.FileInputStream

class AlbumActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAlbumBinding // lateinit으로 선언
    private val imageSegmentation: ImageSegmentation by lazy {
        ImageSegmentation(this) // ImageSegmentation 인스턴스 생성
    }

    private val requestPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val segmentedBitmap = imageSegmentation.segmentImage(bitmap) // 이미지 세그멘테이션 수행

            binding.imageViewPhoto.setImageBitmap(segmentedBitmap)
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPhoto.setOnClickListener {
            requestPhoto.launch("image/*")
        }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }


}
