package com.nikki.news.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.nikki.news.R
import com.nikki.news.databinding.ActivityLoginBinding
import com.nikki.news.ui.main.MainActivity
import com.nikki.news.utils.SessionManager

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding

    lateinit var auth: FirebaseAuth
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(this)
        binding.btnLogin.setOnClickListener {
            login()
        }
        binding.tvRedirectSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

    }

    fun login(){
        auth.signInWithEmailAndPassword(binding.etEmailAddress.text.toString().trim(), binding.etPassword.text.toString().trim()).addOnCompleteListener(this){
            Log.d("TAG", "login: $it")
            if (it.isSuccessful){
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                sessionManager.setIsLogin()
            }else{
                Toast.makeText(this, "Log In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}