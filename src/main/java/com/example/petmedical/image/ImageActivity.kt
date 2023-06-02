//package com.example.petmedical.image
//
//import android.content.ContentValues.TAG
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Color
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
//import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
//import com.google.firebase.ml.custom.*
//import java.io.IOException
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//
//class ImageActivity {
//
//    val remoteModel = FirebaseCustomRemoteModel.Builder("model.tflite").build()
//
//    val conditions = FirebaseModelDownloadConditions.Builder()
//        .requireWifi()
//        .build()
//
//    fun download() {
//        FirebaseModelManager.getInstance().download(remoteModel, conditions)
//            .addOnCompleteListener {
//                // Download complete. Depending on your app, you could enable the ML
//                // feature, or switch from the local model to the remote model, etc.
//            }
//    }
//
//    val localModel = FirebaseCustomLocalModel.Builder()
//        .setAssetFilePath("model.tflite")
//        .build()
//
//    val options = FirebaseModelInterpreterOptions.Builder(localModel).build()
//    val interpreter = FirebaseModelInterpreter.getInstance(options)
//
//    fun isModelDownloaded() {
//        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
//            .addOnSuccessListener { isDownloaded ->
//                val options =
//                    if (isDownloaded) {
//                        FirebaseModelInterpreterOptions.Builder(remoteModel).build()
//                    } else {
//                        FirebaseModelInterpreterOptions.Builder(localModel).build()
//                    }
//                val interpreter = FirebaseModelInterpreter.getInstance(options)
//            }
//    }
//
//    fun complete() {
//        FirebaseModelManager.getInstance().download(remoteModel, conditions)
//            .addOnCompleteListener {
//                // Download complete. Depending on your app, you could enable the ML
//                // feature, or switch from the local model to the remote model, etc.
//            }
//    }
//
//    val inputOutputOptions = FirebaseModelInputOutputOptions.Builder()
//        .setInputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 3, 480, 320))
//        .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 3, 480, 320))
//        .build()
//
//    fun segment(image: Bitmap, imageRotation: Int, inputWidth: Int, inputHeight: Int){
//
//    }
//
//
//    fun scalesImage(yourInputImage: Bitmap) {
//        val bitmap = Bitmap.createScaledBitmap(yourInputImage, 320, 480, true)
//        val input = Array(1) { Array(3) { Array(480) { FloatArray(320) } } }
//
//        for (y in 0 until 480) {
//            for (x in 0 until 320) {
//                val px = bitmap.getPixel(x, y)
//
//                // Get channel values from the pixel value.
//                val r = Color.red(px)
//                val g = Color.green(px)
//                val b = Color.blue(px)
//
//                // Normalize channel values to [-1.0, 1.0].
//                val rf = (r - 127) / 255.0f
//                val gf = (g - 127) / 255.0f
//                val bf = (b - 127) / 255.0f
//
//                input[0][0][y][x] = rf
//                input[0][1][y][x] = gf
//                input[0][2][y][x] = bf
//            }
//        }
//        val bufferSize = input.size
//        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
////        interpreter?.run(input, modelOutput)
//    }
////    private fun loadModelFromAsset(context: Context, modelFileName: String): String? {
////        return try {
////            val assetManager = context.assets
////            val inputStream = assetManager.open(modelFileName)
////            val byteArray = ByteArray(inputStream.available())
////            inputStream.read(byteArray)
////            byteArray.inputStream().bufferedReader().use { it.readText() }
////        } catch (e: IOException) {
////            Log.e(TAG, "Failed to load model from asset: $modelFileName", e)
////            null
////        }
////    }
//
//}