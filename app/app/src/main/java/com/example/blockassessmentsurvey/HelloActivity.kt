package com.example.blockassessmentsurvey

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HelloActivity : AppCompatActivity() {
    private lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        val intent = intent
        val textToShow = intent.getStringExtra("text")
        text = findViewById(R.id.textView)
        if(textToShow != null){
            text.text = textToShow
        }
    }
}
