package com.example.blockassessmentsurvey

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class MultipleAnswerActivity : AppCompatActivity() {

    private var questionText: TextView? = null
    private var doneBtn: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_answer)


        val toAsk = intent.getStringExtra(QUESTION_STRING)

        questionText = findViewById(R.id.QuestionText)

        questionText?.text = toAsk

        val questions = intent.getStringExtra(QANSWER_STRING)
    }

    companion object {
        private val TAG = "BAS-MultipleChoice"
        private val QUESTION_STRING = "questionstring"
        private val QANSWER_STRING = "qanswer"
        private val QID_STRING = "qid"

    }
}
