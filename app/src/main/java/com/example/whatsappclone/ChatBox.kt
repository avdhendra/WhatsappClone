package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import java.security.AccessController.getContext

const val UID="uid"
const val NAME="name"
const val IMAGE="photo"
class ChatBox : AppCompatActivity() {
    private val friend:String ?by lazy {
intent.getStringExtra(UID)
    }
    private val name: String? by lazy {
        intent.getStringExtra(NAME)
    }
    private val image: String? by lazy {
        intent.getStringExtra(IMAGE)
    }
    private val mCurrentUid: String by lazy {
        FirebaseAuth.getInstance().uid!!
    }
    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser: User
    lateinit var nameTv:TextView
    lateinit var userimgview:ImageView
    lateinit var chatText:EmojiEditText
    lateinit var sendBtn:FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_chat_box)
        FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get().addOnSuccessListener {
            currentUser= it.toObject(User::class.java)!!
        }
nameTv=findViewById(R.id.nameTv)
userimgview=findViewById(R.id.avatar_group)
        sendBtn=findViewById(R.id.voiceRecordingOrSend)
        chatText=findViewById(R.id.messageInput)
        nameTv.text=name
if(chatText.text.toString().isEmpty())
{


}else{
    sendBtn.setImageResource(R.drawable.send_icon)
}

        Picasso.get().load(image).into(userimgview)

    }
    private fun getMessage(FriendId: String)=db.reference.child("message/${getId(FriendId)}")

    private fun getInbox(toUser:String,fromUser:String)
    {
        db.reference.child("chats/$toUser/$fromUser")
    }

    private fun getId(FriendId:String):String{
        return if(FriendId>mCurrentUid)
        {
            mCurrentUid+FriendId
        }
        else{
            FriendId+mCurrentUid

        }
    }
}





