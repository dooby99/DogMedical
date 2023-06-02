package com.example.petmedical

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petmedical.data.AppDatabase
import com.example.petmedical.data.ImageDao
import com.example.petmedical.databinding.ActivityListBinding
import com.example.petmedical.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityListBinding.inflate(layoutInflater) }

    private lateinit var imageDao: ImageDao
    private lateinit var db: AppDatabase

    private val home = View.OnClickListener {
        finish()
    }

    private val reset = View.OnClickListener {
        // 데이터 초기화 로직을 추가합니다.
        GlobalScope.launch(Dispatchers.IO) {
            imageDao.deleteAllImages()
        }
        Toast.makeText(this, "데이터가 초기화되었습니다.", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        imageDao = db.imageDao()

        val dogList = ArrayList<DogItem>()

        GlobalScope.launch(Dispatchers.Main) {
            val imageList = withContext(Dispatchers.IO) { imageDao.getAllImages() }
            for (image in imageList) {
                val bitmap = BitmapFactory.decodeByteArray(image.image, 0, image.image.size)
                val dogItem = DogItem(bitmap, image.id.toString(), image.dateTime)
                dogList.add(dogItem)
            }

            val rv_doglist: RecyclerView = findViewById(R.id.rv_doglist)
            rv_doglist.layoutManager = LinearLayoutManager(this@ListActivity, LinearLayoutManager.VERTICAL, false)
            rv_doglist.setHasFixedSize(true)

            rv_doglist.adapter = DogAdapter(dogList)
        }

        binding.mainbutton.setOnClickListener(home)
        binding.buttonReset.setOnClickListener(reset)

    }
}