package com.example.blockassessmentsurvey

import android.os.Bundle
<<<<<<< HEAD
import android.widget.Button
=======
>>>>>>> select1
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HelloActivity : AppCompatActivity() {
    private lateinit var text: TextView
    private lateinit var exitBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

<<<<<<< HEAD
        text = findViewById(R.id.textView)
        exitBtn = findViewById(R.id.exit_button)


        val intent = intent
        val textToShow = intent.getStringExtra("text")
        if(textToShow != null){
            text.text = textToShow
        }

        exitBtn.setOnClickListener {
            finish()
        }
=======
        val survey = intent.getStringExtra("surveyType")
        val tv : TextView = findViewById(R.id.textView1)
        tv.text = survey


>>>>>>> select1
    }
}
