package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.example.messenger.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Picasso

class ChatLogActivity : ComponentActivity() {
    companion object {
        val TAG = "Chat Log"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        findViewById<Button>(R.id.button_back_chat_log).setOnClickListener {
            finish()
        }
        findViewById<RecyclerView>(R.id.recyclerview_chat_log).adapter = adapter
//        val intent = Intent(this, User::class.java)
//        val username = intent.getStringExtra(NewMessageActivity.USER_KEY)

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        Log.d("REDDEAD", "What ${toUser?.username} ")


        findViewById<TextView>(R.id.name_users_chat_log).setText(toUser?.username)
//        findViewById<TextView>(R.id.name_users_chat_log).setText(username.uid)

//        setupDummyData()
        listenForMessages()

        findViewById<Button>(R.id.button_send_chat_log).setOnClickListener {
            Log.d(TAG, "Attempt to send message.....")
            performSendMessage()
        }
    }

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage =snapshot.getValue(ChatMessage::class.java)
                if (chatMessage!=null){
                    Log.d(TAG, chatMessage.text)
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid)
                    {
                        val currentUser = LatestMessagesActivity.currentUser?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }



                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage(){
        val text = findViewById<EditText>(R.id.editText_chat_log).text.toString()
//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        if (fromId == null) return
        if (toId == null) return
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message ${reference.key}")
                findViewById<EditText>(R.id.editText_chat_log).text.clear()
                findViewById<RecyclerView>(R.id.recyclerview_chat_log).scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)
    }

//    private fun setupDummyData(){
//        val recyclerView_chat_log = findViewById<RecyclerView>(R.id.recyclerview_chat_log)
//        val adapter = GroupAdapter<GroupieViewHolder>()
//
//        adapter.add(ChatFromItem("From message"))
//        adapter.add(ChatToItem("To message\nTo message"))
//
//        recyclerView_chat_log.adapter = adapter
//    }
}

class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textview_from_row).text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_chat_from_row)
        Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textview_to_row).text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_chat_to_row)
        Picasso.get().load(uri).into(targetImageView)

    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}