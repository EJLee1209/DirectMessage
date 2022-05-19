package com.example.directmessage

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.directmessage.databinding.ActivityChattingBinding
import com.example.directmessage.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp

class ChattingActivity : AppCompatActivity() {
    var mBinding : ActivityChattingBinding? = null
    val binding get() = mBinding!!
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    var userName = ""
    val chatList = mutableListOf<Chat>()
    lateinit var  friend : User
    lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val intent = intent
        friend = intent.getSerializableExtra("friend") as User // 상대방의 정보를 가져옴

        // 현재 사용자의 이름 가져오기
        db.collection("users")
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                it.forEach {
                    userName = it["name"].toString()
                }
            }

        binding.friendName.text = friend.name // 채팅방 이름 설정

        binding.sendMessage.setOnClickListener { // 메세지 보내기 버튼 클릭 시
            val content = binding.messageText.text.toString()
            val chat = Chat(content, userName,0)
            db.collection("chat")
                .document(auth.currentUser!!.uid)
                .collection(friend.UID)
                .document(com.google.firebase.Timestamp.now().toString())
                .set(chat)
            binding.messageText.setText("")
            getChatList()
        }

        // 채팅 리스트 가져오기 + 리사이클러 뷰 어답터 적용
        getChatList()
    }

    fun getChatList(){
        chatList.clear()
        db.collection("chat")
            .document(auth.currentUser!!.uid)
            .collection(friend.UID)
            .get()
            .addOnSuccessListener {
                it.forEach {
                    val content = it["content"].toString()
                    val name = it["name"].toString()
                    val chat = Chat(content, name,0) // multiType:0 -> 내가 보낸 메세지

                    chatList.add(chat)
                }
                // 어답터 적용
                adapter = ChatAdapter(chatList, LayoutInflater.from(this), this)
                binding.chatRecyclerview.adapter = adapter

            }
    }
}



class Chat(
    val content: String,
    val name: String,
    val multiType: Int
)

class ChatAdapter(
    val chatList: MutableList<Chat>,
    val inflater: LayoutInflater,
    val context: Context
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun getItemViewType(position: Int): Int {
        return chatList[position].multiType

    }

    inner class myChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val myMsg : TextView
        init {
            myMsg = itemView.findViewById(R.id.chat_msg_me)
        }

    }

    inner class friendChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val friendMsg : TextView
        val name: TextView
        init {
            friendMsg = itemView.findViewById(R.id.chat_msg_friend)
            name = itemView.findViewById(R.id.chat_friend_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0->{ // 나의 메세지
                myChatViewHolder(inflater.inflate(R.layout.chat_content_me, parent, false))
            }
            else->{ // 친구의 메세지
                myChatViewHolder(inflater.inflate(R.layout.chat_content_friend, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = chatList[position]
        when(chat.multiType){
            0->{ // 나의 메세지
                (holder as myChatViewHolder).myMsg.text = chat.content

            }
            else->{ // 친구의 메세지
                (holder as friendChatViewHolder).friendMsg.text = chat.content
                (holder as friendChatViewHolder).name.text = chat.name

            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}