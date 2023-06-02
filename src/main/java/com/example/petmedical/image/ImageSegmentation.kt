package com.example.petmedical.image

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import androidx.core.graphics.toColor
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ImageSegmentation(context: Context) {
    private val modelPath = "model36.tflite"
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

    fun preprocessImage(imageBitmap: Bitmap): Bitmap {
        val resizedBitmap =
            Bitmap.createScaledBitmap(imageBitmap, inputShape[2], inputShape[3], true)
        Log.d("inputShape_width", inputShape[2].toString())
        Log.d("inputShape_height", inputShape[3].toString())
        val matrix = Matrix().apply { setRotate(90f) }
        return Bitmap.createBitmap(
            resizedBitmap,
            0,
            0,
            resizedBitmap.width,
            resizedBitmap.height,
            matrix,
            true
        )
    }

    private fun runInference(inputBitmap: Bitmap): Bitmap {
        val inputArray = Array(1) { Array(3) { Array(480) { FloatArray(320) } } }

        for (i in 0 until inputArray.size) {
            for (j in 0 until inputArray[i].size) {
                for (y in 0 until inputArray[i][j].size) {
                    for (x in 0 until inputArray[i][j][y].size) {
                        if (x < inputArray[i][j][y].size && y < inputArray[i][j][y].size) {
                            val pixel = inputBitmap.getPixel(x, y)
                            inputArray[i][j][y][x] = (pixel and 0xFF) / 255.0f
                        }
                    }
                }
            }
        }

        val outputArray = Array(1) { Array(3) { Array(inputArray[0][0].size) { FloatArray(inputArray[0][0][0].size) } } }
        interpreter.run(inputArray, outputArray)

        val outputBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)

//        var maxVal: Float = outputArray[0][0][0][0]
//        var minVal: Float = outputArray[0][0][0][0]

        for (x in 0 until outputArray[0][0].size) {
            for (y in 0 until outputArray[0][0][x].size) {
//                val label = if (outputArray[0][0][y][x] > 0.5) 1 else 0
//                val pixelValue = (((label / 2 + 3) * 255) and 0xFF).toInt()
//                Log.d("pixelValue", "${pixelValue.toString()}")
                val label = outputArray[0][0][x][y]
                var pixelValue = ((label / 2.0f + 2.5f) * 255.0f)
                pixelValue = if (pixelValue < 0F) 0F else pixelValue

//                Log.d("pixelValue", "${outputArray[0][0][x][y].toString()}")
//                minVal = if (minVal < label) minVal else label
//                maxVal = if (maxVal > label) maxVal else label

//                val color = when {
//                    pixelValue < (-5.53) -> Color.RED
//                    pixelValue < (-5.40) -> Color.GREEN
//                    else -> Color.BLUE
//                }

                outputBitmap.setPixel(y, x, Color.rgb(pixelValue, pixelValue, pixelValue))
//                outputBitmap.setPixel(y, x, color)

            }
        }

        return outputBitmap
    }

//    fun segmentImage(imageBitmap: Bitmap): Bitmap {
//        val inputBitmap = preprocessImage(imageBitmap)
//        return runInference(inputBitmap)
//    }

    fun segmentImage(imageBitmap: Bitmap): Bitmap {
        val inputBitmap = preprocessImage(imageBitmap)
        val outputBitmap = runInference(inputBitmap)

        val combinedBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)
        canvas.drawBitmap(inputBitmap, 0f, 0f, null)

        val paint = Paint()
        paint.alpha = 80 // 50% 투명도 (0-255 범위의 값을 사용)

        canvas.drawBitmap(outputBitmap, 0f, 0f, paint)

        return combinedBitmap
    }



}
