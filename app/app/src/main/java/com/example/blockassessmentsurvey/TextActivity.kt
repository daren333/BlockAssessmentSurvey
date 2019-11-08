package com.example.blockassessmentsurvey

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

class TextActivity : AppCompatActivity() {

    private var question: TextView? = null
    private var response: EditText? = null
    private var doneBtn: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        question = findViewById(R.id.QuestionText)
        response = findViewById(R.id.responseText)
        doneBtn = findViewById(R.id.done)

        //Sets the Question to be passed in
        question?.text = intent.getStringExtra(QUESTION_STRING)


        //returns the response from the Edit Text
        doneBtn?.setOnClickListener({

            val intent = Intent()
            intent.putExtra(ANSWER_STRING,response?.text)
            setResult(Activity.RESULT_OK,intent)
            finish()

        })
    }


    companion object {

        private val QUESTION_STRING = "questionstring"
        private val ANSWER_STRING = "answer_string"
    }


}
