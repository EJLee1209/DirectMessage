package com.example.directmessage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.directmessage.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class RegisterActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    var mBinding: ActivityRegisterBinding? = null
    val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            val id = binding.registerId.text.toString()
            val pw = binding.registerPw.text.toString()
            val name = binding.registerName.text.toString()
            val age = binding.registerAge.text.toString()

            auth.createUserWithEmailAndPassword(id,pw)
                .addOnSuccessListener {
                    var friendList = mutableListOf<User>()
                    db.collection("users")
                        .document(it.user!!.uid)
                        .set(User(id,name,age.toInt(),it.user!!.uid))
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
        }

    }

}

class User(
    var id: String,
    var name: String,
    var age: Int,
    val UID: String,
): Serializable
