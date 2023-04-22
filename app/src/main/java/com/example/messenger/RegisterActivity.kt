package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID

class RegisterActivity : ComponentActivity() {
    companion object{
        val TAG = "RegistterActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val username = findViewById<EditText>(R.id.username_edittext_registration)
        val register_btn = findViewById<Button>(R.id.register_button_registration)
        val already_have_account_textView = findViewById<TextView>(R.id.already_have_account_textView)
        val select_photo_button = findViewById<Button>(R.id.selectphoto_button)
        register_btn.setOnClickListener{
            performRegister()
        }
        already_have_account_textView.setOnClickListener{
            Log.d("RegisterActivity", "Try to show login activity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        select_photo_button.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent, 0)
        }

    }

    var SelectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null)
        {
            Log.d("RegisterActivity", "Photo was selected")

//            SelectedPhotoUri = data.data
            SelectedPhotoUri = data.data

            Log.d("RegisterActivity", "Photo name: $SelectedPhotoUri")
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, SelectedPhotoUri)
            val circle_image = findViewById<CircleImageView>(R.id.circle_image_view)
            circle_image.setImageBitmap(bitmap)

//            val bitmapDrawable = BitmapDrawable(bitmap)
            val select_photo_button = findViewById<Button>(R.id.selectphoto_button)
            select_photo_button.alpha = 0f
//            select_photo_button.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun performRegister() {
        val email = findViewById<EditText>(R.id.email_edittext_registration).text.toString()
        val password = findViewById<EditText>(R.id.password_edittext_registration).text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text email/password", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("RegisterActivity", "Email is" + email)
        Log.d("RegisterActivity", "Password: $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                Log.d("Main", "Successfully created user with uid: ${it.result.user!!.uid}")

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun uploadImageToFirebaseStorage()
    {
        if (SelectedPhotoUri == null) return
        Log.d("RegisterActivity", "Enter function uploadImage")
        val filename = UUID.randomUUID().toString()
//        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        val ref:StorageReference = FirebaseStorage.getInstance().getReference("/images/$filename")

        Log.d("RegisterActivity", "Name : $filename : $ref : $SelectedPhotoUri")

        ref.putFile(SelectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully upload image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
//            .addOnFailureListener{
//                Log.d("RegisterActivity", "Error: ${it.message}")
//            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String)
    {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val username = findViewById<EditText>(R.id.username_edittext_registration).text.toString()
        val user = User(uid, username, profileImageUrl)
        Log.d("RegisterActivity", "Entrance saveUserToFirebaseDatabaase $uid, $username, $user")
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved the user on database")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
        }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Error: ${it.message}")
            }
    }

}

class User(val uid: String, val username: String, val profileImageUrl: String)
{
    constructor() : this("", "", "")
}