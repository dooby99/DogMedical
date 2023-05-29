package com.example.petmedical.image

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ImageSegmentation(context: Context) {
    private val modelPath = "model.tflite"
    private val inputShape = intArrayOf(1, 3, 480, 320)
    private lateinit var interpreter: Interpreter

    init {
        loadModel(context)
    }

    private fun loadModel(context: Context) {
        val modelFile = loadModelFile(context, modelPath)
        val options = Interpreter.Options()
        interpreter = Interpreter(modelFile, options)
    }

    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd(modelPath)
        val fileDescriptor = assetFileDescriptor.fileDescriptor
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        val fileChannel = FileInputStream(fileDescriptor).channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun preprocessImage(imageBitmap: Bitmap): Bitmap {
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, inputShape[3], inputShape[2], true)
        val matrix = Matrix().apply { setRotate(90f) } // 이미지 회전 (필요한 경우)
        val rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.width, resizedBitmap.height, matrix, true)
        return rotatedBitmap
    }

    private fun runInference(inputBitmap: Bitmap): Bitmap {
        val inputArray = Array(1) { Array(inputShape[1]) { Array(inputShape[2]) { FloatArray(inputShape[3]) } } }
        for (y in 0 until inputShape[2]) {
            for (x in 0 until inputShape[3]) {
                if (x < inputBitmap.width && y < inputBitmap.height) {
                    val pixel = inputBitmap.getPixel(x, y)
                    inputArray[0][0][y][x] = (pixel and 0xFF) / 255.0f
                    inputArray[0][1][y][x] = (pixel shr 8 and 0xFF) / 255.0f
                    inputArray[0][2][y][x] = (pixel shr 16 and 0xFF) / 255.0f
                }
            }
        }

        val outputArray = Array(1) { Array(inputShape[2]) { Array(inputShape[3]) { FloatArray(1) } } }
        interpreter.run(inputArray, outputArray)

        val outputBitmap = Bitmap.createBitmap(inputShape[3], inputShape[2], Bitmap.Config.ARGB_8888)
        for (y in 0 until inputShape[2]) {
            for (x in 0 until inputShape[3]) {
                val label = if (outputArray[0][y][x][0] > 0.5) 1 else 0 // Adjust threshold as per your requirements
                outputBitmap.setPixel(x, y, if (label == 1) 0xFFFFFFFF.toInt() else 0xFF000000.toInt())
            }
        }

        return outputBitmap
    }



    fun segmentImage(imageBitmap: Bitmap): Bitmap {
        val inputBitmap = preprocessImage(imageBitmap)
        return runInference(inputBitmap)
    }
}
