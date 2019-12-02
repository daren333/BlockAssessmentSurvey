package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {

    private var emailTV: EditText? = null
    private var passwordTV: EditText? = null
    private var password2TV: EditText? = null
    private var regBtn: Button? = null
    private var progressBar: ProgressBar? = null

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()

        initializeUI()

        regBtn!!.setOnClickListener { registerNewUser() }
    }

    private fun registerNewUser() {
        progressBar!!.visibility = View.VISIBLE

        val email: String
        val password: String
        val password2: String
        email = emailTV!!.text.toString()
        password = passwordTV!!.text.toString()
        password2 = password2TV!!.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            progressBar!!.visibility = View.GONE
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password...", Toast.LENGTH_LONG).show()
            progressBar!!.visibility = View.GONE
            return
        }
        if(password != password2){
            Toast.makeText(applicationContext, "Password not matching, please reenter...", Toast.LENGTH_LONG).show()
            passwordTV!!.setText("")
            password2TV!!.setText("")
            progressBar!!.visibility = View.GONE
            return
        }

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()
                    progressBar!!.visibility = View.GONE

                    val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Registration failed! Please try again later", Toast.LENGTH_LONG).show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }

    private fun initializeUI() {
        emailTV = findViewById(R.id.email)
        passwordTV = findViewById(R.id.password)
        password2TV = findViewById(R.id.password2)
        regBtn = findViewById(R.id.register)
        progressBar = findViewById(R.id.progressBar)
    }
}
