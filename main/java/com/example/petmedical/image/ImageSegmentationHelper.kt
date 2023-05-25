package com.example.petmedical.image

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import org.tensorflow.lite.DataType
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.segmenter.ImageSegmenter
import org.tensorflow.lite.task.vision.segmenter.OutputType
import org.tensorflow.lite.task.vision.segmenter.Segmentation
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class ImageSegmentationHelper(
    var numThreads: Int = 2,
    var currentDelegate: Int = 0,
    val context: Context,
    val imageSegmentationListener: SegmentationListener?
) {
    private var imageSegmenter: ImageSegmenter? = null

    init {
        setupImageSegmenter()
    }

    // ImageSegmenter 초기화
    private fun setupImageSegmenter() {
        // 사용할 스레드 수를 포함한 일반적인 이미지 분할 옵션 설정
        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        // 지정된 하드웨어를 사용하여 모델 실행. 기본값은 CPU
        when (currentDelegate) {
            DELEGATE_CPU -> {
                // 기본값
            }
            DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {
                    imageSegmentationListener?.onError("이 기기에서는 GPU를 지원하지 않습니다.")
                }
            }
            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        val baseOptions = baseOptionsBuilder.build()

        // 분할을 위한 기본 옵션 생성
        val optionsBuilder =
            ImageSegmenter.ImageSegmenterOptions.builder()
                .setBaseOptions(baseOptions)
                .setOutputType(OutputType.CATEGORY_MASK)

        try {
            // ImageSegmenter 생성
            imageSegmenter =
                ImageSegmenter.createFromFileAndOptions(
                    context,
                    loadModelFromAsset(context, MODEL_Unet)!!.toString(),
                    optionsBuilder.build()
                )
        } catch (e: IllegalStateException) {
            imageSegmentationListener?.onError(
                "이미지 분할을 초기화하는 데 실패했습니다. 자세한 내용은 오류 로그를 참조하세요."
            )
            Log.e(TAG, "TFLite가 모델을 로드하지 못했습니다. 오류: " + e.message)
        }
    }

    // ImageSegmenter 초기화
    fun clearImageSegmenter() {
        imageSegmenter = null
    }

    // 이미지 분할 수행
    @RequiresApi(Build.VERSION_CODES.Q)
    fun segment(image: Bitmap, imageRotation: Int, inputHeight: Int, inputWidth: Int) {
        if (imageSegmenter == null) {
            setupImageSegmenter()
        }

        // 추론 시간은 프로세스 시작과 종료 시의 시스템 시간 차이입니다.
        var inferenceTime = SystemClock.uptimeMillis()

        // 이미지 전처리를 위한 ImageProcessor 생성
        val imageProcessor =
            ImageProcessor.Builder()
                .add(Rot90Op(-imageRotation / 90))
                .build()

        // TensorBuffer 생성
        val tensorBuffer = TensorBuffer.createFixedSize(
            intArrayOf(1, inputHeight, inputWidth, 3),
            DataType.FLOAT32
        )
        loadModelFromAsset(context, MODEL_Unet)?.let { tensorBuffer.loadBuffer(it) }

        val tensorImage = TensorImage(DataType.FLOAT32)

        // 이미지 분할 수행
        val segmentResult = imageSegmenter?.segment(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        // 결과를 리스너에 전달
        imageSegmentationListener?.onResults(
            segmentResult,
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )
    }

    // 이미지 분할 결과 및 오류 처리를 위한 리스너 인터페이스
    interface SegmentationListener {
        fun onError(error: String)
        fun onResults(
            results: List<Segmentation>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2
        const val MODEL_Unet = "model.tflite"

        private const val TAG = "Image Segmentation Helper"

        // assets에서 모델을 로드하는 함수
        private fun loadModelFromAsset(context: Context, modelFileName: String): ByteBuffer? {
            try {
                val assetManager = context.assets
                val inputStream = assetManager.open(modelFileName)
                val modelBytes = ByteArray(inputStream.available())
                inputStream.read(modelBytes)
                return ByteBuffer.wrap(modelBytes)
            } catch (e: IOException) {
                Log.e(TAG, "Failed to load model from asset: $modelFileName", e)
            }
            // 오류 발생 시 기본적으로 null을 반환하도록 설정
            return null
        }
    }
}
