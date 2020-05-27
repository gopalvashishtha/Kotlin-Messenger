package com.gopal.kotlinmessenger.Messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gopal.kotlinmessenger.Models.User
import com.gopal.kotlinmessenger.R
//import com.gopal.kotlinmessenger.RegisterLogin.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    var Adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"
        recyclerview_newmessage.apply {
            adapter = Adapter
            layoutManager = LinearLayoutManager(this@NewMessageActivity)
        }

        fetchUsers()
    }
    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("NewMessage activity", it.toString())
                    val  user = it.getValue(User::class.java)
                    if(user != null) {
                    Adapter.add(UserItem(user))
                }
                }
                Adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }
                recyclerview_newmessage.adapter = Adapter
                recyclerview_newmessage.layoutManager = LinearLayoutManager(this@NewMessageActivity)

            }



            override fun onCancelled(p0: DatabaseError) {

            }



        })
    }
}
 class UserItem(val user: User): Item<ViewHolder>() {
     override fun bind(viewHolder: ViewHolder, position: Int) {
         viewHolder.itemView.username_textview_new_message.text = user.username

         if (!user.profileImageUrl!!.isEmpty()) {

             Picasso
                 .get()
                 .load(user.profileImageUrl)
                 .placeholder(R.drawable.default_image) // can also be a drawable
                 .into(viewHolder.itemView.imageview_new_message);


             //Picasso.get().load(R.drawable.default_image).into(viewHolder.itemView.imageview_new_message) //
         }
//         else {
//
//             Picasso.get().load(user.profileImageUrl)
//                 .into(viewHolder.itemView.imageview_new_message)
//
//         }
     }


     override fun getLayout(): Int {
         return R.layout.user_row_new_message

     }

 }
