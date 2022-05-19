package com.example.directmessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.directmessage.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    var mBinding : ActivityProfileBinding? = null
    val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val friend = intent.getSerializableExtra("friend") as User

        binding.profileName.text = friend.name
        binding.btnChat.setOnClickListener {
            val intent = Intent(this, ChattingActivity::class.java)
            intent.putExtra("friend", friend)
            startActivity(intent)
        }
    }
}