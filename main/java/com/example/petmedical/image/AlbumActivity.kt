package com.example.petmedical.image

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.petmedical.R
import com.example.petmedical.databinding.ActivityAlbumBinding
import org.tensorflow.lite.task.vision.segmenter.Segmentation

class AlbumActivity : AppCompatActivity(), View.OnClickListener, ImageSegmentationHelper.SegmentationListener,
    OverlayView.OverlayViewListener {

    private lateinit var binding: ActivityAlbumBinding // lateinit으로 선언

    private lateinit var overlayView: OverlayView
    private lateinit var imageView: ImageView
    private lateinit var imageSegmentationHelper: ImageSegmentationHelper

    // 이미지 선택을 위한 ActivityResultLauncher
    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = decodeUriToBitmap(uri) // 새로운 함수로 URI를 비트맵으로 변환
            bitmap?.let {
                imageView.setImageBitmap(bitmap)

                val inputHeight = bitmap.height
                val inputWidth = bitmap.width
                imageSegmentationHelper.segment(bitmap, 1, inputHeight, inputWidth)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater) // inflate를 onCreate에서 수행
        setContentView(binding.root)

        overlayView = binding.overlayView
        imageView = binding.imageViewPhoto

        binding.buttonPhoto.setOnClickListener(this) // View.OnClickListener를 사용하여 클릭 리스너 설정

//        imageSegmentationHelper = ImageSegmentationHelper(
//            numThreads = 2,
//            context = this,
//            imageSegmentationListener = this,
//            currentDelegate = ImageSegmentationHelper.DELEGATE_CPU
//        )

        binding.buttonComfirm.setOnClickListener(this) // View.OnClickListener를 사용하여 클릭 리스너 설정

        overlayView.setOnOverlayViewListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonPhoto -> requestImage.launch("image/*")
            R.id.buttonComfirm -> {
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("imageBitmap", bitmap)
                startActivity(intent)
            }
        }
    }

    override fun onError(error: String) {
        // ImageSegmentationHelper의 오류 처리 코드를 작성하세요.
    }

    override fun onResults(
        results: List<Segmentation>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        overlayView.setResults(results, imageHeight, imageWidth)
    }

    override fun onLabels(colorLabels: List<OverlayView.ColorLabel>) {
        // 아직 구현되지 않음
    }

    private fun decodeUriToBitmap(uri: Uri): Bitmap? {
        return contentResolver.openFileDescriptor(uri, "r")?.use { fileDescriptor ->
            BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor)
        }
    }

}
