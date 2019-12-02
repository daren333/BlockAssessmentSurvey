package com.example.blockassessmentsurvey

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity.CENTER
import android.view.View
import android.widget.*

import androidx.core.view.updatePadding
import java.lang.IllegalStateException

import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity


private lateinit var radioContainer : RadioGroup



class MultipleChoiceActivity : AppCompatActivity() {

    private var questionText: TextView? = null
    private var doneBtn: ImageButton? = null
    private var progressBar: ProgressBar? = null
    private var submitButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_choice)

//        val questions = "Like,Ok,Dislike"

        val toAsk = intent.getStringExtra(QUESTION_STRING)

        questionText = findViewById(R.id.QuestionText)

        questionText?.text = toAsk

        val questions = intent.getStringExtra(QANSWER_STRING)

        progressBar = findViewById(R.id.progressBar4)

        val questionProgress = intent.getStringExtra(PROGRESS_STRING)
        progressBar!!.setProgress(questionProgress.toInt())

        radioContainer = findViewById<RadioGroup>(R.id.radioGroup)
        radioContainer.setOrientation(LinearLayout.VERTICAL)

        for (option in questions.split("/")){

            val toAdd = RadioButton(this)
            toAdd.id = View.generateViewId()
            toAdd.gravity = CENTER
            toAdd.background = null
//            toAdd.updatePadding(20)


            toAdd.setText(option)

            Log.i(TAG,option)



            radioContainer.addView(toAdd)
        }

        submitButton = findViewById(R.id.submit)

        submitButton?.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        doneBtn = findViewById(R.id.next)

        doneBtn?.setOnClickListener{ submit() }


    }

    private fun submit() {


        //gets the selected radio buttons text
        val ans = findViewById<RadioButton>(radioContainer.checkedRadioButtonId)

        if(ans == null) {
            Toast.makeText(applicationContext, "Please select an answer to continue.", Toast.LENGTH_LONG).show()
            return
        }
        else {

            val data = Intent()

            // puts the ans text into the intent
            data.putExtra(QANSWER_STRING, ans.text.toString())

            //put the question id in the intent
            data.putExtra(QID_STRING, intent.getStringExtra(QID_STRING))

            setResult(Activity.RESULT_OK, data)
            finish()

            Log.i(TAG, ans.text.toString())

            return
        }
    }


    companion object {
        private val TAG = "BAS-MultipleChoice"
        private val QUESTION_STRING = "questionstring"
        private val QANSWER_STRING = "qanswer"
        private val QID_STRING = "qid"
        private val PROGRESS_STRING = "progess"
    }
}
