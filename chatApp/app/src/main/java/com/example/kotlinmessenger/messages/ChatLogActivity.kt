package com.example.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatFromItem
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.ChatToItem
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_newmessage.adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY);
        supportActionBar?.title = user?.username

        //setDummyData()
        listenForMessage()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message")
            performSendMessage()
        }
    }

    private fun listenForMessage() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if(chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    /** How to send message to Firebase */
    private fun performSendMessage() {
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)?.uid

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId!!, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message : ${reference.key}")
            }
    }

    private fun setDummyData() {
        val adapter = GroupAdapter<ViewHolder>()
//        adapter.add(ChatFromItem())
//        adapter.add(ChatToItem())
//        adapter.add(ChatFromItem())
//        adapter.add(ChatToItem())

        recyclerview_newmessage.adapter = adapter
    }
    companion object {
        val TAG = "ChatLog"
    }
}