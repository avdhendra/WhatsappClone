package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity() {

    val auth by lazy{
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            if(auth.currentUser==null)
            {
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        }, 3000) // 3000 is the delayed time in milliseconds.

    }
}