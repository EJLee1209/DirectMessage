package com.example.directmessage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.directmessage.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    var mBinding : ActivityLoginBinding? = null
    val binding get() = mBinding!!
    lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnLogin.setOnClickListener {
            val id = binding.inputId.text.toString()
            val pw = binding.inputPw.text.toString()

            auth.signInWithEmailAndPassword(id,pw)
                .addOnFailureListener {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("user_uid", it.user!!.uid)
                    editor.commit()

                    startActivity(Intent(this, MainActivity::class.java))
                }
        }
    }
}