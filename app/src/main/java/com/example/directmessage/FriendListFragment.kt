package com.example.directmessage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FriendListFragment : Fragment() {
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var friendRecyclerView: RecyclerView
    var friendList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        friendRecyclerView = view.findViewById<RecyclerView>(R.id.friend_list_recyclerView)


        getFriendList()

    }
    fun getFriendList(){
        // 친구목록을 불러와서 리사이클러뷰의 어답터를 연결
        db.collection(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {

                it.forEach {
                    val friendId = it["id"].toString()
                    val friendName = it["name"].toString()
                    val friendAge = it["age"].toString().toInt()
                    val friendUid = it["uid"].toString()
                    val friend = User(friendId,friendName,friendAge,friendUid)
                    friendList.add(friend)
                }

                friendRecyclerView.adapter = FriendRecyclerViewAdapter(
                    friendList,
                    LayoutInflater.from(context),
                    context as MainActivity
                )
            }
    }
}

class FriendRecyclerViewAdapter(
    val friendList: MutableList<User>,
    val inflater: LayoutInflater,
    val context: Context
): RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView

        init {
            name = itemView.findViewById(R.id.friend_name)

            itemView.setOnClickListener {
                val friend = friendList[adapterPosition]
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("friend", friend)
                context.startActivity(intent)
            }
        }



    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendRecyclerViewAdapter.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.friends_list, parent, false))
    }

    override fun onBindViewHolder(holder: FriendRecyclerViewAdapter.ViewHolder, position: Int) {
        val user = friendList[position]
        holder.name.text = user.name
    }

    override fun getItemCount(): Int {
        return friendList.size
    }
}