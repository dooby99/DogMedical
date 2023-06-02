package com.example.petmedical.guideline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.petmedical.R
import com.example.petmedical.databinding.FragmentSliderBinding

class SliderPagerFragment: Fragment() {

    lateinit var binding: FragmentSliderBinding

    private var position: Int = 0
    private var dataList : MutableList<SliderEntity> = mutableListOf()


    override fun onCreateView(inflater: LayoutInflater, container:
    ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSliderBinding.inflate(inflater, container, false)
        return binding?.root
//        return inflater.inflate(R.layout.item_viewpager, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this).load(dataList[position].imgSrc)  // 이미지
            .placeholder(R.drawable.ic_home)
            .error(R.drawable.ic_home)
            .into(binding.itemImg)


        binding.itemName.text = "${position+1}. ${dataList[position].imgName}"
    }


    fun setFragment(paramPosition: Int, paramList: MutableList<SliderEntity>) : Fragment {
        val fragment = SliderPagerFragment()

        fragment.position = paramPosition
        fragment.dataList = paramList
        return fragment
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

}