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

        // AlbumActivity에서 전달된 이미지를 가져옴
        val bitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")

        // 가져온 이미지를 ImageView에 설정
        imageView.setImageBitmap(bitmap)

        // 분할된 결과 이미지를 가져올 수 있는 코드 작성
        // 이 코드는 ImageSegmentationHelper의 결과에 따라 다를 수 있으므로
        // ImageSegmentationHelper의 사용 방법에 따라 결과 이미지를 가져와야 함

        // 분할 결과 이미지를 ImageView에 설정
        // imageView.setImageBitmap(resultBitmap)
    }
}
