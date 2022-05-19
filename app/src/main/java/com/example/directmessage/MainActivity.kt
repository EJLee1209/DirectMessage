package com.example.directmessage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.directmessage.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    var mBinding: ActivityMainBinding? = null
    val binding get() = mBinding!!
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()


        binding.mainTab.addTab(binding.mainTab.newTab().setText("친구"))
        binding.mainTab.addTab(binding.mainTab.newTab().setText("친구 추가"))

        binding.mainPager.adapter = PagerAdapter(this, 2)
        binding.mainTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.mainPager.setCurrentItem(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }
}

class PagerAdapter(
    val fragmentActivity: FragmentActivity,
    val tabCount: Int
): FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
        return tabCount
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 ->{
                FriendListFragment()
            }
            else -> {
                FriendAddFragment()
            }
        }
    }
}