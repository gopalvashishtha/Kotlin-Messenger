package com.gopal.kotlinmessenger.Messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gopal.kotlinmessenger.Models.ChatMessage
import com.gopal.kotlinmessenger.Models.User
import com.gopal.kotlinmessenger.R
import com.gopal.kotlinmessenger.ui.RegisterActivity
import com.gopal.kotlinmessenger.Views.LatestMessageRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlin.collections.HashMap

class LatestMessagesActivity : AppCompatActivity() {

    companion object{
       var currentuser: User? = null

    }
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        recyclerview_latestmessage.adapter = adapter
        recyclerview_latestmessage.layoutManager = LinearLayoutManager(this@LatestMessagesActivity)
        recyclerview_latestmessage.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
         adapter.setOnItemClickListener { item, view ->
             val intent = Intent(this, ChatLogActivity::class.java)
             val row = item as LatestMessageRow
             intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
             startActivity(intent)
         }
        listenForLatestMessages()

        fetchCurrentUser()

        val uid = FirebaseAuth.getInstance().uid
        if(uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    var latestMessagesMap = HashMap<String, ChatMessage>()
    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(
                LatestMessageRow(
                    it
                )
            )
        }
    }

   private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{


            override fun onCancelled(p0: DatabaseError) {

            }


            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }


            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                p0.children.forEach {
                    Log.d("Latest Messages", it.toString())
                    val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                    latestMessagesMap[p0.key!!] = chatMessage
                    refreshRecyclerViewMessages()

                }


            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
               // val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                p0.children.forEach {
                    Log.d("Latest Messages", it.toString())
                    val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                    latestMessagesMap[p0.key!!] = chatMessage
                    refreshRecyclerViewMessages()
                    //adapter.add(LatestMessageRow(chatMessage))

                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }


        })

    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                currentuser = p0.getValue(User::class.java)
                Log.d("LatestMessage Activity", "Current User ${currentuser?.username}")
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)

            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
