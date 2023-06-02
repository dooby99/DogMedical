package com.example.petmedical.guideline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.petmedical.MainActivity
import com.example.petmedical.R
import com.example.petmedical.databinding.ActivitySliderMainBinding

class SliderMainActivity : AppCompatActivity(),View.OnClickListener {


    lateinit var binding: ActivitySliderMainBinding
    private lateinit var mSliderPagerAdapter: SliderPagerAdapter

    private var sliderList: MutableList<SliderEntity> = mutableListOf()


    private val back = View.OnClickListener{
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    /* viewPagerVertical 에 사용돠는 handler, runnable */
    private val vHandler = Handler()
    private val vRunnable =
        Runnable {
            binding.viewPagerVertical.currentItem =
                binding.viewPagerVertical.currentItem + 1
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySliderMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        binding.btnClose.setOnClickListener (back)
        Log.i("onCreate", "go Main")
    }


    private fun initView() {
        // 리스트 데이터 세팅
        sliderList.add(0, SliderEntity(R.drawable.guideline1, "guideline1"))
        sliderList.add(1, SliderEntity(R.drawable.guideline2, "guideline2"))
        sliderList.add(2, SliderEntity(R.drawable.guideline3, "guideline3"))
        sliderList.add(3, SliderEntity(R.drawable.guideline4, "guideline4"))

        initViewForVertical()
    }



    /**
     * initViewForVertical()
     * FragmentStateAdapter 를 사용한 ViewPager2 구현
     */
    private fun initViewForVertical() {
        if(sliderList.size > 0) {
            binding.viewPagerVertical.isVisible = true
            binding.tvEmptyVertical.isGone = true

            mSliderPagerAdapter = SliderPagerAdapter(this)

            binding.viewPagerVertical.adapter = mSliderPagerAdapter
            binding.viewPagerVertical.orientation = ViewPager2.ORIENTATION_VERTICAL
            mSliderPagerAdapter.setSliderList(sliderList)

            binding.viewPagerVertical.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    vHandler.removeCallbacks(vRunnable)
                    vHandler.postDelayed(vRunnable, 2000) // Slide duration 2 seconds
                }
            })

        } else {  // sliderList 가 없는 경우 viewPager2 hidden 처리
            binding.viewPagerVertical.isInvisible = true
            binding.tvEmptyVertical.isVisible = true
        }
    }


    override fun onResume() {
        super.onResume()

        vHandler.postDelayed(vRunnable, 2000)  // viewPagerVertical 2초마다 Slide
    }


    override fun onPause() {
        super.onPause()

        vHandler.removeCallbacks(vRunnable)  // viewPagerVertical 2초마다 Slide
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }


}