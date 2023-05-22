package com.example.petmedical

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.petmedical.databinding.ActivityMainBinding
import com.example.petmedical.databinding.ActivityMapBinding
import com.example.petmedical.databinding.ActivityMyalbumBinding
import com.example.petmedical.databinding.ActivityResultBinding
import com.example.petmedical.map.ConvertActivity
import net.daum.android.map.MapActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }


    private val MyAlbum = View.OnClickListener {
        val intent = Intent(this, MyalbumActivity:: class.java)
        startActivity(intent)
    }

    private val result = View.OnClickListener{
        val intent = Intent(this, ListActivity:: class.java)
        startActivity(intent)
    }

    private val testmap = View.OnClickListener {
        val intent = Intent(this, ConvertActivity::class.java)
        startActivity(intent)
    }

    private val finish = View.OnClickListener{
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.camerabutton.setOnClickListener {
            val cameraPermissionCheck = ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.CAMERA
            )
            if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    1000
                )
            } else {
                val REQUEST_IMAGE_CAPTURE = 1
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }

        binding.albumbutton.setOnClickListener(MyAlbum)
        Log.i("MainActivity", "onCreate")

        binding.recentbutton.setOnClickListener(result)
        Log.i("MainActivity", "onCreate")

        binding.testbutton.setOnClickListener(testmap)
        Log.i("MainActivity", "onCreate")

        binding.finishbutton.setOnClickListener(finish)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) { //거부
                Toast.makeText(this@MainActivity, "카메라 권환을 거부했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, "Title", "Description")
            Toast.makeText(this, "사진이 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()

            // MyalbumActivity 로 전달할 intent 생성
            val intent = Intent(this, MyalbumActivity:: class.java)
            intent.putExtra("imageBitmap", imageBitmap)
            startActivity(intent)
        }
    }

    override fun onClick(v: View?) {

    }
}