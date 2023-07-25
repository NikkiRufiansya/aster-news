package com.nikki.news.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nikki.news.R
import com.nikki.news.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignUpBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        binding.btnSSigned.setOnClickListener {
            signUp()
        }

        binding.tvRedirectLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    fun signUp(){
        if(binding.etSEmailAddress.text.isBlank() || binding.etSPassword.text.isBlank() || binding.etSConfPassword.text.isBlank()){
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.etSPassword.text.toString() != binding.etSConfPassword.text.toString()){
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(binding.etSEmailAddress.text.toString(), binding.etSPassword.text.toString()).addOnCompleteListener(this){
            Log.d("TAG", "signUp: $it")
            if (it.isSuccessful){
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}