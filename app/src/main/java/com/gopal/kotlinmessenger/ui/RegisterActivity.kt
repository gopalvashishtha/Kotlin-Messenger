package com.gopal.kotlinmessenger.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.gopal.kotlinmessenger.Messages.LatestMessagesActivity
import com.gopal.kotlinmessenger.Models.User
import com.gopal.kotlinmessenger.R
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            val email = email_edittext_register.text.toString()
            val password = password_edittext_register.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("MainActivity", "Email is: " + email)
            Log.d("MainActivity", "Password: $password")

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener

                    Log.d("Register Activity", "Successfully created user with uid: ${it.result?.user?.uid}")

                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Log.d("Register Activity", "Failed to create user: ${it.message}")
                    Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        already_have_account_text_view.setOnClickListener {
            Log.d("MainActivity", "Try to show login activity")

            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        selectphoto_button_register.setOnClickListener {
            Log.d("Register Activity", "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedImageUri == null){
            Toast.makeText(this, "Please select a profile photo", Toast.LENGTH_SHORT).show()
            return}
        val filename = UUID.randomUUID().toString()
      val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                Log.d("Register Activity", "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Register Activity", "File location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{

            }


    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
       val ref =  FirebaseDatabase.getInstance().getReference("/users/$uid")
       val user = User(
           uid,
           username_edittext_register.text.toString(),
           profileImageUrl
       )
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Register Activity","Finally we saved the user to Firebase Database")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Register Activity", "Failed to save user to Firebase Database: ${it.message}")
            }
    }

    var selectedImageUri:Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data !=null){
            Log.d("Register Activity", "Image was selected")

            selectedImageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f
        }
    }
}
