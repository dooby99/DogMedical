package com.example.petmedical.guideline


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class SliderPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private lateinit var dataList: MutableList<SliderEntity>
    private var dataSize: Int = 0


    override fun getItemCount(): Int = dataSize


    override fun createFragment(position: Int): Fragment {
        return SliderPagerFragment().setFragment(position, dataList)
    }


    fun setSliderList(paramList: MutableList<SliderEntity>) {
        this.dataList = paramList
        this.dataSize = paramList.size
    }

}