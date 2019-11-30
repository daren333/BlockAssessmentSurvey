package com.example.blockassessmentsurvey

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

class fillInTheBlankActivity : AppCompatActivity() {

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

            val data = Intent()

            //puts the answer in the intent
            data.putExtra(QANSWER_STRING,response?.text)

            //puts the question id in the string
            data.putExtra(QID_STRING,intent.getStringExtra(QID_STRING))

            setResult(Activity.RESULT_OK,data)
            finish()

        })
    }


    companion object {

        private val QUESTION_STRING = "questionstring"
        private val QANSWER_STRING = "qanswer"
        private val QID_STRING = "qid"
    }
}
