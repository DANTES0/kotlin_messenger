package com.example.messenger

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatLogActivity : ComponentActivity() {
    companion object {
        val TAG = "Chat Log"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        findViewById<Button>(R.id.button_back_chat_log).setOnClickListener {
            finish()
        }

        val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
//        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
//        if (user != null){findViewById<TextView>(R.id.name_users_chat_log).setText("${user.username}")}
        findViewById<TextView>(R.id.name_users_chat_log).setText(username)

        setupDummyData()

        findViewById<Button>(R.id.button_send_chat_log).setOnClickListener {
            Log.d(TAG, "Attempt to send message.....")
        }
    }

    private fun setupDummyData(){
        val recyclerView_chat_log = findViewById<RecyclerView>(R.id.recyclerview_chat_log)
        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatFromItem("From message"))
        adapter.add(ChatToItem("To message\nTo message"))

        recyclerView_chat_log.adapter = adapter
    }
}

class ChatFromItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textview_to_row).text = text
    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textview_to_row).text = text

    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}