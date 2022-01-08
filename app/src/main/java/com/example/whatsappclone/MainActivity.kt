package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var viewPager:ViewPager2
    lateinit var tabs:TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager=findViewById(R.id.viewPager);
        tabs=findViewById(R.id.tabs)
viewPager.adapter=ScreenSliderAdapter(this)
        TabLayoutMediator(tabs,viewPager,TabLayoutMediator.TabConfigurationStrategy{tab:TabLayout.Tab,pos:Int ->
            when(pos){
                0->tab.setIcon(R.drawable.ic_baseline_photo_camera_24)
                1->tab.text="CHATS"
                2->tab.text="STATUS"
                3->tab.text="CALLS"

            }

        } ).attach()
        viewPager.setCurrentItem(1,true)


    }
}