package com.example.directmessage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.fragment.app.Fragment as Fragment

class FriendAddFragment : Fragment(){
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_add, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        sharedPreferences = (activity as MainActivity).getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        view.findViewById<TextView>(R.id.logout).setOnClickListener {
            auth.signOut()
            editor.putString("user_uid", "")
            editor.commit()
            startActivity(Intent(context, SplashActivity::class.java))
        }


        view.findViewById<TextView>(R.id.add_friend).setOnClickListener {
            // 친구 추가 버튼 클릭 시 이벤트 처리
            // 친구를 users 라는 컬렉션에서 찾은 뒤 나의 필드값인 friendList 에 user 객체로 저장
            val friendId = view.findViewById<EditText>(R.id.friend_id).text.toString()

            // 친구의 정보를 찾아서 불러오는 과정
            db.collection("users")
                .whereEqualTo("id", friendId)
                .get()
                .addOnSuccessListener {
                    var id: String = ""
                    var name: String = ""
                    var age: Int = 0
                    var uid: String = ""
                    for (field in it) {
                        id = field["id"].toString()
                        name = field["name"].toString()
                        age = field["age"].toString().toInt()
                        uid = field["uid"].toString()
                    }

                    val user = User(id, name, age, uid)

                    // 친구의 정보를 유저마다 각각의 컬렉션을 만들어서 그곳에 저장
                    db.collection(auth.currentUser!!.uid)
                        .document()
                        .set(user)
                    Toast.makeText(context, "${user.name}님을 친구추가 했습니다.", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {
                    Toast.makeText(context, "잘못된 아이디 입니다.", Toast.LENGTH_SHORT).show()
                }

        }

    }
}