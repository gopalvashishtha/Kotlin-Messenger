package com.gopal.kotlinmessenger.Messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.gopal.kotlinmessenger.Models.ChatMessage
import com.gopal.kotlinmessenger.Models.User
import com.gopal.kotlinmessenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerview_chat_log.adapter = adapter
        recyclerview_chat_log.layoutManager = LinearLayoutManager(this@ChatLogActivity)

//        val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.username

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d("ChatLog Activity", " Attempt to send message")
            performSendMessage()
        }


    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = toUser.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d("chatLog Activity", chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentuser= LatestMessagesActivity.currentuser ?: return
                    adapter.add(
                        ChatFromItem(
                            chatMessage.text,
                            currentuser
                        )
                    )}
                    else{
                        val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                        adapter.add(
                            ChatToItem(
                                chatMessage.text,
                                toUser
                            )
                        )
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }


    private fun performSendMessage() {

        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = toUser.uid
        if(fromId == null) return
      //  val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(
            reference.key!!,
            text,
            fromId,
            toId,
            System.currentTimeMillis() / 1000
        )
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("ChatLog Activity", " Saved chat messages: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
       latestMessageRef.setValue(chatMessage)


        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
}

class ChatFromItem(val text : String, val user: User) : Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
        val uri = user.profileImageUrl
        val targetimageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(uri).into(targetimageView)


    }

    override fun getLayout(): Int {
       return R.layout.chat_from_row
    }



}

class ChatToItem(val text: String, val user: User) : Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        val uri = user.profileImageUrl
        val targetimageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetimageView)

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }



}
