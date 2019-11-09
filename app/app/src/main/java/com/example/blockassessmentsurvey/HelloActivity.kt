package com.example.blockassessmentsurvey

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HelloActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        val survey = intent.getStringExtra("surveyType")
        val tv : TextView = findViewById(R.id.textView1)
        tv.text = survey


    }
}
