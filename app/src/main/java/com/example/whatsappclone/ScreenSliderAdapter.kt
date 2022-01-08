package com.example.whatsappclone

import android.os.Build.VERSION_CODES.O
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ScreenSliderAdapter(fa:FragmentActivity):FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
return 4
    }

    override fun createFragment(position: Int): Fragment=when(position) {
              O->CameraFragment()
              1 -> InboxFragment()
              2->StatusFragment()
              else->CallsFragment()
    }
}


