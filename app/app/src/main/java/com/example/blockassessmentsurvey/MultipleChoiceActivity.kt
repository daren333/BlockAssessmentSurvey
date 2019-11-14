package com.example.blockassessmentsurvey

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

class MultipleChoiceActivity : AppCompatActivity() {

//    private var questionText: TextView? = null
    private var doneBtn: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_choice)

        val questions = "Like,Ok,Dislike"

//        val questions = intent.getStringExtra(QUESTION_STRING)

        val radioContainer = findViewById<RadioGroup>(R.id.radioGroup)
        radioContainer.setOrientation(LinearLayout.HORIZONTAL)

        for (option in questions.split(",")){

            val toAdd = RadioButton(this)
            toAdd.id = View.generateViewId()

            toAdd.setText(option)

            Log.i(TAG,option)



            radioContainer.addView(toAdd)
        }

        doneBtn = findViewById(R.id.done)

        doneBtn?.setOnClickListener({



            val data = Intent()


            //gets the selected radio buttons text
            val ans = findViewById<RadioButton>(radioContainer!!.checkedRadioButtonId).text.toString()

            // puts the ans text into the intent
            data.putExtra(QANSWER_STRING,ans)

            //put the question id in the intent
            data.putExtra(QID_STRING, intent.getStringExtra(QID_STRING))

            setResult(Activity.RESULT_OK, data)
            finish()



            Log.i(TAG,ans)

        })

//        radioContainer.checkedRadioButtonId.text



    }



    companion object {
        private val TAG = "BAS-MultipleChoice"
        private val QUESTION_STRING = "questionstring"
        private val QANSWER_STRING = "qanswer"
        private val QID_STRING = "qid"
    }
}
