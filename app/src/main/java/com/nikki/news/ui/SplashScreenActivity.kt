package com.nikki.news.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.nikki.news.R
import com.nikki.news.ui.auth.LoginActivity
import com.nikki.news.ui.main.MainActivity
import com.nikki.news.utils.SessionManager

class SplashScreenActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sessionManager = SessionManager(this)

        Handler().postDelayed({
            if (sessionManager.getIsLogin()) {
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
        },3000)

    }
}